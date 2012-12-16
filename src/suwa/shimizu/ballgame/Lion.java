package suwa.shimizu.ballgame;

public class Lion extends DynamicGameObject{
	final float neutralX;
	public static final float WIDTH = 1.5f;
	public static final float HEIGHT = 0.8f;
	
	public Lion(float x, float y, float width, float height) {
		super(x, y, width, height);
		neutralX = x;
		velocity.x = 2f;
	}
	
	public Lion(float x,float y){
		super(x,y,WIDTH,HEIGHT);
		neutralX = x;
		velocity.x = 2f;
	}

	public void update(float deltaTime){
		position.add(velocity.x*deltaTime,velocity.y*deltaTime);
		bounds.lowerLeft.set(position.x,position.y);
		if(position.x < 0){
			position.x = 0;
			velocity.x = -velocity.x;
		}
		if(position.x > BallGameScreen.WORLD_WIDTH){
			position.x = BallGameScreen.WORLD_WIDTH;
			velocity.x = -velocity.x;
		}

		if(position.x > neutralX+5){
			position.x = neutralX+5;
			velocity.x = -velocity.x;
		}
		if(position.x < neutralX-5){
			position.x = neutralX -5;
			velocity.x = -velocity.x;
		}
	}

}

