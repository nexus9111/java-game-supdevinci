package com.platformer.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Iterator;
import java.util.List;

public class Character {
    private final int MAX_PROJECTILES = 5;
    private final Animation<TextureRegion>[] animCharacter;
    private final int width;
    private final int height;
    private final float speed;
    private final float animationSpeed;
    private final int MAX_JUMP_COUNT = 2;
    private final float GRAVITY = -800f;
    private final float JUMP_SPEED = 550f;
    private final float FAST_FALL_FORCE = -4_000f;
    private final int jumpKey;
    private final int leftKey;
    private final int rightKey;
    private final int downKey;
    private final int shootKey;
    private final int DIRECTIONS = 4;
    private final int offsetX;
    private final int offsetY;
    private float distanceToLeft;
    private float distanceToBottom;
    private float velocityY = 0;
    private boolean isJumping = false;
    private float time = .0f;
    private int jumpCount = 0;
    private int anim = -1;
    private int stop = 0;
    private boolean isLeft = true;
    private final List<Projectile> projectiles = new java.util.ArrayList<>();
    private int lives = 5;

    private final Texture txCharacter;
    private final Texture txHeart;
    private final TextureRegion[][] txrCharacterTiles;

    private final String projectileFile;

    /**
     * Constructs a Character object.
     *
     * @param fileName                the file name of the texture
     * @param characterWidth          the width of the character in pixels (in the source image)
     * @param characterHeight         the height of the character in pixels (in the source image)
     * @param characterSpeed          the speed of the character
     * @param characterAnimationSpeed the speed of the character animation
     * @param percentToLeft           the percentage of the screen to place the character from the left
     * @param percentToBottom         the percentage of the screen to place the character from the bottom
     * @param scale                   the scale of the character (the size will be divided by this number)
     * @param jumpKey                 the key to jump
     * @param leftKey                 the key to move left
     * @param rightKey                the key to move right
     * @param downKey                 the key to move down
     * @param shootKey                the key to shoot
     * @param offsetX                 the offset of the character in the x direction (the weight reduction or increase in case of character oversized background).
     *                                Set to 0 if you don't want to change the weight of the character
     * @param offsetY                 the offset of the character in the y direction (the height reduction or increase in case of character oversized background)
     *                                Set to 0 if you don't want to change the weight of the character
     * @throws IllegalArgumentException if fileName is null, or if width, height, scale, percentToBottom, or percentToLeft is less than 0
     */
    public Character(
            String fileName,
            int characterWidth,
            int characterHeight,
            float characterSpeed,
            float characterAnimationSpeed,
            float percentToLeft,
            float percentToBottom,
            double scale,
            int jumpKey,
            int leftKey,
            int rightKey,
            int downKey,
            int shootKey,
            int offsetX,
            int offsetY,
            String projectileFile
    ) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName cannot be null");
        }

        if (characterWidth <= 0 || characterHeight <= 0 || scale <= 0 || percentToBottom < 0 || percentToLeft < 0) {
            throw new IllegalArgumentException("width, height, scale, percentToBottom, percentToLeft cannot be less than 0");
        }

        this.width = (int) (characterWidth / scale);
        this.height = (int) (characterHeight / scale);
        this.speed = characterSpeed;
        this.animationSpeed = characterAnimationSpeed;
        this.distanceToLeft = percentToLeft;
        this.distanceToBottom = percentToBottom;
        this.jumpKey = jumpKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.downKey = downKey;
        this.shootKey = shootKey;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.projectileFile = projectileFile;

        this.txCharacter = new Texture(fileName);
        this.txrCharacterTiles = TextureRegion.split(this.txCharacter, characterWidth, characterHeight);

        this.txHeart = new Texture("heart.png");

        this.animCharacter = new Animation[DIRECTIONS];
        for (int i = 0; i < this.animCharacter.length; i++) {
            this.animCharacter[i] = new Animation<>(this.animationSpeed, this.txrCharacterTiles[i]);
        }
    }

    public float getDistanceToLeft() {
        return distanceToLeft;
    }

    public float getDistanceToBottom() {
        return distanceToBottom;
    }

    private void updateCharacter(float dt, Platform[] platforms) {
        int dx = 0;

        if (Gdx.input.isKeyPressed(this.leftKey) || Gdx.input.isKeyPressed(this.rightKey))
            if (Gdx.input.isKeyPressed(this.leftKey)) {
                this.isLeft = true;
                dx += -1;
            } else {
                this.isLeft = false;
                dx += 1;
            }

        if (jumpCount < MAX_JUMP_COUNT && Gdx.input.isKeyJustPressed(this.jumpKey)) {
            if (isJumping) {
                jumpCount++;
            } else {
                isJumping = true;
                jumpCount = 1;
            }
            velocityY = JUMP_SPEED;
        }

        if (Gdx.input.isKeyJustPressed(this.shootKey) && getAvailableProjectiles() > 0) {
            projectiles.add(new Projectile(
                    this,
                    45,
                    37,
                    500f,
                    this.isLeft,
                    this.projectileFile
            ));
        }

        anim = -1;

        // Apply gravity and fast fall force
        float gravityForce = GRAVITY;
        boolean fastFallUsed = false;
        if (isJumping && Gdx.input.isKeyPressed(this.downKey)) {
            gravityForce += FAST_FALL_FORCE;
            fastFallUsed = true;
        }
        velocityY += gravityForce * dt;
        this.distanceToBottom += velocityY * dt;

        // Check if the character is on the ground
        if (distanceToBottom <= 0) {
            // this.distanceToBottom = 0;
            isJumping = false;
            jumpCount = 0;
            velocityY = 0;
        }

        // Update character's horizontal position
        distanceToLeft += dx * this.speed * dt;

        // Determine animation index
        if (dx < 0) {
            anim = 1;
        } else if (dx > 0) {
            anim = 2;
        }

        time += dt;

        boolean isGoingDown = velocityY < 0;
        for (Platform p : platforms) {
            if (p.isCharacterOnIt(distanceToLeft, distanceToBottom, this.width + this.offsetX, this.height - this.offsetY) && (isGoingDown || fastFallUsed)) {
                this.distanceToBottom = p.getPercentToBottom() + p.getHeight();
                isJumping = false;
                velocityY = 0;
                jumpCount = 0;
            }
        }

        for (Projectile p : projectiles) {
            p.update(dt);
        }
    }

    private void renderCharacter(SpriteBatch batch) {
        TextureRegion txrCharacter = anim < 0 ? txrCharacterTiles[stop][0] : animCharacter[stop = anim].getKeyFrame(this.time, true);
        batch.draw(txrCharacter, this.distanceToLeft - 20, this.distanceToBottom, this.width, this.height);

        for (int i = 0; i < this.lives; i++) {
            batch.draw(this.txHeart, this.distanceToLeft + (i * 20), this.distanceToBottom + this.height + 5, 15, 15);
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
        updateCharacter(dt, platforms);
    }

    public void setSpawn(float x, float y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("x and y cannot be less than 0");
        }

        this.distanceToLeft = x;
        this.distanceToBottom = y;
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

    public int getAvailableProjectiles() {
        return MAX_PROJECTILES - projectiles.size();
    }

    public void kill () {
        this.lives--;
    }

    public int getLives () {
        return this.lives;
    }
}
