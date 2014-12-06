package edu.emp.gameworld;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
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
	

	private int enemyMaxMove = 0;
	// The sprites placed in spriteObjects will be drawn
	// in the Renderer file.
	// UNUSED FOR THE MOMENT
	// private Sprite[] spriteObjects; 
	

	//PATH FINDER
	PathFinder pathFinder;
	boolean pathFound;
	Array<Node> heroPath = new Array<Node>();
	Array<Node> enemyPath = new Array<Node>();
	//for moving...
	int movementIndex = 0;
	
	//game states when action is chosen
	boolean attackState = false;
	boolean moveState = false;
	
	//states performed
	boolean moved = false;
	boolean attacked =false;
	
	// TURN states must be switched with "WAIT" option 
	boolean playerTurn = true;
	boolean enemyTurn = false;
	
	// confirm action from Character Options Menu
	private boolean confirmAction = false;
	private CharacterOptions action = CharacterOptions.MOVE;
	
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
		pathFinder = new PathFinder(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 32);
		
		// details for the Movement Box Sprite
		movementBoxTexture = new Texture(Gdx.files.internal("move-box.png"));
		movementBoxSprite = new Sprite(movementBoxTexture);
		movementBoxPosition = new Vector2(0, 0);
		
		//testPathFinder();
		
		hero = new Hero(0, 0);
		
		
		enemy = new Enemy((32*7), (32*17));

	}
	

	// update the game objects
	public void update(float deltaTime) {
		updateMovementBox();
		updateHero(deltaTime);
		updateEnemy(deltaTime);
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
		pathFinder.setNode((32*15), (32*15), NodeType.END);
		pathFound = pathFinder.findPath(); //1 = found --- 2 = no path
		heroPath = pathFinder.GetPath();
		if(pathFound) {
			System.out.println("Start cell "+"x: "+heroPath.get(0).getX()/32+" y: "+heroPath.get(0).getY()/32);
		for(int i=0; i<heroPath.size; i++) {
			System.out.println("x: "+heroPath.get(i).getX()/32+" y: "+heroPath.get(i).getY()/32);
		}
		System.out.print("End cell ");
		System.out.println("x: "+heroPath.peek().getX()/32+" y: "+heroPath.peek().getY()/32);
		System.out.println("steps: "+(heroPath.size-1));
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
		
		//System.out.println(hero.getHeroPosition().x + " " + hero.getHeroPosition().y);
		if(moveState && playerTurn)
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
		if(moveState) {
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
				if(hero.getX() >= 608.0f) {
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
				if(hero.getY() >= 608.0f) {
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
		if(moveState && enemyTurn) {
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
				if(enemy.getX() >= 608.0f) {
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
				if(enemy.getY() >= 608.0f) {
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
		}
		if(keycode == Keys.SPACE && action == CharacterOptions.MOVE) {
			heroActionMove();
		}
		/////////////////////////////////////////////////////////////
		
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
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
		
		
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
		/* TEST TRANSITION TO MiniMenu */
		if (keycode == Keys.ENTER) {
			// create the MiniMenu, send this current screen, pass "this" Controller object
			game.setScreen(new CharacterOptionsMenu(game, game.getScreen(), this));
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
			if( 	!(movementBoxPosition.x > hero.getHeroPosition().x+(32*maxMove)) && //RIGHT
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
					)
				pathFinder.setNode((int)movementBoxPosition.x, (int)movementBoxPosition.y, NodeType.END);
			else 
				System.out.println("not in move range");
			
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
		this.action = action;
		
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

}
