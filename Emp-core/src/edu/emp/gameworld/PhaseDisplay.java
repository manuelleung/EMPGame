/*
 * InputProcessor is only needed to deal or make the "magic" numbers
 */

package edu.emp.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import edu.emp.game.EMPGame;

public class PhaseDisplay implements Screen, InputProcessor {
	final EMPGame game;
	
	private SpriteBatch batch;
	private Texture background;
	
	// pixel locations for the mini menu
	// These numbers needs to be changed. Taken from doing a "trick"
	private final int PHASE_BOX_X = 192;
	private final int PHASE_BOX_Y = 320;

	private Screen bgGameScreen;
	private Controller gameController;
	
	// timer to show the phase in seconds
	private float timer = 0;
	// how long the screen is rendered before disposed
	// here it says for 0.5 seconds
	private float timeLasts = 0.5f;
	
	
	public PhaseDisplay(final EMPGame game, Screen bgGameScreen, Controller gameController, TurnIndicator turn_of) {
		super();
		this.game = game;
		Gdx.input.setInputProcessor(this);
		batch = new SpriteBatch();
		
		this.bgGameScreen = bgGameScreen;
		this.gameController = gameController;
		
		if (turn_of == TurnIndicator.PLAYER)
			background = new Texture("phases/player_phase.png");
		else
			background = new Texture("phases/enemy_phase.png");
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
		
	}

	@Override
	public boolean keyTyped(char arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int arg2, int arg3) {
		// Print out x and y coordinates
		
		System.out.println("x: " + x + " y: " + y);
		
		return false;
	}

	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float arg0) {
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		timer += Gdx.graphics.getDeltaTime();
		
		batch.begin();
		batch.draw(background, PHASE_BOX_X, PHASE_BOX_Y);
		batch.end();
		
		// the screens lasts for x seconds
		if (timer >= timeLasts) {
			// remove MiniMenu Screen
			game.getScreen().dispose();
			game.setScreen(bgGameScreen);
			Gdx.input.setInputProcessor(gameController);
			dispose();
		}
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

}
