/*
 * MainMenuScreen class will render the Main Menu for the game.
 * 
 * NOTE: THIS CLASS GREATLY NEEDS CHANGES
 */

package edu.emp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MainMenuScreen implements Screen {
	final EMPGame game;
	
	private OrthographicCamera camera;
	
	public MainMenuScreen (final EMPGame game) {
		this.game = game;
		
		camera = new OrthographicCamera();
		
		// NEEDS TO BE CHANGED
		camera.setToOrtho(false, 640, 640);
	}
	
	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// needed?
		camera.update();
		
		// using DRY principle, don't repeat yourself
		// from the book: The Pragmatic Programmer
		game.batch.begin();
		game.font.draw(game.batch, "Welcome to EMPGame", 100, 150);
		game.font.draw(game.batch, "Click anywhere to begin", 100, 100);
		game.batch.end();
		
		if (Gdx.input.isTouched()) {
			game.setScreen(new GameScreen(game));
			dispose();
		}
	}
	
	@Override
	public void resize(int width, int height) {		
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {		
	}

	@Override
	public void resume() {		
	}

	@Override
	public void dispose() {		
	}
}
