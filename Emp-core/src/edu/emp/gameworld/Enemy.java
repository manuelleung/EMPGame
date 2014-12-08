package edu.emp.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;



public class Enemy {
	
	// Walk Animation
	private Animation enemyWalkDownAnim;
	private Animation enemyWalkRightAnim;
	private Animation enemyWalkUpAnim;
	private Animation enemyWalkLeftAnim;
	
	// Attack Animation
	private Animation enemyAttackDownAnim;
	private Animation enemyAttackRightAnim;
	private Animation enemyAttackUpAnim;
	private Animation enemyAttackLeftAnim;
	
	// Death Animation
	private Animation enemyDeathAnim;
	
	// for the Walking Frames
	private TextureRegion [] walkEnemyFrames;
	// for the Attacking Frames
	private TextureRegion [] attackEnemyFrames;
	
	// contains the specific movement of the characters
	private TextureRegion [][] enemyWalkingFramesSet;
	// contains the specific movement of the characters
	private TextureRegion [][] enemyAttackingFramesSet;
	
	// number of unique character actions
	private int uniqueActionsWalk = 4;
	private int uniqueActionsAttack = 4;
	
	// frame count for before the beginning of a new animation sprite
	// or the frame count of each uniqueAction set
	private int frameCountWalk = 8;
	private int frameCountAttack = 3;
	
	private TextureRegion enemyCurrentFrame;
	
	private Texture enemyWalkTexture;
	private Texture enemyAttackTexture;
	private Texture enemyDeathTexture;
	
	private float enemyStateTime;
	private Vector2 enemyPosition;

	private int enemyHealth;
	private int enemyAttackDamage;
	private int enemyAccuracy;
	private int enemyEvasion;
	private int enemyDefense;
	
	// Walking style for the enemy
	private WalkStyle wStyle = WalkStyle.DOWN;
	// Attacking style for the enemy
	private AttackStyle aStyle = AttackStyle.NONE; 

	
	public Enemy(float x, float y) {
		// details for the Enemy Object
		// Using a Goblin an enemy
		// enemyTexture = new Texture(Gdx.files.internal("Hero.png"));
		enemyWalkTexture = new Texture(Gdx.files.internal("enemy/goblinsword_03.png"));
		enemyAttackTexture = new Texture(Gdx.files.internal("enemy/goblinsword_03.png"));
		setEnemyPosition(new Vector2(x, y));
		// Initialize the enemy
		initEnemy();
	}

	// Make the Enemy of the game.
	private void initEnemy() {
		
		// frame_col and frame_row is based on a specific sprite
		
		// for Walk Animation
		int walkFrame_cols = 8;	
		int walkFrame_rows = 4;
		
		// for Attack Animation
		int attackFrame_cols = 3;	
		int attackFrame_rows = 4;		
		
		// for Walk Animation
		TextureRegion [][] walkTemp = TextureRegion.split(enemyWalkTexture, enemyWalkTexture.getWidth()/walkFrame_cols, enemyWalkTexture.getHeight()/walkFrame_rows);
		walkEnemyFrames = new TextureRegion[walkFrame_cols * walkFrame_rows]; // 32
		
		// for Attack Animation
		TextureRegion [][] attackTemp = TextureRegion.split(enemyWalkTexture, enemyWalkTexture.getWidth()/walkFrame_cols, enemyWalkTexture.getHeight()/walkFrame_rows);
		attackEnemyFrames = new TextureRegion[attackFrame_cols * attackFrame_rows]; // 12
		
		// Store all action of enemy walking frames
		int index = 0;
		for (int i = 0; i < walkFrame_rows; i++) {
			for (int j = 0; j < walkFrame_cols; j++) {
				walkEnemyFrames[index++] = walkTemp[i][j];
			}
		}
		
		// Store all action of enemy attacking frames
		index = 0;
		for (int i = 0; i < attackFrame_cols; i++) {
			for (int j = 0; j < attackFrame_rows; j++) {
				attackEnemyFrames[index++] = attackTemp[i][j];
			}
		}
		
		// initialize the two-dimensional arrays for Walking
		enemyWalkingFramesSet = new TextureRegion[uniqueActionsWalk][frameCountWalk];
		// initialize the two-dimensional arrays for Attacking
		enemyAttackingFramesSet = new TextureRegion[uniqueActionsAttack][frameCountAttack];
		
		index = 0;
		// for every enemyFrame Walk action
		for (int i = 0; i < uniqueActionsWalk; i++ ) {
			// for each set of sprite movement
			for (int j = 0; j < frameCountWalk; j++) {
				enemyWalkingFramesSet[i][j] = walkEnemyFrames[index++];
			}
		}
		
		index = 0;
		// for every enemyFrame Attack action
		for (int i = 0; i < uniqueActionsAttack; i++ ) {
			// for each set of sprite movement
			for (int j = 0; j < frameCountAttack; j++) {
				enemyAttackingFramesSet[i][j] = attackEnemyFrames[index++];
			}
		}
		
		// Set the Walking Animations for the selected sprite
		enemyWalkDownAnim = new Animation(0.20f, enemyWalkingFramesSet[0]);
		enemyWalkRightAnim = new Animation(0.20f, enemyWalkingFramesSet[1]);
		enemyWalkUpAnim = new Animation(0.20f, enemyWalkingFramesSet[2]);
		enemyWalkLeftAnim = new Animation(0.20f, enemyWalkingFramesSet[3]);
		
		// Set the Attacking Animations for the selected sprite
		enemyAttackDownAnim = new Animation(0.20f, enemyAttackingFramesSet[0]);
		enemyAttackLeftAnim = new Animation(0.20f, enemyAttackingFramesSet[1]);
		enemyAttackUpAnim = new Animation(0.20f, enemyAttackingFramesSet[2]);
		enemyAttackRightAnim = new Animation(0.20f, enemyAttackingFramesSet[3]);
		
		// Test:
		enemyWalkDownAnim = new Animation(0.20f, enemyWalkingFramesSet[1]);
		
		enemyStateTime = 0f;
		
		//STATS
		enemyHealth = 100;
		enemyAttackDamage = 20;
		enemyAccuracy=100;
		enemyEvasion=20;
		enemyDefense=5;
	}
	
	// walk actions for the hero
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
	
	// attack actions for the hero
	public void setEnemyAttack() {
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
	
	// access and getter for the Attacking Style for the Hero
	public AttackStyle getAttackingStyle() {
		return this.aStyle;		
	}
	
	public void setAttackingStyle(AttackStyle aStyle) {
		this.aStyle = aStyle;
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
