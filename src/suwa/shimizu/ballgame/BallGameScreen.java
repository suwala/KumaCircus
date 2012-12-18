package suwa.shimizu.ballgame;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.test.IsolatedContext;
import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.gl.Animation;
import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.SpatialHashGrid;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.gl.Texture;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.impl.GLGame;
import com.badlogic.androidgames.framework.impl.GLGraphics;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Vector2;

public class BallGameScreen extends Screen{

	enum GameState{
		running,ready
	}

	static final float WORLD_WIDTH = 15f;
	static final float WORLD_HEIGHT = 25f;
	GameState state;
	final int NUM_WALLS =5;
	final int NUM_WALLSIDES =5;
	final int NUM_LION = 20;
	
	GLGraphics glGraphics;

	Ball ball;
	Hole hole;
	Kuma kuma;
	List<Wall> walls;
	List<Wall> wallSides;
	List<Lion> lions;
	GameObject tile;
	SpatialHashGrid grid;
	GameObject help;
	boolean crash = false;

	Camera2D camera;
	Texture atlasTexture;
	Texture tileTexture;
	Texture helpTexture;
	TextureRegion tileRegion;
	TextureRegion holeRegion;
	TextureRegion helpRegion;
	TextureRegion wallRegion;
	TextureRegion wallSideRegion;

	SpriteBatcher batcher;
	Animation[] ballAnime;
	Animation kumaAnime;
	Animation lionAnime;
	Animation ballCrashAnime;

	Vector2 touchPos = new Vector2();

	public BallGameScreen(Game game) {
		super(game);
		glGraphics = ((GLGame)game).getGLGraphics();
		grid = new SpatialHashGrid(WORLD_WIDTH, WORLD_HEIGHT, 8.0f);
		
		
		//オブジェクトの作成
		ball = new Ball(0, 0);
		kuma = new Kuma(0, 0);
		hole = new Hole(WORLD_WIDTH/2, WORLD_HEIGHT/2, 1.5f,1.5f);
		tile = new GameObject(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
		help = new GameObject(0,0,1.5f,2.5f);
		walls = new ArrayList<Wall>(NUM_WALLS);
		wallSides = new ArrayList<Wall>(NUM_WALLSIDES);
		lions = new ArrayList<Lion>(NUM_LION);
		
		for(int i=0;i<NUM_LION;i++){
			Lion lion;
			lion = new Lion((float)Math.random()*WORLD_WIDTH,(float)Math.random()*WORLD_HEIGHT,2,1);
			lions.add(lion);
			grid.insertDynamicObject(lion);
		}
		
		for(int i=0;i<NUM_WALLS;i++){
			Wall wall;
			wall = new Wall(1, WORLD_HEIGHT/2, 2, 4);
			
			grid.insertStaticObject(wall);
			walls.add(wall);
		}
		
		for(int i=0;i<NUM_WALLSIDES;i++){
			Wall wallSide;
			wallSide = new Wall(4, 1+i*2, 4, 2);
			
			grid.insertStaticObject(wallSide);
			wallSides.add(wallSide);
		}

		//gridへオブジェクトの登録
		
		grid.insertStaticObject(hole);


		//描画する最大オブジェクト数
		batcher = new SpriteBatcher(glGraphics,100);
		camera = new Camera2D(glGraphics, WORLD_WIDTH, WORLD_HEIGHT);		
		camera.zoom = 1;
		state = GameState.ready;
	}

	@Override
	public void update(float deltaTime) {

		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		
		if(state == GameState.running)
			runningUpdate(touchEvents,deltaTime);
		else if(state == GameState.ready)
			readyUpdate(touchEvents);		
		
	}

	@Override
	public void present(float deltaTime) {
			GL10 gl = glGraphics.getGL();
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			camera.setViewportAndMatrices();

			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL10.GL_TEXTURE_2D);

			//タイルの描画
			batcher.beginBatch(tileTexture);
			batcher.drawSprite(WORLD_WIDTH/2, WORLD_HEIGHT/2,15,25, tileRegion);
			batcher.endBatch();
			
			//バッチセット
			batcher.beginBatch(atlasTexture);
			
			//壁の描画
			int len = walls.size();
			for(int i=0;i<len;i++){
				Wall wall = walls.get(i);
				batcher.drawSprite(wall.position.x, wall.position.y, 2,4, wall.angle,wallRegion);
			}

			len = wallSides.size();
			for(int i=0;i<len;i++){
				Wall wall = wallSides.get(i);				
				batcher.drawSprite(wall.position.x, wall.position.y, 4,2, wall.angle,wallSideRegion);
			}
			//ホールの描画
			batcher.drawSprite(WORLD_WIDTH/2, WORLD_HEIGHT/2, 1.5f, 1.5f, holeRegion);

			TextureRegion keyFrame;
			//ボールの描画
			if(!crash){
				if(ball.velocity.angle() < 45 || ball.velocity.angle() > 315 || ball.velocity.angle() > 115 && ball.velocity.angle() < 225)
					keyFrame = ballAnime[0].getKeyFrame(ball.time, Animation.ANIMATION_LOOPING);
				else
					keyFrame = ballAnime[1].getKeyFrame(ball.time, Animation.ANIMATION_LOOPING);

				batcher.drawSprite(ball.position.x, ball.position.y, ball.velocity.x < 0?1:-1,1, keyFrame);
			}else{
				keyFrame = ballCrashAnime.getKeyFrame(ball.time, Animation.ANIMATION_NONLOOPING);
				batcher.drawSprite(ball.position.x, ball.position.y, 2,2, keyFrame);
			}
			batcher.drawSprite(ball.position.x, ball.position.y, ball.velocity.x < 0?1:-1,1, keyFrame);

			//クマの描画
			keyFrame = kumaAnime.getKeyFrame(ball.time, Animation.ANIMATION_LOOPING);
			batcher.drawSprite(ball.position.x, ball.position.y+0.5f,ball.velocity.x < 0?-1:1,2, keyFrame);


			//ライオンの描画
			len = lions.size();
			keyFrame = lionAnime.getKeyFrame(ball.time, Animation.ANIMATION_LOOPING);
			for(int i=0;i<len;i++){
				Lion lion = lions.get(i);
				batcher.drawSprite(lion.position.x, lion.position.y, lion.velocity.x < 0?-2:2, 2, keyFrame);				
			}
			batcher.endBatch();
			
			
			//ヘルプ
			if(state == GameState.ready){
				batcher.beginBatch(helpTexture);
				batcher.drawSprite(7.5f,12.5f,12,20,helpRegion);
				batcher.endBatch();
				
			}
		
	}

