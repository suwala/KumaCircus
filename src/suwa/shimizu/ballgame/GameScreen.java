package suwa.shimizu.ballgame;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import suwa.shimizu.ballgame.World.WorldListener;

import android.graphics.Shader.TileMode;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.gl.Animation;
import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.gl.Texture;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

public class GameScreen extends GLScreen{

	static final int GAME_READY = 0;
	static final int GAME_RUNNING = 1;
	static final int GAME_PAUSED = 2;
	static final int GAME_OVER = 3;
	
	int state;
	Camera2D camera;
	SpriteBatcher batcher;
	World world;
	WorldListener worldListener;
	WorldRenderer renderer;
	Texture paused;
	TextureRegion pausedRegion;
	Texture gameOver;
	TextureRegion gameOverRegion;
	Vector2 touchPoint;
	Rectangle retryBounds;
	Rectangle titleBounds;
	
	public GameScreen(Game game) {
		super(game);
		//state = GAME_READY;
		state = GAME_RUNNING;
		camera = new Camera2D(glGraphics, 320,480);
		camera.zoom = 1;
		batcher = new SpriteBatcher(glGraphics, 200);
		touchPoint = new Vector2();
		//96,128retry 96,208title	
		retryBounds = new Rectangle(96, 298, 128, 64);
		titleBounds = new Rectangle(96, 208, 128,64);
		worldListener = new WorldListener() {
			
			@Override
			public void wall() {//ここにSE
				Assets.boyon.play(1);
			}
			
			@Override
			public void lion() {
			}
			
			@Override
			public void holeIN() {
				
				Assets.holeIN.play(1);
			}
			
			@Override
			public void hit() {
			}
		};
		
		world = new World(worldListener);
		renderer = new WorldRenderer(glGraphics, batcher, world);
		
	}

	@Override
	public void update(float deltaTime) {
		if(deltaTime > 0.1f)
			deltaTime = 0.1f;
		switch(state){
		case GAME_READY:
			updateReady();
			break;
		case GAME_RUNNING:
			updateRunning(deltaTime);
			break;
		case GAME_PAUSED:
			updatePaused();
			break;
		case GAME_OVER:
			updateGameOver();
			break;
		}
	}
	
	private void updateReady(){
		if(game.getInput().getTouchEvents().size()>0){
			state = GAME_RUNNING;
		}
	}
	private void updateRunning(float deltaTime){
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int len = touchEvents.size();
		for(int i=0;i<len;i++){
			TouchEvent event = touchEvents.get(i);
			if(event.type != TouchEvent.TOUCH_UP)
				continue;
			state = GAME_PAUSED;
		}
		world.update(deltaTime, game.getInput().getAccelX(), game.getInput().getAccelY());
		//if(world.state == World.WORLD_NEXT)
		//	state = GAME_READY;
		if(world.state == World.WORLD_GAMEOVER)
			state = GAME_OVER;
	}
	private void updatePaused(){
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int len = touchEvents.size();
		for(int i=0;i<len;i++){
			TouchEvent event = touchEvents.get(i);
			if(event.type != TouchEvent.TOUCH_UP)
				continue;
			state = GAME_RUNNING;
			return;
		}
	}
	private void updateGameOver(){
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int len = touchEvents.size();
		for(int i=0;i<len;i++){		
			TouchEvent event = touchEvents.get(i);
			if(event.type == TouchEvent.TOUCH_UP){
				touchPoint.set(event.x,event.y);
				camera.touchToWorld(touchPoint);
				if(OverlapTester.pointInRectangle(retryBounds, touchPoint))
					game.setScreen(new GameScreen(game));
				if(OverlapTester.pointInRectangle(titleBounds, touchPoint))
					game.setScreen(new MainMenuScreen(game));
			}
		}
	}

	@Override
	public void present(float deltaTime) {
		GL10 gl = glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		renderer.render();
		
		camera.setViewportAndMatrices();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				
		switch(state){
		case GAME_READY:
			presentReady();
			break;
		case GAME_RUNNING:
			presentRunning();
			break;
		case GAME_PAUSED:
			presentPaused();
			break;
		case GAME_OVER:
			presentGameOver();
			break;
		}
			
		gl.glDisable(GL10.GL_BLEND);
	}
	private void presentReady(){
		
		
	}
	private void presentRunning(){
		if(world.life>0){
			batcher.beginBatch(Assets.items);
			for(int i=0;i<world.life;i++){
				TextureRegion keyFrame = Assets.ball[0].getKeyFrame(0, Animation.ANIMATION_NONLOOPING);
				batcher.drawSprite((i*20)+320*0.1f, 480*0.9f, 32, 32, keyFrame);
			}

			batcher.endBatch();
		}
	}
	private void presentGameOver(){
		batcher.beginBatch(gameOver);
		batcher.drawSprite(160, 240, 320, 480, gameOverRegion);
		batcher.endBatch();
	}
	private void presentPaused(){
		batcher.beginBatch(paused);
		batcher.drawSprite((int)(320/2), (int)(480/2), 256, 384, pausedRegion);	
		batcher.endBatch();
	}

	@Override
	public void pause() {
		paused.dispose();
		paused.dispose();
	}

	@Override
	public void resume() {
		paused = new Texture(glGame, "dir/texture/help.png");
		pausedRegion = new TextureRegion(paused, 0, 0,256 , 384);
		gameOver = new Texture(glGame,"dir/texture/gameover.png");
		gameOverRegion = new TextureRegion(gameOver,0,0,320,480);
	}

	@Override
	public void dispose() {
	}

}
