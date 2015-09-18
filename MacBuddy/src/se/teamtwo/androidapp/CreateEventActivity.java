package se.teamtwo.androidapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

// This class focuses on creating game invites 
public class CreateEventActivity extends Activity {

	// Add the preferences
	//public static final String PREFS_NAME = "EventCreationPrefs";

	// Variable declarations
	EditText edittext_event_name, edittext_event_limit;
	DatePicker datePicker_event_date;
	TimePicker timePicker_event_time;
	Button createButton;
	String event_schedule;
	String string_event_name, string_event_limit;
	String username;
	//String limit;
	ProgressDialog progressDialogEvent;
	int eventCheck = 0;
	JSONParser jsonParserEvent = new JSONParser();
	JSONParser jsonParserReg = new JSONParser();
	Button submit;
	Spinner gameSpinner;
	String[] macGames = { "-- Choose sport --", "Badminton", "Basketball",
			"Gym", "Pool", "Racquetball", "Table Tennis", "Soccer", "Swimming",
			"Volleyball", "Wall Climbing" };
	private static final String EVENT_SUCCESS = "success";
	String chosenGame = "";
	ArrayAdapter<String> adapter;
	Bundle extras;
	int success = 0;
	
	// Overridden method onCreate
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_event);
		extras = getIntent().getExtras();
		if (extras != null) {
			   username = extras.getString("username");

			}

		// Spinner from XML
		gameSpinner = (Spinner) findViewById(R.id.spinner);

		// Get the text box value
		edittext_event_name = (EditText) findViewById(R.id.et_event_name);

		// Get the selected date from calendar
		datePicker_event_date = (DatePicker) findViewById(R.id.create_event_date);

		// Get the selected time from Calendar
		timePicker_event_time = (TimePicker) findViewById(R.id.create_event_time);
		
		edittext_event_limit = (EditText)findViewById(R.id.et_event_limit);

		// Link to button
		createButton = (Button) findViewById(R.id.createEventButton);

		// Use array adapter to display spinner and get the chosen value
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, macGames);
		gameSpinner.setAdapter(adapter);
		gameSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					// Get the selected spinner value
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						int position = gameSpinner.getSelectedItemPosition();
						if (macGames[+position] != "-- Choose sport --") {
							
							/*Toast.makeText(getApplicationContext(),
									"You have selected " + macGames[+position],
									Toast.LENGTH_LONG).show();*/
							chosenGame = macGames[+position];
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		// Add a listner to the create event button
		createButton.setOnClickListener(new OnClickListener() {

			// On click of create event button call the class eventCreation
			@Override
			public void onClick(View v) {
				String eventName = edittext_event_name.getText().toString().trim();
				String eventLimit = edittext_event_limit.getText().toString().trim();
				// Check for server connectivity issues
				if (!checkServer(CreateEventActivity.this)) {
					/*Toast.makeText(CreateEventActivity.this, "Server issue exists",
							Toast.LENGTH_LONG).show();*/
					return;
				}
				if (chosenGame.equals("")) {
					Toast.makeText(CreateEventActivity.this, "Please select a game to invite",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (eventName.equals("")) {
					Toast.makeText(CreateEventActivity.this, "Please enter an event name",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (eventLimit.equals("")) {
					Toast.makeText(CreateEventActivity.this, "Please set the event limit",
							Toast.LENGTH_LONG).show();
					return;
				}
				
				// Call the execute method for AsyncTask
				new eventCreation().execute();
			}
			

			// Method to check connection errors
			private boolean checkServer(Context mContext) {
				ConnectivityManager cm = (ConnectivityManager) mContext
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = cm.getActiveNetworkInfo();
				if (netInfo != null && netInfo.isConnectedOrConnecting()) {
					return true;
				}
				return false;
			}
		});
	}

	// Class eventCreaton inherits AsyncTask
	class eventCreation extends AsyncTask<String, String, String> {

		// Functionality to be performed before execution
		protected void onPreExecute() {
			super.onPreExecute();

			// Assign the progress dialog values
			progressDialogEvent = new ProgressDialog(CreateEventActivity.this);
			progressDialogEvent.setMessage("Creating Event");
			progressDialogEvent.setIndeterminate(false);
			progressDialogEvent.setCancelable(true);
			progressDialogEvent.show();
		}

		// Method to perform functionality on background of the execution
		@Override
		protected String doInBackground(String... args) {

			// Prepare the list of parameters to be passed as part of HTTP request
			List<NameValuePair> passEventParams = new ArrayList<NameValuePair>();
			List<NameValuePair> passRegParams = new ArrayList<NameValuePair>();
			// Assign the edit text value to a string
			string_event_name = edittext_event_name.getText().toString().trim();
			string_event_limit = edittext_event_limit.getText().toString().trim();
			System.out.println("string_event_limit "+string_event_limit);
			// Get the date and time details to bind it to a calendar object
			Calendar cal = Calendar.getInstance();
			cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH),
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0);

			// Fetch year from the selected date
			Integer dp_Year = datePicker_event_date.getYear();

			// Fetch month from the selected date
			Integer dp_Month = datePicker_event_date.getMonth() + 1;

			// Fetch date from the selected date
			Integer dp_Date = datePicker_event_date.getDayOfMonth();

			// Get the hour and minute from time picker
			Integer tp_Hour = timePicker_event_time.getCurrentHour();
			Integer tp_Minute = timePicker_event_time.getCurrentMinute();

			StringBuilder sb = new StringBuilder();

			// Prepare the string builder to support the DATETIME format to be written into the Database table
			sb.append(dp_Year.toString()).append("-")
					.append(dp_Month.toString()).append("-")
					.append(dp_Date.toString()).append(" ")
					.append(tp_Hour.toString()).append(":")
					.append(tp_Minute.toString()).append(":00");
			
			
			System.out.println("after date "+ sb);
			// Now the schedule is converted to a string and passed with the request
			event_schedule = sb.toString();

			// Bind the parameters
			passEventParams.add(new BasicNameValuePair("eventname",
					string_event_name));
			passEventParams.add(new BasicNameValuePair("eventtime",
					event_schedule));
			passEventParams
					.add(new BasicNameValuePair("chosenGame", chosenGame));
			passEventParams
			.add(new BasicNameValuePair("username", username));
			
			passEventParams
			.add(new BasicNameValuePair("eventlimit", string_event_limit));

			System.out.println("after binding params");
			// Send the HTTP request and parse the response through JSON
			JSONObject jsonEventObj = jsonParserEvent.makeHttpRequest(
					"http://omega.uta.edu/~akk1814/EventActions/createEvent.php",
					"POST", passEventParams);
			Log.d("Create Response", jsonEventObj.toString());

			System.out.println("after json response");
			
			
			String evenTimeArr[] = event_schedule.split("\\s");
			System.out.println("evenTimeArr"+evenTimeArr);
			String time[] = evenTimeArr[1].split(":");
			System.out.println("time"+time);
			String date[] = evenTimeArr[0].split("-");
			System.out.println("date"+date);
			String timeDisp = time[0]+":"+time[1]+" ON "+date[1]+"-"+date[2]+"-"+date[0];
			System.out.println("timeDisp"+timeDisp);
			
			passRegParams.add(new BasicNameValuePair("eventName", string_event_name));
			passRegParams.add(new BasicNameValuePair("chosenGame", chosenGame));
			passRegParams.add(new BasicNameValuePair("eventtime", timeDisp));
			passRegParams.add(new BasicNameValuePair("username", username));
			
			
			System.out.println("after binding params");
			// Send the HTTP request and parse the response through JSON
			JSONObject jsonRegObj = jsonParserReg.makeHttpRequest(
					"http://omega.uta.edu/~akk1814/GCMDemo/index.php",
					"POST", passRegParams);
			//Log.d("Create Notification log ", jsonRegObj.toString());

			System.out.println("after json response");
			
			// If the JSON response returned SUCCESS then proceed with functionality
			try {
				if (jsonEventObj != null) {
					// Get the status of from JSON
					success = jsonEventObj.getInt(EVENT_SUCCESS);

					System.out.println("success is "+success);
					// Return values based on JSON
					if (success == 1) {
						return "1";
					} else{
						return "0";
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		// Functionality on completion
		protected void onPostExecute(String result) {

			// Dismiss the progress dialog
			if (progressDialogEvent != null)
				progressDialogEvent.dismiss();

			if(success == 0)
			{
				Toast.makeText(
						getApplicationContext(),
						"Please enter all the mandatory fields",
						Toast.LENGTH_SHORT).show();
			}
			// start activity on success
			if (success == 1) {
				System.out.println("before intent");
				
				
				
				
				Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
				i.putExtra("username",username);
				startActivity(i);
				System.out.println("after intent");
				finish();
			} 
		}
	}
	public void onBackPressed()
	{
	    finish();
	}
}