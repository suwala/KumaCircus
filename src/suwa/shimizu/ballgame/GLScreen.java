package suwa.shimizu.ballgame;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.GLGame;
import com.badlogic.androidgames.framework.impl.GLGraphics;

public abstract class GLScreen extends Screen{

	protected final GLGraphics glGraphics;
	protected final GLGame glGame;
	
	public GLScreen(Game game) {
		super(game);
		glGame = (GLGame)game;
		glGraphics = ((GLGame)game).getGLGraphics();
	}
	

}
