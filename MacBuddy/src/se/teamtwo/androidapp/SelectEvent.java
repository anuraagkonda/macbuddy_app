package se.teamtwo.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class SelectEvent extends Activity{
	
	Spinner gameSpinner;
	String[] macGames = { "-- Choose sport --", "Badminton", "Basketball",
			"Gym", "Pool", "Racquetball", "Table Tennis", "Soccer", "Swimming",
			"Volleyball", "Wall Climbing", "All" };
	ArrayAdapter<String> adapter;
	Bundle extras;
	Button submitButton;
	String username;
	String chosenGame;
	TextView textview_title=null;
	TextView textview_select=null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_sport);
		extras = getIntent().getExtras();
		if (extras != null) {
			   username = extras.getString("username");

			}

		// Spinner from XML
		textview_title=(TextView)findViewById(R.id.txt);
		textview_select=(TextView)findViewById(R.id.textView1);	
		submitButton = (Button)findViewById(R.id.submitbutton);
		gameSpinner = (Spinner) findViewById(R.id.spinner);

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
			submitButton.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					if(chosenGame==null){
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(SelectEvent.this, "Please select an event to proceed",
										Toast.LENGTH_SHORT).show();

							}
						});
					}
				
					else{
					 Intent i = new Intent(getApplicationContext(), CurrentActivity.class);
					 i.putExtra("username",username);
					 i.putExtra("chosenGame",chosenGame);
					 startActivity(i);
					}
				}	
				
	});
	}
}
