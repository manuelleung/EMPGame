/*
* EMPGame renders the first screen.
*/

package edu.emp.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class EMPGame extends Game {

	public SpriteBatch batch;
	public BitmapFont font;
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		// Uses LibGDX's default font: Arial
		font = new BitmapFont();
		
		this.setScreen(new MainMenuScreen(this));		
	}
	
	public void render() {
		// com.badlogic.gdx.Game will render the MainMenuScreen!
		super.render();	// very important!
	}
	
	public void dispose() {
		batch.dispose();
		font.dispose();
	}

}