package edu.emp.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import edu.emp.gameworld.Controller;
import edu.emp.gameworld.Renderer;


// This is the game source 
public class EmpGame extends ApplicationAdapter {
	private static final String TAG = EmpGame.class.getName();
	
	// a reference to renderer and controller
	private Renderer gameRenderer;
	private Controller gameController;
	
	// control pausing of the game
	private boolean paused;
	
	@Override
	public void create () {
		// Set Libgdx log level to DEBUG
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// change this to LOG_NONE or LOG_INFO when we are ready to publish the game
		
		gameController = new Controller();
		gameRenderer = new Renderer(gameController);		
		
	}
	
	@Override
	public void resize(int width, int height) {
		// resizing will happen in the Renderer file.
		gameRenderer.resize(width, height);
	}

	@Override
	public void render () {
		// Do not update game world when paused.
		if (!paused) {
			// Update game world by the time that has passed
			// since the last rendered frame.
			gameController.update(Gdx.graphics.getDeltaTime());			
		}
		
		// Clear the screen
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Render the game world to screen
		gameRenderer.render();
		
		// Game world is active on start
		paused = false;		
	}
	
	@Override
	public void pause() {
		paused = true;		
	}
	
	@Override
	public void resume() {
		paused = false;		
	}
	
	@Override
	public void dispose() {
		// Again, let the Renderer dispose the resources
		gameRenderer.dispose();		
	}
	
}























