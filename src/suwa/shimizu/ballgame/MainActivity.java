package suwa.shimizu.ballgame;

import com.badlogic.androidgames.framework.Screen;
import com.badlogic.androidgames.framework.impl.AndroidGame;
import com.badlogic.androidgames.framework.impl.GLGame;


public class MainActivity extends GLGame {

	@Override
	public Screen getStartScreen() {
		// TODO 自動生成されたメソッド・スタブ
		return new BallGameScreen(this);
	}
}
