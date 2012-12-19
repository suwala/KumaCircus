package com.badlogic.androidgames.framework.gl;

public class Font {
	public final Texture texture;
	public final int glyphWidth;
	public final int glyphHeight;
	public final TextureRegion[] glyphs = new TextureRegion[96];
	
	public Font(Texture texutre,int offsetX,int offsetY,
			int glYphsPerRow,int glyphWidth,int glyphHeight){
		this.texture = texutre;
		this.glyphWidth = glyphWidth;
		this.glyphHeight = glyphHeight;
		int x = offsetX;
		int y = offsetY;
		for(int i=0;i<96;i++){
			glyphs[i] = new TextureRegion(texutre, x, y, glyphWidth, glyphHeight);
			x += glyphWidth;
			if(x == offsetX + glYphsPerRow*glyphWidth){
				x = offsetX;
				y+=glyphHeight;
			}
		}
		
	}
	
	public void drawText(SpriteBatcher batcher,String text,float x,float y){
		int len = text.length();
		for(int i=0;i<len;i++){
			int c = text.charAt(i) - ' ';
			if(c < 0 || c > glyphs.length - 1)
				continue;
			TextureRegion glyph = glyphs[c-16];
			batcher.drawSprite(x, y, glyphWidth-16, glyphHeight-16, glyph);
			x += glyphWidth-16;
		}
	}
	

}
