package com.platformer.game.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Iterator;
import java.util.List;

public class Character implements ICharacter {
    // constants
    private final float ANIMATION_SPEED = .1f;
    private final float FAST_FALL_FORCE = -4_000f;
    private final float GRAVITY = -800f;
    private final float JUMP_SPEED = 550f;
    private final int MAX_PROJECTILES_COUNT = 5;
    private final int MAX_JUMP_COUNT = 2;
    private final int POSSIBLE_DIRECTIONS = 4;

    // controls
    private final Controles controles;
    private final int height;
    private final int offsetX;
    private final int offsetY;
    private final String projectileFile;
    private final float speed;
    private final int width;

    // textures
    private final Texture texture;
    private final Texture heartTexture;
    private final TextureRegion[][] animationInstances;
    private final Animation<TextureRegion>[] animation;
    private final List<Projectile> projectiles = new java.util.ArrayList<>();
    private int LIVES_COUNT = 5;

    // variables
    private float positionY;
    private float positionX;

    // states
    private int animationState = -1;
    private boolean isLeftLooking = true;
    private boolean isJumping = false;
    private int jumpCount = 0;
    private int lastValidAnimationIndex = 0;
    private float time = .0f;
    private float velocityY = 0;


    public Character(String fileName, int characterWidth, int characterHeight, float characterSpeed, float percentToLeft, float percentToBottom, double scale, Controles controles, int offsetX, int offsetY, String projectileFile) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName cannot be null");
        }

        if (characterWidth <= 0 || characterHeight <= 0 || scale <= 0 || percentToBottom < 0 || percentToLeft < 0) {
            throw new IllegalArgumentException("width, height, scale, percentToBottom, percentToLeft cannot be less than 0");
        }

        this.width = (int) (characterWidth / scale);
        this.height = (int) (characterHeight / scale);
        this.speed = characterSpeed;
        this.positionX = percentToLeft;
        this.positionY = percentToBottom;
        this.controles = controles;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.projectileFile = projectileFile;

        this.texture = new Texture(fileName);
        this.animationInstances = TextureRegion.split(this.texture, characterWidth, characterHeight);

        this.heartTexture = new Texture("heart.png");

        this.animation = new Animation[POSSIBLE_DIRECTIONS];
        for (int i = 0; i < this.animation.length; i++) {
            this.animation[i] = new Animation<>(this.ANIMATION_SPEED, this.animationInstances[i]);
        }
    }

    public void render(SpriteBatch batch, float size) {
        renderCharacter(batch);
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile p = iterator.next();
            if (p.isOut(size)) {
                iterator.remove();
            } else {
                p.render(batch);
            }
        }
    }

    public void update(float dt, Platform[] platforms) {
        time += dt;
        updateCharacter(dt, platforms);
        updateProjectiles(dt);
    }

    public void setSpawn(float x, float y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("x and y cannot be less than 0");
        }

        this.positionX = x;
        this.positionY = y;
    }


    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void removeProjectile(Projectile projectile) {
        if (projectile == null) {
            throw new IllegalArgumentException("projectile cannot be null");
        }

        if (!projectiles.contains(projectile)) {
            throw new IllegalArgumentException("projectile is not in the list");
        }

        projectiles.remove(projectile);
    }


    public boolean hasProjectile(Projectile projectile) {
        if (projectile == null) {
            throw new IllegalArgumentException("projectile cannot be null");
        }

        return projectiles.contains(projectile);
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getRemainingProjectiles() {
        return MAX_PROJECTILES_COUNT - projectiles.size();
    }

    public void kill() {
        this.LIVES_COUNT--;
    }

    public int getLives() {
        return this.LIVES_COUNT;
    }

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    private void updateCharacter(float dt, Platform[] platforms) {
        isJumping();
        isShooting();

        float gravityForce = GRAVITY;
        boolean fastFallUsed = false;
        if (isJumping && controles.goDown()) {
            gravityForce += FAST_FALL_FORCE;
            fastFallUsed = true;
        }
        velocityY += gravityForce * dt;
        this.positionY += velocityY * dt;

        updateJumpStatus();

        // Update character's horizontal position
        int dx = isMoving();
        positionX += dx * this.speed * dt;
        determineNextAnimationState(dx);
        isOnAPlatform(platforms, fastFallUsed);
    }

    private void updateProjectiles(float dt) {
        for (Projectile p : projectiles) {
            p.update(dt);
        }
    }

    private void isOnAPlatform(Platform[] platforms, boolean fastFallUsed) {
        boolean isGoingDown = velocityY < 0;
        for (Platform p : platforms) {
            if (p.isCharacterOnIt(positionX, positionY, this.width + this.offsetX, this.height + this.offsetY) && (isGoingDown || fastFallUsed)) {
                this.positionY = p.getPositionY() + p.getHeight();
                isJumping = false;
                velocityY = 0;
                jumpCount = 0;
            }
        }
    }

    private void determineNextAnimationState(int dx) {
        animationState = -1;
        if (dx < 0) {
            animationState = 1;
        } else if (dx > 0) {
            animationState = 2;
        }
    }

    private void updateJumpStatus() {
        if (positionY <= 0) {
            isJumping = false;
            jumpCount = 0;
            velocityY = 0;
        }
    }

    private int isMoving() {
        if (controles.goLeft() || controles.goRight()) {
            if (controles.goLeft()) {
                this.isLeftLooking = true;
                return -1;
            }
            this.isLeftLooking = false;
            return 1;
        }
        return 0;
    }

    private void isJumping() {
        if (jumpCount < MAX_JUMP_COUNT && controles.jump()) {
            if (isJumping) {
                jumpCount++;
            } else {
                isJumping = true;
                jumpCount = 1;
            }
            velocityY = JUMP_SPEED;
        }
    }

    private void isShooting() {
        if (controles.shoot() && getRemainingProjectiles() > 0) {
            projectiles.add(new Projectile(this, 45, 37, 500f, this.isLeftLooking, this.projectileFile));
        }
    }

    private void renderCharacter(SpriteBatch batch) {
        TextureRegion characterTexture = animationState < 0 ? animationInstances[lastValidAnimationIndex][0] : animation[lastValidAnimationIndex = animationState].getKeyFrame(this.time, true);
        batch.draw(characterTexture, this.positionX - 20, this.positionY, this.width, this.height);

        int heartSize = this.LIVES_COUNT * this.heartTexture.getWidth() + (this.LIVES_COUNT - 1) * 20;
        float heartPositionX = this.positionX + (this.width + this.offsetX) / 2 - heartSize / 2;
        for (int i = 0; i < this.LIVES_COUNT; i++) {
            batch.draw(this.heartTexture, heartPositionX + (i * 20), this.positionY + this.height + 5, 15, 15);
        }
    }

    public TextureRegion getTexture() {
        return animationInstances[0][0];
    }
}

