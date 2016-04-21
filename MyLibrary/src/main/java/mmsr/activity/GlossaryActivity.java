package mmsr.activity;

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
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GlossaryActivity extends Activity implements OnClickListener,
		TextToSpeech.OnInitListener {

	private static final int MY_DATA_CHECK_CODE = 0;
	float xy; // Touch coordinate point

	private String intID = null; // ID of story
	// private String name = null; // name of storybook

	private File dir; // Directory of story file
	//private File picFile; // Directory of picture file

	private TextToSpeech enTTS;
	private TextToSpeech cnTTS;
	private TextToSpeech bmTTS;
	private TextToSpeech tmTTS;

	private boolean pref_en;
	private boolean pref_bm;
	private boolean pref_cn;
	private boolean pref_tm;

	// ID of new buttons
	//private int index = 0;

	// Text size
	private int[] textSize;
	private int font_size;

	// Screen dimension
	private int width = 0;
	private LinearLayout tl;

	// ImageView imagePage;
	private Book myBook = new Book(9, null);

	//private ArrayList<String> imageList = new ArrayList<String>();

	/** Create Object For SiteList Class */
	private PageList sitesList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glossary);
		setTitle(R.string.glosssary);

		// Create text to speech engine
		enTTS = new TextToSpeech(this, this);
		cnTTS = new TextToSpeech(this, this);
		bmTTS = new TextToSpeech(this, this);
		tmTTS = new TextToSpeech(this, this);

		// To display the Up button
	//	getActionBar().setDisplayHomeAsUpEnabled(true);

		// Identify width of layout
		// Obtain screen width
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		width = (int)(displaymetrics.widthPixels/4);

		loadPref();

		tl = (LinearLayout) findViewById(R.id.GlossaryText);

		// Set Font size
		SharedPreferences mySharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		font_size = Integer.parseInt(mySharedPreferences.getString("text_size", "18"));

		// Retrieve ID a book
		Bundle extras = getIntent().getExtras();
		if (!extras.isEmpty()) {
			intID = (String) extras.getString("ID");
			Log.d("Glossary", "intID =" + intID);
		}

		// To link story book text and illustration from XML file to UI
		createStory();

		myBook.setCurrentPage(0);

		setPage();
	}

	@Override
	public void onDestroy() {
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
		super.onDestroy();
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

			File file = new File(dir, "/mmsr/" + intID + "/glossary.xml");

			FileInputStream raw = new FileInputStream(file);

			xr.parse(new InputSource(raw));

		} catch (Exception e) {
			System.out.println("XML Pasing Excpetion = " + e);
		}

		/** Get result from MyXMLHandler List Object */
		sitesList = PageXMLHandler.sitesList;

		List<String> picture = new ArrayList<String>();
		Map<String, List<String>> script = new HashMap<String, List<String>>();

		List<String> scriptEN = new ArrayList<String>();
		List<String> scriptBM = new ArrayList<String>();
		List<String> scriptCN = new ArrayList<String>();
		List<String> scriptTM = new ArrayList<String>();

		for (int i = 0; i < sitesList.getEN().size(); i++) {
			scriptEN.add(sitesList.getEN().get(i));
			scriptBM.add(sitesList.getBM().get(i));
			scriptCN.add(sitesList.getCN().get(i));
			scriptTM.add(sitesList.getTM().get(i));
			picture.add(sitesList.getPicture().get(i));
		}

		script.put("EN", scriptEN);
		script.put("BM", scriptBM);
		script.put("CN", scriptCN);
		script.put("TM", scriptTM);

		myBook.setPicture(picture);
		myBook.setScript(script);
	}

	private void setPage() {
		LinearLayout header = new LinearLayout(this);
		header.setLayoutParams(new GridView.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		header.setOrientation(LinearLayout.HORIZONTAL);
		
		LinearLayout tlHeader = (LinearLayout)findViewById(R.id.LinearLayoutHeader);
		
		tlHeader.removeAllViews();
		tl.removeAllViews();

		if (pref_en) {
			final TextView tx = new TextView(this);
			tx.setText(R.string.en);
			tx.setTextSize(TypedValue.COMPLEX_UNIT_PX, font_size);
			tx.setGravity(Gravity.CENTER);
			tx.setWidth(width);
			header.addView(tx);
		}
		if (pref_bm) {
			final TextView tx = new TextView(this);
			tx.setText(R.string.bm);
			tx.setTextSize(TypedValue.COMPLEX_UNIT_PX, font_size);
			tx.setGravity(Gravity.CENTER);
			tx.setWidth(width);
			header.addView(tx);
		}
		if (pref_cn) {
			final TextView tx = new TextView(this);
			tx.setText(R.string.cn);
			tx.setTextSize(TypedValue.COMPLEX_UNIT_PX, font_size);
			tx.setGravity(Gravity.CENTER);
			tx.setWidth(width);
			header.addView(tx);
		}
		if (pref_tm) {
			final TextView tx = new TextView(this);
			tx.setText(R.string.tm);
			tx.setTextSize(TypedValue.COMPLEX_UNIT_PX, font_size);
			tx.setGravity(Gravity.CENTER);
			tx.setWidth(width);
			header.addView(tx);
		}

		tlHeader.addView(header);

		for (int i = 0; i < myBook.getTotalPage(); i++) {
			LinearLayout tr = new LinearLayout(this);
			tr.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

			if (pref_en) {
				final Button chk = new Button(this);

				chk.setTextSize(TypedValue.COMPLEX_UNIT_PX, font_size);
				chk.setGravity(Gravity.CENTER);
				chk.setWidth(width);
				chk.setText(myBook.getScriptByPage("EN", i));
				chk.setOnClickListener((OnClickListener) this);
				chk.setBackgroundResource(R.drawable.button_pressed);//new 
				//chk.setBackgroundColor(Color.WHITE);
				//chk.setTextColor(Color.BLACK);
				chk.setId(i + 1);
				
				chk.setContentDescription("EN"
						+ myBook.getScriptByPage("EN", i));
				tr.addView(chk);
			}
			if (pref_bm) {
				final Button chk = new Button(this);

				chk.setTextSize(TypedValue.COMPLEX_UNIT_PX, font_size);
				chk.setGravity(Gravity.CENTER);
				chk.setWidth(width);
				chk.setText(myBook.getScriptByPage("BM", i));
				chk.setOnClickListener((OnClickListener) this);
				chk.setBackgroundResource(R.drawable.button_pressed);//new 
				//chk.setBackgroundColor(Color.WHITE);
				//chk.setTextColor(Color.BLACK);
				chk.setId(i + 50);
				chk.setContentDescription("BM"
						+ myBook.getScriptByPage("BM", i));
				tr.addView(chk);
			}
			if (pref_cn) {
				final Button chk = new Button(this);

				chk.setTextSize(TypedValue.COMPLEX_UNIT_PX, font_size);
				chk.setGravity(Gravity.CENTER);
				chk.setWidth(width);
				chk.setText(myBook.getScriptByPage("CN", i));
				chk.setOnClickListener((OnClickListener) this);
				chk.setBackgroundResource(R.drawable.button_pressed);//new 
				//chk.setBackgroundColor(Color.WHITE);
				//chk.setTextColor(Color.BLACK);
				chk.setId(i + 100);
				chk.setContentDescription("CN"
						+ myBook.getScriptByPage("CN", i));
				tr.addView(chk);
			}
			if (pref_tm) {
				final Button chk = new Button(this);

				chk.setTextSize(TypedValue.COMPLEX_UNIT_PX, font_size);
				chk.setGravity(Gravity.CENTER);
				chk.setWidth(width);
				chk.setText(myBook.getScriptByPage("TM", i));
				chk.setOnClickListener((OnClickListener) this);
				chk.setBackgroundResource(R.drawable.button_pressed);//new 
				//chk.setBackgroundColor(Color.WHITE);
				//chk.setTextColor(Color.BLACK);
				chk.setId(i + 150);
				chk.setContentDescription("TM"
						+ myBook.getScriptByPage("TM", i));
				tr.addView(chk);
			}

			tl.addView(tr);

			myBook.gotoNext();
		}
	}

	private void loadPref() {
		SharedPreferences mySharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		pref_en = mySharedPreferences.getBoolean("en", false);
		pref_bm = mySharedPreferences.getBoolean("bm", false);
		pref_cn = mySharedPreferences.getBoolean("cn", false);
		pref_tm = mySharedPreferences.getBoolean("tm", false);
	}

	public void onClick(View v) {

		String words = (String) v.getContentDescription();

		String lang = words.substring(0, 2);// Retrieve language code
		String storyText = words.substring(2);
		
		if(lang.equals("EN")){
			enTTS.speak(storyText, TextToSpeech.QUEUE_FLUSH, null);
		}else if(lang.equals("BM")){
			bmTTS.speak(storyText, TextToSpeech.QUEUE_FLUSH, null);
		}else if(lang.equals("CN")){
			cnTTS.speak(storyText, TextToSpeech.QUEUE_FLUSH, null);
		}else if(lang.equals("TM")){
			tmTTS.speak(storyText, TextToSpeech.QUEUE_FLUSH, null);
		}
		
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
	
	public void onInit(int status) {

		if (status == TextToSpeech.SUCCESS) {			
			enTTS.setLanguage(Locale.ENGLISH);
			cnTTS.setLanguage(Locale.CHINESE);
			bmTTS.setLanguage(new Locale("id"));
			tmTTS.setLanguage(new Locale("ta"));
		} else {
			Log.e("TTS", "Initilization Failed!");
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
		
		return super.onOptionsItemSelected(item);
	}
}
