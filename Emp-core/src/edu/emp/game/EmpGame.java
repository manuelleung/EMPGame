package edu.emp.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


// This is the game source 
public class EmpGame extends ApplicationAdapter implements InputProcessor {
	// Variables for the map, camera, renderer
	TiledMap tileMap;
	OrthographicCamera camera;
	TiledMapRenderer tiledMapRenderer;
	
	// movement box texture
	Texture movementBoxTexture;
	Sprite movementBoxSprite;
	Vector2 movementBoxPosition;
	
	// spritebatch
	SpriteBatch batch;
	
	// empty sprite and texture 
	Sprite sprite;
	Texture texture;
	
	// Animation for the character
	Animation heroAnimation;
	TextureRegion [] heroFrames;
	TextureRegion heroCurrentFrame;
	Texture heroTexture;
	float heroStateTime;
	
	Vector2 heroPosition;
	
	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		// Initialize our camera as orthographic (top-down) and Set the orthogonal position
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w, h);
		// Load our tile map into our renderer
		tileMap = new TmxMapLoader().load("test3.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap);	
		Gdx.input.setInputProcessor(this);
		
		batch = new SpriteBatch();
		// Movement Box Sprite
		movementBoxTexture = new Texture(Gdx.files.internal("move-box.png"));
		movementBoxSprite = new Sprite(movementBoxTexture);
		movementBoxPosition = new Vector2(0, 0);
		
		heroPosition = new Vector2(0, 0);
		
		characterRender();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// This update is in case we want to have a camera that can scroll with the arrow keys
		// --> camera.update(); 
		// Set the viewpoint from camera and render map
		tiledMapRenderer.setView(camera);
		// array tiles (can use this later to be able to have character behind tiles) 
		tiledMapRenderer.render(new int[] {1, 3, 4});
		// 0 = Wall , 2 = object
		batch.setProjectionMatrix(camera.combined);
		
		// Set the new position of the movement box
		movementBoxSprite.setPosition(movementBoxPosition.x, movementBoxPosition.y);

		
		batch.begin();
		
		// draw the movement box
		movementBoxSprite.draw(batch);
		
		
		// heroAnimation is made with the whole array .. so when animation is running, it displays every animation
		// instead of only the ones we want...
		// -----> We might need to separate each animation into separate arrays before making the hero animation
		// like we discussed in the LAB -
		heroStateTime += Gdx.graphics.getDeltaTime();
		heroCurrentFrame = heroAnimation.getKeyFrame(heroStateTime, true);
		batch.draw(heroCurrentFrame, heroPosition.x, heroPosition.y);
		
		
		batch.end();
		
		getCollisionTiles();
		
	}
	
	// use this?
	// or use a blocked tile method ?
	// if we use this... check before movements (we might get bouncing effect)
	public boolean checkCollision() {
		MapObjects objects = tileMap.getLayers().get("object").getObjects();
		for(MapObject object : objects) {
			if(object instanceof RectangleMapObject) {
				Rectangle r = ((RectangleMapObject) object).getRectangle();
				// do collision here 
				if(Intersector.overlaps(r, sprite.getBoundingRectangle())) {
					return true;
				}
			}
		}
		return false;
	}
	
	// Might use this for collision instead. it loops through a layer and gives us cells that are occupied
	// One way to use this might be to place all non-null tiles into an array (not the best way)
	// or use the custom properties ... in TEST3.TMX we have a wall layer (0) with custom tiles only on 
	// the objects property = "Collision" 
	public void getCollisionTiles() {
		TiledMapTileLayer layer = (TiledMapTileLayer) tileMap.getLayers().get(0);
			for (int x = 0; x < layer.getWidth(); x++) {
				for (int y = 0; y < layer.getHeight(); y++) {
					TiledMapTileLayer.Cell cell = layer.getCell(x, y);
						if(cell == null) {
							continue; // There is no cell
						}
						if(cell.getTile() == null) {
							continue; // No tile inside cell
						}
						// cell has a tile
						Object property = cell.getTile().getProperties().get("Collision");
						if(property != null) {
							// add functionality~
							//System.out.println(cell.getTile().getId() + " x:" + x + " y:" + y);
						}
				}
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
		System.out.println("box-x: " + movementBoxPosition.x + " box-y: " + movementBoxPosition.y);
		
		return true;
	}

	@Override
	public boolean keyTyped(char arg0) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public boolean keyUp(int arg0) {
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
	
	public void characterRender() {
		// check 2D animation on resources.txt for reference as well the Renderer file in Lab 5
		// frame_col and frame_row is based on a specific sprite, in this case: Hero.png
		int frame_cols = 8;	
		int frame_rows = 3;
		
		heroTexture = new Texture(Gdx.files.internal("Hero.png"));
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
	
	
}























