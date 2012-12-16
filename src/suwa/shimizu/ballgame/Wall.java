package suwa.shimizu.ballgame;


public class Wall extends GameObject{

	public static final float WIDTH = 2.0f;
	public static final float HEIGHT = 4.0f;
	public float angle;
	
	public Wall(float x, float y, float width, float height) {
		super(x, y, width, height);
		this.angle = 0;
	}
	public Wall(float x,float y){
		super(x,y,WIDTH,HEIGHT);
		this.angle = 0;
	}

}
