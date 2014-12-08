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
	// for the Death Frames
	private TextureRegion [] deathEnemyFrames;
	
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
	private Texture enemyAttackLeftTexture;
	private Texture enemyAttackRightTexture;
	private Texture enemyAttackUpTexture;
	private Texture enemyAttackDownTexture;
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
		enemyWalkTexture = new Texture(Gdx.files.internal("enemy/goblinsword_03.png"));
		enemyDeathTexture = new Texture(Gdx.files.internal("enemy/goblinsword_05.png"));
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
		
		// for death animation
		int deathFrame_cols = 5;
		int deathFrame_rows = 1;		
		
		// for Walk Animation
		TextureRegion [][] walkTemp = TextureRegion.split(enemyWalkTexture, enemyWalkTexture.getWidth()/walkFrame_cols, enemyWalkTexture.getHeight()/walkFrame_rows);
		walkEnemyFrames = new TextureRegion[walkFrame_cols * walkFrame_rows]; // 32

		// for Death Animation
		TextureRegion [][] deathTemp = TextureRegion.split(enemyDeathTexture, enemyDeathTexture.getWidth()/deathFrame_cols, enemyDeathTexture.getHeight()/deathFrame_rows);
		deathEnemyFrames = new TextureRegion[deathFrame_cols * deathFrame_rows]; // 5
		
		// Store all action of enemy walking frames
		int index = 0;
		for (int i = 0; i < walkFrame_rows; i++) {
			for (int j = 0; j < walkFrame_cols; j++) {
				walkEnemyFrames[index++] = walkTemp[i][j];
			}
		}
		
		// Store all action of enemy death frames
		index = 0;
		for (int i = 0; i < deathFrame_rows; i++) {
			for (int j = 0; j < deathFrame_cols; j++) {
				deathEnemyFrames[index++] = deathTemp[i][j];
			}
		}
		
		// initialize the two-dimensional arrays for Walking
		enemyWalkingFramesSet = new TextureRegion[uniqueActionsWalk][frameCountWalk];
		
		index = 0;
		// for every enemyFrame Walk action
		for (int i = 0; i < uniqueActionsWalk; i++ ) {
			// for each set of sprite movement
			for (int j = 0; j < frameCountWalk; j++) {
				enemyWalkingFramesSet[i][j] = walkEnemyFrames[index++];
			}
		}
		
		// Set the Walking Animations for the selected sprite
		enemyWalkDownAnim = new Animation(0.20f, enemyWalkingFramesSet[0]);
		enemyWalkRightAnim = new Animation(0.20f, enemyWalkingFramesSet[1]);
		enemyWalkUpAnim = new Animation(0.20f, enemyWalkingFramesSet[2]);
		enemyWalkLeftAnim = new Animation(0.20f, enemyWalkingFramesSet[3]);

		// Set the Death Animations for the selected sprite
		enemyDeathAnim = new Animation(0.20f, deathEnemyFrames);
		
		enemyStateTime = 0f;
		
		//STATS
		enemyHealth = 100;
		enemyAttackDamage = 20;
		enemyAccuracy=100;
		enemyEvasion=20;
		enemyDefense=5;
	}
	
	// walk actions for the enemy
	public void setEnemyWalk() {
		// the character is moving up, set its animation moving up
		if (wStyle == WalkStyle.UP)
			enemyCurrentFrame = enemyWalkUpAnim.getKeyFrame(enemyStateTime, true);
		// the character is moving left, set its animation moving left
		if (wStyle == WalkStyle.LEFT)
			enemyCurrentFrame = enemyWalkLeftAnim.getKeyFrame(enemyStateTime, true);
		// the character is moving down, set its animation moving down
		if (wStyle == WalkStyle.DOWN)
			// enemyCurrentFrame = enemyWalkDownAnim.getKeyFrame(enemyStateTime, true);
			// TO TEST THE DEATH ANIMATION, comment the above line and call the below line
			// enemyCurrentFrame = enemyDeathAnim.getKeyFrame(enemyStateTime, true);
			// this setting is also made in the triggerEnemyDeath() function below
			// so just call the method below to trigger the animation	
		// the character is moving right, set its animation moving right
		if (wStyle == WalkStyle.RIGHT)
			enemyCurrentFrame = enemyWalkRightAnim.getKeyFrame(enemyStateTime, true);
	}
	
	// attack actions for the enemy
	public void setEnemyAttack() {
		// the character is moving up, set its animation moving up
		if (aStyle == AttackStyle.UP)
			enemyCurrentFrame = enemyAttackUpAnim.getKeyFrame(enemyStateTime, true);
		// the character is moving left, set its animation moving left
		if (aStyle == AttackStyle.LEFT)
			enemyCurrentFrame = enemyAttackLeftAnim.getKeyFrame(enemyStateTime, true);
		// the character is moving down, set its animation moving down
		if (aStyle == AttackStyle.DOWN)
			enemyCurrentFrame = enemyAttackDownAnim.getKeyFrame(enemyStateTime, true);
		// the character is moving right, set its animation moving right
		if (aStyle == AttackStyle.RIGHT)
			enemyCurrentFrame = enemyAttackRightAnim.getKeyFrame(enemyStateTime, true);
	}
	
	// death action for the enemy
	public void triggerEnemyDeath() {
		enemyCurrentFrame = enemyDeathAnim.getKeyFrame(enemyStateTime, true);
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
