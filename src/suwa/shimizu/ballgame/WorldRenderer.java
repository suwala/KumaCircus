package suwa.shimizu.ballgame;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.androidgames.framework.gl.Animation;
import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.impl.GLGraphics;

public class WorldRenderer {
	static final float FRUSTUM_WIDTH = 15;
	static final float FRUSTUM_HEIGHT = 25;
	GLGraphics glGraphics;
	World world;
	Camera2D camera;
	SpriteBatcher batcher;
	
	public WorldRenderer(GLGraphics glGraphics,SpriteBatcher batcher,World world){
		this.glGraphics = glGraphics;
		this.world = world;
		this.camera = new Camera2D(glGraphics,FRUSTUM_WIDTH,FRUSTUM_HEIGHT);
		this.batcher = batcher;
		camera.zoom = 0.5f;
	}
	
	public void render(){
		camera.position.set(world.ball.position);
		if(camera.position.x < FRUSTUM_WIDTH*0.25f)
			camera.position.x = FRUSTUM_WIDTH*0.25f;
		if(camera.position.x > FRUSTUM_WIDTH*0.75f)
			camera.position.x = FRUSTUM_WIDTH*0.75f;
		
		if(camera.position.y < FRUSTUM_HEIGHT*0.25f)
			camera.position.y = FRUSTUM_HEIGHT*0.25f;
		if(camera.position.y > FRUSTUM_HEIGHT*0.75f)
			camera.position.y = FRUSTUM_HEIGHT*0.75f;
		
		camera.setViewportAndMatrices();
		renderBackground();
		renderObject();
	}
	
	public void renderObject(){
		GL10 gl = glGraphics.getGL();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		batcher.beginBatch(Assets.items);
		renderWalls();
		renderLion();
		renderHole();
		renderBall();
		renderKuma();
		batcher.endBatch();
		gl.glDisable(GL10.GL_BLEND);
	}
	private void renderBackground(){
		batcher.beginBatch(Assets.tile);
		batcher.drawSprite(FRUSTUM_WIDTH/2, FRUSTUM_HEIGHT/2, FRUSTUM_WIDTH, FRUSTUM_HEIGHT, Assets.tileRegion);
		batcher.endBatch();
	}
	private void renderWalls(){
		int len = world.walls.size();
		for(int i=0;i<len;i++){
			Wall wall = world.walls.get(i);
			batcher.drawSprite(wall.position.x, wall.position.y, 2,4, Assets.wall);
		}
		len=world.wallSides.size();
		for(int i=0;i<len;i++){
			WallSide wallSide = world.wallSides.get(i);
			batcher.drawSprite(wallSide.position.x, wallSide.position.y, 4, 2, Assets.wallSide);
		}
	}
	private void renderBall(){
		TextureRegion keyFrame;
		float side;
		float front;
		switch(world.ball.state){
		case Ball.NORMAL:
			if(Math.abs(world.ball.velocity.x) > Math.abs(world.ball.velocity.y)){
				keyFrame = Assets.ball[0].getKeyFrame(world.ball.time, Animation.ANIMATION_LOOPING);
				side = world.ball.velocity.x<0?-1:1;
				front = world.ball.velocity.y<0?-1:1;
			}else{
				keyFrame = Assets.ball[1].getKeyFrame(world.ball.time, Animation.ANIMATION_LOOPING);
				side = world.ball.velocity.x<0?-1:1;
				front = world.ball.velocity.y<0?-1:1;
			}
			break;
		case Ball.HIT:
			keyFrame = Assets.ball[2].getKeyFrame(world.ball.time, Animation.ANIMATION_NONLOOPING);
			side =1;
			front = 1;
			break;
		case Ball.HOLE_IN:
		default:
			keyFrame = Assets.ball[2].getKeyFrame(world.ball.time, Animation.ANIMATION_LOOPING);
			side=1;front=1;
				
		}
		batcher.drawSprite(world.ball.position.x, world.ball.position.y, side*1, front*1, keyFrame);
	}
	private void renderKuma(){
		TextureRegion keyFrame;
		switch(world.ball.state){
		case Ball.NORMAL:
		default:

			keyFrame = Assets.kuma.getKeyFrame(world.ball.time, Animation.ANIMATION_LOOPING);

			break;
		case Ball.HIT:
			keyFrame = Assets.kumaHit.getKeyFrame(world.ball.time, Animation.ANIMATION_LOOPING);
			break;
		case Ball.HOLE_IN:
			keyFrame = Assets.kumaHoleIN.getKeyFrame(world.ball.time, Animation.ANIMATION_LOOPING);
			break;
		}
		float side = world.ball.velocity.x<0?-1:1;
		batcher.drawSprite(world.ball.position.x, world.ball.position.y+0.5f, side*1, 2, keyFrame);
	}
	private void renderLion(){
		TextureRegion keyFrame;
		int len = world.lions.size();
		for(int i=0;i<len;i++){
			Lion lion = world.lions.get(i);
			keyFrame = Assets.lion.getKeyFrame(world.ball.time, Animation.ANIMATION_LOOPING);
			batcher.drawSprite(lion.position.x, lion.position.y, lion.velocity.x < 0?-1.5f:1.5f, 1, keyFrame);
		}
		
	}
	private void renderHole(){
		batcher.drawSprite(world.hole.position.x, world.hole.position.y, 2, 2, Assets.hole);
	}

}
