package edu.emp.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Hero {

	
	// Animation for the character
	private Animation heroAnimation;
	private TextureRegion [] heroFrames;
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
		setHeroCurrentFrame(heroAnimation.getKeyFrame(heroStateTime, true));
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
