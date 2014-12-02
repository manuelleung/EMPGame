package edu.emp.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Hero {

	
	// Animation for the character
	private Animation heroWalkUpAnim;
	private Animation heroWalkRightAnim;
	private Animation heroWalkDownAnim;
	private Animation heroWalkLeftAnim;
	private TextureRegion [] heroFrames;
	
	// contains the specific movement of the characters
	private TextureRegion [][] heroFramesSeparated;
	
	// number of unique character actions
	private int uniqueActions = 4;
	// frame count for before the beginning of a new animation sprite
	// or the frame count of each uniqueAction set
	private int frameCount = 4;
	
	private TextureRegion heroCurrentFrame;
	private Texture heroTexture;
	private float heroStateTime;
	private Vector2 heroPosition;
	
	private int heroHealth;
	private int heroAttackDamage;
	private int heroAccuracy;
	private int heroEvasion;
	private int heroDefense;
	
	public Hero(float x, float y) {
		// details for the Hero Object		
		heroTexture = new Texture(Gdx.files.internal("Hero.png"));
		setHeroPosition(new Vector2(0, 0));
		// Initialize the Hero!
		initHero();
	}
	
	// Make the Hero of the game.
	private void initHero() {
		// frame_col and frame_row is based on a specific sprite, in this case: walkcyclevarious.png
		int frame_cols = 8;	
		int frame_rows = 3;
		
		TextureRegion [][] temp = TextureRegion.split(heroTexture, heroTexture.getWidth()/frame_cols, heroTexture.getHeight()/frame_rows);
		heroFrames = new TextureRegion[frame_cols * frame_rows]; // 96
		
		// Store all action hero frames
		int index = 0;
		for (int i = 0; i < frame_rows; i++) {
			for (int j = 0; j < frame_cols; j++) {
				heroFrames[index++] = temp[i][j];
			}
		}
		
		// initialize the two-dimensional arrays
		heroFramesSeparated = new TextureRegion[uniqueActions][frameCount];
		
		index = 0;
		// for every heroFrame action (there are 12 unique actions based on the sprite sheet)
		for (int i = 0; i < uniqueActions; i++ ) {
			// for each set of sprite movement
			for (int j = 0; j < frameCount; j++) {
				heroFramesSeparated[i][j] = heroFrames[index++];
			}
		}
		
		/*
		 * This heroFramesSeparated double array ends with this:
		 * heroFramesSeparated[0][...] = walk up
		 * heroFramesSeparated[1][...] = walk right
		 * heroFramesSeparated[2][...] = walk down
		 * heroFramesSeparated[3][...] = walk left
		 */
		
		heroWalkUpAnim = new Animation(0.20f, heroFramesSeparated[0]);
		heroWalkRightAnim = new Animation(0.20f, heroFramesSeparated[1]);
		heroWalkDownAnim = new Animation(0.20f, heroFramesSeparated[2]);
		heroWalkLeftAnim = new Animation(0.20f, heroFramesSeparated[3]);
		
		heroStateTime = 0f;
		
		// STATS
		heroHealth=100;
		heroAttackDamage=20;
		heroAccuracy=100;
		heroEvasion=20;
		heroDefense=5;
	}
	
	
	// update Hero animations
	public void updateHero() {
		// heroAnimation is made with the whole array .. so when animation is running, it displays every animation
		// instead of only the ones we want...
		// -----> We might need to separate each animation into separate arrays before making the hero animation
		// like we discussed in the LAB -
		heroStateTime += Gdx.graphics.getDeltaTime();
		
		// Standard movement
		heroWalkUp();
	}
	
	// actions for the hero
	public void heroWalkUp() {
		setHeroCurrentFrame(heroWalkUpAnim.getKeyFrame(heroStateTime, true));
	}
	
	public void heroWalkRight() {
		setHeroCurrentFrame(heroWalkRightAnim.getKeyFrame(heroStateTime, true));
	}
	
	public void heroWalkDown() {
		setHeroCurrentFrame(heroWalkDownAnim.getKeyFrame(heroStateTime, true));
	}
	
	public void heroWalkLeft() {
		setHeroCurrentFrame(heroWalkLeftAnim.getKeyFrame(heroStateTime, true));
	}

	public TextureRegion getHeroCurrentFrame() {
		return heroCurrentFrame;
	}

	public void setHeroCurrentFrame(TextureRegion heroCurrentFrame) {
		this.heroCurrentFrame = heroCurrentFrame;
	}

	public Vector2 getHeroPosition() {
		return heroPosition;
	}

	public void setHeroPosition(Vector2 heroPosition) {
		this.heroPosition = heroPosition;
	}

	public int getHeroHealth() {
		return heroHealth;
	}

	public void setHeroHealth(int heroHealth) {
		this.heroHealth = heroHealth;
	}

	public int getHeroAttackDamage() {
		return heroAttackDamage;
	}

	public void setHeroAttackDamage(int heroAttackDamage) {
		this.heroAttackDamage = heroAttackDamage;
	}

	public int getHeroAccuracy() {
		return heroAccuracy;
	}

	public void setHeroAccuracy(int heroAccuracy) {
		this.heroAccuracy = heroAccuracy;
	}

	public int getHeroEvasion() {
		return heroEvasion;
	}

	public void setHeroEvasion(int heroEvasion) {
		this.heroEvasion = heroEvasion;
	}

	public int getHeroDefense() {
		return heroDefense;
	}

	public void setHeroDefense(int heroDefense) {
		this.heroDefense = heroDefense;
	}
}
