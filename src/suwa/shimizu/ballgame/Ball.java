package suwa.shimizu.ballgame;

import com.badlogic.androidgames.framework.math.Circle;

public class Ball extends DynamicGameObject{

	public float time = 0;
	public int state;
	public final Circle circle;
	public static final float BALL_WIDTH = 1.0f;
	public static final float BALL_HEIGHT = 1.0f;
	public static final int NORMAL = 0;
	public static final int HIT = 1;
	public static final int HOLE_IN = 2;
	public static final int LIMIT_SPEED = 25;


	public Ball(float x, float y) {
		super(x, y, BALL_WIDTH, BALL_HEIGHT);
		state = NORMAL;
		this.circle = new Circle(x, y,BALL_WIDTH/2);
	}

	public void update(float deltaTime){
		float oldSpeed = velocity.len();

		if(state == NORMAL){
			if(velocity.len() < LIMIT_SPEED){
				velocity.add(accel.x*deltaTime,accel.y*deltaTime);
			}else{
				velocity.add(accel.x*deltaTime,accel.y*deltaTime);
				if(oldSpeed > velocity.len())
					velocity.add(accel.x*deltaTime,accel.y*deltaTime);
				else
					velocity.sub(accel.x*deltaTime,accel.y*deltaTime);
			}

			position.add(velocity.x * deltaTime,velocity.y * deltaTime);
			circle.center.set(this.position);
			bounds.lowerLeft.set(position).sub(bounds.width/2, bounds.height/2);			
		}

		time += deltaTime;
	}
	
	public void holeIN(){
		accel.set(0,0);
		velocity.set(0,0);
		state = HIT;
		time = 0;		
	}
	
	public void hitLion(){
		accel.set(0,0);
		velocity.set(0,0);
		state = HOLE_IN;
		time = 0;		
	}

}
