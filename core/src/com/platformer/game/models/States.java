package com.platformer.game.models;

import java.util.List;

public class States {
    private final Character[] characters;
    private final List<Projectile> projectiles = new java.util.ArrayList<Projectile>();

    public States(Character[] characters) {
        this.characters = characters;

        for (Character character : this.characters) {
            this.projectiles.addAll(character.getProjectiles());
        }
    }

    public List<Character> getCharactersHitByProjectile() {
        List<Character> charactersHitByProjectile = new java.util.ArrayList<Character>();
        List<Projectile> projectilesToRemove = new java.util.ArrayList<Projectile>();

        for (Projectile projectile : this.projectiles) {
            for (Character character : this.characters) {
                if (projectile.getX() >= character.getDistanceToLeft() &&
                        projectile.getX() <= character.getDistanceToLeft() + character.getWidth() + character.getOffsetX() &&
                        projectile.getY() >= character.getDistanceToBottom() &&
                        projectile.getY() <= character.getDistanceToBottom() + character.getHeight() &&
                        !character.hasProjectile(projectile)) {
                    charactersHitByProjectile.add(character);
                    projectilesToRemove.add(projectile);
                }
            }
        }

        for (Projectile projectile : projectilesToRemove) {
            for (Character character : this.characters) {
                if (character.hasProjectile(projectile)) {
                    character.removeProjectile(projectile);
                }
            }
        }

        return charactersHitByProjectile;
    }
}
