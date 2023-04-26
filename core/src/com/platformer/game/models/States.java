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

        // kill characters hit by projectile
        for (Projectile projectile : this.projectiles) {
            for (Character character : this.characters) {
                if (projectile.getX() >= character.getPositionX() &&
                        projectile.getX() <= character.getPositionX() + character.getWidth() + character.getOffsetX() &&
                        projectile.getY() >= character.getPositionY() &&
                        projectile.getY() <= character.getPositionY() + character.getHeight() &&
                        !character.hasProjectile(projectile)) {
                    charactersHitByProjectile.add(character);
                    projectilesToRemove.add(projectile);
                }
            }
        }

        // if two projectiles hit together, remove them both
        for (Projectile projectile : this.projectiles) {
            for (Projectile projectile2 : this.projectiles) {
                if (projectile != projectile2 &&
                        projectile.getX() <= projectile2.getX() + projectile2.getWidth() &&
                        projectile.getX() >= projectile2.getX() &&
                        projectile.getY() <= projectile2.getY() + projectile2.getHeight() &&
                        projectile.getY() >= projectile2.getY() &&
                        projectile.isLeft() != projectile2.isLeft()) {
                    if (!projectilesToRemove.contains(projectile)) {
                        projectilesToRemove.add(projectile);
                    }
                    if (!projectilesToRemove.contains(projectile2)) {
                        projectilesToRemove.add(projectile2);
                    }
                }
            }
        }

        // remove projectiles used
        for (Character character : this.characters) {
            for (Projectile projectile : projectilesToRemove) {
                if (character.hasProjectile(projectile)) {
                    character.removeProjectile(projectile);
                }
            }
        }

        return charactersHitByProjectile;
    }
}
