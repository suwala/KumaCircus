package suwa.shimizu.ballgame;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.gl.Texture;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

public class HelpScreen extends GLScreen{

	Texture help;
	TextureRegion helpRegion;
	Texture help2;
	TextureRegion help2Region;
	Camera2D camera;
	SpriteBatcher batcher;
	Rectangle arrow;
	Vector2 touchPoint;
	int page;
	
	public HelpScreen(Game game) {
		super(game);
		camera = new Camera2D(glGraphics,320,480);
		batcher = new SpriteBatcher(glGraphics, 100);
		
	}

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		int len = touchEvents.size();
		for(int i=0;i<len;i++){
			TouchEvent event = touchEvents.get(i);
			if(event.type == TouchEvent.TOUCH_UP){
				page++;
			}
			if(page > 1)
				game.setScreen(new MainMenuScreen(game));
		}
	}

	@Override
	public void present(float deltaTime) {
		GL10 gl = glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.setViewportAndMatrices();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		if(page == 0){
			batcher.beginBatch(help);
			batcher.drawSprite(160, 240, 320, 480, helpRegion);
		}else{
			batcher.beginBatch(help2);
			batcher.drawSprite(160, 240, 320, 480, help2Region);
		}
		batcher.endBatch();
		
	}

	@Override
	public void pause() {
		help.dispose();
		help2.dispose();
		
	}

	@Override
	public void resume() {
		help = new Texture(glGame, "dir/texture/help1.png");
		helpRegion = new TextureRegion(help, 0, 0, 256, 384);
		help2 = new Texture(glGame, "dir/texture/help.png");
		help2Region = new TextureRegion(help, 0, 0, 256, 384);
	}

	@Override
	public void dispose() {
	}

}
