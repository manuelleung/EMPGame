/*
* EMPGame renders the first screen.
*/

package edu.emp.game;

import com.badlogic.gdx.Game;

public class EMPGame extends Game {
	
	@Override
	public void create() {
		setScreen(new MainMenuScreen(this));		
	}
	
	public void render() {
		// com.badlogic.gdx.Game will render the MainMenuScreen!
		super.render();	// very important!
	}
	
	public void dispose() {
		getScreen().dispose();
	}

}