	@Override
	public void pause() {
		
		if(state == GameState.running)
			state = GameState.ready;

	}

	@Override
	public void resume() {
		atlasTexture = new Texture(((GLGame)game), "dir/texture/ball.png");
		ballAnime = new Animation[2];
		ballAnime[0] = new Animation(0.2f, new TextureRegion(atlasTexture,0, 0, 32, 32),
				new TextureRegion(atlasTexture, 32, 0, 32, 32),
				new TextureRegion(atlasTexture, 64, 0, 32, 32));
		ballAnime[1] = new Animation(0.2f, new TextureRegion(atlasTexture, 96, 0, 32, 32),
				new TextureRegion(atlasTexture, 128, 0, 32, 32),
				new TextureRegion(atlasTexture, 160, 0, 32, 32));

		tileTexture = new Texture(((GLGame)game), "dir/texture/tile2.png");
		tileRegion = new TextureRegion(tileTexture, 0, 0, 320, 480);
		//座標x,y そこからの長さ！x,y
		kumaAnime = new Animation(0.2f,new TextureRegion(atlasTexture, 0, 32, 32, 64),
				new TextureRegion(atlasTexture,32, 32, 32, 64));

		//48*47
		holeRegion = new TextureRegion(atlasTexture, 64, 32, 64, 64);
		//32*21
		lionAnime = new Animation(0.2f, new TextureRegion(atlasTexture, 224, 0, 32, 32),
				new TextureRegion(atlasTexture, 256, 0, 32, 32),
				new TextureRegion(atlasTexture, 288, 0, 32, 32));

		//111to0   39*39
		int i = 512/13;
		ballCrashAnime = new Animation(0.1f,new TextureRegion(atlasTexture, i*0, 111, 39, 39),
				new TextureRegion(atlasTexture, i*1, 111, 39, 39),
				new TextureRegion(atlasTexture, i*2, 111, 39, 39),
				new TextureRegion(atlasTexture, i*3, 111, 39, 39),
				new TextureRegion(atlasTexture, i*4, 111, 39, 39),
				new TextureRegion(atlasTexture, i*5, 111, 39, 39),
				new TextureRegion(atlasTexture, i*6, 111, 39, 39),
				new TextureRegion(atlasTexture, i*7, 111, 39, 39),
				new TextureRegion(atlasTexture, i*8, 111, 39, 39),
				new TextureRegion(atlasTexture, i*9, 111, 39, 39),
				new TextureRegion(atlasTexture, i*10, 111, 39, 39),
				new TextureRegion(atlasTexture, i*11, 111, 39, 39),
				new TextureRegion(atlasTexture, i*12, 111, 39, 39),
				new TextureRegion(atlasTexture, i*13, 111, 39, 39));
		
		
		wallRegion = new TextureRegion(atlasTexture, 192, 0, 32, 64);
		//横壁120*46
		wallSideRegion = new TextureRegion(atlasTexture, 128, 32, 64, 32);
		
		
		helpTexture = new Texture(((GLGame)game), "dir/texture/help.png");
		helpRegion = new TextureRegion(helpTexture, 0, 0, 256, 384);
		
	}

