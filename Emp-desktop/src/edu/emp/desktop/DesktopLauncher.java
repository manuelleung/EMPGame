package edu.emp.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import edu.emp.game.EMPGame;
import edu.emp.game.EmpGame;

// This is the launcher (Main)
public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "EMP";
		// We will change this later depending on our chose size for screen resolution
		config.width = 640;
		config.height = 640; 
		new LwjglApplication(new EMPGame(), config);
	}
}