<<<<<<< HEAD
package edu.emp.gameworld;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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

import edu.emp.game.EMPGame;

public class Controller implements InputProcessor {
	final EMPGame game;
	
	private static final String TAG = Controller.class.getName();
	
	// Control the sprites! that's why the sprites are here but
	// rendering the sprite (drawn by the SpriteBatch) is in Renderer :)
	
	// Movement-Box object
	private Texture movementBoxTexture;
	private Sprite movementBoxSprite;
	private Vector2 movementBoxPosition;
	
	// Moving the sprites by this amount of pixel
	// should this datatype be int or float??
	private static final float MOVE_PIXEL_BY_32 = 32;

	// indicate selected sprite?
	public int selectedSprite;
	
	// For multiple enemies or heroes make into an array
	private Enemy enemy;
	
	// Hero object
	private Hero hero;
	

	private int enemyMaxMove=0;
	// The sprites placed in spriteObjects will be drawn
	// in the Renderer file.
	// UNUSED FOR THE MOMENT
	// private Sprite[] spriteObjects; 
	
	boolean cameraUp;
	boolean cameraDown;
	boolean cameraLeft;
	boolean cameraRight;

	//PATH FINDER
	PathFinder pathFinder;
	boolean pathFound;
	Array<Node> heroPath;
	Array<Node> enemyPath;
	//for moving...
	int movementIndex;
	
	//game states when action is chosen
	private boolean attackState;
	private boolean moveState;
	
	//states performed
	boolean moved;
	boolean attacked;
	
	// TURN states must be switched with "WAIT" option 
	boolean playerTurn;
	boolean enemyTurn;
	
	// confirm action from Character Options Menu
	private boolean confirmAction;
	private CharacterOptions action;
	
	
	ShapeRenderer shape = new ShapeRenderer();
	
	public Controller(final EMPGame game) {
		this.game = game;
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
		pathFinder = new PathFinder((32*38), (32*40), 32);//Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 32);
		
		//testPathFinder();
		
		hero = new Hero((32*16), (32*33));
		heroPath = new Array<Node>();
		
		enemy = new Enemy((32*13), (32*31));
		enemyPath = new Array<Node>();
		
		// details for the Movement Box Sprite
		movementBoxTexture = new Texture(Gdx.files.internal("selectBox.png"));
		movementBoxSprite = new Sprite(movementBoxTexture);
		movementBoxPosition = new Vector2(hero.getHeroPosition().x, hero.getHeroPosition().y);
		

		
		cameraUp=false;
		cameraDown=false;
		cameraLeft=false;
		cameraRight=false;
		
		movementIndex = 0;
		
		attackState=false;
		moveState=false;
		moved = false;
		attacked =false;
		
		playerTurn = true;
		enemyTurn = false;
		
		confirmAction = false;
		action=CharacterOptions.NONE;
	}
	

	// update the game objects
	public void update(float deltaTime) {
		updateMovementBox();
		updateHero(deltaTime);
		updateEnemy(deltaTime);
	}
	
	// update the Movement Box
	public void updateMovementBox() {
		movementBoxSprite.setPosition(movementBoxPosition.x, movementBoxPosition.y);
	}
	
	// update Hero animations
	public void updateHero(float deltaTime) {
		hero.setHeroStateTime(hero.getHeroStateTime() + deltaTime);
		
		//System.out.println(hero.getHeroPosition().x + " " + hero.getHeroPosition().y);
		if(isMoveState() && playerTurn)
			this.moveHero(Gdx.graphics.getDeltaTime());
		// standard movement while standing
		hero.setHeroWalk();
	}
	
	public void updateEnemy(float deltaTime) {
		enemy.setEnemyStateTime(enemy.getEnemyStateTime() + deltaTime);

		if(enemyTurn == true) {
			if(attacked == false) {
				attackHero();
			}
			if(moved==false) {
					movementIndex=0; // reset index
					//action being performed 	(should be when "move" is selected)
					//moved=true;
					pathFinder.setNode((int)enemy.getEnemyPosition().x, (int)enemy.getEnemyPosition().y, NodeType.START);
					//pathFinder.setNode(32, 0, NodeType.BLOCKED);
					pathFinder.setNode((int)hero.getHeroPosition().x, (int)hero.getHeroPosition().y, NodeType.END);
					pathFound = pathFinder.findPath();
					enemyPath = pathFinder.GetPath();
					
					moveState=true;
					
				if(pathFound && enemyMaxMove<=3) {	
					enemyMaxMove++;
					if(enemy.getEnemyPosition().x + 32 < hero.getHeroPosition().x ||
							enemy.getEnemyPosition().x - 32 > hero.getHeroPosition().x ||
							enemy.getEnemyPosition().y + 32 < hero.getHeroPosition().y ||
							enemy.getEnemyPosition().y - 32 > hero.getHeroPosition().y ) 
					{
						this.moveEnemy(Gdx.graphics.getDeltaTime());
					}
					else if (enemy.getEnemyPosition().x+32 == hero.getHeroPosition().x && enemy.getEnemyPosition().y != hero.getHeroPosition().y) {
						this.moveEnemy(Gdx.graphics.getDeltaTime());
					}
					else if(enemy.getEnemyPosition().x-32 == hero.getHeroPosition().x && enemy.getEnemyPosition().y != hero.getHeroPosition().y) {
						this.moveEnemy(Gdx.graphics.getDeltaTime());
					}
					/// STOP MOVEMENT WHEN REACHED MAX STEPS
					else {
						moved = true;

					}
				}
				else 
					moved=true;
					
				//end of if
			}
			//if(attacked == false) {
				//attackHero();
			//}
			if(moved==true) {
				switchTurn();
			}
		}
		
		
		
		// standard movement while standing
		enemy.setEnemyWalk();
	}
	
	public Node getCurrentNode(int index, Array<Node> path) {
		Node n = null;
		if(index==0) {
			//n = getFirstNode();
			n = path.get(0);
		}
		if(index>0 && index<path.size) {
			n = path.get(index);
		}
		else {
			System.out.println("1-we are hitting end");
		}
		return n;
	}
	
	public Node getFirstNode(Array<Node> path) {
		pathFound = pathFinder.findPath();
		path = pathFinder.GetPath();
		return path.get(0);
	}
	public Node getNextNode(int index, Array<Node> path) {
		Node n = null;
		if(index < path.size) {
			//System.out.println("index " +index+ " size "+testPath.size);
				n = path.get(index);//+1);

		}
		else {
			moveState=false;
			System.out.println("2-we are at the end");
		}
		return n;
	}
	public Node getPreviousNode(int index, Array<Node> path) {
		Node n=null;
		if(index > 0) {
			n = path.get(index-1);
		}
		else {
			moveState=false;
			System.out.println("3-we are at the end");
		}
		return n;
	}
	public void incrementCurrentNode(Array<Node> path) {
		if(!(movementIndex >= path.size)) {
			movementIndex++;
		}
		else {
			moveState=false;
			System.out.println("Cant do that");
			movementIndex=-1;
		}
	}
	
	public void moveHero(float deltaTime) {
		if(isMoveState()) {
			Node currentNode = getCurrentNode(movementIndex, heroPath);
			Node nextNode = getNextNode(movementIndex+1, heroPath);
			
			float speed = 32.0f;
			//MOVE RIGHT
			if(currentNode.getX() < nextNode.getX()) {
				hero.setWalkingStyle(WalkStyle.RIGHT);
				hero.incrementX(speed);
				if(hero.getX() > (hero.getX()+speed)) {
					 hero.incrementX(speed);
				}
				if(enemy.getX() >= (32*37)) {
				//if(hero.getX() >= 608.0f) {
					hero.incrementX(0f);
				}
			}
			//MOVE LEFT
			else if(currentNode.getX() > nextNode.getX()) {
				hero.setWalkingStyle(WalkStyle.LEFT);
				hero.incrementX(-speed);
				if(hero.getX() < (hero.getX()-speed)) {
					hero.incrementX(-speed);
				}
				if(hero.getX() <= 0f) {
					hero.incrementX(0f);
				}
			}
			// MOVE UP
			else if(currentNode.getY() > nextNode.getY()) {
				hero.setWalkingStyle(WalkStyle.DOWN);
				hero.incrementY(-speed);
				if(hero.getY() < (hero.getY()-speed)) {
					hero.incrementY(-speed);
				}
				if(hero.getY() <= 0f) {
					hero.incrementY(0f);
				}
			}
			// MOVE DOWN
			else if(currentNode.getY() < nextNode.getY()) {
				hero.setWalkingStyle(WalkStyle.UP);
				hero.incrementY(speed);
				if(hero.getY() > (hero.getY()+speed)) {
					hero.incrementY(speed);
				}
				if(hero.getY() >= (32*39)) {
				//if(hero.getY() >= 608.0f) {
					hero.incrementY(0f);
				}
			}
			incrementCurrentNode(heroPath);
			if(movementIndex == heroPath.size-1) {
				if(getCurrentNode(movementIndex, heroPath).getX() == 15 &&
						getCurrentNode(movementIndex, heroPath).getY() == 15) {
					System.out.println("reached destination");
					moveState=false;
				}
			}
			if(movementIndex == -1) {
				System.out.println("no good");
			}
			
			if(getNextNode(movementIndex+1, heroPath)==null) {
				moveState=false;
			}
		}
	}
	
