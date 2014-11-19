package edu.emp.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
	
	//character
	Texture texture;
	
	//sprites
	SpriteBatch batch;
	Sprite sprite;
	
	Vector2 position;
	
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
		texture = new Texture(Gdx.files.internal("image.png"));
		sprite = new Sprite(texture);
		
		position = new Vector2(0, 0);

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
		tiledMapRenderer.render(new int[] {0, 1, 2});
		batch.setProjectionMatrix(camera.combined);
		
		sprite.setPosition(position.x, position.y);
		
		batch.begin();
		sprite.draw(batch);
		batch.end();
		
		
	}
	
	// use this?
	// or use a blocked tile method ?
	// if we use this... change to boolean and check before movements (so we dont get bouncing effect )
	public float checkCollision(float x) {
		MapObjects objects = tileMap.getLayers().get("object").getObjects();
		for(MapObject object : objects) {
			if(object instanceof RectangleMapObject) {
				Rectangle r = ((RectangleMapObject) object).getRectangle();
				// do collision here 
				if(Intersector.overlaps(r, sprite.getBoundingRectangle())) {
					
					
					// just testing 
					if(x==-32)
						return 64;
					if(x==32)
						return -64;
					
				}
			}
		}
		
		return 0;
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
		// movement of sprite (for now)

		
		if(keycode == Keys.UP) {
			position.y += 32;
			if(sprite.getY()>=600) 
				position.y-=32;
			position.y += checkCollision(32);
		}
		if(keycode == Keys.DOWN) {
			position.y -= 32;
			if(sprite.getY()<=0) 
				position.y+=32;
		}
		if(keycode == Keys.LEFT) {
			position.x -= 32;
			if(sprite.getX()<=0) 
				position.x+=32;
		}
		if(keycode == Keys.RIGHT) {
			position.x += 32;
			if(sprite.getX()>=600) 
				position.x-=32;
		}
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
	
	
}























