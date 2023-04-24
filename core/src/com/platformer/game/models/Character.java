package com.platformer.game.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Character {
    private final Texture txCharacter;
    private final TextureRegion[][] txrCharacterTiles;
    private final Animation<TextureRegion>[] animCharacter;
    private final int characterWidth;
    private final int characterHeight;
    private final float characterSpeed;
    private final float characterAnimationSpeed;
    private final float characterX;
    private final float characterY;
    private final int directions;

    public Character(String fileName, int characterWidth, int characterHeight, float characterSpeed, float characterAnimationSpeed, float characterX, float characterY, int directions) {
        this.characterWidth = characterWidth;
        this.characterHeight = characterHeight;
        this.characterSpeed = characterSpeed;
        this.characterAnimationSpeed = characterAnimationSpeed;
        this.characterX = characterX;
        this.characterY = characterY;
        this.directions = directions;

        this.txCharacter = new Texture(fileName);
        this.txrCharacterTiles = TextureRegion.split(this.txCharacter, this.characterWidth, this.characterHeight);

        this.animCharacter = new Animation[2];
        for (int i = 0; i < this.animCharacter.length; i++) {
            this.animCharacter[i] = new Animation<>(this.characterAnimationSpeed, this.txrCharacterTiles[i]);
        }
    }

}
