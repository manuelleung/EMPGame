/*
 * MainMenuScreen class will render the Main Menu for the game.
 * 
 * NOTE: THIS CLASS GREATLY NEEDS CHANGES
 */

package edu.emp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenuScreen implements Screen, InputProcessor {
	final EMPGame game;
	
	private OrthographicCamera camera;
	
	private SpriteBatch batch;
	private Texture background;
	
	private Music bgMusic;
	
	public MainMenuScreen (final EMPGame game) {
		super();
		Gdx.input.setInputProcessor(this);
		this.game = game;
		
		camera = new OrthographicCamera();
		// NEEDS TO BE CHANGED
		camera.setToOrtho(false, 640, 640);
		
		batch = new SpriteBatch();
		// background = new Texture("");
		
		background = new Texture("mainmenu.png");
		bgMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/296-TheTeaGarden.ogg"));
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
		if (x > 103 && x < 233 && y > 245 && y < 293) {
			game.setScreen(new GameScreen(game));
			bgMusic.dispose();
			dispose();
		}
		
		if (x > 101 && x < 285 && y > 344 && y < 388) {
			// set game screen to tutorial screen
			background = new Texture("tutorial.png");
		}
		
		if (x > 304 && x < 499 && y > 264 && y < 317) {
			background = new Texture("mainmenu.png");
		}
		
		// System.out.println("X: "+x+" Y: "+y);
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
