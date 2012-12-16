package suwa.shimizu.ballgame;

import com.badlogic.androidgames.framework.math.Circle;

public class Hole extends GameObject{
	public final Circle circle;
	public final Circle minCircle;
	public static final float WIDTH = 1.5f;
	public static final float HEIGHT = 1.5f;
	public Hole(float x, float y, float width, float height) {
		super(x, y, width, height);
		this.circle = new Circle(x, y, width*0.6f/2);
		this.minCircle = new Circle(x,y,width*0.3f/2);
	}
	
	public Hole(float x,float y){
		super(x, y,WIDTH, HEIGHT);
		this.circle = new Circle(x, y, WIDTH/2);
		this.minCircle = new Circle(x,y,WIDTH*0.3f/2);
		
	}

}
