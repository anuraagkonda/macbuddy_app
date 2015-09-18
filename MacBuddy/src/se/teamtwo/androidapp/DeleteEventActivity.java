package se.teamtwo.androidapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;

public class DeleteEventActivity extends Activity {
	int i=0;
    String username = "";
    
    int size=0;
    private long mLastClickTime = 0;
    String eventId;
    String userName, buttonValueUnjoin, eventIdForUnjoin, eventIdForJoin, buttonValueJoin, eventTimeEdit;
    HttpResponse response;
    HttpPost httpPost;
	HttpClient httpClient;
	EditText username_textview;
	EditText eventname_textview;
	EditText eventtime_textview;
	EditText chosengame_textview;
	
	Button deleteEventButton;
	Button editEventButton;
	JSONParser jsonParserReg = new JSONParser();
	String timeDisp = "";
	//Boolean flag = false;
	List<NameValuePair> nameValuePairs, passNewParam, passParam, passEventParams;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_event);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    username = extras.getString("username");
		    //buttonValueUnjoin = extras.getString("buttonValueUnjoin");
		    //eventIdForUnjoin = extras.getString("eventIdForUnjoin");
		    //buttonValueJoin = extras.getString("buttonValueJoin");
		    //eventIdForJoin = extras.getString("eventIdForJoin");
		    /*flag = extras.getBoolean("flag");
		    if(flag){
		    	joinEventButton.setText("Unjoin Event");
		    }*/
		}
		System.out.println("from bundle "+username);
		Button prevEventButton = (Button) findViewById(R.id.prevEventButton);
		 deleteEventButton = (Button) findViewById(R.id.delete_button);
		 editEventButton = (Button)findViewById(R.id.editEventButton);
		Button nextEventButton = (Button) findViewById(R.id.nextEventButton);
		runOnUiThread(new Runnable() {
		public void run() {
             
        }
        
    });
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		getData();
		
		deleteEventButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
		            return;
		        }
			  mLastClickTime = SystemClock.elapsedRealtime();
            	
			  new Thread(new Runnable() {
					public void run() {
						deleteevent();
					}
				}).start();
         }
  });	
		editEventButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
		            return;
		        }
			  mLastClickTime = SystemClock.elapsedRealtime();
            	
			  new Thread(new Runnable() {
					public void run() {
						editevent();
					}
				}).start();
         }
  });	
		
		nextEventButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	if(i>=size-1)
            	{
            		runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(DeleteEventActivity.this,"No more invites", Toast.LENGTH_SHORT).show();
                        }
                    });
            	}
            	else
            	{
            		i++;
            		getData();
            	}
            		            	
            }                        	
		});
		
		prevEventButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {            	
            	if(i<=0)
            	{
            		runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(DeleteEventActivity.this,"This is the latest invite", Toast.LENGTH_SHORT).show();
                        }
                    });
            	}
            	else
                {
            		i--;
            		getData();
                }
            }                        	
		});
		

	}
	
	public void getData(){
		System.out.println("inside getData");
	String result = "";
	InputStream isr = null;
	HttpResponse response = null;
	HttpEntity entity = null;
	StringBuilder sb = new StringBuilder();
	
	try{
		System.out.println("inside try");
	HttpClient httpclient = new DefaultHttpClient();
	HttpPost httppost = new HttpPost("http://omega.uta.edu/~akk1814/CurrentEventsActions/currentuserEvent.php");
	nameValuePairs = new ArrayList<NameValuePair>();

	nameValuePairs.add(new BasicNameValuePair("username", username));
	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	System.out.println("after http post");
	if(httppost != null)
	{
	response = httpclient.execute(httppost);
	entity = response.getEntity();
	System.out.println("get entity"+entity.toString());
	isr = entity.getContent();
	System.out.println("isr"+isr);
	}
	}
	catch(Exception e){
	Log.e("log_tag", "Error in http connection "+e.toString());
	
	}
	
	try{
		if(isr != null)
		{
	BufferedReader reader = new BufferedReader(new InputStreamReader(isr,"iso-8859-1"),8);
	
	String line = null;
	while ((line = reader.readLine()) != null) {
		System.out.println("line "+line);
	sb.append(line + "\n");
	
	}
	}
	isr.close();
	response.getEntity().consumeContent();
	result=sb.toString();
	
	}
	catch(Exception e){
	Log.e("log_tag", "Error converting result "+e.toString());
	}
	Log.d("JSON Parser", result);
	//parse json data
	try {
	String  eventName, eventTime, chosenGame; 
	
	//JSONObject jObject = null;
	//jObject = new JSONObject();
	//JSONArray jsonImageArray = jObject.getJSONArray("imageTarget");
	JSONArray jArray = new JSONArray(result);
	
	size = jArray.length();
	JSONObject json = jArray.getJSONObject(i);
	userName = json.getString("dbusername");
	eventName = json.getString("dbeventname");
	eventTime = json.getString("dbeventtime");
	eventTimeEdit = json.getString("dbeventtime");
	chosenGame = json.getString("dbchosengame");
	eventId = json.getString("dbeventid");
	
	
	
	System.out.println("from db values");
	System.out.println(userName + eventName + eventTime + chosenGame);
	
	 username_textview = (EditText) findViewById(R.id.event_username_value);
	 eventname_textview = (EditText) findViewById(R.id.event_eventname_value);
	 eventtime_textview = (EditText) findViewById(R.id.event_eventtime_value);
	 chosengame_textview = (EditText) findViewById(R.id.event_chosengame_value);
	
	 String evenTimeArr[] = eventTime.split("\\s");
		System.out.println("evenTimeArr"+evenTimeArr);
		String time[] = evenTimeArr[1].split(":");
		System.out.println("time"+time);
		String date[] = evenTimeArr[0].split("-");
		System.out.println("date"+date);
		timeDisp = time[0]+":"+time[1]+" ON "+date[1]+"-"+date[2]+"-"+date[0];
		System.out.println("timeDisp"+timeDisp);
	 
	 
	 
	username_textview.setText(userName);
	eventname_textview.setText(eventName);
	eventtime_textview.setText(timeDisp);
	chosengame_textview.setText(chosenGame);
	 
	} catch (Exception e) {
	 
		Log.e("log_tag", "Error Parsing Data "+e.toString());
	}
	
		
		
	}	
	
	public void deleteevent(){
			
		try{
	    HttpPost httpPostJoin=null;	
	    //HttpPost httpPostUnjoin=null;
		HttpClient httpClientJoin = new DefaultHttpClient();
		
		
		 httpPostJoin = new HttpPost("http://omega.uta.edu/~akk1814/EventActions/deleteEvent.php");
		
		System.out.println("from bundle in joinUser "+username);
		
		passNewParam = new ArrayList<NameValuePair>();
		passNewParam.add(new BasicNameValuePair("eventId", eventId)); 
		//passNewParam.add(new BasicNameValuePair("username", username));
		httpPostJoin.setEntity(new UrlEncodedFormEntity(passNewParam));
		
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		// Execute the HTTP request
		
		 final String responseLogin = httpClientJoin.execute(httpPostJoin,
				responseHandler);
		
		
		//entity = responseLogin.getEntity();
		System.out.println("response after join"+responseLogin);
		if (responseLogin.equalsIgnoreCase("you have succesfully deleted this event")) {
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(DeleteEventActivity.this, "You have deleted the event successfully",
							Toast.LENGTH_SHORT).show();
					//joinEventButton.setText("Unjoin Event");
					Intent i = new Intent(getApplicationContext(), DeleteEventActivity.class);
					i.putExtra("username",username);
					//i.putExtra("buttonValueUnjoin","Unjoin Event");
					i.putExtra("eventIdForUnjoin",eventId);
					//i.putExtra("flag",true);
					startActivity(i);
					
				}
			});
		}
		else {
			DeleteEventActivity.this.runOnUiThread(new Runnable() {
				public void run() {
				}
			});
			//responseLogin.getEntity().consumeContent();
		}
	} 	catch (Exception e) {
		e.printStackTrace();
	}
	}
	public void editevent(){
		String event_name = eventname_textview.getText().toString().trim();
		//String event_time = eventtime_textview.getText().toString().trim();
		String event_game = chosengame_textview.getText().toString().trim();
		
		System.out.println(" "+event_game+" "+eventTimeEdit);
		try{
		    HttpPost httpPostJoin=null;	
		    //HttpPost httpPostUnjoin=null;
			HttpClient httpClientJoin = new DefaultHttpClient();
			
			
			 httpPostJoin = new HttpPost("http://omega.uta.edu/~akk1814/EventActions/updateEvent.php");
			
			//System.out.println("from bundle in joinUser "+username);
			
			passParam = new ArrayList<NameValuePair>();
			List<NameValuePair> passRegParams = new ArrayList<NameValuePair>();
			passParam.add(new BasicNameValuePair("event_name", event_name));
			passParam.add(new BasicNameValuePair("event_time", eventTimeEdit));
			passParam.add(new BasicNameValuePair("event_game", event_game));
			passParam.add(new BasicNameValuePair("eventID", eventId));
			passParam.add(new BasicNameValuePair("username", username));
			//passNewParam.add(new BasicNameValuePair("username", username));
			httpPostJoin.setEntity(new UrlEncodedFormEntity(passParam));
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			// Execute the HTTP request
			
			/*String evenTimeArr[] = event_time.split("\\s");
			System.out.println("evenTimeArr"+evenTimeArr);
			String time[] = evenTimeArr[1].split(":");
			System.out.println("time"+time);
			String date[] = evenTimeArr[0].split("-");
			System.out.println("date"+date);
			String timeDisp = time[0]+":"+time[1]+" ON "+date[1]+"-"+date[2]+"-"+date[0];
			System.out.println("timeDisp"+timeDisp);*/
			
			
			 final String responseLogin = httpClientJoin.execute(httpPostJoin,
					responseHandler);
			    passRegParams.add(new BasicNameValuePair("eventname", event_name));
				passRegParams.add(new BasicNameValuePair("chosenGame", event_game));
				passRegParams.add(new BasicNameValuePair("eventtime", timeDisp));
				passRegParams.add(new BasicNameValuePair("username", username));
				passRegParams.add(new BasicNameValuePair("value", "- Event update"));
			 JSONObject jsonRegObj = jsonParserReg.makeHttpRequest(
						"http://omega.uta.edu/~akk1814/GCMDemo/index.php",
						"POST", passRegParams);
				//Log.d("Create Notification log ", jsonRegObj.toString());
			
			//entity = responseLogin.getEntity();
			//System.out.println("response after join"+responseLogin);
			if (responseLogin.equalsIgnoreCase("you have succesfully updated this event")) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(DeleteEventActivity.this, "You have updated the event successfully",
								Toast.LENGTH_SHORT).show();
						//joinEventButton.setText("Unjoin Event");
						Intent i = new Intent(getApplicationContext(), DeleteEventActivity.class);
						i.putExtra("username",username);
						//i.putExtra("buttonValueUnjoin","Unjoin Event");
						i.putExtra("eventIdForUnjoin",eventId);
						//i.putExtra("flag",true);
						startActivity(i);
						
					}
				});
			}
			
			else {
				DeleteEventActivity.this.runOnUiThread(new Runnable() {
					public void run() {
					}
				});
				//responseLogin.getEntity().consumeContent();
			}
		} 	catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showAlert(){
		
		DeleteEventActivity.this.runOnUiThread(new Runnable() {
	        public void run() {	        	
	            AlertDialog.Builder builder = new AlertDialog.Builder(DeleteEventActivity.this);
	            builder.setTitle("Error");
	            builder.setMessage("Error")  
	                   .setCancelable(false)
	                   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                       public void onClick(DialogInterface dialog, int id) {

	                       }
	                   });                     
	            AlertDialog alert = builder.create();
	            alert.show();               
	        }
	    });
        
    }
	
	public void onBackPressed()
	{
	    //finish();
		Intent i = new Intent(DeleteEventActivity.this, DashboardActivity.class);
		i.putExtra("username",username);
		startActivity(i);
	}
	
}


