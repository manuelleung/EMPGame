/*
 * MainMenuScreen class will render the Main Menu for the game.
 * 
 * NOTE: THIS CLASS GREATLY NEEDS CHANGES
 */

package edu.emp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenuScreen implements Screen, InputProcessor {
	final EMPGame game;
	
	private OrthographicCamera camera;
	
	private SpriteBatch batch;
	private Texture background;
	
	// REMOVE THIS?
	private BitmapFont font;
	
	public MainMenuScreen (final EMPGame game) {
		super();
		Gdx.input.setInputProcessor(this);
		this.game = game;
		
		camera = new OrthographicCamera();
		// NEEDS TO BE CHANGED
		camera.setToOrtho(false, 640, 640);
		
		batch = new SpriteBatch();
		// background = new Texture("");
		
		background = new Texture("mainmenu.jpg");
		
		// REMOVE THIS?
		font = new BitmapFont();
	}
	
	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// needed?
		camera.update();
		
		batch.begin();
		//font.draw(batch, "Welcome to EMPGame", 320, 320);
		//font.draw(batch, "Click anywhere to begin", 150, 150);
		batch.draw(background, 0, 0);
		batch.end();
		
		//if (Gdx.input.isTouched()) {
			// game.setScreen(new MiniMenu(game, game.getScreen()));
			//game.setScreen(new GameScreen(game));
			//dispose();
		//}
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
		// TODO: Magic numbers! :(
		if(x > 76 && x < 271 && y > 303 && y < 348){
			 game.setScreen(new GameScreen(game));
			 dispose();
		}
		//else if(x > 324 && x < 512 && y > 292 && y < 332){
			//game.setScreen(new OptionsScreen(game));
			//dispose();
		//}
		
		//System.out.println("X: "+x+" Y: "+y);
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
