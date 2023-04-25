package com.platformer.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.platformer.game.MyGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setWindowedMode(1200, 800);
		config.setResizable(false);
		// set keyboard to azerty
		config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);
		config.setTitle("Platformer");
		new Lwjgl3Application(new MyGame(), config);
	}
}
