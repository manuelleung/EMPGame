package edu.emp.gameworld;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class Controller extends InputAdapter {
	private static final String TAG = Controller.class.getName();
	
	// Control the sprites! that's why the sprites are here but
	// rendering the sprite (drawn by the SpriteBatch) is in Renderer :)
	
	// Movement-Box object
	private Texture movementBoxTexture;
	private Sprite movementBoxSprite;
	private Vector2 movementBoxPosition;
	
	// Moving the sprites by this amount of pixel
	// should this datatype be int or float??
	private static final float movePixel = 32;

	// indicate selected sprite?
	public int selectedSprite;
	
	// For multiple enemies or heroes make into an array
	private Enemy enemy;
	
	// Hero object
	private Hero hero;
	
	// TURN states must be switched with "WAIT" option 
	private int turn = 1; //1 = hero, 2 = enemy
	
	// The sprites placed in spriteObjects will be drawn
	// in the Renderer file.
	// UNUSED FOR THE MOMENT
	// private Sprite[] spriteObjects; 
	

	//PATH FINDER
	PathFinder pathFinder;
	boolean pathFound;
	Array<Node> testPath = new Array<Node>();
	
	
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
		//init pathfinder
		pathFinder = new PathFinder(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 32);
		
		// details for the Movement Box Sprite
		movementBoxTexture = new Texture(Gdx.files.internal("move-box.png"));
		movementBoxSprite = new Sprite(movementBoxTexture);
		movementBoxPosition = new Vector2(0, 0);
		
		hero = new Hero(0, 0);
		
		
		enemy = new Enemy(0, 32);
		
		
		
		
		
	}
	

	// update the game objects
	public void update(float deltaTime) {
		updateMovementBox();
		updateHero(deltaTime);
		enemy.updateEnemy();
	}
	
	public void testPathFinder() {
		
		//TEST
		//.................................................................//
		//TESTING WITH VALUES PATHFINDER
						//Pixel location of cell (x, y) + type
		pathFinder.setNode(0, 0, NodeType.START);
		//pathFinder.setNode(32, 0, NodeType.BLOCKED);
		//pathFinder.setNode(0, (32*15), NodeType.BLOCKED);
		//pathFinder.setNode(0, (32*14), NodeType.BLOCKED);
		//pathFinder.setNode((1*32), (32*14), NodeType.BLOCKED);
		//pathFinder.setNode((1*32), (32*15), NodeType.BLOCKED);
		pathFinder.setNode(0, (32*14), NodeType.END);
		pathFound = pathFinder.findPath(); //1 = found --- 2 = no path
		testPath = pathFinder.GetPath();
		if(pathFound) {
			System.out.println("Start cell "+"x: "+testPath.get(0).getX()/32+" y: "+testPath.get(0).getY()/32);
		for(int i=0; i<testPath.size; i++) {
			System.out.println("x: "+testPath.get(i).getX()/32+" y: "+testPath.get(i).getY()/32);
		}
		System.out.print("End cell ");
		System.out.println("x: "+testPath.peek().getX()/32+" y: "+testPath.peek().getY()/32);
		System.out.println("steps: "+(testPath.size-1));
		}
		else { System.out.println("NO PATH"); }
	//.................................................................//
		
	}
	
	// update the Movement Box
	public void updateMovementBox() {
		movementBoxSprite.setPosition(movementBoxPosition.x, movementBoxPosition.y);
	}
	
	// update Hero animations
	public void updateHero(float deltaTime) {
		hero.setHeroStateTime(hero.getHeroStateTime() + deltaTime);
		
		// Standard movement in sync with the Path finding Algorithm
		// NEED TO FIX THE TEST: 
		// - if (hero. ??? > testPath.get(i).getY())
		// should the comparision be made to 
		// to the sprite of the hero or to the vector of the hero such as the hero.getHeroPosition().y
		/*
		if (pathFound) {
			
			for (int i = 0; i < testPath.size; i++) {
				// character in the same location
				if (hero. == testPath.get(i).getX()/movePixel && hero. == testPath.get(i).getY())/movePixel) {
					; // nop
				}
				// character moving up
				if (hero. > testPath.get(i).getY()) {
					// show the corresponding animation
					hero.setHeroWalk(WalkStyle.UP);
					// update the current position
					hero.setHeroPosition(hero.getHeroPosition().y + Gdx.graphics.getDeltaTime());
				}
				// character moving down
				if (hero. > testPath.get(i).getX()/movePixel) {
					// show the corresponding animation
					hero.setHeroWalk(WalkStyle.RIGHT);
					// update the current position
					hero.setHeroPosition(hero.getHeroPosition().x + Gdx.graphics.getDeltaTime());
				}
				// character moving left
				if (hero. > testPath.get(i).getX()/movePixel) {
					// show the corresponding animation
					hero.setHeroWalk(WalkStyle.RIGHT);
					// update the current position
					hero.setHeroPosition(hero.getHeroPosition().x + Gdx.graphics.getDeltaTime());
				}
				// character moving right
				if (hero. > testPath.get(i).getX()/movePixel) {
					// show the corresponding animation
					hero.setHeroWalk(WalkStyle.RIGHT);
					// update the current position
					hero.setHeroPosition(hero.getHeroPosition().x + Gdx.graphics.getDeltaTime());
				}
			}
		}*/
		
		// sample movement; THIS SHOULD BE REMOVED after test fix
		hero.setHeroWalk(WalkStyle.UP);
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
		
		// TESTING//////////////////////////////////////////////////////
		// ALL FORMULAS TO BE CHANGED
		int hitRate = hero.getHeroAccuracy() - enemy.getEnemyEvasion(); // 80%
		int damage = hero.getHeroAttackDamage() - enemy.getEnemyDefense(); // 15
		Random random = new Random();
		if(keycode == Keys.SPACE) {
			if(turn==1) {
				if(enemy.getEnemyHealth()>0) {
					//enemy is alive
					if( movementBoxPosition.x == enemy.getEnemyPosition().x && movementBoxPosition.y == enemy.getEnemyPosition().y){
						//selected enemy to attack
						if( (hero.getHeroPosition().x+32==enemy.getEnemyPosition().x&&hero.getHeroPosition().y==enemy.getEnemyPosition().y) ||
							(hero.getHeroPosition().x-32==enemy.getEnemyPosition().x&&hero.getHeroPosition().y==enemy.getEnemyPosition().y) ||
							(hero.getHeroPosition().y+32==enemy.getEnemyPosition().y&&hero.getHeroPosition().x==enemy.getEnemyPosition().x) ||
							(hero.getHeroPosition().y-32==enemy.getEnemyPosition().y&&hero.getHeroPosition().x==enemy.getEnemyPosition().x) ) {
							//enemy in range
							if( random.nextInt((100-1)+1)+1 < hitRate) {
								//hit
								enemy.setEnemyHealth(damage);
								System.out.println("HIT! Enemy health: " +enemy.getEnemyHealth());
							} else {
								//miss
								System.out.println("MISSED! Enemy health: "+enemy.getEnemyHealth());
							}
						} else {
							//enemy not in range 
							System.out.println("Enemy chosen is not in range");
						}
					} else {
						//enemy not chosen
						System.out.println("Please choose enemy to attack!");
					}
				} else {
					//enemy has been eliminated
					//assuming we delete the enemy we dont need this
				}
			} else {
				// ENEMIES turn
			}
		}
		///////////////////////////////////////////////////////////////
		// TEST KEY
		if(keycode==Keys.T) {
			pathFinder.setNode((int)hero.getHeroPosition().x, (int)hero.getHeroPosition().y, NodeType.START);
			//pathFinder.setNode(32, 0, NodeType.BLOCKED);
			//pathFinder.setNode(0, (32*15), NodeType.BLOCKED);
			//pathFinder.setNode(0, (32*14), NodeType.BLOCKED);
			//pathFinder.setNode((1*32), (32*14), NodeType.BLOCKED);
			//pathFinder.setNode((1*32), (32*15), NodeType.BLOCKED);
			pathFinder.setNode((int)movementBoxPosition.x, (int)movementBoxPosition.y, NodeType.END);
			pathFound = pathFinder.findPath(); //1 = found --- 2 = no path
			testPath = pathFinder.GetPath();
			if(pathFound) {
				System.out.println("Start cell "+"x: "+testPath.get(0).getX()/32+" y: "+testPath.get(0).getY()/32);
			for(int i=0; i<testPath.size; i++) {
				System.out.println("x: "+testPath.get(i).getX()/32+" y: "+testPath.get(i).getY()/32);
			}
			System.out.print("End cell ");
			System.out.println("x: "+testPath.peek().getX()/32+" y: "+testPath.peek().getY()/32);
			System.out.println("steps: "+(testPath.size-1));
			}
			else { System.out.println("NO PATH"); }
		}
		
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
		//Gdx.app.debug(TAG, "box-x: " + movementBoxPosition.x + " box-y: " + movementBoxPosition.y);
		
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
		return hero.getHeroPosition();
	}
	
	public Hero getHero() {
		return hero;
	}
	
	public Enemy getEnemy() {
		return enemy;
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
