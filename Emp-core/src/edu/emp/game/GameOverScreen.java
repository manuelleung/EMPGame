/*
 * GameOverScreen class will render the Game Over for the game.
 * 
 * NOTE: THIS CLASS GREATLY NEEDS CHANGES
 */

package edu.emp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameOverScreen implements Screen, InputProcessor {
	final EMPGame game;
	
	private OrthographicCamera camera;
	
	private SpriteBatch batch;
	private Texture background;

	private Music bgMusic;
	
	public GameOverScreen (final EMPGame game) {
		super();
		Gdx.input.setInputProcessor(this);
		this.game = game;
		
		camera = new OrthographicCamera();
		// NEEDS TO BE CHANGED
		camera.setToOrtho(false, 640, 640);
		
		batch = new SpriteBatch();
		// background = new Texture("");
		
		background = new Texture("gameover.png");
		bgMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/301-Memories.ogg"));
		bgMusic.setLooping(true);	
	}
	
	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// needed?
		camera.update();
		
		batch.begin();
		batch.draw(background, 0, 0);
		batch.end();
		
    	if (Gdx.input.isKeyPressed(Keys.ENTER)) {
	    	game.setScreen(new MainMenuScreen(game));
	    	bgMusic.dispose();
			dispose();
    	}
	}
	
	@Override
	public void resize(int width, int height) {		
	}

	@Override
	public void show() {
		bgMusic.play();
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

	@Override
	public boolean keyDown(int arg0) {
		// TODO Auto-generated method stub
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
}
