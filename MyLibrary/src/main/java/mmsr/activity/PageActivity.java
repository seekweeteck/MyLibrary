package mmsr.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.NavUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import mmsr.fragment.Book;
import mmsr.fragment.PageList;
import mmsr.fragment.PageXMLHandler;
import mmsr.fragment.R;
import mmsr.stat.DatabaseHandler;
import mmsr.stat.ReadRecord;

public class PageActivity extends Activity implements OnClickListener,
		TextToSpeech.OnInitListener {

	/** Called when the activity is first created. */

	private static final int MY_DATA_CHECK_CODE = 0;

	TextView textViewStatus;
	float xy; // Touch coordinate point

	String intID = null; // ID of story
	String name = null; // name of storybook

	File dir; // Directory of story file
	File picFile; // Directory of picture file

	// Text to speech
	private TextToSpeech enTTS;
	private TextToSpeech cnTTS;
	private TextToSpeech bmTTS;
	private TextToSpeech tmTTS;

	private boolean pref_en;
	private boolean pref_bm;
	private boolean pref_cn;
	private boolean pref_tm;

	// ID of new buttons
	int index = 0;
	int view_id; // ID of a button selected by user

	// Text size
	int font_size;

	// Screen dimension
	long width = 0;
	LinearLayout tl;

	// ImageView imagePage;
	private Book myBook = new Book(9, null);

	// Animation
	AnimationDrawable animation;
	// Boolean blnAnimationStarted=false;
	ArrayList<String> imageList = new ArrayList<String>();

	/** Create Object For SiteList Class */
	private PageList sitesList = null;

	// Create a reading record
	ReadRecord rec;
	Boolean complete = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// setContentView(R.layout.main);
		setContentView(R.layout.page);

		// To display the Up button
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Identify width of layout
		// Obtain screen width
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		width = (displaymetrics.widthPixels / 2);

		tl = (LinearLayout) findViewById(R.id.LinearLayoutText);

		// Retrieve ID and Name of a book
		Bundle extras = getIntent().getExtras();
		if (!extras.isEmpty()) {
			name = extras.getString("NAME");
			intID = extras.getString("ID");

			// Set Title of activity
			setTitle(name);

			// Assign to reading record
			Time today = new Time(Time.getCurrentTimezone());
			today.setToNow();

			rec = new ReadRecord();

			rec.setR_id(Integer.valueOf(intID));
			rec.setR_date(today.format("%Y/%m/%d"));
			rec.setR_time(today.format("%H:%M"));

		}

		// Page number
		textViewStatus = (TextView) findViewById(R.id.textViewStatus);

		// Load language selection
		loadPref();

		// To link story book text and illustration from XML file to UI
		createStory();

		// Display first page
		myBook.setCurrentPage(0);

		setPage();

		updatePageStatus();

		// Check Text-to-speech engine
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_my_fragment, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.glossary:
			Intent intent = new Intent(this, GlossaryActivity.class);
			intent.putExtra("ID", intID);
			startActivity(intent);
			return true;
		case R.id.comment:
			Intent intent2 = new Intent(this, FeedbackActivity.class);
			intent2.putExtra("ID", intID);
			startActivity(intent2);
			return true;
		case R.id.menu_prev:
			goPrevPage();
			return true;
		case R.id.menu_next:
			goNextPage();
			return true;
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return true;
		}

	}

	private void goNextPage() {
		if (myBook.getCurrentPage() < myBook.getTotalPage()) {
			myBook.gotoNext();
			setPage();
			updatePageStatus();
			if (myBook.getCurrentPage() == (myBook.getTotalPage() - 1)) {
				complete = true; // Read is completed	
			}
		}

	}

	private void goPrevPage() {
		myBook.gotoPrevious();
		setPage();
		updatePageStatus();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success
				enTTS = new TextToSpeech(this, this);
				cnTTS = new TextToSpeech(this, this);
				bmTTS = new TextToSpeech(this, this);
				tmTTS = new TextToSpeech(this, this);
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}

	}

	private void createStory() {
		try {

			/** Handling XML */
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();

			/** Create handler to handle XML Tags ( extends DefaultHandler ) */
			PageXMLHandler myXMLHandler = new PageXMLHandler();
			xr.setContentHandler(myXMLHandler);

			dir = Environment.getExternalStorageDirectory();

			File file = new File(dir, "/mmsr/" + intID + "/story.xml");

			FileInputStream raw = new FileInputStream(file);

			xr.parse(new InputSource(raw));

		} catch (Exception e) {
			System.out.println("XML Pasing Excpetion = " + e);
		}

		/** Get result from MyXMLHandler List Object */
		sitesList = PageXMLHandler.sitesList;

		List<String> picture = new ArrayList<String>();
		Map<String, List<String>> script = new HashMap<String, List<String>>();

		if (pref_en){
			List<String> scriptEN = new ArrayList<String>();
			for (int i = 0; i < sitesList.getEN().size(); i++) {
				scriptEN.add(sitesList.getEN().get(i));

				picture.add(sitesList.getPicture().get(i));
			}
			script.put("EN", scriptEN);
		}

		if (pref_bm){
			List<String> scriptBM = new ArrayList<String>();
			for (int i = 0; i < sitesList.getEN().size(); i++) {
				scriptBM.add(sitesList.getBM().get(i));
			}
			script.put("BM", scriptBM);
		}

		if (pref_cn){
			List<String> scriptCN = new ArrayList<String>();
			for (int i = 0; i < sitesList.getEN().size(); i++) {
				scriptCN.add(sitesList.getCN().get(i));
			}
			script.put("CN", scriptCN);
		}

		if (pref_tm){
			List<String> scriptTM = new ArrayList<String>();
			for (int i = 0; i < sitesList.getEN().size(); i++) {
				scriptTM.add(sitesList.getTM().get(i));
			}
			script.put("TM", scriptTM);
		}

		myBook.setPicture(picture);
		myBook.setScript(script);
	}

	private void updatePageStatus() {
		int currentPage = myBook.getCurrentPage() + 1;
		int totalPage = myBook.getTotalPage();

		textViewStatus.setText(currentPage + " / " + totalPage);
	}

	private void createReadRecord() {
		if (complete == true) {
			DatabaseHandler dbh = new DatabaseHandler(getApplicationContext());

			dbh.addRecord(rec);
			dbh.close();
		}
	}

	/*private void loadLastPage() {
		final ImageButton btnReturn = new ImageButton(this);

		LinearLayout tr = new LinearLayout(this);
		tr.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		btnReturn.setBackgroundResource(R.drawable.back);
		btnReturn.setOnClickListener(this);
		btnReturn.setContentDescription("RETURN");
		btnReturn.setId(index);

		tr.addView(btnReturn);

		final ImageView imagePage = (ImageView) this
				.findViewById(R.id.imageViewPic);

		imagePage.setImageDrawable(getResources().getDrawable(R.drawable.end));
		tl.removeAllViews(); // clear table layout
		tl.addView(tr);
		textViewStatus.setText("");
	}*/

	private void addText(String lang, String input) {
		// LinearLayout tl = (LinearLayout) findViewById(R.id.LinearLayoutText);
		int i;
		long currentWidth;

		// float width = 0; // To store width of buttons inserted into the
		// layout
		Paint paint = new Paint();

		final ImageButton btnPlay = new ImageButton(this);
		ImageView btnBlank = new ImageView(this);

		LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(42, 42);

		LinearLayout tr = new LinearLayout(this);

		tr.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		btnPlay.setBackgroundResource(R.drawable.play);
		btnPlay.setContentDescription(input);
		btnPlay.setOnClickListener(this);
		btnPlay.setId(index);

		// Set description of Play button
		btnPlay.setContentDescription(lang);

		tr.addView(btnPlay);
		currentWidth = 0;
		btnPlay.measure(0, 0);
		currentWidth += btnPlay.getMeasuredWidth();

		// Split a sentence into different words
		String del = " ";
		String[] output = input.split(del);

		for (i = 0; i < output.length; i++) {
			final Button chk = new Button(this);
			//chk.setBackgroundColor(Color.WHITE);
			//chk.setTextColor(Color.BLACK);
			chk.setTextSize(TypedValue.COMPLEX_UNIT_PX, font_size);
			chk.setGravity(Gravity.LEFT);
			chk.setText(output[i]);
			chk.setId(i + 1);
			chk.setBackgroundResource(R.drawable.button_pressed);//new 
			paint.setTextSize(font_size);
			
			chk.setContentDescription(lang + " " + output[i]);
			chk.setOnClickListener(this);
			tr.addView(chk);

			chk.measure(0, 0);

			currentWidth += chk.getMeasuredWidth();

			if (currentWidth > width) {
				tr.removeView(chk);
				tl.addView(tr);

				currentWidth = 42;

				tr = new LinearLayout(this);
				tr.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				btnBlank = new ImageView(this);
				btnBlank.setBackgroundResource(R.drawable.blank);
				btnBlank.setLayoutParams(parms);
				tr.addView(btnBlank);
				tr.addView(chk);
				chk.measure(0, 0);
				currentWidth += chk.getMeasuredWidth();
			}
		}
		tl.addView(tr);
	}

	// private void addText(String lang, String input) {
	//
	// createView cV = new createView(lang, input, font_size, tl, this);
	// cV.setView();
	// }


	public void onInit(int arg0) {
		new Thread(new Runnable() {
			public void run() {
				int status = enTTS.setLanguage(new Locale("en"));;
				if(status != TextToSpeech.ERROR) // initialization me error to nae ha
				{
					enTTS.setPitch(1.1f); // saw from internet
					enTTS.setSpeechRate(0.4f); // f denotes float, it actually type casts 0.5 to float
					enTTS.setLanguage(Locale.US);
				}
			}
		}).start();

		/*
		if (arg0 == TextToSpeech.SUCCESS) {
			int status = enTTS.setLanguage(new Locale("en"));

			if (status == TextToSpeech.SUCCESS) {
				enTTS.speak("", TextToSpeech.QUEUE_FLUSH, null);
			}else{
				Log.d("onInit", "Error EN TTS");
			}
			status = cnTTS.setLanguage(new Locale("zh_CN"));
			if (status == TextToSpeech.SUCCESS) {
				cnTTS.speak("华语", TextToSpeech.QUEUE_FLUSH, null);
			}else{
				Log.d("onInit", "Error CN TTS");
			}

			status = bmTTS.setLanguage(new Locale("id"));
			if (status == TextToSpeech.SUCCESS) {
				bmTTS.speak("Bahasa", TextToSpeech.QUEUE_FLUSH, null);
			}else{
				Log.d("onInit", "Error BM TTS");
			}

			status = tmTTS.setLanguage(new Locale("ta"));

			if (status == TextToSpeech.SUCCESS) {
				tmTTS.speak("தமிழ்", TextToSpeech.QUEUE_FLUSH, null);
			}else{
				Log.d("onInit", "Error TM TTS");
			}
		} else if (arg0 == TextToSpeech.ERROR) {
			Toast.makeText(this, "Error initializing Text-To-Speech engine",
					Toast.LENGTH_SHORT).show();
		}
		*/
	}
	public void onClick(View v) {

		view_id = v.getId(); // Get id of a view; button

		String words = (String) v.getContentDescription();
		String storyText;

		if (words.compareTo("EN") == 0) {
			storyText = myBook.getScriptByPage("EN", myBook.getCurrentPage());
			enTTS.speak(storyText, TextToSpeech.QUEUE_FLUSH, null);
			rec.addEN();
		} else if (words.compareTo("CN") == 0) {
			storyText = myBook.getScriptByPage("CN", myBook.getCurrentPage());
			cnTTS.speak(storyText, TextToSpeech.QUEUE_FLUSH, null);
			rec.addCN();
		} else if (words.compareTo("BM") == 0) {
			storyText = myBook.getScriptByPage("BM", myBook.getCurrentPage());
			bmTTS.speak(storyText, TextToSpeech.QUEUE_FLUSH, null);
			rec.addBM();
		} else if (words.compareTo("TM") == 0) {
			storyText = myBook.getScriptByPage("TM", myBook.getCurrentPage());
			tmTTS.speak(storyText, TextToSpeech.QUEUE_FLUSH, null);
			rec.addTM();
		} else if (words.compareTo("RETURN") == 0) {
			myBook.setCurrentPage(0);
			setPage();
			updatePageStatus();
		} else {
			String Lang = words.substring(0, 2);// Retrieve language code

			storyText = words.substring(3);

			HashMap<String, String> map = new HashMap<String, String>();
			map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, storyText);

			if (Lang.compareTo("EN") == 0) {
				enTTS.speak(storyText, TextToSpeech.QUEUE_FLUSH, null);
			} else if (Lang.compareTo("CN") == 0) {
				cnTTS.speak(storyText, TextToSpeech.QUEUE_FLUSH, null);
			} else if (Lang.compareTo("BM") == 0) {
				bmTTS.speak(storyText, TextToSpeech.QUEUE_FLUSH, null);
			} else if (Lang.compareTo("TM") == 0) {
				tmTTS.speak(storyText, TextToSpeech.QUEUE_FLUSH, null);
			}
		}
	}

	private void setPage() {
		// Split a sentence into different words
		String del = " ";
		String[] pictureList = myBook.getCurrentPicture().split(del);

		//Load animation image list
		imageList.clear();
		for (int i = 0; i < pictureList.length; i++) {
			imageList.add(i, pictureList[i] + ".png");
		}
		startAnimation(imageList);
		
		// This gets the first image
		picFile = new File(dir, "/mmsr/" + intID + "/pic/" + pictureList[0]
				+ ".png");
		Bitmap bitmap = BitmapFactory.decodeFile(picFile.toString());
		final ImageView imagePage = (ImageView) this
				.findViewById(R.id.imageViewPic);
		imagePage.setImageBitmap(bitmap);

		tl.removeAllViews(); // clear table layout

		if (pref_en)
			addText("EN", myBook.getScriptByPage("EN", myBook.getCurrentPage()));
		if (pref_bm)
			addText("BM", myBook.getScriptByPage("BM", myBook.getCurrentPage()));
		if (pref_cn)
			addText("CN", myBook.getScriptByPage("CN", myBook.getCurrentPage()));
		if (pref_tm)
			addText("TM", myBook.getScriptByPage("TM", myBook.getCurrentPage()));
	}

	// Animation Part
	class Starter implements Runnable {

		public void run() {
			animation.start();
		}
	}

	/*
	 * private void speakOut(String text) { if(!mTts.isSpeaking()){
	 * HashMap<String, String> map = new HashMap<String, String>();
	 * map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);
	 * mTts.speak(text, TextToSpeech.QUEUE_ADD, map); } }
	 */

	class ttsUtteraceListener extends UtteranceProgressListener {

		@Override
		public void onStart(String utteranceId) {
			runOnUiThread(new Runnable() {

				public void run() {
					View view;
					view = findViewById(view_id);
					view.setBackgroundColor(Color.YELLOW);
				}

			});

		}

		@Override
		public void onDone(String utteranceId) {
			runOnUiThread(new Runnable() {

				public void run() {
					View view;
					view = findViewById(view_id);
					view.setBackgroundColor(Color.WHITE);

				}

			});

		}

		@Override
		public void onError(String utteranceId) {
			// TODO Auto-generated method stub

		}

	}

	private void startAnimation(ArrayList<String> scene) {
		animation = new AnimationDrawable();

		File dir = Environment.getExternalStorageDirectory();
		File picFile;
		Bitmap bitmap;

		for (int i = 0; i < scene.size(); i++) {
			picFile = new File(dir, "/mmsr/" + intID + "/pic/" + scene.get(i));
			bitmap = BitmapFactory.decodeFile(picFile.toString());
			Drawable d = new BitmapDrawable(getResources(), bitmap);
			animation.addFrame(d, 500);
		}

		animation.setOneShot(false);
		final ImageView imagePage = (ImageView) findViewById(R.id.imageViewPic);
		imagePage.setImageDrawable(animation);
		imagePage.post(new Starter());
	}

	// Loading preference: language selection
	private void loadPref() {
		SharedPreferences mySharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		pref_en = mySharedPreferences.getBoolean("en", true);
		pref_bm = mySharedPreferences.getBoolean("bm", false);
		pref_cn = mySharedPreferences.getBoolean("cn", false);
		pref_tm = mySharedPreferences.getBoolean("tm", false);
		
		// Set Font size
		font_size = Integer.parseInt(mySharedPreferences.getString("text_size", "40"));
	}

	@Override
	protected void onDestroy() {
		// Stop all TTS
		if (enTTS != null) {
			enTTS.stop();
			enTTS.shutdown();
			bmTTS.stop();
			bmTTS.shutdown();
			cnTTS.stop();
			cnTTS.shutdown();
			tmTTS.stop();
			tmTTS.shutdown();
		}

		// Create a reading record
		createReadRecord();
		super.onDestroy();
	}

}