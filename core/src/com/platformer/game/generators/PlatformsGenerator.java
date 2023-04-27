package com.platformer.game.generators;

import com.platformer.game.models.Platform;

import java.util.List;

public class PlatformsGenerator {
    private int platformIndex;

    private class PlatformObject {
        public float scale;
        public String fileName;
        public int mapIndex;
        public int animationFramesCount;
        public int height;
        public int width;
        public int percentToBottom;
        public int percentToLeft;

        public PlatformObject(String fileName, int width, int height, float scale, int percentToBottom, int percentToLeft, int animationFramesCount, int mapIndex) {
            this.scale = scale;
            this.fileName = fileName;
            this.mapIndex = mapIndex;
            this.animationFramesCount = animationFramesCount;
            this.height = height;
            this.width = width;
            this.percentToBottom = percentToBottom;
            this.percentToLeft = percentToLeft;
        }
    }

    private final List<PlatformObject> platforms = new java.util.ArrayList<>();

    public PlatformsGenerator() {
        platforms.add(new PlatformObject("platform1.png", 1998, 917, 5, 50, 50, 5, 0));
        platforms.add(new PlatformObject("small_platform1.png", 1578, 201, 10, 25, 15, 1, 0));
        platforms.add(new PlatformObject("small_platform1.png", 1578, 201, 10, 25, 85, 1, 0));
        platforms.add(new PlatformObject("small_platform1.png", 1578, 201, 10, 75, 85, 1, 0));
        platforms.add(new PlatformObject("small_platform1.png", 1578, 201, 10, 75, 15, 1, 0));

        platforms.add(new PlatformObject("platform2.png", 1226, 637, 3, 40, 50, 1, 1));
        platforms.add(new PlatformObject("small_platform2.png", 481, 60, 3, 35, 15, 1, 1));
        platforms.add(new PlatformObject("small_platform2.png", 481, 60, 3, 35, 85, 1, 1));
        platforms.add(new PlatformObject("small_platform2.png", 481, 60, 3, 70, 50, 1, 1));

        platforms.add(new PlatformObject("small_platform3.png", 525, 1456, 3, 30, 5, 1, 2));
        platforms.add(new PlatformObject("small_platform3.png", 525, 1456, 3, 15, 28, 1, 2));
        platforms.add(new PlatformObject("small_platform3.png", 525, 1456, 3, 0, 50, 1, 2));
        platforms.add(new PlatformObject("small_platform3.png", 525, 1456, 3, 15, 72, 1, 2));
        platforms.add(new PlatformObject("small_platform3.png", 525, 1456, 3, 30, 95, 1, 2));
        platforms.add(new PlatformObject("small_platform1.png", 1578, 201, 5, 75, 50, 1, 2));

        platforms.add(new PlatformObject("small_platform6.png", 1171, 169, 7, 35, 15, 1, 5));
        platforms.add(new PlatformObject("small_platform6.png", 1171, 169, 7, 35, 85, 1, 5));
        platforms.add(new PlatformObject("small_platform6.png", 1171, 169, 7, 20, 50, 1, 5));
        platforms.add(new PlatformObject("small_platform6.png", 1171, 169, 7, 60, 15, 1, 5));
        platforms.add(new PlatformObject("small_platform6.png", 1171, 169, 7, 60, 85, 1, 5));
        platforms.add(new PlatformObject("small_platform6.png", 1171, 169, 7, 50, 50, 1, 5));
    }

    public Platform[] getAllPlatforms(int mapIndex) {
        return platforms.
                stream().
                filter(platform -> platform.mapIndex == mapIndex).
                map(platform -> new Platform(platform.fileName, platform.width, platform.height, (int) platform.scale, platform.percentToBottom, platform.percentToLeft, platform.animationFramesCount)).
                toArray(Platform[]::new);
    }
}
