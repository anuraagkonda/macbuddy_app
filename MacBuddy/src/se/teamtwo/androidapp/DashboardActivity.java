package se.teamtwo.androidapp;

import se.teamtwo.androidapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DashboardActivity extends Activity {

	Bundle extras;
	String username;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.dashboard);
		extras = getIntent().getExtras();
		if (extras != null) {
			   username = extras.getString("username");
			}
		Button createButton = (Button) findViewById(R.id.create_event_button);
		createButton.setOnClickListener(new OnClickListener() {
			// onclicklistener for create event
			public void onClick(View v) {
				Intent i = new Intent(DashboardActivity.this, CreateEventActivity.class);
				i.putExtra("username",username);
				startActivity(i);
				// navigates from the dashboard to create event screen
			}
		});
		Button currentButton = (Button) findViewById(R.id.current_event_display_button);
		currentButton.setOnClickListener(new OnClickListener() {
			// onclicklistener for create event
			public void onClick(View v) {
				Intent i = new Intent(DashboardActivity.this, SelectEvent.class);
				i.putExtra("username",username);
				startActivity(i);
				// navigates from the dashboard to create event screen
			}
		});
		Button deleteButton = (Button) findViewById(R.id.delete_button);
		deleteButton.setOnClickListener(new OnClickListener() {
			
			// onclicklistener for create event
			public void onClick(View v) {
				Intent i = new Intent(DashboardActivity.this, DeleteEventActivity.class);
				i.putExtra("username",username);
				startActivity(i);
				// navigates from the dashboard to create event screen
			}
		});
	
	Button logoutButton = (Button) findViewById(R.id.logout_button);
	logoutButton.setOnClickListener(new OnClickListener() {
		
		// onclicklistener for create event
		public void onClick(View v) {
			Intent i = new Intent(DashboardActivity.this, LoginActivity.class);
			//i.putExtra("username",username);
			startActivity(i);
			// navigates from the dashboard to create event screen
		}
	});
	
	Button user_profile_button = (Button) findViewById(R.id.user_profile_button);
	user_profile_button.setOnClickListener(new OnClickListener() {
	// onclicklistener for create event
	public void onClick(View v) {
	Intent i = new Intent(DashboardActivity.this, UserProfileActivity.class);
	i.putExtra("username",username);
	startActivity(i);
	// navigates from the dashboard to user profile screen
	}
	});
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// This adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed()
	{ 
		//finish();
		Intent i = new Intent(DashboardActivity.this, MainActivity.class);
		i.putExtra("username",username);
		startActivity(i);
	}
}