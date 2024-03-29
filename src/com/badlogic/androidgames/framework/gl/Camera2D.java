package com.badlogic.androidgames.framework.gl;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.androidgames.framework.impl.GLGraphics;
import com.badlogic.androidgames.framework.math.Vector2;

public class Camera2D {
	
	public final Vector2 position;
	public float zoom;
	public final float frusumWidth;
	public final float frustumHeight;
	final GLGraphics glGraphics;
	
	public Camera2D(GLGraphics glGraphics,float frustumWIdth,float frustumHeight){
		this.glGraphics = glGraphics;
		this.frusumWidth = frustumWIdth;
		this.frustumHeight = frustumHeight;
		this.position = new Vector2(frustumWIdth /2,frustumHeight /2);
		this.zoom = 1.0f;
	}
	
	public void setViewportAndMatrices(){
		GL10 gl = glGraphics.getGL();
		gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(position.x-frusumWidth*zoom /2,position.x + frusumWidth * zoom /2, 
					position.y-frustumHeight*zoom /2, position.y+frustumHeight*zoom/2, 1, -1);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	public void touchToWorld(Vector2 touch){
		touch.x = (touch.x / (float)glGraphics.getWidth())*frusumWidth*zoom;
		touch.y = (1 - touch.y / (float)glGraphics.getHeight())*frustumHeight * zoom;
		touch.add(position).sub(frusumWidth*zoom/2,frustumHeight*zoom/2);
	}

}