	@Override
	public void dispose() {

	}
	
	private void runningUpdate(List<TouchEvent> touchEvents,float deltaTime){
		
		int len = touchEvents.size();
		
		
		for(int i=0;i<len;i++){
			TouchEvent event = touchEvents.get(i);
			camera.touchToWorld(touchPos.set(event.x,event.y));

			if(event.type == TouchEvent.TOUCH_UP){
				camera.zoom = camera.zoom < 1?1:0.7f;
				if(camera.zoom < 1){
					camera.position.set(touchPos.x,touchPos.y);
				}else
					camera.position.set(WORLD_WIDTH/2,WORLD_HEIGHT/2);
				state = GameState.ready;
				return;
			}
		}
		

		//センサーから加速度を得る+オブジェクトの更新
		ball.accel.set(-game.getInput().getAccelX(),-game.getInput().getAccelY());
		ball.update(deltaTime);
		len = lions.size();
		for(int i=0;i<len;i++){
			Lion lion = lions.get(i);
			lion.update(deltaTime);
		}
		len = walls.size();

		//接触判定
		List<GameObject> colliders = grid.getPotentialColliders(ball);
		len = colliders.size();
		for(int i=0;i<len;i++){
			//ホールとの判定
			if(OverlapTester.overlapCircles(ball.circle, hole.circle)){
				if(ball.velocity.len() < 0.5f){
					Log.d("gole","GOOOOOOOOOOOOOOOOOLE");
					crash=true;
				}else{
					ball.velocity.mul(0.98f);
				}
			}
			//ライオンとの判定
			if( colliders.get(i) instanceof Lion){
				Lion lion = (Lion) colliders.get(i);
				if(OverlapTester.overlapCircleRectangle(ball.circle, lion.bounds)){
					if(!crash)
						ball.time = 0;
					crash = true;
					ball.velocity.set(0,0);//完全停止
				}
			}
			//壁との判定
			GameObject wall = colliders.get(i);
			if(OverlapTester.overlapCircleRectangle(ball.circle, wall.bounds)){
				//ball.position.sub(ball.velocity.x*deltaTime, ball.velocity.y*deltaTime);
				ball.velocity.mul(-1);
				ball.accel.set(0,0);
				//ball.velocity.mul(-1f);
			}
		}
	}
	
	private void readyUpdate(List<TouchEvent> touchEvents){
		int len = touchEvents.size();
		for(int i=0;i<len;i++){
			TouchEvent event = touchEvents.get(i);
			if(event.type == TouchEvent.TOUCH_UP){
				state = GameState.running;
				return;
			}
		}
	}
}
