package com.emp.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.emp.game.Emp;

// This is the launcher (Main)
public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "EMP";
		// We will change this later depending on our chose size for screen resolution
		config.width = 480;
		config.height = 480; 
		new LwjglApplication(new Emp(), config);
	}
}
