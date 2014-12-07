package edu.emp.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;



public class Enemy {
	
	// Animation for the enemy
	private Animation enemyWalkDownAnim;
	private Animation enemyAttackDownAnim;
	private Animation enemyWalkRightAnim;
	private Animation enemyAttackRightAnim;
	private Animation enemyWalkUpAnim;
	private Animation enemyAttackUpAnim;
	private Animation enemyWalkLeftAnim;
	private Animation enemyAttackLeftAnim;
	private Animation enemyDeathAnim;
	private TextureRegion [] enemyFrames;
	
	// contains the specific movement of the characters
	private TextureRegion [][] enemyFramesSeparated_LeftSet;
	private TextureRegion [][] enemyFramesSeparated_RightSet;
	
	// number of unique character actions
	private int uniqueActions = 9;
	
	/*
	 * These are:
	 * 
	 * walk down
	 * attack down
	 * walk right
	 * attack right
	 * walk up
	 * attack up
	 * walk left
	 * attack left
	 * death 
	 */
	
	// frame count for before the beginning of a new animation sprite
	// or the frame count of each uniqueAction set
	private int frameCount_LeftSet = 5;
	private int frameCount_RightSet = 6;
	// control which set to use:
	private boolean setSwitch;
	
	private TextureRegion enemyCurrentFrame;
	private Texture enemyTexture;
	private float enemyStateTime;
	private Vector2 enemyPosition;

	private int enemyHealth;
	private int enemyAttackDamage;
	private int enemyAccuracy;
	private int enemyEvasion;
	private int enemyDefense;
	
	// Walking style for the enemy
	private WalkStyle wStyle = WalkStyle.DOWN; 
	
	public Enemy(float x, float y) {
		// details for the Enemy Object
		// Using a Goblin an enemy
		// enemyTexture = new Texture(Gdx.files.internal("Hero.png"));
		enemyTexture = new Texture(Gdx.files.internal("goblinsword_01.png"));		
		setEnemyPosition(new Vector2(x, y));
		// Initialize the enemy
		initEnemy();
	}

	// Make the Enemy of the game.
	private void initEnemy() {
		
		// frame_col and frame_row is based on a specific sprite
		// in this case: goblinsword_01.png
		int frame_cols = 11;	
		int frame_rows = 5;
		
		TextureRegion [][] temp = TextureRegion.split(enemyTexture, enemyTexture.getWidth()/frame_cols, enemyTexture.getHeight()/frame_rows);
		enemyFrames = new TextureRegion[frame_cols * frame_rows]; // 55
		
		// Store all action enemy frames
		int index = 0;
		for (int i = 0; i < frame_rows; i++) {
			for (int j = 0; j < frame_cols; j++) {
				enemyFrames[index++] = temp[i][j];
			}
		}
		
		// initialize the two-dimensional arrays
		enemyFramesSeparated_LeftSet = new TextureRegion[uniqueActions][frameCount_LeftSet];
		enemyFramesSeparated_RightSet = new TextureRegion[uniqueActions][frameCount_RightSet];
		
		// we treat the value of true to refer to the "left set" of the spritesheet
		// before a uniqueAction occurs
		setSwitch = true;
		
		index = 0;
		// for every heroFrame action (there are 4 unique actions based on the sprite sheet)
		for (int i = 0; i < uniqueActions; i++ ) {
			// for each set of sprite movement
			if (setSwitch) {
				for (int j = 0; j < frameCount_LeftSet; j++) {
					enemyFramesSeparated_LeftSet[i][j] = enemyFrames[index++];
				}
			}
			else {
				for (int j = 0; j < frameCount_RightSet; j++) {
					enemyFramesSeparated_RightSet[i][j] = enemyFrames[index++];
				}				
			}
			
			// flip the switch!
			setSwitch = !setSwitch;
		}
		
		// Set the Animations for the selected sprite: goblinsword_01.png
		
		// NEEDS SERIOUS CHANGES
		enemyWalkDownAnim = new Animation(0.20f, enemyFramesSeparated_RightSet[3]);
		enemyWalkLeftAnim = new Animation(0.20f, enemyFramesSeparated_LeftSet[2]);
		enemyWalkUpAnim = new Animation(0.20f, enemyFramesSeparated_LeftSet[4]);
		
		enemyAttackDownAnim = new Animation(0.20f, enemyFramesSeparated_RightSet[1]);
		
		/*
		enemyWalkDownAnim = new Animation(0.20f, enemyFramesSeparated_LeftSet[0]);
		enemyWalkRightAnim = new Animation(0.20f, enemyFramesSeparated_LeftSet[1]);
		enemyWalkUpAnim = new Animation(0.20f, enemyFramesSeparated_LeftSet[2]);
		enemyWalkLeftAnim = new Animation(0.20f, enemyFramesSeparated_LeftSet[3]);
		enemyDeathAnim = new Animation(0.20f, enemyFramesSeparated_LeftSet[4]);
		
		enemyAttackDownAnim = new Animation(0.20f, enemyFramesSeparated_RightSet[0]);
		enemyAttackRightAnim = new Animation(0.20f, enemyFramesSeparated_RightSet[1]);
		enemyAttackUpAnim = new Animation(0.20f, enemyFramesSeparated_RightSet[2]);
		enemyAttackLeftAnim = new Animation(0.20f, enemyFramesSeparated_RightSet[3]);
		*/
		enemyStateTime = 0f;
		
		//STATS
		enemyHealth = 100;
		enemyAttackDamage = 20;
		enemyAccuracy=100;
		enemyEvasion=20;
		enemyDefense=5;
	}
	
