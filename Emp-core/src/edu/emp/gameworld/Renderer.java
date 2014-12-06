package edu.emp.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Disposable;

public class Renderer implements Disposable {

	private Controller gameController;
	
	private TiledMap tileMap;
	private TiledMapRenderer tiledMapRenderer;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	// ?
	private Sprite sprite;
	
	
	public Renderer(Controller gameController) {
		// This is necessary since this allows the Renderer
		// to access the game objects being managed by the Controller
		this.gameController = gameController;
		init();
	}
	
	/* Initialize the rendering settings here.
	 * This code could also be placed in the Construtor. The reason for this separation is
	 * to address the question: what if we need to just reset drawing an object in the game or just fix the camera?
	 * This is a better way to dealing with this problem than to completely rebuilding the object itself,
	 * saving performance time. 
	 */
	// load the settings
	public void init() {
		// send your drawing commands to spritebatch!
		// this draws the image
		batch = new SpriteBatch();
		
		// Initialize our camera as orthographic (top-down) and Set the orthogonal position
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		// Load our tile map into our renderer
		tileMap = new TmxMapLoader().load("demomap.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap);
		getCollisionTiles();
		System.out.println(Gdx.graphics.getWidth()+ " " + Gdx.graphics.getHeight());
	}
	
	public void render() {
		renderMaps();
		renderGameObjects();
	}
	
	public void renderMaps() {
		// This update is in case we want to have a camera that can scroll with the arrow keys
		camera.update(); 
		// Set the viewpoint from camera and render map
		tiledMapRenderer.setView(camera);
		// array tiles (can use this later to be able to have character behind tiles) 
		//tiledMapRenderer.render(new int[] {1, 3, 4});
		// 0 = Wall , 2 = object
		tiledMapRenderer.render(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
	}
	
	public void renderGameObjects() {
		/* It basically just means drawing will be done in 2D space using the position and bounds of the given camera. */
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// draw the movement box
		gameController.getMovementBoxSprite().draw(batch);
		// draw the hero
		batch.draw(gameController.getHero().getHeroCurrentFrame(), gameController.getHero().getHeroPosition().x, gameController.getHero().getHeroPosition().y);
		// draw enemy
		batch.draw(gameController.getEnemy().getEnemyCurrentFrame(), gameController.getEnemy().getEnemyPosition().x, gameController.getEnemy().getEnemyPosition().y);
		batch.end();
		
		
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
							System.out.println(cell.getTile().getId() + " x:" + x + " y:" + y);
							gameController.pathFinder.setNode((32*x), (32*y), NodeType.BLOCKED);
						}
				}
	      }
	}
	
	public void resize (int width, int height) {
		// something to do with the camera
	}
	
	@Override
	public void dispose() {
		batch.dispose();		
	}

}
