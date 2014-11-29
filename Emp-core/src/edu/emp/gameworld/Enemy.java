package edu.emp.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;



public class Enemy {
	
	//enemy
	private Animation enemyAnimation;
	private TextureRegion[] enemyFrames;
	private TextureRegion enemyCurrentFrame;
	private Texture enemyTexture;
	private float enemyStateTime;
	private Vector2 enemyPosition;

	private int enemyHealth;
	private int enemyAttackDamage;
	private int enemyAccuracy;
	private int enemyEvasion;
	private int enemyDefense;
	
	public Enemy(float x, float y) {
		// details for the Enemy Object
		
		enemyTexture = new Texture(Gdx.files.internal("Hero.png"));
		setEnemyPosition(new Vector2(32, 0));
		// Initialize the enemy
		initEnemy();
	}

	// Make the Hero of the game.
	private void initEnemy() {
		int frame_cols = 8;	
		int frame_rows = 3;
		
		TextureRegion [][] temp = TextureRegion.split(enemyTexture, enemyTexture.getWidth()/frame_cols, enemyTexture.getHeight()/frame_rows);
		enemyFrames = new TextureRegion[frame_cols * frame_rows]; //24
		
		int index = 0;
		for (int i = 0; i < frame_rows; i++) {
			for (int j = 0; j < frame_cols; j++) {
				enemyFrames[index++] = temp[i][j];
			}
		}
		enemyAnimation = new Animation(0.15f, enemyFrames);
		enemyStateTime = 0f;
		
		//STATS
		enemyHealth = 100;
		enemyAttackDamage = 20;
		enemyAccuracy=100;
		enemyEvasion=20;
		enemyDefense=5;
	}
	
	public void updateEnemy() {
		enemyStateTime += Gdx.graphics.getDeltaTime();
		setEnemyCurrentFrame(enemyAnimation.getKeyFrame(enemyStateTime, true));
	}

	public int getEnemyHealth() {
		return enemyHealth;
	}

	public void setEnemyHealth(int enemyHealth) {
		this.enemyHealth = enemyHealth;
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

	public TextureRegion getEnemyCurrentFrame() {
		return enemyCurrentFrame;
	}

	public void setEnemyCurrentFrame(TextureRegion enemyCurrentFrame) {
		this.enemyCurrentFrame = enemyCurrentFrame;
	}
	
	
}
