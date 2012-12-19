package suwa.shimizu.ballgame;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.gl.Animation;
import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.gl.Texture;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.math.Circle;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Vector2;

public class HiScoreScreen extends GLScreen{

	Texture hiScore;
	TextureRegion hiScoreRegion;
	Texture scoreBoad;
	TextureRegion scoreBoadRegion;
	Camera2D camera;
	SpriteBatcher batcher;
	Circle returnBtn;
	Vector2 touchPoint;
	Vector2 move = new Vector2(1.5f, 0);
	String[] strHiScore;
	String[] strLevels;
	
	float time;
	
	List<DynamicGameObject> objs;
	final int NUM_OBJ = 3;
	
	
	public HiScoreScreen(Game game) {
		super(game);
		camera = new Camera2D(glGraphics, 320, 480);
		batcher = new SpriteBatcher(glGraphics, 100);
		returnBtn = new Circle(240, 16, 80);
		touchPoint = new Vector2();
		objs = new ArrayList<DynamicGameObject>(NUM_OBJ*5);
		strHiScore = new String[5];
		strLevels = new String[5];
		for(int i=0;i<5;i++){
			strHiScore[i] = (i+1)+":"+String.format("%d", Settings.highscores[i]);
			strLevels[i] = ""+Settings.levels[i];
		}
		
		int i=0;
		while(i<NUM_OBJ*3){
			switch (i/NUM_OBJ) {
			case 0:
				Kuma kuma = new Kuma(i%NUM_OBJ*352/NUM_OBJ, 16);
				objs.add(kuma);
				Log.d("int","int");
				break;
			case 1:
				Ball ball = new Ball(i%NUM_OBJ*352/NUM_OBJ, 48);
				objs.add(ball);	
				break;
			case 2:
				Lion lion = new Lion(i%NUM_OBJ*352/NUM_OBJ, 416);
				lion.velocity.set(-1,0);
				objs.add(lion);
				break;
			}
			i++;
		}
	}

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		int len = touchEvents.size();
		for(int i=0;i<len;i++){
			TouchEvent event = touchEvents.get(i);
			if(event.type == TouchEvent.TOUCH_UP){
				touchPoint.set(event.x,event.y);
				camera.touchToWorld(touchPoint);
				if(OverlapTester.pointInCircle(returnBtn, touchPoint)){
					objs.clear();
					game.setScreen(new MainMenuScreen(game));
				}
			}
		}
		time += deltaTime;
		len = objs.size();
		for(int i=0;i<len;i++){
			DynamicGameObject obj = objs.get(i);
			if(obj.position.x < -16)
				obj.position.x = 336;
			if(obj.position.x > 336)
				obj.position.x =-16;
			if(i > 5 && i < 9){
				obj.position.add(obj.velocity);			
			}
			else
				obj.position.add(move);			
		}
	}

	@Override
	public void present(float deltaTime) {
		GL10 gl = glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.setViewportAndMatrices();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		batcher.beginBatch(scoreBoad);
		batcher.drawSprite(160, 240, 320, 480, scoreBoadRegion);
		batcher.endBatch();
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		batcher.beginBatch(Assets.items);
		
		//クマとボールの描画
		renderObject(deltaTime);
		//スコアの描画
		renderScores();
		//HiScore看板の描画
		batcher.drawSprite(160, 442, 272, 80, Assets.hiScoreRegion);
		batcher.endBatch();
		
		gl.glDisable(GL10.GL_BLEND);
	}
	
	private void renderObject(float deltaTime){
		int len = objs.size();
		DynamicGameObject obj;
		TextureRegion keyFrame;
		for(int i=0;i<len;i++){
			obj = objs.get(i);
			switch (i/NUM_OBJ) {
			case 0:
				keyFrame= Assets.ball[0].getKeyFrame(time*1.5f, Animation.ANIMATION_LOOPING);
				break;
			case 1:
				keyFrame= Assets.kuma.getKeyFrame(time*1.5f, Animation.ANIMATION_LOOPING);
				break;
			default:
				keyFrame= Assets.lion.getKeyFrame(time*1.5f, Animation.ANIMATION_LOOPING);
				break;
			}
			if(i > 2 && i < 6)
				batcher.drawSprite(obj.position.x, obj.position.y-16, 32, 64, keyFrame);
			else
				batcher.drawSprite(obj.position.x, obj.position.y, obj.velocity.x < 0?-32:32, 32, keyFrame);
		}
	}
	private void renderScores(){
		float y=288; 
		for(int i=0;i<5;i++){
			Assets.font.drawText(batcher, strHiScore[i] , 64, y);
			Assets.font.drawText(batcher, strLevels[i], 211, y);
			y-=44;
		}
	}
	@Override
	public void pause() {
		scoreBoad.dispose();
	}

	@Override
	public void resume() {
		scoreBoad = new Texture(glGame, "dir/texture/scoreBard.png");
		scoreBoadRegion = new TextureRegion(scoreBoad, 0, 0, 320, 480);		
	}

	@Override
	public void dispose() {
	}

}