	// actions for the hero
	public void setEnemyWalk() {
		// the character is moving up, set its animation moving up
		if (wStyle == WalkStyle.UP)
			enemyCurrentFrame = enemyWalkUpAnim.getKeyFrame(enemyStateTime, true);
		// the character is moving left, set its animation moving left
		if (wStyle == WalkStyle.LEFT)
			enemyCurrentFrame = enemyWalkLeftAnim.getKeyFrame(enemyStateTime, true);
		// the character is moving down, set its animation moving down
		if (wStyle == WalkStyle.DOWN)
			enemyCurrentFrame = enemyWalkDownAnim.getKeyFrame(enemyStateTime, true);
		// the character is moving right, set its animation moving right
		if (wStyle == WalkStyle.RIGHT)
			enemyCurrentFrame = enemyWalkRightAnim.getKeyFrame(enemyStateTime, true);
	}

	// access and getter for the Walking Style for the Hero
	public WalkStyle getWalkingStyle() {
		return this.wStyle;		
	}
	
	public void setWalkingStyle(WalkStyle wStyle) {
		this.wStyle = wStyle;
	}
	
	public void setEnemyStateTime(float enemyStateTime) {
		this.enemyStateTime = enemyStateTime;
	}
	
	public float getEnemyStateTime() {
		return enemyStateTime;
	}

	public int getEnemyHealth() {
		return enemyHealth;
	}

	public void setEnemyHealth(int enemyHealth) {
		this.enemyHealth = enemyHealth;
	}
	
	public void takeDamage(int damage) {
		enemyHealth -= damage;
	}

	public int getEnemyAttackDamage() {
		return enemyAttackDamage;
	}

	public void setEnemyAttackDamage(int enemyAttackDamage) {
		this.enemyAttackDamage = enemyAttackDamage;
	}

	public int getEnemyAccuracy() {
		return enemyAccuracy;
	}

	public void setEnemyAccuracy(int enemyAccuracy) {
		this.enemyAccuracy = enemyAccuracy;
	}

	public int getEnemyEvasion() {
		return enemyEvasion;
	}

	public void setEnemyEvasion(int enemyEvasion) {
		this.enemyEvasion = enemyEvasion;
	}

	public int getEnemyDefense() {
		return enemyDefense;
	}

	public void setEnemyDefense(int enemyDefense) {
		this.enemyDefense = enemyDefense;
	}

	public Vector2 getEnemyPosition() {
		return enemyPosition;
	}

	public void setEnemyPosition(Vector2 enemyPosition) {
		this.enemyPosition = enemyPosition;
	}
	
	public void setEnemyPositionX(float xValue) {
		this.enemyPosition.x = xValue;
	}
	
	public void setEnemyPositionY(float yValue) {
		this.enemyPosition.y = yValue;
	}

	public TextureRegion getEnemyCurrentFrame() {
		return enemyCurrentFrame;
	}

	public void setEnemyCurrentFrame(TextureRegion enemyCurrentFrame) {
		this.enemyCurrentFrame = enemyCurrentFrame;
	}

	public void incrementX(float f) {
		enemyPosition.x += f;
	}
	public void incrementY(float f) {
		enemyPosition.y += f;
	}
	public void setX(float f) {
		enemyPosition.x = f;
	}
	public void setY(float f) {
		enemyPosition.y = f;
	}
	public float getX() {
		return enemyPosition.x;
	}
	public float getY() {
		return enemyPosition.y;
	}
	
	
}
