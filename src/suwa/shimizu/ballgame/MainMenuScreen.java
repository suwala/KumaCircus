package suwa.shimizu.ballgame;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

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

public class MainMenuScreen extends GLScreen{

	Texture title;
	TextureRegion titleRegion;
	Camera2D camera;
	SpriteBatcher batcher;
	Rectangle startBounds;
	Rectangle scoreBounds;
	Rectangle helpBounds;
	Rectangle kumaAABounds;
	Rectangle soundBounds;
	Vector2 touchPoint;
	float time;
	
	public MainMenuScreen(Game game) {
		super(game);
		camera = new Camera2D(glGraphics, 320, 480);
		batcher = new SpriteBatcher(glGraphics, 100);
		//64,16*1 5 9
		startBounds = new Rectangle(64, 144, 160, 48);
		scoreBounds = new Rectangle(64,90,160,48);
		helpBounds = new Rectangle(64, 16, 160, 48);
		kumaAABounds = new Rectangle(32, 38, 32, 64);
		soundBounds = new Rectangle(276-64/2, 48-64/2, 64, 64);
		touchPoint = new Vector2();
		
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
				if(OverlapTester.pointInRectangle(startBounds, touchPoint))
					game.setScreen(new GameScreen(game));
				if(OverlapTester.pointInRectangle(scoreBounds, touchPoint))
					game.setScreen(new HiScoreScreen(game));
				if(OverlapTester.pointInRectangle(helpBounds, touchPoint))
					game.setScreen(new HelpScreen(game));
				if(OverlapTester.pointInRectangle(kumaAABounds, touchPoint))
					WorldRenderer.kumaAA = -WorldRenderer.kumaAA;
				if(OverlapTester.pointInRectangle(soundBounds, touchPoint)){
					Settings.soundEnabled  = !Settings.soundEnabled;
					if(Settings.soundEnabled)
						Assets.music.play();
					else
						Assets.music.pause();
				}
			}
		}
		time += deltaTime;
	}

	@Override
	public void present(float deltaTime) {
		GL10 gl = glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.setViewportAndMatrices();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		batcher.beginBatch(title);
		batcher.drawSprite(160, 240, 320, 480, titleRegion);
		batcher.endBatch();
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		batcher.beginBatch(Assets.items);
		TextureRegion keyFrame;
		
		keyFrame = Assets.ball[0].getKeyFrame(time, Animation.ANIMATION_LOOPING);
		batcher.drawSprite(32, 22, 32, 32, keyFrame);
		
		if(WorldRenderer.kumaAA == -1){
			keyFrame = Assets.kumaAA.getKeyFrame(time, Animation.ANIMATION_LOOPING);
			batcher.drawSprite(32, 54, 32, 64, keyFrame);
		}else{
			keyFrame = Assets.kuma.getKeyFrame(time, Animation.ANIMATION_LOOPING);
			batcher.drawSprite(32, 38, 32, 64, keyFrame);
		}
		
		batcher.drawSprite(320-44, 48, 64, 64, 
				Settings.soundEnabled?Assets.soundON:Assets.soundOFF);
		
		batcher.endBatch();
		
		gl.glDisable(GL10.GL_BLEND);
		
	}

	@Override
	public void pause() {
		title.dispose();
	}

	@Override
	public void resume() {
		title = new Texture(glGame, "dir/texture/title.png");
		titleRegion = new TextureRegion(title, 0, 0, 320, 480);
	}

	@Override
	public void dispose() {
	}

}
