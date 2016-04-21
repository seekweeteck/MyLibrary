package mmsr.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import mmsr.fragment.R;
import mmsr.fragment.Title;
import mmsr.fragment.TitleAdapter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TitleActivity extends Activity {
	private ArrayList<Title> list;
	private ListView lv;
	private ArrayAdapter<Title> aa;
	private File fileDir;
	private File storyDir;
	private File dir;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_title);

		try {
			dir = Environment.getExternalStorageDirectory();
			setLibraryDir("library.xml"); // Assign library directory
			loadXML(); // Loading XML file into List

			// Attached books to List
			aa = new TitleAdapter(this, R.layout.item, list);
			lv = (ListView) findViewById(R.id.listViewLibrary);

			lv.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					
					TextView txTitle = (TextView) arg1
							.findViewById(R.id.textViewTitle);
					TextView txID = (TextView) arg1
							.findViewById(R.id.textViewID);

					String title = (String) txTitle.getText();
					String id = (String) txID.getText();

					Intent in = new Intent(getApplicationContext(),
							PageActivity.class);

					in.putExtra("NAME", title);
					in.putExtra("ID", id);
							
					startActivity(in);
				}
			});

			lv.setAdapter(aa);

		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent intent = new Intent();
			intent.setClass(this, SetPreferenceActivity.class);
			startActivityForResult(intent, 0);
			return true;
		case R.id.menu_statistic:
			Intent i = new Intent(this, StatisticActivity.class);
			startActivity(i);
			return true;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void setLibraryDir(String fileName) {
		fileDir = new File(dir, "/mmsr/" + fileName);
	}

	public File getLibraryDir() {
		return fileDir;
	}

	public void setFileDir(String storyID) {
		storyDir = new File(dir, "/mmsr/" + storyID + "/story.xml");
	}

	public File getStoryDir() {
		return storyDir;
	}

	private void loadXML() throws XmlPullParserException, IOException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();

		// Reading XML file from SD Card folder

		File file = getLibraryDir();

		if (!dir.exists()) {
			dir.mkdir();
		}

		FileInputStream raw = new FileInputStream(file);

		xpp.setInput(new InputStreamReader(raw));

		int eventType = xpp.getEventType();

		boolean done = false;
		Title currentTitle = null;

		while (eventType != XmlPullParser.END_DOCUMENT && !done) {
			String name = null;

			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				list = new ArrayList<Title>();
				break;
			case XmlPullParser.START_TAG:
				name = xpp.getName();
				if (name.equalsIgnoreCase("book")) {
					currentTitle = new Title();
				} else if (currentTitle != null) {
					if (name.equalsIgnoreCase("title")) {
						currentTitle.setTitle(xpp.nextText());
					} else if (name.equalsIgnoreCase("id")) {
						currentTitle.setID(xpp.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = xpp.getName();
				if (name.equalsIgnoreCase("book") && currentTitle != null) {
					list.add(currentTitle);
				} else if (name.equalsIgnoreCase("library"))
					done = true;
				break;
			}// END OF SWITCH
			eventType = xpp.next();
		}// END OF WHILE
	}

}