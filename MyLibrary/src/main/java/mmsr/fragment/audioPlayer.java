package mmsr.fragment;

import java.io.IOException;

import android.media.MediaPlayer;

public class audioPlayer {
	MediaPlayer mp = new MediaPlayer();
	String fileName;
	
	audioPlayer(String fileName){
		this.fileName = fileName;
	}
	
	public void play() throws IllegalStateException, IOException{
		try{
			mp.setDataSource(fileName);
		}catch (IllegalArgumentException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
		
		try{
			mp.prepare();
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
		mp.start();
	}
}