	public void moveEnemy(float deltaTime) {
		if(isMoveState() && enemyTurn) {
			Node currentNode = getCurrentNode(movementIndex, enemyPath);
			Node nextNode = getNextNode(movementIndex+1, enemyPath);
			
			float speed = 32.0f;
			//MOVE RIGHT
			if(currentNode.getX() < nextNode.getX()) {
				enemy.setWalkingStyle(WalkStyle.RIGHT);
				enemy.incrementX(speed);
				if(enemy.getX() > (enemy.getX()+speed)) {
					 hero.incrementX(speed);
				}
				if(enemy.getX() >= (32*37)) {
				//if(enemy.getX() >= 608.0f) {
					enemy.incrementX(0f);
				}
			}
			//MOVE LEFT
			else if(currentNode.getX() > nextNode.getX()) {
				enemy.setWalkingStyle(WalkStyle.LEFT);
				enemy.incrementX(-speed);
				if(enemy.getX() < (enemy.getX()-speed)) {
					enemy.incrementX(-speed);
				}
				if(hero.getX() <= 0f) {
					hero.incrementX(0f);
				}
			}
			// MOVE UP
			else if(currentNode.getY() > nextNode.getY()) {
				enemy.setWalkingStyle(WalkStyle.DOWN);
				enemy.incrementY(-speed);
				if(enemy.getY() < (enemy.getY()-speed)) {
					enemy.incrementY(-speed);
				}
				if(enemy.getY() <= 0f) {
					enemy.incrementY(0f);
				}
			}
			// MOVE DOWN
			else if(currentNode.getY() < nextNode.getY()) {
				enemy.setWalkingStyle(WalkStyle.UP);
				enemy.incrementY(speed);
				if(enemy.getY() > (enemy.getY()+speed)) {
					enemy.incrementY(speed);
				}
				if(enemy.getY() >= (32*39)) {
				//if(enemy.getY() >= 608.0f) {
					enemy.incrementY(0f);
				}
			}
			incrementCurrentNode(enemyPath);
			if(movementIndex == enemyPath.size-1) {
				if(getCurrentNode(movementIndex, enemyPath).getX() == 15 &&
						getCurrentNode(movementIndex, enemyPath).getY() == 15) {
					System.out.println("reached destination");
					moveState=false;
				}
			}
			if(movementIndex == -1) {
				System.out.println("no good");
			}
			
			if(getNextNode(movementIndex+1, enemyPath)==null) {
				moveState=false;
			}
		}
	}
	
