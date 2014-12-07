package edu.emp.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import edu.emp.game.EMPGame;

public class CharacterOptionsMenu implements Screen, InputProcessor {
	final EMPGame game;
	
	private SpriteBatch batch;
	private Texture background;
	
	// pixel locations for the mini menu
	private final int MINIMENU_X = 375;
	private final int MINIMENU_Y = 375;
	
	// Using the mouse listener requires the pixel locations listed below
	// not needed when using the keyboard input
	// pixel location for move submenu
	
	/*
	private final int MOVE_LEFTBORDER_X = 415;
	private final int MOVE_RIGHTBORDER_X = 470;
	private final int MOVE_LEFTBORDER_Y = 195;
	private final int MOVE_RIGHTBORDER_Y = 205;
	
	// pixel location for attack submenu
	private final int ATTACK_LEFTBORDER_X = 415;
	private final int ATTACK_RIGHTBORDER_Y = 470;
	...
	*/
	
	// will transition to this Screen and change InputProcessor to this Controller
	// after MiniMenu is disposed
	private Screen bgGameScreen;
	private Controller gameController;
	
	// holds the currently selected option
	private CharacterOptions selected = CharacterOptions.MOVE;
	
	public CharacterOptionsMenu(final EMPGame game, Screen bgGameScreen, Controller gameController) {
		super();
		this.game = game;
		Gdx.input.setInputProcessor(this);
		batch = new SpriteBatch();
		
		this.bgGameScreen = bgGameScreen;
		this.gameController = gameController;
		
		// Default selected item for the mini menu is the MOVE
		background = new Texture("gameplay_options/move-selected.png");
		// use this to use a none selected menu
		// background = new Texture("gameplay_options/minimenu.png");
	}

	@Override
	public boolean keyDown(int keycode) {		
		// Keyboard Events
		if (keycode == Keys.DOWN) {
			// currently on MOVE
			if (selected == CharacterOptions.MOVE) {
				selected = CharacterOptions.ATTACK;
				background = new Texture("gameplay_options/attack-selected.png");
				System.out.println("ATTACK command highlighted");
			}
			// currently on ATTACK
			else if (selected == CharacterOptions.ATTACK) {
				selected = CharacterOptions.WAIT;
				background = new Texture("gameplay_options/wait-selected.png");				
				System.out.println("WAIT command highlighted");
			}
			// currently on WAIT
			else if (selected == CharacterOptions.WAIT) {
				selected = CharacterOptions.MOVE;
				background = new Texture("gameplay_options/move-selected.png");
				System.out.println("MOVE command is highlighted");
			}
		}
		
		if (keycode == Keys.UP) {
			// currently on MOVE
			if (selected == CharacterOptions.MOVE) {
				selected = CharacterOptions.WAIT;
				background = new Texture("gameplay_options/wait-selected.png");
				System.out.println("WAIT command highlighted");
			}
			// currently on ATTACK
			else if (selected == CharacterOptions.ATTACK) {
				selected = CharacterOptions.MOVE;
				background = new Texture("gameplay_options/move-selected.png");				
				System.out.println("MOVE command highlighted");
			}
			// currently on WAIT
			else if (selected == CharacterOptions.WAIT) {
				selected = CharacterOptions.ATTACK;
				background = new Texture("gameplay_options/attack-selected.png");				
				System.out.println("ATTACK command is highlighted");
			}
		}

		System.out.println("current option: " + selected);
		System.out.println("Press Enter to Select");
		
		// confirm action
		if (keycode == Keys.ENTER) {
			// MOVE command is the confirmed choice
			if (selected == CharacterOptions.MOVE && gameController.moved==false) {
				
				// remove MiniMenu Screen
				game.getScreen().dispose();
				game.setScreen(bgGameScreen);
				gameController.TestConfirmAction(true, selected);
				Gdx.input.setInputProcessor(gameController);				
				dispose();
			}
			
			// ATTACK command is the confirmed choice
			if (selected == CharacterOptions.ATTACK) {
				System.out.println("Commencing Attack Mode!");

				// remove MiniMenu Screen
				game.getScreen().dispose();
				game.setScreen(bgGameScreen);
				gameController.TestConfirmAction(true, selected);
				Gdx.input.setInputProcessor(gameController);				
				dispose();
			}	
			
			// WAIT command is the confirmed choice
			if (selected == CharacterOptions.WAIT) {
				
				// remove MiniMenu Screen
				game.getScreen().dispose();
				game.setScreen(bgGameScreen);
				gameController.TestConfirmAction(true, selected);
				Gdx.input.setInputProcessor(gameController);				
				dispose();
			}			
		}
		
		if(keycode == Keys.ESCAPE) {
			game.getScreen().dispose();
			game.setScreen(bgGameScreen);
			gameController.TestConfirmAction(true, CharacterOptions.NONE);
			Gdx.input.setInputProcessor(gameController);
			dispose();
		}
		
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
		// System.out.println("x: " + x + " y: " + y);
		
		/*
		// Mouse Events
		if (x > MOVE_LEFTBORDER_X && x < MOVE_RIGHTBORDER_X
			&& y > MOVE_LEFTBORDER_Y && y < MOVE_RIGHTBORDER_Y) {
				System.out.println("Moving The Character");
				// CALL ATTACK FUNCTIONS
				game.setScreen(screenbg);
				dispose();
		}
		*/
		
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
		batch.begin();
		batch.draw(background, MINIMENU_X, MINIMENU_Y);
		batch.end();
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
