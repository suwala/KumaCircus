package suwa.shimizu.ballgame;

import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.Sound;
import com.badlogic.androidgames.framework.gl.Animation;
import com.badlogic.androidgames.framework.gl.Font;
import com.badlogic.androidgames.framework.gl.Texture;
import com.badlogic.androidgames.framework.gl.TextureRegion;
import com.badlogic.androidgames.framework.impl.GLGame;

public class Assets {
	public static Texture tile;
	public static TextureRegion tileRegion;
	public static Texture items;
	public static Animation[] ball;
	public static Animation kuma;
	public static Animation kumaHit;
	public static Animation lion;
	public static Animation kumaHoleIN;
	public static Animation kumaAA;
	public static TextureRegion hole;
	public static TextureRegion wall;
	public static TextureRegion wallSide;
	public static TextureRegion hiScoreRegion;
	public static TextureRegion soundON;
	public static TextureRegion soundOFF;
	public static TextureRegion paused;
	public static TextureRegion lv;
	
	public static Font font;
	
	public static Music music;
	public static Sound hit;
	public static Sound holeIN;
	public static Sound lionSe;
	public static Sound boyon;
	
	public static void load(GLGame game){
		tile = new Texture(game,"dir/texture/tile2.png");
		tileRegion = new TextureRegion(tile, 0, 0, 320, 480);
		
		items = new Texture(game,"dir/texture/items.png");
		ball = new Animation[3];
		ball[0] = new Animation(0.2f, new TextureRegion(items,0,0,32,32),
				new TextureRegion(items, 32, 0, 32, 32),
				new TextureRegion(items, 64, 0, 32, 32));
		ball[1] = new Animation(0.2f, new TextureRegion(items, 96, 0, 32, 32),
				new TextureRegion(items, 128, 0, 32, 32),
				new TextureRegion(items, 160, 0, 32, 32));
		int i = 512/13;
		ball[2] = new Animation(0.1f,new TextureRegion(items, i*0, 111, 39, 39),
				new TextureRegion(items, i*1, 111, 39, 39),
				new TextureRegion(items, i*2, 111, 39, 39),
				new TextureRegion(items, i*3, 111, 39, 39),
				new TextureRegion(items, i*4, 111, 39, 39),
				new TextureRegion(items, i*5, 111, 39, 39),
				new TextureRegion(items, i*6, 111, 39, 39),
				new TextureRegion(items, i*7, 111, 39, 39),
				new TextureRegion(items, i*8, 111, 39, 39),
				new TextureRegion(items, i*9, 111, 39, 39),
				new TextureRegion(items, i*10, 111, 39, 39),
				new TextureRegion(items, i*11, 111, 39, 39),
				new TextureRegion(items, i*12, 111, 39, 39),
				new TextureRegion(items, i*13, 111, 39, 39));
		
		kuma = new Animation(0.2f,new TextureRegion(items, 0, 32, 32, 64),
				new TextureRegion(items,32, 32, 32, 64));
		kumaHit = new Animation(0.2f,new TextureRegion(items, 224, 32, 32, 64),
				new TextureRegion(items,256, 32, 32, 64));
		kumaHoleIN = new Animation(0.2f,new TextureRegion(items, 288, 32, 32, 64),
				new TextureRegion(items,320, 32, 32, 64));
		//0,224
		kumaAA = new Animation(0.1f,new TextureRegion(items,0,224,64,80),
				new TextureRegion(items,64,224,64,80),
				new TextureRegion(items,128,224,64,80),
				new TextureRegion(items,192,224,64,80));
		
		lion = new Animation(0.2f, new TextureRegion(items, 256, 0, 32, 32),
				new TextureRegion(items, 256, 0, 32, 32),
				new TextureRegion(items, 288, 0, 32, 32));
		
		hole = new TextureRegion(items, 64, 32, 64, 64);
		wall = new TextureRegion(items, 192, 0, 32, 64);
		wallSide = new TextureRegion(items, 128, 32, 64, 32);
		
		hiScoreRegion = new TextureRegion(items, 0, 304, 272, 80);
		soundON = new TextureRegion(items, 0, 144, 32, 32);
		soundOFF = new TextureRegion(items, 32, 144, 32, 32);
		paused = new TextureRegion(items, 64, 144, 48*6, 48);
		lv = new TextureRegion(items, 320, 192, 64, 32);
		
		font = new Font(items, 0, 192, 10, 32, 32);
		
		
		music = game.getAudio().newMusic("dir/sound/bgm.mp3");
		music.setLooping(true);
		music.setVolume(0.5f);
		if(Settings.soundEnabled)
			music.play();
		
		boyon = game.getAudio().newSound("dir/sound/boyon.wav");
		holeIN = game.getAudio().newSound("dir/sound/hakusyu.mp3");		
		
	}
	
	public static void reload(){
		tile.reload();
		items.reload();
		music.play();
	}
	
	public static void playSound(Sound sound){
		sound.play(1);
	}
	

}
