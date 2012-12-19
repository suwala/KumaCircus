package suwa.shimizu.ballgame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.badlogic.androidgames.framework.FileIO;

public class Settings {

	public static boolean soundEnabled = true;
	public final static int[] highscores = new int[] {10000,7000,5000,3000,1000};
	public final static int[] levels = new int[]{1,1,1,1,1};
	public final static String file = ".kumacircus";

	public static void load(FileIO files){
		BufferedReader in = null;
		try{
			in = new BufferedReader(new InputStreamReader(files.readFile(file)));
			soundEnabled = Boolean.parseBoolean(in.readLine());
			for(int i=0;i<5;i++){
				highscores[i] = Integer.parseInt(in.readLine());
				levels[i] = Integer.parseInt(in.readLine());
			}
		}catch (IOException e){

		}catch (NumberFormatException e){

		}finally{
			try{
				if(in != null)
					in.close();
			}catch (IOException e){
			}
		}
	}

	public static void save(FileIO files){
		BufferedWriter out = null;
		try{
			out = new BufferedWriter(new OutputStreamWriter(files.writeFile(file)));
			out.write(Boolean.toString(soundEnabled));
			out.write("\n");
			for(int i=0;i<5;i++){
				out.write(Integer.toString(highscores[i]));
				out.write("\n");
				out.write(Integer.toString(levels[i]));
				out.write("\n");
			}
		}catch (IOException e){
		}finally{
			try{
				if(out != null)
					out.close();
			}catch(IOException e){
			}
		}
	}

	public static void addScore(int score,int level){
		for(int i=0;i<5;i++){
			if(highscores[i]<score){
				for(int j=4;j>i;j--)
					highscores[j] = highscores[j-1];
				highscores[i] = score;
				levels[i]=level;
				break;
			}
		}
	}
}

