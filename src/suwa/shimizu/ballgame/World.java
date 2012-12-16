package suwa.shimizu.ballgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.androidgames.framework.math.OverlapTester;

public class World {
	
	public interface WorldListener{
		public void hit();
		public void holeIN();
		public void lion();
		public void wall();
	}
	
	public static final float WORLD_WIDTH = 15f;
	public static final float WORLD_HEIGHT = 25f;
	
	public static final int WORLD_RUNNING = 0;
	public static final int WORLD_NEXT = 1;
	public static final int WORLD_GAMEOVER = 2;
	
	final int NUM_WALLS =10;
	final int NUM_WALLSIDES =10;
	final int NUM_LION = 20;
	
	public final Ball ball;
	public final Hole hole;
	public final Kuma kuma;
	public final List<Wall> walls;
	public final List<WallSide> wallSides;
	public final List<Lion> lions;
	public final GameObject tile;
	public final GameObject help;
	public final WorldListener worldListener;
	public final Random rand;
	
	public int score;
	public int state;
	public int level;
		
	public World(WorldListener worldListener){
		rand = new Random();
		this.ball = new Ball(0.2f,0.2f);
		this.kuma = new Kuma(0.2f,0.2f);
		this.hole = new Hole(rand.nextFloat()*WORLD_WIDTH*0.8f,WORLD_HEIGHT*0.85f);
		this.tile = new GameObject(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
		this.help = new GameObject(0, 0, WORLD_WIDTH*0.8f, WORLD_HEIGHT*0.8f);
		this.walls = new ArrayList<Wall>(NUM_WALLS);
		this.wallSides = new ArrayList<WallSide>(NUM_WALLSIDES);
		this.lions = new ArrayList<Lion>(NUM_LION);
		this.worldListener = worldListener;		
		this.level = 1;
		
		generateLevel();
				
		this.score = 0;
		this.state = WORLD_RUNNING;
		
	}
	
	public void generateLevel(){
		
		hole.position.set(rand.nextFloat()*WORLD_WIDTH*0.8f,WORLD_HEIGHT*0.85f);
		hole.circle.center.set(hole.position.x,hole.position.y);
		
		int num = NUM_LION - (NUM_LION-level);
		for(int i=0;i<num;i++){
			Lion lion = new Lion(0.3f+rand.nextFloat()*WORLD_WIDTH*0.7f, rand.nextFloat()*WORLD_HEIGHT);
			lions.add(lion);			
		}
		
		for(int i=0;i<5+level/2;i++){
			Wall wall;
			wall = new Wall(WORLD_WIDTH/i,rand.nextFloat()*WORLD_HEIGHT);
			walls.add(wall);			
		}		
		for(int i=0;i<5+level/2;i++){
			WallSide wall;
			wall = new WallSide(rand.nextFloat()*WORLD_WIDTH,WORLD_HEIGHT/i);
			wallSides.add(wall);	
		}
	}
	
	public void update(float deltaTime,float accelX,float accelY){
		updateBall(deltaTime,accelX,accelY);
		updateLion(deltaTime);
		if(ball.state == Ball.NORMAL){
			checkCollisions(deltaTime);
		}
			//checkGameOver();
	}
	
	private void updateBall(float deltaTime,float accelX,float accelY){
		ball.update(deltaTime);
		if(ball.state == Ball.NORMAL){
			ball.accel.set(-accelX,-accelY);			
			if(ball.position.x < 0){
				ball.position.x = 0;
				ball.accel.x *= 0.5f;
				ball.velocity.x = -ball.velocity.x*0.8f;
				worldListener.wall();
			}
			if(ball.position.x > BallGameScreen.WORLD_WIDTH){
				ball.position.x = BallGameScreen.WORLD_WIDTH;
				ball.accel.x *= 0.5f;
				ball.velocity.x = -ball.velocity.x*0.8f;
				worldListener.wall();
			}
			if(ball.position.y < 0){
				ball.position.y = 0;
				ball.accel.y *= 0.5f;
				ball.velocity.y = -ball.velocity.y*0.8f;
				worldListener.wall();
			}
			if(ball.position.y > BallGameScreen.WORLD_HEIGHT){
				ball.position.y = BallGameScreen.WORLD_HEIGHT;
				ball.accel.y *= 0.5f;
				ball.velocity.y = -ball.velocity.y*0.8f;
				worldListener.wall();
			}
		}if(ball.state == Ball.HIT){
			if(ball.time / 0.2f  > 8)
				reStart(deltaTime);
		}
		if(ball.state == Ball.HOLE_IN){
			if(ball.time / 0.2f  > 8){
				level++;
				generateLevel();
				reStart(deltaTime);
			}
		}
		
	}
	
	private void updateLion(float deltaTime){
		for(int i=0;i<lions.size();i++){
			Lion lion = lions.get(i);
			lion.update(deltaTime);
		}
	}
	
	private void checkCollisions(float deltaTime){
		checkHole();
		checkLions();
		checkWalls(deltaTime);		
	}
	
	private void checkHole(){
		if(OverlapTester.overlapCircles(ball.circle, hole.circle)){
			if(OverlapTester.overlapCircles(ball.circle, hole.minCircle))
				if(ball.velocity.len() < 0.5f){
					ball.state = Ball.HOLE_IN;
					ball.time = 0;
					ball.velocity.set(0,0);
					worldListener.holeIN();					
				}
			ball.velocity.mul(0.98f);
			ball.accel.mul(0.9f);
		}
	}
	
	private void checkLions(){
		int len = lions.size();
		for(int i=0;i<len;i++){
			Lion lion = lions.get(i);
			if(OverlapTester.overlapCircleRectangle(ball.circle, lion.bounds)){
				ball.state = Ball.HIT;
				ball.time=0;
				worldListener.hit();
			}
		}
	}
	
	private void checkWalls(float deltaTime){
		int len = walls.size();
		for(int i=0;i<len;i++){
			Wall wall = walls.get(i);
			if(OverlapTester.overlapCircleRectangle(ball.circle, wall.bounds)){
				worldListener.wall();
				ball.position.sub(ball.velocity.x*deltaTime,ball.velocity.y*deltaTime);
				ball.velocity.mul(-0.7f);
			}			
		}
		for(int i=0;i<len;i++){
			WallSide wall = wallSides.get(i);
			if(OverlapTester.overlapCircleRectangle(ball.circle, wall.bounds)){
				worldListener.wall();
				ball.position.sub(ball.velocity.x*deltaTime,ball.velocity.y*deltaTime);
				ball.velocity.mul(-0.7f);
			}			
		}
	}
	
	private void reStart(float deltaTime){
		
		ball.velocity.set(0,0);
		ball.accel.set(0,0);
		ball.position.set(0.2f,0.2f);
		ball.state = Ball.NORMAL;
		ball.update(deltaTime);
	}
	
	private void checkGameOver(){
		if(ball.state == Ball.HIT)
			state = WORLD_GAMEOVER;
	}

}
