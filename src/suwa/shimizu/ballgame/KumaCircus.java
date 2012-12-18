package suwa.shimizu.ballgame;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.GLGame;

public class KumaCircus extends GLGame{
	boolean firstTimeCreate = true;
	@Override
	public void onPause() {
		super.onPause();
		Assets.music.stop();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		super.onSurfaceChanged(gl, width, height);
		if(firstTimeCreate){
			Assets.load(this);
			firstTimeCreate = false;			
		}else{
			Assets.reload();
		}
	}

	@Override
	public Screen getStartScreen() {
		return new MainMenuScreen(this);
	}
	
	

}
