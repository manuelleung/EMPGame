package edu.emp.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Controller extends InputAdapter {
	private static final String TAG = Controller.class.getName();
	
	// Control the sprites! that's why the sprites are here but
	// rendering the sprite (drawn by the SpriteBatch) is in Renderer :)
	
	// Movement-Box object
	private Texture movementBoxTexture;
	private Sprite movementBoxSprite;
	private Vector2 movementBoxPosition;
	
	// Hero object
	private Vector2 heroPosition;
	
	// indicate selected sprite?
	public int selectedSprite;
	
	// Animation for the character
	private Animation heroAnimation;
	private TextureRegion [] heroFrames;
	private TextureRegion heroCurrentFrame;
	private Texture heroTexture;
	private float heroStateTime;
	
	// The sprites placed in spriteObjects will be drawn
	// in the Renderer file.
	// UNUSED FOR THE MOMENT
	private Sprite[] spriteObjects; 
	
	public Controller() {
		init();
	}
	
	private void init() {
		// this tells LibGDX to send its received input events here, on this class: Controller
		Gdx.input.setInputProcessor(this);
		initControllableObjects();
	}
	
	// Make all the game objects
	private void initControllableObjects() {
		// details for the Movement Box Sprite
		movementBoxTexture = new Texture(Gdx.files.internal("move-box.png"));
		movementBoxSprite = new Sprite(movementBoxTexture);
		movementBoxPosition = new Vector2(0, 0);
		
		// details for the Hero Object		
		heroTexture = new Texture(Gdx.files.internal("Hero.png"));
		heroPosition = new Vector2(0, 0);
		
		// Initialize the Hero!
		initHero();
	}
	
	// Make the Hero of the game.
	private void initHero() {
		// check 2D animation on resources.txt for reference as well the Renderer file in Lab 5
		// frame_col and frame_row is based on a specific sprite, in this case: Hero.png
		int frame_cols = 8;	
		int frame_rows = 3;
		
		TextureRegion [][] temp = TextureRegion.split(heroTexture, heroTexture.getWidth()/frame_cols, heroTexture.getHeight()/frame_rows);
		heroFrames = new TextureRegion[frame_cols * frame_rows]; //24
		
		int index = 0;
		for (int i = 0; i < frame_rows; i++) {
			for (int j = 0; j < frame_cols; j++) {
				heroFrames[index++] = temp[i][j];
			}
		}
		heroAnimation = new Animation(0.15f, heroFrames);
		heroStateTime = 0f;
		// heroFrames[0 to 3] move up
		// heroFrames[4 to 7] move down
		// heroFrames[8 to 11] move left
		// heroFrames[12 to 15] move right
		// heroFrames[16 to 17] attack left
		// heroFrames[18 to 19] attack right
		// heroFrames[20 to 21] attack up
		// heroFrames[22 to 23] attack down
	}
	
	// update the game objects
	public void update(float deltaTime) {
		updateMovementBox();
		updateHero();
	}
	
	// update the Movement Box
	public void updateMovementBox() {
		movementBoxSprite.setPosition(movementBoxPosition.x, movementBoxPosition.y);
	}
	
	// update Hero animations
	public void updateHero() {
		// heroAnimation is made with the whole array .. so when animation is running, it displays every animation
		// instead of only the ones we want...
		// -----> We might need to separate each animation into separate arrays before making the hero animation
		// like we discussed in the LAB -
		heroStateTime += Gdx.graphics.getDeltaTime();
		heroCurrentFrame = heroAnimation.getKeyFrame(heroStateTime, true);
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		//use keyboard input for now
		//Vector3 clickCoordinates = new Vector3(screenX, screenY, 0);
		//Vector3 position = camera.unproject(clickCoordinates);
		//sprite.setPosition(position.x, position.y);
		return false;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		// movement of box ---- not the hero sprite
		// boundaries to be changed with walls?
		if(keycode == Keys.UP) {
				movementBoxPosition.y += 32;
				if(movementBoxSprite.getY()>=608) 
					movementBoxPosition.y-=32;
		}
		if(keycode == Keys.DOWN) {
				movementBoxPosition.y -= 32;
				if(movementBoxSprite.getY()<=0) 
					movementBoxPosition.y+=32;
		}
		if(keycode == Keys.LEFT) {
				movementBoxPosition.x -= 32;
				if(movementBoxSprite.getX()<=0) 
					movementBoxPosition.x+=32;
		}
		if(keycode == Keys.RIGHT) {
				movementBoxPosition.x += 32;
				if(movementBoxSprite.getX()>=608) 
					movementBoxPosition.x-=32;
		}
		
		// print move-box position for debugging and testing
		Gdx.app.debug(TAG, "box-x: " + movementBoxPosition.x + " box-y: " + movementBoxPosition.y);
		
		return true;
	}
	
	// accessor and getter to render the Movement Box 
	public Sprite getMovementBoxSprite() {
		return movementBoxSprite;
	}
	
	public void setMovementBoxSprite(Sprite movementBoxSprite) {
		this.movementBoxSprite = movementBoxSprite;
	}
	
	// access and getter to render the Hero
	public Vector2 getHeroPosition() {
		return heroPosition;
	}
	
	public TextureRegion getHeroCurrentFrame() {
		return heroCurrentFrame;
	}
	
	@Override
	public boolean keyUp (int keycode) {
		// Reset game world
		if (keycode == Keys.R) {
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		}
		return false;
	}
}
