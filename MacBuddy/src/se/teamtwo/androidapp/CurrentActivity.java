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
import android.widget.TextView;
import android.widget.Toast;


public class CurrentActivity extends Activity {
	int i=0;
    String username = "";
    
    int size=0;
    private long mLastClickTime = 0;
    String eventId;
    String userName, buttonValueUnjoin, eventIdForUnjoin, eventIdForJoin, buttonValueJoin;
    HttpResponse response;
    HttpPost httpPost;
	HttpClient httpClient;
	Button joinEventButton;
	Button unjoinEventButton;
	String chosengame;
	//Boolean flag = false;
	List<NameValuePair> nameValuePairs, passNewParam;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.current_events);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    username = extras.getString("username");
		    //buttonValueUnjoin = extras.getString("buttonValueUnjoin");
		    //eventIdForUnjoin = extras.getString("eventIdForUnjoin");
		    buttonValueJoin = extras.getString("buttonValueJoin");
		    eventIdForJoin = extras.getString("eventIdForJoin");
		    chosengame = extras.getString("chosenGame");
		    /*flag = extras.getBoolean("flag");
		    if(flag){
		    	joinEventButton.setText("Unjoin Event");
		    }*/
		}
		System.out.println("from bundle "+username);
		System.out.println("from bundle chosen game "+chosengame);
		Button prevEventButton = (Button) findViewById(R.id.prevEventButton);
		 joinEventButton = (Button) findViewById(R.id.joinEventButton);
		 unjoinEventButton = (Button)findViewById(R.id.unjoinEventButton);
		Button nextEventButton = (Button) findViewById(R.id.nextEventButton);
		runOnUiThread(new Runnable() {
		public void run() {
             
        }
        
    });
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		getData();
		
		joinEventButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
		            return;
		        }
			  mLastClickTime = SystemClock.elapsedRealtime();
            	
			  new Thread(new Runnable() {
					public void run() {
						joinUser();
					}
				}).start();
         }
  });	
		unjoinEventButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
		            return;
		        }
			  mLastClickTime = SystemClock.elapsedRealtime();
            	
			  new Thread(new Runnable() {
					public void run() {
						unjoinUser();
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
                            Toast.makeText(CurrentActivity.this,"No more invites", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CurrentActivity.this,"This is the latest invite", Toast.LENGTH_SHORT).show();
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
	HttpPost httppost = new HttpPost("http://omega.uta.edu/~akk1814/CurrentEventsActions/currentEventNew.php"); 
	nameValuePairs = new ArrayList<NameValuePair>(1);
	System.out.println("Chosengame.. "+chosengame);
	nameValuePairs.add(new BasicNameValuePair("chosengame", chosengame));
	//nameValuePairs.add(new BasicNameValuePair("username", username));
	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	//httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
	
	//parse json data
	try {
	String  eventName, eventTime, chosenGame; 
	
	
	JSONArray jArray = new JSONArray(result);
	
	size = jArray.length();
	JSONObject json = jArray.getJSONObject(i);
	userName = json.getString("dbusername");
	eventName = json.getString("dbeventname");
	eventTime = json.getString("dbeventtime");
	chosenGame = json.getString("dbchosengame");
	eventId = json.getString("dbeventid");
	
	
	
	System.out.println("from db values");
	System.out.println(userName + eventName + eventTime + chosenGame);
	
	String evenTimeArr[] = eventTime.split("\\s");
	System.out.println("evenTimeArr"+evenTimeArr);
	String time[] = evenTimeArr[1].split(":");
	System.out.println("time"+time);
	String date[] = evenTimeArr[0].split("-");
	System.out.println("date"+date);
	String timeDisp = time[0]+":"+time[1]+" ON "+date[1]+"-"+date[2]+"-"+date[0];
	System.out.println("timeDisp"+timeDisp);
	
	TextView username_textview = (TextView) findViewById(R.id.event_username_value);
	TextView eventname_textview = (TextView) findViewById(R.id.event_eventname_value);
	TextView eventtime_textview = (TextView) findViewById(R.id.event_eventtime_value);
	TextView chosengame_textview = (TextView) findViewById(R.id.event_chosengame_value);
	
	username_textview.setText(userName);
	eventname_textview.setText(eventName);
	eventtime_textview.setText(timeDisp);
	chosengame_textview.setText(chosenGame);
	 
	} catch (Exception e) {
	 
		Log.e("log_tag", "Error Parsing Data "+e.toString());
	}
	
		
		
	}	
	
	public void joinUser(){
		
		if(userName.equals(username)){
			
			System.out.println("loggedin"+username);
			System.out.println("event owner" + userName);
			
			runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(CurrentActivity.this,"You are owner of this event!", Toast.LENGTH_SHORT).show();
                }
            });
			
		}
		
		else{
			
		try{
	    HttpPost httpPostJoin=null;	
	    //HttpPost httpPostUnjoin=null;
		HttpClient httpClientJoin = new DefaultHttpClient();
		System.out.println("button text is "+joinEventButton.getText());
		
		 httpPostJoin = new HttpPost("http://omega.uta.edu/~akk1814/EventActions/joinEvent.php");
		
		System.out.println("from bundle in joinUser "+username);
		
		passNewParam = new ArrayList<NameValuePair>();
		passNewParam.add(new BasicNameValuePair("eventId", eventId)); 
		passNewParam.add(new BasicNameValuePair("username", username));
		httpPostJoin.setEntity(new UrlEncodedFormEntity(passNewParam));
		
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		// Execute the HTTP request
		
		 final String responseLogin = httpClientJoin.execute(httpPostJoin,
				responseHandler);
		
		
		//entity = responseLogin.getEntity();
		System.out.println("response after join"+responseLogin);
		if (responseLogin.equalsIgnoreCase("you have succesfully joined this event")) {
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(CurrentActivity.this, "You have joined the event successfully",
							Toast.LENGTH_SHORT).show();
					//joinEventButton.setText("Unjoin Event");
					Intent i = new Intent(getApplicationContext(), CurrentActivity.class);
					i.putExtra("username",username);
					i.putExtra("chosenGame",chosengame);
					//i.putExtra("buttonValueUnjoin","Unjoin Event");
					i.putExtra("eventIdForUnjoin",eventId);
					//i.putExtra("flag",true);
					startActivity(i);
					
				}
			});
		}
		else {
			CurrentActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					/*Intent i = new Intent(getApplicationContext(), CurrentActivity.class);
					i.putExtra("username",username);
					i.putExtra("chosenGame",chosengame);
					i.putExtra("eventIdForUnjoin",eventId);
					//i.putExtra("flag",true);
					startActivity(i);*/
					Toast.makeText(CurrentActivity.this, "You have already joined the event",
							Toast.LENGTH_SHORT).show();
				}
			});
			//responseLogin.getEntity().consumeContent();
		}
	} 	catch (Exception e) {
		e.printStackTrace();
	}
	}
	}
	public void unjoinUser(){
		if(userName.equals(username)){
			
			System.out.println("loggedin"+username);
			System.out.println("event owner" + userName);
			
			runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(CurrentActivity.this,"You are owner of this event!", Toast.LENGTH_SHORT).show();
                }
            });
			
		}
		else{
		try{
		HttpPost httpPostJoin=null;	
	    //HttpPost httpPostUnjoin=null;
		HttpClient httpClientJoin = new DefaultHttpClient();
		System.out.println("button text is "+joinEventButton.getText());
		
		 httpPostJoin = new HttpPost("http://omega.uta.edu/~akk1814/EventActions/unjoinEvent.php");
		
		System.out.println("from bundle in unjoinUser "+username);
		
		passNewParam = new ArrayList<NameValuePair>();
		passNewParam.add(new BasicNameValuePair("eventId", eventId)); 
		passNewParam.add(new BasicNameValuePair("username", username));
		httpPostJoin.setEntity(new UrlEncodedFormEntity(passNewParam));
		
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		// Execute the HTTP request
		
		 final String responseJoin = httpClientJoin.execute(httpPostJoin,
				responseHandler);
			
		 if(responseJoin.equalsIgnoreCase("You haven't joined this event yet"))
			{
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(CurrentActivity.this, "You haven't joined this event yet",
								Toast.LENGTH_SHORT).show();
						//joinUser();
						Intent i = new Intent(getApplicationContext(), CurrentActivity.class);
						i.putExtra("username",username);
						i.putExtra("chosenGame",chosengame);
						i.putExtra("eventIdForJoin",eventId);
						startActivity(i);

					}
				});
			}
		 else
		 {
	    if(responseJoin.equalsIgnoreCase("you have succesfully unjoined this event"))
		{
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(CurrentActivity.this, "You have unjoined the event successfully",
							Toast.LENGTH_SHORT).show();
					//joinUser();
					Intent i = new Intent(getApplicationContext(), CurrentActivity.class);
					i.putExtra("username",username);
					i.putExtra("chosenGame",chosengame);
					i.putExtra("eventIdForJoin",eventId);
					startActivity(i);

				}
			});
		}
		else {
			CurrentActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					/*Intent i = new Intent(getApplicationContext(), CurrentActivity.class);
					i.putExtra("username",username);
					i.putExtra("chosenGame",chosengame);
					i.putExtra("eventIdForJoin",eventId);
					//i.putExtra("flag",true);
					startActivity(i);*/
					Toast.makeText(CurrentActivity.this, "You haven't joined this event yet",
							Toast.LENGTH_SHORT).show();
				}
			});
			//responseLogin.getEntity().consumeContent();
		}
		 }
	} 	catch (Exception e) {
		e.printStackTrace();
	}
	}
	}
	public void showAlert(){
		
	    CurrentActivity.this.runOnUiThread(new Runnable() {
	        public void run() {	        	
	            AlertDialog.Builder builder = new AlertDialog.Builder(CurrentActivity.this);
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
		Intent i = new Intent(CurrentActivity.this, DashboardActivity.class);
		i.putExtra("username",username);
		startActivity(i);
	}
	
}
