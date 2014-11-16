package edu.emp.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

// This is the game source 
public class EmpGame extends ApplicationAdapter {
	// Variables for the map, camera, renderer
	TiledMap tileMap;
	OrthographicCamera camera;
	TiledMapRenderer tiledMapRenderer;
	
	@Override
	public void create () {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		// Initialize our camera as orthographic (top-down) and Set the orthogonal position
		camera = new OrthographicCamera();
		camera.setToOrtho(false, w, h);
		// Load our tile map into our renderer
		tileMap = new TmxMapLoader().load("test.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tileMap);	
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
		tiledMapRenderer.render();
	}
}