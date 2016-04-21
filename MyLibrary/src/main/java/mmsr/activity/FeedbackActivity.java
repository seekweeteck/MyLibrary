package mmsr.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import mmsr.fragment.R;
import mmsr.stat.DatabaseHandler;
import mmsr.stat.FeedbackRecord;
import mmsr.stat.ReadRecord;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Toast;

public class FeedbackActivity extends Activity {
	private String intID;
	private static final String LOG_TAG = "AudioRecordTest";
	private boolean mStartRecording = false;
	private boolean mStartPlaying = false;
	private MediaRecorder mRecorder;
	private MediaPlayer mPlayer = null;
	private String filename_comment;
	private File file_comment;
	private String fileName;
	private File filePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		// Retrieve ID and Name of a book
		Bundle extras = getIntent().getExtras();
		if (!extras.isEmpty()) {
			intID = (String) extras.getString("ID");
		}

		// Assign to reading record
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();

		fileName = today.format("%Y%m%d") + "_"
				+ today.format("%H%M");

		// Option external storage directory
		File dir = Environment.getExternalStorageDirectory();

		filePath = new File(dir + "/mmsr/feedback/" + intID);

		if (!filePath.exists()) {
			filePath.mkdir();
		}

		filename_comment = filePath + "/" + "comment" + fileName;
		file_comment = new File(filename_comment);

		Log.d("Feedback:", filename_comment);
		readComment();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.feedback_menu, menu);
		return true;
	}

	// Voice Recording Section
	// ------------------------------------------------------------------------
	public void recVoice(View v) {

		if (mStartRecording == false) {
			Toast.makeText(getApplicationContext(), "Start recording.",
					Toast.LENGTH_SHORT).show();
			startRecording();
		} else {
			Toast.makeText(getApplicationContext(), "Stop recording.",
					Toast.LENGTH_SHORT).show();
			stopRecording();
		}

		mStartRecording = !mStartRecording;
		changeRecIcon(v.getId());
	}

	private void startRecording() {

		/*
		 * if (file_comment.exists()) { file_comment.delete(); }
		 */
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(filename_comment);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	// Voice Playing section
	// ------------------------------------------------------------------------------
	public void playRec(View v) {
		if (file_comment.exists()) {

			if (mStartPlaying == false) {
				startPlaying();
			} else {
				stopPlaying();
			}

			mStartPlaying = !mStartPlaying;
			changeRecIcon(v.getId());
		} else {
			Toast.makeText(getApplicationContext(), "No voice comments.",
					Toast.LENGTH_SHORT).show();
		}

	}

	private void startPlaying() {
		Toast.makeText(getApplicationContext(), "Start playing.",
				Toast.LENGTH_SHORT).show();
		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mPlayer.setDataSource(filename_comment);
			mPlayer.prepare();
			mPlayer.start();
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					stopPlaying();
					mStartPlaying = !mStartPlaying;
					changeRecIcon(R.id.imageViewPlay);
				}
			});
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}

	}

	private void stopPlaying() {
		Toast.makeText(getApplicationContext(), "Stop playing.",
				Toast.LENGTH_SHORT).show();
		mPlayer.release();
		mPlayer = null;
	}

	// End of Voice Playing
	// -----------------------------------------------------------------------------------------------------
	private void changeRecIcon(int id) {
		ImageView buttonView = (ImageView) findViewById(id);
		Drawable buttonIcon;

		if (id == R.id.imageViewMic) {
			if (mStartRecording) {
				buttonIcon = getResources().getDrawable(R.drawable.ic_stop);
			} else {
				buttonIcon = getResources().getDrawable(R.drawable.ic_mic);
			}

		} else {
			if (mStartPlaying) {
				buttonIcon = getResources().getDrawable(R.drawable.ic_stop);
			} else {
				buttonIcon = getResources().getDrawable(R.drawable.play);
			}
		}

		buttonView.setImageDrawable(buttonIcon);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemSave:
			try {
				saveComment();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finish();
			return true;
		case R.id.itemCancel:
			DatabaseHandler dbh = new DatabaseHandler(getApplicationContext());

			dbh.deleteFeedback(Integer.parseInt(intID));
			finish();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public void saveComment() throws IOException {
		FeedbackRecord feedback = new FeedbackRecord();

		feedback.setId(Integer.parseInt(intID));

		float fltRate;
		RatingBar ratingBarRate;
		ratingBarRate = (RatingBar) findViewById(R.id.ratingBarRate);
		fltRate = ratingBarRate.getRating();
		feedback.setRating(fltRate);

		RadioButton radParent, radSibling, radOthers;

		radParent = (RadioButton) findViewById(R.id.radioParent);
		radSibling = (RadioButton) findViewById(R.id.radioSibling);
		radOthers = (RadioButton) findViewById(R.id.radioOthers);

		if (radParent.isChecked()) {
			feedback.setParent(1);
		} else if (radSibling.isChecked()) {
			feedback.setSibling(1);
		} else if (radOthers.isChecked()) {
			feedback.setOthers(1);
		}

		DatabaseHandler dbh = new DatabaseHandler(getApplicationContext());

		dbh.addFeedRecord(feedback);

		// Save record to a text file
		String outputData = "<id>" + feedback.getId() + "</id>" + "<rating>"
				+ feedback.getRating() + "</rating>" + "<parent>"
				+ feedback.getParent() + "</parent>" + "<sibling>"
				+ feedback.getSibling() + "</sibling>" + "<others>"
				+ feedback.getOthers() + "</others>";

		FileOutputStream fos = openFileOutput(filePath + "/" + "rating" + fileName,	Context.MODE_APPEND);
		fos.write(outputData.getBytes());
		fos.close();

		Toast.makeText(getApplicationContext(), "Comment saved.",
				Toast.LENGTH_SHORT).show();
	}

	public void readComment() {
		FeedbackRecord feedback = new FeedbackRecord();
		DatabaseHandler dbh = new DatabaseHandler(getApplicationContext());

		Log.d("Book ID", intID);

		feedback = dbh.getFeedback(Integer.parseInt(intID));

		if (feedback != null) {
			// Toast.makeText(getApplicationContext(), "Rating was :" +
			// feedback.getRating().toString(),
			// Toast.LENGTH_SHORT).show();
			RatingBar ratingBarRate = (RatingBar) findViewById(R.id.ratingBarRate);
			RadioButton radParent = (RadioButton) findViewById(R.id.radioParent);
			RadioButton radSibling = (RadioButton) findViewById(R.id.radioSibling);
			RadioButton radOther = (RadioButton) findViewById(R.id.radioOthers);

			ratingBarRate.setRating(feedback.getRating());

			if (feedback.getParent() == 1) {
				radParent.setChecked(true);
			} else if (feedback.getSibling() == 1) {
				radSibling.setChecked(true);
			} else if (feedback.getOthers() == 1) {
				radOther.setChecked(true);
			}
		} else {
			Toast.makeText(getApplicationContext(), "Please rate this story.",
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mRecorder != null) {
			mRecorder.release();
		}
		if (mPlayer != null) {
			mPlayer.release();
		}
		super.onDestroy();
	}
}
