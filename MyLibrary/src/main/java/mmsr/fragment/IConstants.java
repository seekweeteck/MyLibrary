package mmsr.fragment;

import android.graphics.Color;

public interface IConstants {

	public static final String MMSR_Properties = "storyBook.properties";
	
	public static final String FolderPath = "/sdcard/";
	//put file into /mnt/sdcard/MMSR/import
	public static final String ImportPath = "/MMSR/import";
	//grab file from /mnt/sdcard/MMSR/export
	public static final String ExportPath = "/MMSR/export";
	
	public static final int MY_DATA_CHECK_CODE = 200;
	
	public static final int ACTION_AUDIO_INTENT = 123456;
	public static final String AUDIO_PATH = "audioPath";
	public static final String ACTION_PLAY_AUDIO = "android.mystorybook.action.PLAY";
	public static final String ACTION_STOP_AUDIO = "android.mystorybook.action.STOP";
	public static final String ACTION_PAUSE_AUDIO = "android.mystorybook.action.PAUSE";
	public static final String ACTION_RESUME_AUDIO = "android.mystorybook.action.RESUME";
		
	public static final Integer DEFAULT_FONT_COLOR = Color.BLACK;
	
	public static final String LANG_BM = "BM";
	public static final String LANG_CN = "CN";
	public static final String LANG_EN = "EN";
	
	public static final String UPDATE_LYRIC = "U";
	public static final String STOP_LYRIC = "S";
	
	public static final int SHOW_SUBACTIVITY = 1;
	
	public static final String PREFIX_TEXTVIEW = "TextView";

}