	public void attackEnemy() {
		if(playerTurn==true && attacked==false) {
			int hitRate = hero.getHeroAccuracy() - enemy.getEnemyEvasion(); // 80%
			int damage = hero.getHeroAttackDamage() - enemy.getEnemyDefense(); // 15
			Random random = new Random();
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
							enemy.takeDamage(damage);
							System.out.println("HIT! Enemy health: " +enemy.getEnemyHealth());
						} else {
							//miss
							System.out.println("MISSED! Enemy health: "+enemy.getEnemyHealth());
						}
						attacked=true;
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
			System.out.println("already attacked once");
			// ENEMIES turn dont let player attack
			//dont need this since Enemy is controlled by AI
		}
	}
	
	public void attackHero() {
		if(enemyTurn==true && attacked==false) {
			int hitRate = enemy.getEnemyAccuracy() - hero.getHeroEvasion(); // 80%
			int damage = enemy.getEnemyAttackDamage() - hero.getHeroDefense(); // 15
			Random random = new Random();
			if(hero.getHeroHealth()>0) {
					//selected enemy to attack
					if( (enemy.getEnemyPosition().x+32==hero.getHeroPosition().x&&enemy.getEnemyPosition().y==hero.getHeroPosition().y) ||
						(enemy.getEnemyPosition().x-32==hero.getHeroPosition().x&&enemy.getEnemyPosition().y==hero.getHeroPosition().y) ||
						(enemy.getEnemyPosition().y+32==hero.getHeroPosition().y&&enemy.getEnemyPosition().x==hero.getHeroPosition().x) ||
						(enemy.getEnemyPosition().y-32==hero.getHeroPosition().y&&enemy.getEnemyPosition().x==hero.getHeroPosition().x) ) {
						//enemy in range
							if( random.nextInt((100-1)+1)+1 < hitRate) {
								//hit
								hero.takeDamage(damage);
								System.out.println("HIT! Hero health: " +hero.getHeroHealth());
							} else {
								//miss
								System.out.println("MISSED! Hero health: "+hero.getHeroHealth());
							}
							attacked=true;
						} else {
							//hero not in range 
							System.out.println("Hero chosen is not in range");
						}
			} else {
				//hero has been eliminated
				//we dont need this
			}
		} else {
			System.out.println("already attacked once");
			// dont let it attack
			//dont need this since Enemy is controlled by AI
		}
	}
	
	public void switchTurn() {
		movementIndex = 0;
		attacked = false;
		moved = false;
		moveState=false;
		attackState=false;
		enemyMaxMove = 0;
		action=CharacterOptions.NONE;
		if(playerTurn==true) {
			System.out.println("IT IS THE ENEMIES TURN -- PRES W TO PASS");
			playerTurn = false;
			enemyTurn = true;
		}
		else { 	// in the case of more enemies we can use an index to identify whether all enemies have made
				// their actions before switching to player turn
			// and this part should be done by the AI after all enemies are done with their actions
			System.out.println("IT IS THE PLAYERS TURN -- PRES W TO PASS");
			playerTurn = true;
			enemyTurn=false;
		}
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

		if(keycode == Keys.SPACE && action == CharacterOptions.ATTACK) {
			attackEnemy();
			if(attacked)
				action=CharacterOptions.NONE;
		}
		if(keycode == Keys.SPACE && action == CharacterOptions.MOVE) {
			heroActionMove();
			if(moved)
				action=CharacterOptions.NONE;
		}
		
		if(keycode==Keys.ESCAPE && (action==CharacterOptions.ATTACK || action==CharacterOptions.MOVE)) {
			action=CharacterOptions.NONE;
		}
		/////////////////////////////////////////////////////////////
		
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		// movement of box ---- not the hero sprite
		// boundaries to be changed with walls?
		if(action==CharacterOptions.ATTACK || action==CharacterOptions.MOVE) {	
			if(keycode == Keys.UP) {
					movementBoxPosition.y += 32;
					if(movementBoxSprite.getY() >= (32*39))
					//if(movementBoxSprite.getY()>=608) 
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
					if(movementBoxSprite.getX() >= (32*37))
					//if(movementBoxSprite.getX()>=608) 
						movementBoxPosition.x-=32;
			}
		}
			
		
	    if(keycode == Keys.LEFT) {
	    	cameraLeft=true;
	    }
	    if(keycode == Keys.RIGHT) {
	        cameraRight =true;
	    }
	    if(keycode == Keys.UP) {
	        cameraUp = true;
	    }
	    if(keycode == Keys.DOWN) {
	        cameraDown = true;
	    }
		
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
		/* TEST TRANSITION TO MiniMenu */
		if (keycode == Keys.ENTER) {
			// create the MiniMenu, send this current screen, pass "this" Controller object
			game.setScreen(new CharacterOptionsMenu(game, game.getScreen(), this));
			movementBoxPosition.x = hero.getHeroPosition().x;
			movementBoxPosition.y = hero.getHeroPosition().y;
		}
		
		
		return true;
	}
	
	// accessor and getter to render the Movement Box 
	public Sprite getMovementBoxSprite() {
		return movementBoxSprite;
	}
	
	public void setMovementBoxSprite(Sprite movementBoxSprite) {
		this.movementBoxSprite = movementBoxSprite;
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
	
	public boolean moveCameraLeft() {
		return cameraLeft;
	}
	public boolean moveCameraRight() {
		return cameraRight;
	}
	public boolean moveCameraUp() {
		return cameraUp;
	}
	public boolean moveCameraDown() {
		return cameraDown;
	}
	public void resetCamera() {
		cameraLeft=false;
		cameraRight=false;
		cameraUp=false;
		cameraDown=false;
	}
	

	@Override
	public boolean keyTyped(char arg0) {
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
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void heroActionMove() {
		if(playerTurn && moved==false) {
			movementIndex=0; // reset index
			moveState=true; //action being performed (should be when "move" is selected)
			moved=true;
			pathFinder.setNode((int)hero.getHeroPosition().x, (int)hero.getHeroPosition().y, NodeType.START);
			//pathFinder.setNode(32, 0, NodeType.BLOCKED);
			
			//movement limiter 
			int maxMove = hero.getHeroMoveSpeed(); // 5
				// must implement this better ... becacause the higher maxMove the more conditions that need to be added...
			if(pathFinder.isNodeBlocked((int)movementBoxPosition.x/32, (int)movementBoxPosition.y/32) || 
					(movementBoxPosition.x == enemy.getEnemyPosition().x && movementBoxPosition.y == enemy.getEnemyPosition().y) ) {
				System.out.println("Cell is blocked");
				pathFound=false;
			}
			else if( 	!(movementBoxPosition.x > hero.getHeroPosition().x+(32*maxMove)) && //RIGHT
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*maxMove)) && //LEFT
					!(movementBoxPosition.y > hero.getHeroPosition().y+(32*maxMove)) && //UP
					!(movementBoxPosition.y < hero.getHeroPosition().y-(32*maxMove)) && //DOWN
					//LEFT-UP DIAGONAL
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-3)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-3))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-4)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-4))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-5)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-5))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-3)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-3))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-4)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-4))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-3)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-3))) &&
					//LEFT-DOWN DIAGONAL
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-3)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-3))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-4)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-4))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-5)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-5))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-3)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-3))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-4)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-4))) &&
					!(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-3)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-3))) &&
					
					//RIGHT-UP DIAGONAL
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-3)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-3))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-4)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-4))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-5)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-5))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-3)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-3))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-4)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-4))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-3)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-3))) &&
					
					//RIGHT-DOWN DIAGONAL
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-3)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-3))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-4)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-4))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-5)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-5))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-3)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-3))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-4)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-4))) &&
					!(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-3)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-3))) 
					) {

				pathFinder.setNode((int)movementBoxPosition.x, (int)movementBoxPosition.y, NodeType.END);
			}
			else { 
				System.out.println("not in move range");
			}
			pathFound = pathFinder.findPath();
			heroPath = pathFinder.GetPath();
			
			
			/*if(pathFound) {
				System.out.println("Start cell "+"x: "+heroPath.get(0).getX()/32+" y: "+heroPath.get(0).getY()/32);
				for(int i=0; i<heroPath.size; i++) {
					System.out.println("x: "+heroPath.get(i).getX()/32+" y: "+heroPath.get(i).getY()/32);
				}
				System.out.print("End cell ");
				System.out.println("x: "+heroPath.peek().getX()/32+" y: "+heroPath.peek().getY()/32);
				System.out.println("steps: "+(heroPath.size-1));
			}
			else {
				moved = false;
				moveState = false;
				System.out.println("NO PATH"); 
			}
			*/
			if(!pathFound) {
				moved=false;
				moveState=false;
				heroPath=null;
				System.out.println("NO PATH");
			}
		//end of if
		} else {
			System.out.println("already moved once");
			//enemy turn dont let player move
			//dont need this enemy controller by AI
		}
	}
	
	// Confirming action from the Character Options Menu
	// This method will be called from CharacterOptionsMenu
	// after the player decides to chooses an action: MOVE or ATTACK or WAIT
	public void TestConfirmAction(boolean confirmAction, CharacterOptions action) {
		// this checks that the action has been confirmed
		this.confirmAction = confirmAction;
		this.action=action;
		
		// TEST METHOD for the setting of action
		// MUST BE CHANGED or REMOVED
		if (confirmAction == false && action == CharacterOptions.MOVE) {
			System.out.println("Default Settings");
			
			// Reset the State of Action
			confirmAction = false;
		}
		
		else if (confirmAction == true) {
			if (action == CharacterOptions.MOVE) {
				System.out.println("Inside Controller.java: Moving The Hero...");
				
				// call MOVE functions here
				
				// Reset the State of Action
				confirmAction = false;
			}
			else if (action == CharacterOptions.ATTACK) {
				System.out.println("Inside Controller.java: Commencing Attack Mode!");
				
				// call ATTACK functions here
				
				// Reset the State of Action
				confirmAction = false;
			}
			else if (action == CharacterOptions.WAIT) {				
				System.out.println("Inside Controller.java: Waiting command chosen");

				// call WAIT functions here
				// END TURN
				switchTurn();
				
				// Reset the State of Action
				confirmAction = false;
			}
		}
	}
	
	public void drawGrid() {
		Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
		shape.begin(ShapeType.Filled);
		if(playerTurn && action==CharacterOptions.MOVE) {
			int max = hero.getHeroMoveSpeed();
			int heroY = (int)(hero.getHeroPosition().y/32);
			int heroX = (int)(hero.getHeroPosition().x/32);
			for(int i=0; i<=max; i++) {
				// RIGHT
				if(heroX+i >37) {
					//out of bounds
				}
				else if( pathFinder.isNodeBlocked((heroX+i), (heroY)) ||((heroX+i)*32 == enemy.getEnemyPosition().x && heroY*32 == enemy.getEnemyPosition().y) ) {
					shape.setColor(new Color(255, 0, 0, 0.3f));
					shape.rect((heroX+i)*32, (heroY)*32, 32, 32);	
				}
				else {
					shape.setColor(new Color(0, 0, 255, 0.3f));
					shape.rect((heroX+i)*32, (heroY)*32, 32, 32);
				}
				//LEFT
				if (heroX-i <0) {
					//out of bounds
				}
				else if(pathFinder.isNodeBlocked((heroX-i), (heroY)) ||((heroX-i)*32 == enemy.getEnemyPosition().x && heroY*32 == enemy.getEnemyPosition().y) ) {
					shape.setColor(new Color(255, 0, 0, 0.3f));
					shape.rect((heroX-i)*32, (heroY)*32, 32, 32);
				}
				else {
					shape.setColor(new Color(0, 0, 255, 0.3f));
					shape.rect((heroX-i)*32, (heroY)*32, 32, 32);
				}
				//UP
				if(heroY+i > 39) {
					//out of bounds
				}
				else if(pathFinder.isNodeBlocked((heroX), (heroY+i))   ||((heroX)*32 == enemy.getEnemyPosition().x && (heroY+i)*32 == enemy.getEnemyPosition().y) ) {
					shape.setColor(new Color(255, 0, 0, 0.3f));
					shape.rect((heroX)*32, (heroY+i)*32, 32, 32);
				}
				else {
					shape.setColor(new Color(0, 0, 255, 0.3f));
					shape.rect((heroX)*32, (heroY+i)*32, 32, 32);
				}
				//DOWN
				if( heroY-i <0) {
					//out of bounds
				}
				else if(pathFinder.isNodeBlocked((heroX), (heroY-i)) ||((heroX)*32 == enemy.getEnemyPosition().x && (heroY-i)*32 == enemy.getEnemyPosition().y)) {
					shape.setColor(new Color(255, 0, 0, 0.3f));
					shape.rect((heroX)*32, (heroY-i)*32, 32, 32);
				}
				else {
					shape.setColor(new Color(0, 0, 255, 0.3f));
					shape.rect((heroX)*32, (heroY-i)*32, 32, 32);
				}
			}
			for(int cellYUp=heroY+1; cellYUp<=(heroY+max); cellYUp++) {
				for(int cellXRight=heroX+1; cellXRight<=(heroX+max); cellXRight++) {
					if(		cellYUp==heroY+max && cellXRight==heroX+max ||
							cellYUp==heroY+max-1 && cellXRight==heroX+max-1 ||
							cellYUp==heroY+max-2 && cellXRight==heroX+max-2 ||
							cellYUp==heroY+max-1 && cellXRight==heroX+max ||
							cellYUp==heroY+max && cellXRight==heroX+max-1 ||
							cellYUp==heroY+max-2 && cellXRight==heroX+max ||
							cellYUp==heroY+max && cellXRight==heroX+max-2 ||
							cellYUp==heroY+max-2 && cellXRight==heroX+max-1 ||
							cellYUp==heroY+max-1 && cellXRight==heroX+max-2 ||
							cellYUp==heroY+max-3 && cellXRight==heroX+max ||
							cellYUp==heroY+max && cellXRight==heroX+max-3 ||
							cellYUp==heroY+max-4 && cellXRight==heroX+max ||
							cellYUp==heroY+max && cellXRight==heroX+max-4 ||
							cellYUp==heroY+max-3 && cellXRight==heroX+max-1 ||
							cellYUp==heroY+max-1 && cellXRight==heroX+max-3 
					
							) {
						continue;
						//shape.setColor(new Color(0, 255, 0, 0.4f));
						//shape.rect((cellXRight*32), (cellYUp*32), 32, 32);
					}
					else if(cellYUp > 39 || cellXRight>37) {
						//out of bounds
					}
					else if( pathFinder.isNodeBlocked(cellXRight, cellYUp) ||((cellXRight)*32 == enemy.getEnemyPosition().x && cellYUp*32 == enemy.getEnemyPosition().y)) {
						shape.setColor(new Color(255, 0, 0, 0.3f));
						shape.rect((cellXRight*32), (cellYUp*32), 32, 32);	
					}
					else {
						shape.setColor(new Color(0, 0, 255, 0.3f));
						shape.rect((cellXRight*32), (cellYUp*32), 32, 32);
					//System.out.println("Cell-x "+cellX+ " "+"Cell-y"+cellY);
					}
				}
				for(int cellXLeft=heroX-1; cellXLeft>=(heroX-max); cellXLeft--) {
					if(		cellYUp==heroY+max && cellXLeft==heroX-max ||
							cellYUp==heroY+max-1 && cellXLeft==heroX-max+1 ||
							cellYUp==heroY+max-2 && cellXLeft==heroX-max+2 ||
							cellYUp==heroY+max-1 && cellXLeft==heroX-max ||
							cellYUp==heroY+max && cellXLeft==heroX-max+1 ||
							cellYUp==heroY+max-2 && cellXLeft==heroX-max ||
							cellYUp==heroY+max && cellXLeft==heroX-max+2 ||
							cellYUp==heroY+max-2 && cellXLeft==heroX-max+1 ||
							cellYUp==heroY+max-1 && cellXLeft==heroX-max+2 ||
							cellYUp==heroY+max-3 && cellXLeft==heroX-max ||
							cellYUp==heroY+max && cellXLeft==heroX-max+3 ||
							cellYUp==heroY+max-4 && cellXLeft==heroX-max ||
							cellYUp==heroY+max && cellXLeft==heroX-max+4 ||
							cellYUp==heroY+max-3 && cellXLeft==heroX-max+1 ||
							cellYUp==heroY+max-1 && cellXLeft==heroX-max+3
					
							) {
						continue;
						//shape.setColor(new Color(0, 255, 0, 0.4f));
						//shape.rect((cellXLeft*32), (cellYUp*32), 32, 32);
					}
					else if(cellYUp > 39 || cellXLeft<0) {
						//out of bounds
					}
					else if( pathFinder.isNodeBlocked(cellXLeft, cellYUp) ||((cellXLeft)*32 == enemy.getEnemyPosition().x && cellYUp*32 == enemy.getEnemyPosition().y)) {
						shape.setColor(new Color(255, 0, 0, 0.3f));
						shape.rect((cellXLeft*32), (cellYUp*32), 32, 32);	
					}
					else {
						shape.setColor(new Color(0, 0, 255, 0.3f));
						shape.rect((cellXLeft*32), (cellYUp*32), 32, 32);
					//System.out.println("Cell-x "+cellX+ " "+"Cell-y"+cellY);
					}
				}
			}
			for(int cellYDown=heroY-1; cellYDown>=(heroY-max); cellYDown--) {
				for(int cellXRight=heroX+1; cellXRight<=(heroX+max); cellXRight++) {
					if(		cellYDown==heroY-max && cellXRight==heroX+max ||
							cellYDown==heroY-max+1 && cellXRight==heroX+max-1 ||
							cellYDown==heroY-max+2 && cellXRight==heroX+max-2 ||
							cellYDown==heroY-max+1 && cellXRight==heroX+max ||
							cellYDown==heroY-max && cellXRight==heroX+max-1 ||
							cellYDown==heroY-max+2 && cellXRight==heroX+max ||
							cellYDown==heroY-max && cellXRight==heroX+max-2 ||
							cellYDown==heroY-max+2 && cellXRight==heroX+max-1 ||
							cellYDown==heroY-max+1 && cellXRight==heroX+max-2 ||
							cellYDown==heroY-max+3 && cellXRight==heroX+max ||
							cellYDown==heroY-max && cellXRight==heroX+max-3 ||
							cellYDown==heroY-max+4 && cellXRight==heroX+max ||
							cellYDown==heroY-max && cellXRight==heroX+max-4 ||
							cellYDown==heroY-max+3 && cellXRight==heroX+max-1 ||
							cellYDown==heroY-max+1 && cellXRight==heroX+max-3 
							
					
							) {
						continue;
						//shape.setColor(new Color(0, 255, 0, 0.4f));
						//shape.rect((cellXRight*32), (cellYDown*32), 32, 32);
					}
					else if(cellYDown < 0  || cellXRight>37) {
						//out of bounds 
					}
					else if( pathFinder.isNodeBlocked(cellXRight, cellYDown) ||((cellXRight)*32 == enemy.getEnemyPosition().x && cellYDown*32 == enemy.getEnemyPosition().y)) {
						shape.setColor(new Color(255, 0, 0, 0.3f));
						shape.rect((cellXRight*32), (cellYDown*32), 32, 32);	
					}
					else {
						shape.setColor(new Color(0, 0, 255, 0.3f));
						shape.rect((cellXRight*32), (cellYDown*32), 32, 32);
						//System.out.println("Cell-x "+cellX+ " "+"Cell-y"+cellY);
					}
				}
				for(int cellXLeft=heroX-1; cellXLeft>=(heroX-max); cellXLeft--) {
					if(		cellYDown==heroY-max && cellXLeft==heroX-max ||
							cellYDown==heroY-max+1 && cellXLeft==heroX-max+1 ||
							cellYDown==heroY-max+2 && cellXLeft==heroX-max+2 ||
							cellYDown==heroY-max+1 && cellXLeft==heroX-max ||
							cellYDown==heroY-max && cellXLeft==heroX-max+1 ||
							cellYDown==heroY-max+2 && cellXLeft==heroX-max ||
							cellYDown==heroY-max && cellXLeft==heroX-max+2 ||
							cellYDown==heroY-max+2 && cellXLeft==heroX-max+1 ||
							cellYDown==heroY-max+1 && cellXLeft==heroX-max+2 ||
							cellYDown==heroY-max+3 && cellXLeft==heroX-max ||
							cellYDown==heroY-max && cellXLeft==heroX-max+3 ||
							cellYDown==heroY-max+4 && cellXLeft==heroX-max ||
							cellYDown==heroY-max && cellXLeft==heroX-max+4 ||
							cellYDown==heroY-max+3 && cellXLeft==heroX-max+1 ||
							cellYDown==heroY-max+1 && cellXLeft==heroX-max+3
					
							) {
						continue;
						//shape.setColor(new Color(0, 255, 0, 0.4f));
						//shape.rect((cellXLeft*32), (cellYDown*32), 32, 32);
					}
					else if(cellYDown < 0  || cellXLeft <0) {
						//out of bounds 
					}
					else if( pathFinder.isNodeBlocked(cellXLeft, cellYDown) ||((cellXLeft)*32 == enemy.getEnemyPosition().x && cellYDown*32 == enemy.getEnemyPosition().y)) {
						shape.setColor(new Color(255, 0, 0, 0.3f));
						shape.rect((cellXLeft*32), (cellYDown*32), 32, 32);	
					}
					else {
						shape.setColor(new Color(0, 0, 255, 0.3f));
						shape.rect((cellXLeft*32), (cellYDown*32), 32, 32);
						//System.out.println("Cell-x "+cellX+ " "+"Cell-y"+cellY);
					}
	
				}
			}
		}
		
		if(playerTurn && action==CharacterOptions.ATTACK) {
			int heroY = (int)hero.getHeroPosition().y/32;
			int heroX = (int)hero.getHeroPosition().x/32;
			for(int i=0; i<=1; i++) {
				shape.setColor(new Color(0, 255, 0, 0.3f));
				shape.rect(heroX, heroY, 32, 32);
				//RIGHT
				if(heroX+i >37) {
					//out of bounds
				}
				else if( pathFinder.isNodeBlocked(heroX+i, heroY) ) {
					shape.setColor(new Color(255, 0, 0, 0.3f));
					shape.rect((heroX+i)*32, (heroY*32), 32, 32);	
				}
				else {
					shape.setColor(new Color(0, 255, 0, 0.3f));
					shape.rect((heroX+i)*32, heroY*32, 32, 32);
				}
				//LEFT
				if(heroX-i <0) {
					//out of bounds
				}
				else if( pathFinder.isNodeBlocked(heroX-i, heroY) ) {
					shape.setColor(new Color(255, 0, 0, 0.3f));
					shape.rect((heroX-i)*32, (heroY*32), 32, 32);	
				}
				else {
					shape.setColor(new Color(0, 255, 0, 0.3f));
					shape.rect((heroX-i)*32, heroY*32, 32, 32);
				}
				//UP
				if(heroY+i > 39) {
					//out of bounds
				}
				else if( pathFinder.isNodeBlocked(heroX, heroY+i) ) {
					shape.setColor(new Color(255, 0, 0, 0.3f));
					shape.rect((heroX*32), (heroY+i)*32, 32, 32);	
				}
				else {
					shape.setColor(new Color(0, 255, 0, 0.3f));
					shape.rect(heroX*32, (heroY+i)*32, 32, 32);
				}
				//DOWN
				if(heroY-i <0) {
					//out of bounds
				}
				else if( pathFinder.isNodeBlocked(heroX, heroY-i) ) {
					shape.setColor(new Color(255, 0, 0, 0.3f));
					shape.rect((heroX*32), (heroY-i)*32, 32, 32);	
				}
				else {
					shape.setColor(new Color(0, 255, 0, 0.3f));
					shape.rect(heroX*32, (heroY-i)*32, 32, 32);
				}
			}
		}
		
		shape.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	public ShapeRenderer getShape() {
		return shape;
	}

	public CharacterOptions getAction() {
		return action;
	}

	public boolean isMoveState() {
		return moveState;
	}

	public boolean isAttackState() {
		return attackState;
	}

=======
package edu.emp.gameworld;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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

import edu.emp.game.EMPGame;

public class Controller implements InputProcessor {
  final EMPGame game;
  
  private static final String TAG = Controller.class.getName();
  
  // Control the sprites! that's why the sprites are here but
  // rendering the sprite (drawn by the SpriteBatch) is in Renderer :)
  
  // Movement-Box object
  private Texture movementBoxTexture;
  private Sprite movementBoxSprite;
  private Vector2 movementBoxPosition;
  
  // Moving the sprites by this amount of pixel
  // should this datatype be int or float??
  private static final float MOVE_PIXEL_BY_32 = 32;

  // indicate selected sprite?
  public int selectedSprite;
  
  // For multiple enemies or heroes make into an array
  private Enemy enemy;
  
  // Hero object
  private Hero hero;
  

  private int enemyMaxMove=0;
  // The sprites placed in spriteObjects will be drawn
  // in the Renderer file.
  // UNUSED FOR THE MOMENT
  // private Sprite[] spriteObjects; 
  
  boolean cameraUp;
  boolean cameraDown;
  boolean cameraLeft;
  boolean cameraRight;

  //PATH FINDER
  PathFinder pathFinder;
  boolean pathFound;
  Array<Node> heroPath;
  Array<Node> enemyPath;
  //for moving...
  int movementIndex;
  
  //game states when action is chosen
  private boolean attackState;
  private boolean moveState;
  
  //states performed
  boolean moved;
  boolean attacked;
  
  // TURN states must be switched with "WAIT" option 
  boolean playerTurn;
  boolean enemyTurn;
  
  /* NOT USED */
  TurnIndicator currentTurn;
  
  // confirm action from Character Options Menu
  private boolean confirmAction;
  private CharacterOptions action;
  
  
  ShapeRenderer shape = new ShapeRenderer();
  

  
  
  public Controller(final EMPGame game) {
    this.game = game;
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
    pathFinder = new PathFinder((32*38), (32*40), 32);//Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 32);
    
    //testPathFinder();
    
    hero = new Hero((32*27), (32*6));
    heroPath = new Array<Node>();
    
    enemy = new Enemy((32*13), (32*31));
    enemyPath = new Array<Node>();
    
    // details for the Movement Box Sprite
    movementBoxTexture = new Texture(Gdx.files.internal("move-box.png"));
    movementBoxSprite = new Sprite(movementBoxTexture);
    movementBoxPosition = new Vector2(hero.getHeroPosition().x, hero.getHeroPosition().y);
    

    
    cameraUp=false;
    cameraDown=false;
    cameraLeft=false;
    cameraRight=false;
    
    movementIndex = 0;
    
    attackState=false;
    moveState=false;
    moved = false;
    attacked =false;
    
    playerTurn = true;
    enemyTurn = false;
    // currentTurn = TurnIndicator.PLAYER;
    
    confirmAction = false;
    action=CharacterOptions.NONE;
  }
  

  // update the game objects
  public void update(float deltaTime) {
    updateMovementBox();
    updateHero(deltaTime);
    updateEnemy(deltaTime);
  }
  
  // update the Movement Box
  public void updateMovementBox() {
    movementBoxSprite.setPosition(movementBoxPosition.x, movementBoxPosition.y);
  }
  
  // update Hero animations
  public void updateHero(float deltaTime) {
    hero.setHeroStateTime(hero.getHeroStateTime() + deltaTime);
    
    //System.out.println(hero.getHeroPosition().x + " " + hero.getHeroPosition().y);
    if(isMoveState() && playerTurn)
    // if(isMoveState() && currentTurn == TurnIndicator.PLAYER)
      this.moveHero(Gdx.graphics.getDeltaTime());
    // standard movement while standing
    hero.setHeroWalk();
  }
  
  public void updateEnemy(float deltaTime) {
    enemy.setEnemyStateTime(enemy.getEnemyStateTime() + deltaTime);

    if(enemyTurn == true) {
    // if (currentTurn == TurnIndicator.ENEMY) {
      if(attacked == false) {
        attackHero();
      }
      if(moved==false) {
          movementIndex=0; // reset index
          //action being performed 	(should be when "move" is selected)
          //moved=true;
          pathFinder.setNode((int)enemy.getEnemyPosition().x, (int)enemy.getEnemyPosition().y, NodeType.START);
          //pathFinder.setNode(32, 0, NodeType.BLOCKED);
          pathFinder.setNode((int)hero.getHeroPosition().x, (int)hero.getHeroPosition().y, NodeType.END);
          pathFound = pathFinder.findPath();
          enemyPath = pathFinder.GetPath();
          
          moveState=true;
          
        if(pathFound && enemyMaxMove<=3) {	
          enemyMaxMove++;
          if(enemy.getEnemyPosition().x + 32 < hero.getHeroPosition().x ||
              enemy.getEnemyPosition().x - 32 > hero.getHeroPosition().x ||
              enemy.getEnemyPosition().y + 32 < hero.getHeroPosition().y ||
              enemy.getEnemyPosition().y - 32 > hero.getHeroPosition().y ) 
          {
            this.moveEnemy(Gdx.graphics.getDeltaTime());
          }
          else if (enemy.getEnemyPosition().x+32 == hero.getHeroPosition().x && enemy.getEnemyPosition().y != hero.getHeroPosition().y) {
            this.moveEnemy(Gdx.graphics.getDeltaTime());
          }
          else if(enemy.getEnemyPosition().x-32 == hero.getHeroPosition().x && enemy.getEnemyPosition().y != hero.getHeroPosition().y) {
            this.moveEnemy(Gdx.graphics.getDeltaTime());
          }
          /// STOP MOVEMENT WHEN REACHED MAX STEPS
          else {
            moved = true;

          }
        }
        else 
          moved=true;
          
        //end of if
      }
      //if(attacked == false) {
        //attackHero();
      //}
      if(moved==true) {
        switchTurn();
      }
    }
    
    
    
    // standard movement while standing
    enemy.setEnemyWalk();
  }
  
  public Node getCurrentNode(int index, Array<Node> path) {
    Node n = null;
    if(index==0) {
      //n = getFirstNode();
      n = path.get(0);
    }
    if(index>0 && index<path.size) {
      n = path.get(index);
    }
    else {
      System.out.println("1-we are hitting end");
    }
    return n;
  }
  
  public Node getFirstNode(Array<Node> path) {
    pathFound = pathFinder.findPath();
    path = pathFinder.GetPath();
    return path.get(0);
  }
  public Node getNextNode(int index, Array<Node> path) {
    Node n = null;
    if(index < path.size) {
      //System.out.println("index " +index+ " size "+testPath.size);
        n = path.get(index);//+1);

    }
    else {
      moveState=false;
      System.out.println("2-we are at the end");
    }
    return n;
  }
  public Node getPreviousNode(int index, Array<Node> path) {
    Node n=null;
    if(index > 0) {
      n = path.get(index-1);
    }
    else {
      moveState=false;
      System.out.println("3-we are at the end");
    }
    return n;
  }
  public void incrementCurrentNode(Array<Node> path) {
    if(!(movementIndex >= path.size)) {
      movementIndex++;
    }
    else {
      moveState=false;
      System.out.println("Cant do that");
      movementIndex=-1;
    }
  }
  
  public void moveHero(float deltaTime) {
    if(isMoveState()) {
      Node currentNode = getCurrentNode(movementIndex, heroPath);
      Node nextNode = getNextNode(movementIndex+1, heroPath);
      
      float speed = 32.0f;
      //MOVE RIGHT
      if(currentNode.getX() < nextNode.getX()) {
        hero.setWalkingStyle(WalkStyle.RIGHT);
        hero.incrementX(speed);
        if(hero.getX() > (hero.getX()+speed)) {
           hero.incrementX(speed);
        }
        if(enemy.getX() >= (32*37)) {
        //if(hero.getX() >= 608.0f) {
          hero.incrementX(0f);
        }
      }
      //MOVE LEFT
      else if(currentNode.getX() > nextNode.getX()) {
        hero.setWalkingStyle(WalkStyle.LEFT);
        hero.incrementX(-speed);
        if(hero.getX() < (hero.getX()-speed)) {
          hero.incrementX(-speed);
        }
        if(hero.getX() <= 0f) {
          hero.incrementX(0f);
        }
      }
      // MOVE UP
      else if(currentNode.getY() > nextNode.getY()) {
        hero.setWalkingStyle(WalkStyle.DOWN);
        hero.incrementY(-speed);
        if(hero.getY() < (hero.getY()-speed)) {
          hero.incrementY(-speed);
        }
        if(hero.getY() <= 0f) {
          hero.incrementY(0f);
        }
      }
      // MOVE DOWN
      else if(currentNode.getY() < nextNode.getY()) {
        hero.setWalkingStyle(WalkStyle.UP);
        hero.incrementY(speed);
        if(hero.getY() > (hero.getY()+speed)) {
          hero.incrementY(speed);
        }
        if(hero.getY() >= (32*39)) {
        //if(hero.getY() >= 608.0f) {
          hero.incrementY(0f);
        }
      }
      incrementCurrentNode(heroPath);
      if(movementIndex == heroPath.size-1) {
        if(getCurrentNode(movementIndex, heroPath).getX() == 15 &&
            getCurrentNode(movementIndex, heroPath).getY() == 15) {
          System.out.println("reached destination");
          moveState=false;
        }
      }
      if(movementIndex == -1) {
        System.out.println("no good");
      }
      
      if(getNextNode(movementIndex+1, heroPath)==null) {
        moveState=false;
      }
    }
  }
  
  public void moveEnemy(float deltaTime) {
    if(isMoveState() && enemyTurn) {
    // if (isMoveState() && currentTurn == TurnIndicator.ENEMY) {
      Node currentNode = getCurrentNode(movementIndex, enemyPath);
      Node nextNode = getNextNode(movementIndex+1, enemyPath);
      
      float speed = 32.0f;
      //MOVE RIGHT
      if(currentNode.getX() < nextNode.getX()) {
        enemy.setWalkingStyle(WalkStyle.RIGHT);
        enemy.incrementX(speed);
        if(enemy.getX() > (enemy.getX()+speed)) {
           hero.incrementX(speed);
        }
        if(enemy.getX() >= (32*37)) {
        //if(enemy.getX() >= 608.0f) {
          enemy.incrementX(0f);
        }
      }
      //MOVE LEFT
      else if(currentNode.getX() > nextNode.getX()) {
        enemy.setWalkingStyle(WalkStyle.LEFT);
        enemy.incrementX(-speed);
        if(enemy.getX() < (enemy.getX()-speed)) {
          enemy.incrementX(-speed);
        }
        if(hero.getX() <= 0f) {
          hero.incrementX(0f);
        }
      }
      // MOVE UP
      else if(currentNode.getY() > nextNode.getY()) {
        enemy.setWalkingStyle(WalkStyle.DOWN);
        enemy.incrementY(-speed);
        if(enemy.getY() < (enemy.getY()-speed)) {
          enemy.incrementY(-speed);
        }
        if(enemy.getY() <= 0f) {
          enemy.incrementY(0f);
        }
      }
      // MOVE DOWN
      else if(currentNode.getY() < nextNode.getY()) {
        enemy.setWalkingStyle(WalkStyle.UP);
        enemy.incrementY(speed);
        if(enemy.getY() > (enemy.getY()+speed)) {
          enemy.incrementY(speed);
        }
        if(enemy.getY() >= (32*39)) {
        //if(enemy.getY() >= 608.0f) {
          enemy.incrementY(0f);
        }
      }
      incrementCurrentNode(enemyPath);
      if(movementIndex == enemyPath.size-1) {
        if(getCurrentNode(movementIndex, enemyPath).getX() == 15 &&
            getCurrentNode(movementIndex, enemyPath).getY() == 15) {
          System.out.println("reached destination");
          moveState=false;
        }
      }
      if(movementIndex == -1) {
        System.out.println("no good");
      }
      
      if(getNextNode(movementIndex+1, enemyPath)==null) {
        moveState=false;
      }
    }
  }
  
  public void attackEnemy() {
    if(playerTurn==true && attacked==false) {
    // if (currentTurn == TurnIndicator.PLAYER && attacked==false) {
      int hitRate = hero.getHeroAccuracy() - enemy.getEnemyEvasion(); // 80%
      int damage = hero.getHeroAttackDamage() - enemy.getEnemyDefense(); // 15
      Random random = new Random();
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
              enemy.takeDamage(damage);
              System.out.println("HIT! Enemy health: " +enemy.getEnemyHealth());
            } else {
              //miss
              System.out.println("MISSED! Enemy health: "+enemy.getEnemyHealth());
            }
            attacked=true;
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
      System.out.println("already attacked once");
      // ENEMIES turn dont let player attack
      //dont need this since Enemy is controlled by AI
    }
  }
  
  public void attackHero() {
    if(enemyTurn==true && attacked==false) {
    // if (currentTurn == TurnIndicator.ENEMY && attacked == false) {
      int hitRate = enemy.getEnemyAccuracy() - hero.getHeroEvasion(); // 80%
      int damage = enemy.getEnemyAttackDamage() - hero.getHeroDefense(); // 15
      Random random = new Random();
      if(hero.getHeroHealth()>0) {
          //selected enemy to attack
          if( (enemy.getEnemyPosition().x+32==hero.getHeroPosition().x&&enemy.getEnemyPosition().y==hero.getHeroPosition().y) ||
            (enemy.getEnemyPosition().x-32==hero.getHeroPosition().x&&enemy.getEnemyPosition().y==hero.getHeroPosition().y) ||
            (enemy.getEnemyPosition().y+32==hero.getHeroPosition().y&&enemy.getEnemyPosition().x==hero.getHeroPosition().x) ||
            (enemy.getEnemyPosition().y-32==hero.getHeroPosition().y&&enemy.getEnemyPosition().x==hero.getHeroPosition().x) ) {
            //enemy in range
              if( random.nextInt((100-1)+1)+1 < hitRate) {
                //hit
                hero.takeDamage(damage);
                System.out.println("HIT! Hero health: " +hero.getHeroHealth());
              } else {
                //miss
                System.out.println("MISSED! Hero health: "+hero.getHeroHealth());
              }
              attacked=true;
            } else {
              //hero not in range 
              System.out.println("Hero chosen is not in range");
            }
      } else {
        //hero has been eliminated
        //we dont need this
      }
    } else {
      System.out.println("already attacked once");
      // dont let it attack
      //dont need this since Enemy is controlled by AI
    }
  }
  
  public void switchTurn() {
    movementIndex = 0;
    attacked = false;
    moved = false;
    moveState=false;
    attackState=false;
    enemyMaxMove = 0;
    action=CharacterOptions.NONE;
    if(playerTurn==true) {
    // if (currentTurn == TurnIndicator.PLAYER) {
      System.out.println("IT IS THE ENEMIES TURN -- PRES W TO PASS");
      playerTurn = false;
      enemyTurn = true;
      // currentTurn = TurnIndicator.ENEMY;
    }
    else { 	// in the case of more enemies we can use an index to identify whether all enemies have made
        // their actions before switching to player turn
      // and this part should be done by the AI after all enemies are done with their actions
      System.out.println("IT IS THE PLAYERS TURN -- PRES W TO PASS");
      playerTurn = true;
      enemyTurn=false;
      // currentTurn = TurnIndicator.ENEMY;
    }
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

    if(keycode == Keys.SPACE && action == CharacterOptions.ATTACK) {
      attackEnemy();
      if(attacked)
        action=CharacterOptions.NONE;
    }
    if(keycode == Keys.SPACE && action == CharacterOptions.MOVE) {
      heroActionMove();
      if(moved)
        action=CharacterOptions.NONE;
    }
    
    if(keycode==Keys.ESCAPE && (action==CharacterOptions.ATTACK || action==CharacterOptions.MOVE)) {
      action=CharacterOptions.NONE;
    }
    /////////////////////////////////////////////////////////////
    
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
    // movement of box ---- not the hero sprite
    // boundaries to be changed with walls?
    if(action==CharacterOptions.ATTACK || action==CharacterOptions.MOVE) {	
      if(keycode == Keys.UP) {
          movementBoxPosition.y += 32;
          if(movementBoxSprite.getY() >= (32*39))
          //if(movementBoxSprite.getY()>=608) 
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
          if(movementBoxSprite.getX() >= (32*37))
          //if(movementBoxSprite.getX()>=608) 
            movementBoxPosition.x-=32;
      }
    }
      
    
      if(keycode == Keys.LEFT) {
        cameraLeft=true;
      }
      if(keycode == Keys.RIGHT) {
          cameraRight =true;
      }
      if(keycode == Keys.UP) {
          cameraUp = true;
      }
      if(keycode == Keys.DOWN) {
          cameraDown = true;
      }
    
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
    /* Transition to CharacterOptionsMenu */
    if (keycode == Keys.ENTER) {
      // create the MiniMenu, send this current screen, pass "this" Controller object
      game.setScreen(new CharacterOptionsMenu(game, game.getScreen(), this));
      movementBoxPosition.x = hero.getHeroPosition().x;
      movementBoxPosition.y = hero.getHeroPosition().y;
    }
    
    /* DELETE THIS METHOD AFTER PROPER IMPLEMENTATION */
    /* Transition to PhaseIndicator */
    if (keycode == Keys.L) {
      // If the current turn is Player
        // game.setScreen(new PhaseDisplay(game, game.getScreen(), this, TurnIndicator.PLAYER));
      // if the current turn is Enemy
        // game.setScreen(new PhaseDisplay(game, game.getScreen(), this, TurnIndicator.ENEMY));
      
      // TEST
      game.setScreen(new PhaseDisplay(game, game.getScreen(), this, TurnIndicator.PLAYER));
    }
    
    
    return true;
  }
  
  // accessor and getter to render the Movement Box 
  public Sprite getMovementBoxSprite() {
    return movementBoxSprite;
  }
  
  public void setMovementBoxSprite(Sprite movementBoxSprite) {
    this.movementBoxSprite = movementBoxSprite;
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
  
  public boolean moveCameraLeft() {
    return cameraLeft;
  }
  public boolean moveCameraRight() {
    return cameraRight;
  }
  public boolean moveCameraUp() {
    return cameraUp;
  }
  public boolean moveCameraDown() {
    return cameraDown;
  }
  public void resetCamera() {
    cameraLeft=false;
    cameraRight=false;
    cameraUp=false;
    cameraDown=false;
  }
  

  @Override
  public boolean keyTyped(char arg0) {
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
  public boolean touchDragged(int arg0, int arg1, int arg2) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
    // TODO Auto-generated method stub
    return false;
  }
  
  public void heroActionMove() {
    if(playerTurn && moved==false) {
    // if (currentTurn == TurnIndicator.PLAYER && moved==false) {
      movementIndex=0; // reset index
      moveState=true; //action being performed (should be when "move" is selected)
      moved=true;
      pathFinder.setNode((int)hero.getHeroPosition().x, (int)hero.getHeroPosition().y, NodeType.START);
      //pathFinder.setNode(32, 0, NodeType.BLOCKED);
      
      //movement limiter 
      int maxMove = hero.getHeroMoveSpeed(); // 5
        // must implement this better ... becacause the higher maxMove the more conditions that need to be added...
      if(pathFinder.isNodeBlocked((int)movementBoxPosition.x/32, (int)movementBoxPosition.y/32)) {
        System.out.println("Cell is blocked");
        pathFound=false;
      }
      else if( 	!(movementBoxPosition.x > hero.getHeroPosition().x+(32*maxMove)) && //RIGHT
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*maxMove)) && //LEFT
          !(movementBoxPosition.y > hero.getHeroPosition().y+(32*maxMove)) && //UP
          !(movementBoxPosition.y < hero.getHeroPosition().y-(32*maxMove)) && //DOWN
          //LEFT-UP DIAGONAL
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-3)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-3))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-4)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-4))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-5)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-5))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-3)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-3))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-4)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-4))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-3)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-3))) &&
          //LEFT-DOWN DIAGONAL
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-3)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-3))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-4)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-4))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-5)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-5))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-3)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-3))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-4)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-4))) &&
          !(movementBoxPosition.x < hero.getHeroPosition().x-(32*(maxMove-3)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-3))) &&
          
          //RIGHT-UP DIAGONAL
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-3)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-3))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-4)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-4))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-5)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-1))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-5))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-3)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-3))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-4)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-2))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-4))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-3)) && movementBoxPosition.y > hero.getHeroPosition().y+(32*(maxMove-3))) &&
          
          //RIGHT-DOWN DIAGONAL
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-3)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-3))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-4)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-4))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-5)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-1))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-1)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-5))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-3)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-3))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-4)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-2))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-2)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-4))) &&
          !(movementBoxPosition.x > hero.getHeroPosition().x+(32*(maxMove-3)) && movementBoxPosition.y < hero.getHeroPosition().y-(32*(maxMove-3))) 
          ) {

        pathFinder.setNode((int)movementBoxPosition.x, (int)movementBoxPosition.y, NodeType.END);
      }
      else { 
        System.out.println("not in move range");
      }
      pathFound = pathFinder.findPath();
      heroPath = pathFinder.GetPath();
      
      
      /*if(pathFound) {
        System.out.println("Start cell "+"x: "+heroPath.get(0).getX()/32+" y: "+heroPath.get(0).getY()/32);
        for(int i=0; i<heroPath.size; i++) {
          System.out.println("x: "+heroPath.get(i).getX()/32+" y: "+heroPath.get(i).getY()/32);
        }
        System.out.print("End cell ");
        System.out.println("x: "+heroPath.peek().getX()/32+" y: "+heroPath.peek().getY()/32);
        System.out.println("steps: "+(heroPath.size-1));
      }
      else {
        moved = false;
        moveState = false;
        System.out.println("NO PATH"); 
      }
      */
      if(!pathFound) {
        moved=false;
        moveState=false;
        heroPath=null;
        System.out.println("NO PATH");
      }
    //end of if
    } else {
      System.out.println("already moved once");
      //enemy turn dont let player move
      //dont need this enemy controller by AI
    }
  }
  
  // Confirming action from the Character Options Menu
  // This method will be called from CharacterOptionsMenu
  // after the player decides to chooses an action: MOVE or ATTACK or WAIT
  public void TestConfirmAction(boolean confirmAction, CharacterOptions action) {
    // this checks that the action has been confirmed
    this.confirmAction = confirmAction;
    this.action=action;
    
    if (confirmAction == true) {
      if (action == CharacterOptions.MOVE) {
        System.out.println("Inside Controller.java: Moving The Hero...");
        
        // call MOVE functions here
        
        // Reset the State of Action
        confirmAction = false;
      }
      else if (action == CharacterOptions.ATTACK) {
        System.out.println("Inside Controller.java: Commencing Attack Mode!");
        
        // call ATTACK functions here
        
        // Reset the State of Action
        confirmAction = false;
      }
      else if (action == CharacterOptions.WAIT) {				
        System.out.println("Inside Controller.java: Waiting command chosen");

        // call WAIT functions here
        // END TURN
        switchTurn();
        
        // Reset the State of Action
        confirmAction = false;
      }
    }
  }
  
  public void drawGrid() {
    Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
    shape.begin(ShapeType.Filled);
    if(playerTurn && action==CharacterOptions.MOVE) {
    // if (currentTurn == TurnIndicator.PLAYER && action==CharacterOptions.MOVE) {
      int max = hero.getHeroMoveSpeed();
      int heroY = (int)(hero.getHeroPosition().y/32);
      int heroX = (int)(hero.getHeroPosition().x/32);
      for(int cellYUp=heroY; cellYUp<=(heroY+max); cellYUp++) {
        for(int cellXRight=heroX; cellXRight<=(heroX+max); cellXRight++) {
          if(		cellYUp==heroY+max && cellXRight==heroX+max ||
              cellYUp==heroY+max-1 && cellXRight==heroX+max-1 ||
              cellYUp==heroY+max-2 && cellXRight==heroX+max-2 ||
              cellYUp==heroY+max-1 && cellXRight==heroX+max ||
              cellYUp==heroY+max && cellXRight==heroX+max-1 ||
              cellYUp==heroY+max-2 && cellXRight==heroX+max ||
              cellYUp==heroY+max && cellXRight==heroX+max-2 ||
              cellYUp==heroY+max-2 && cellXRight==heroX+max-1 ||
              cellYUp==heroY+max-1 && cellXRight==heroX+max-2 ||
              cellYUp==heroY+max-3 && cellXRight==heroX+max ||
              cellYUp==heroY+max && cellXRight==heroX+max-3 ||
              cellYUp==heroY+max-4 && cellXRight==heroX+max ||
              cellYUp==heroY+max && cellXRight==heroX+max-4 ||
              cellYUp==heroY+max-3 && cellXRight==heroX+max-1 ||
              cellYUp==heroY+max-1 && cellXRight==heroX+max-3 
          
              ) {
            continue;
            //shape.setColor(new Color(0, 255, 0, 0.4f));
            //shape.rect((cellXRight*32), (cellYUp*32), 32, 32);
          }
          else if( pathFinder.isNodeBlocked(cellXRight, cellYUp) ) {
            shape.setColor(new Color(255, 0, 0, 0.3f));
            shape.rect((cellXRight*32), (cellYUp*32), 32, 32);	
          }
          else {
            shape.setColor(new Color(0, 0, 255, 0.3f));
            shape.rect((cellXRight*32), (cellYUp*32), 32, 32);
          //System.out.println("Cell-x "+cellX+ " "+"Cell-y"+cellY);
          }
        }
        for(int cellXLeft=heroX; cellXLeft>=(heroX-max); cellXLeft--) {
          if(		cellYUp==heroY+max && cellXLeft==heroX-max ||
              cellYUp==heroY+max-1 && cellXLeft==heroX-max+1 ||
              cellYUp==heroY+max-2 && cellXLeft==heroX-max+2 ||
              cellYUp==heroY+max-1 && cellXLeft==heroX-max ||
              cellYUp==heroY+max && cellXLeft==heroX-max+1 ||
              cellYUp==heroY+max-2 && cellXLeft==heroX-max ||
              cellYUp==heroY+max && cellXLeft==heroX-max+2 ||
              cellYUp==heroY+max-2 && cellXLeft==heroX-max+1 ||
              cellYUp==heroY+max-1 && cellXLeft==heroX-max+2 ||
              cellYUp==heroY+max-3 && cellXLeft==heroX-max ||
              cellYUp==heroY+max && cellXLeft==heroX-max+3 ||
              cellYUp==heroY+max-4 && cellXLeft==heroX-max ||
              cellYUp==heroY+max && cellXLeft==heroX-max+4 ||
              cellYUp==heroY+max-3 && cellXLeft==heroX-max+1 ||
              cellYUp==heroY+max-1 && cellXLeft==heroX-max+3
          
              ) {
            continue;
            //shape.setColor(new Color(0, 255, 0, 0.4f));
            //shape.rect((cellXLeft*32), (cellYUp*32), 32, 32);
          }
          else if( pathFinder.isNodeBlocked(cellXLeft, cellYUp) ) {
            shape.setColor(new Color(255, 0, 0, 0.3f));
            shape.rect((cellXLeft*32), (cellYUp*32), 32, 32);	
          }
          else {
            shape.setColor(new Color(0, 0, 255, 0.3f));
            shape.rect((cellXLeft*32), (cellYUp*32), 32, 32);
          //System.out.println("Cell-x "+cellX+ " "+"Cell-y"+cellY);
          }
        }
      }
      for(int cellYDown=heroY; cellYDown>=(heroY-max); cellYDown--) {
        for(int cellXRight=heroX; cellXRight<=(heroX+max); cellXRight++) {
          if(		cellYDown==heroY-max && cellXRight==heroX+max ||
              cellYDown==heroY-max+1 && cellXRight==heroX+max-1 ||
              cellYDown==heroY-max+2 && cellXRight==heroX+max-2 ||
              cellYDown==heroY-max+1 && cellXRight==heroX+max ||
              cellYDown==heroY-max && cellXRight==heroX+max-1 ||
              cellYDown==heroY-max+2 && cellXRight==heroX+max ||
              cellYDown==heroY-max && cellXRight==heroX+max-2 ||
              cellYDown==heroY-max+2 && cellXRight==heroX+max-1 ||
              cellYDown==heroY-max+1 && cellXRight==heroX+max-2 ||
              cellYDown==heroY-max+3 && cellXRight==heroX+max ||
              cellYDown==heroY-max && cellXRight==heroX+max-3 ||
              cellYDown==heroY-max+4 && cellXRight==heroX+max ||
              cellYDown==heroY-max && cellXRight==heroX+max-4 ||
              cellYDown==heroY-max+3 && cellXRight==heroX+max-1 ||
              cellYDown==heroY-max+1 && cellXRight==heroX+max-3 
              
          
              ) {
            continue;
            //shape.setColor(new Color(0, 255, 0, 0.4f));
            //shape.rect((cellXRight*32), (cellYDown*32), 32, 32);
          }
          else if( pathFinder.isNodeBlocked(cellXRight, cellYDown) ) {
            shape.setColor(new Color(255, 0, 0, 0.3f));
            shape.rect((cellXRight*32), (cellYDown*32), 32, 32);	
          }
          else {
            shape.setColor(new Color(0, 0, 255, 0.3f));
            shape.rect((cellXRight*32), (cellYDown*32), 32, 32);
            //System.out.println("Cell-x "+cellX+ " "+"Cell-y"+cellY);
          }
        }
        for(int cellXLeft=heroX; cellXLeft>=(heroX-max); cellXLeft--) {
          if(		cellYDown==heroY-max && cellXLeft==heroX-max ||
              cellYDown==heroY-max+1 && cellXLeft==heroX-max+1 ||
              cellYDown==heroY-max+2 && cellXLeft==heroX-max+2 ||
              cellYDown==heroY-max+1 && cellXLeft==heroX-max ||
              cellYDown==heroY-max && cellXLeft==heroX-max+1 ||
              cellYDown==heroY-max+2 && cellXLeft==heroX-max ||
              cellYDown==heroY-max && cellXLeft==heroX-max+2 ||
              cellYDown==heroY-max+2 && cellXLeft==heroX-max+1 ||
              cellYDown==heroY-max+1 && cellXLeft==heroX-max+2 ||
              cellYDown==heroY-max+3 && cellXLeft==heroX-max ||
              cellYDown==heroY-max && cellXLeft==heroX-max+3 ||
              cellYDown==heroY-max+4 && cellXLeft==heroX-max ||
              cellYDown==heroY-max && cellXLeft==heroX-max+4 ||
              cellYDown==heroY-max+3 && cellXLeft==heroX-max+1 ||
              cellYDown==heroY-max+1 && cellXLeft==heroX-max+3
          
              ) {
            continue;
            //shape.setColor(new Color(0, 255, 0, 0.4f));
            //shape.rect((cellXLeft*32), (cellYDown*32), 32, 32);
          }
          else if( pathFinder.isNodeBlocked(cellXLeft, cellYDown) ) {
            shape.setColor(new Color(255, 0, 0, 0.3f));
            shape.rect((cellXLeft*32), (cellYDown*32), 32, 32);	
          }
          else {
            shape.setColor(new Color(0, 0, 255, 0.3f));
            shape.rect((cellXLeft*32), (cellYDown*32), 32, 32);
            //System.out.println("Cell-x "+cellX+ " "+"Cell-y"+cellY);
          }
  
        }
      }
    }
    
    if(playerTurn && action==CharacterOptions.ATTACK) {
    // if (currentTurn == TurnIndicator.PLAYER && action==CharacterOptions.ATTACK) {
      int heroY = (int)hero.getHeroPosition().y;
      int heroX = (int)hero.getHeroPosition().x;
      shape.setColor(new Color(0, 255, 0, 0.3f));
      shape.rect(heroX, heroY, 32, 32);
      shape.rect((heroX+32), heroY, 32, 32);
      shape.rect(heroX, (heroY+32), 32, 32);
      shape.rect((heroX-32), heroY, 32, 32);
      shape.rect(heroX, (heroY-32), 32, 32);
    }
    
    shape.end();
    Gdx.gl.glDisable(GL20.GL_BLEND);
  }
  public ShapeRenderer getShape() {
    return shape;
  }

  public CharacterOptions getAction() {
    return action;
  }

  public boolean isMoveState() {
    return moveState;
  }

  public boolean isAttackState() {
    return attackState;
  }

>>>>>>> b241b83185fe2ed333c3af7a2df91c40263c34bd
}
