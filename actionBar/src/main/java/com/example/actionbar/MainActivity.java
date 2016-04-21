package com.example.actionbar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView textViewMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textViewMessage = (TextView)findViewById(R.id.textViewMessage);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.show_message:
			showMessage();
			return true;
		case R.id.clear_message:
			clearMessage();
			return true;
		case R.id.showSub:
			showSub();
			return true;
			default:
				return super.onOptionsItemSelected(item);	
		}
	}

	private void showSub() {
		Intent intent = new Intent(this, SecondActivity.class);
		startActivity(intent);
	}

	private void clearMessage() {
		textViewMessage.setText("");
	}

	private void showMessage() {
		textViewMessage.setText("Hello");
	}

}
