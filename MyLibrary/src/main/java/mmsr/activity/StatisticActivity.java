package mmsr.activity;

import java.util.ArrayList;

import mmsr.fragment.R;
import mmsr.stat.DatabaseHandler;
import mmsr.stat.ReadRecord;
import mmsr.stat.StatAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class StatisticActivity extends Activity {
	private ArrayList<ReadRecord> list;
	private ArrayAdapter<ReadRecord> aa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistic);

		// To display the Up button
		getActionBar().setDisplayHomeAsUpEnabled(true);

		DatabaseHandler dbh = new DatabaseHandler(this);

		list = (ArrayList<ReadRecord>) dbh.getAllContacts();

		// Attached books to List
		aa = new StatAdapter(this, R.layout.stat_item, list);
		ListView lv = (ListView) findViewById(R.id.listViewStat);

		if (list.size() > 0) {
			setTitle("Statistic Record Count:" + list.size());
		} else {
			Toast.makeText(this, "Statistic is empty.", Toast.LENGTH_LONG)
					.show();
		}

		lv.setAdapter(aa);
		dbh.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.statistic, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {
		case R.id.action_delete_all:

			// Create and show the dialog.
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle("Delete Statistics")
					.setMessage("Do you want to delete all records?")

					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									DatabaseHandler dbh = new DatabaseHandler(
											getApplicationContext());
									dbh.deleteAllContact();
									dbh.close();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, just close
									// the dialog box and do nothing
									dialog.cancel();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = builder.create();

			// show it
			alertDialog.show();
			return true;

		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;

		}

		return super.onOptionsItemSelected(item);
	}
}
