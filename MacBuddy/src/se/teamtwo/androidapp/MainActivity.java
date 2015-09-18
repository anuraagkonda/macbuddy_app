package se.teamtwo.androidapp;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import se.teamtwo.androidapp.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;

import android.widget.Button;

import android.view.View.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class MainActivity extends Activity {
	// This class displays the start screen
	
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	 public static final String EXTRA_MESSAGE = "message";
	 public static final String PROPERTY_REG_ID = "registration_id";
	 private static final String PROPERTY_APP_VERSION = "appVersion";
	 private static final String TAG = "GCMRelated";
	 GoogleCloudMessaging gcm;
	 AtomicInteger msgId = new AtomicInteger();
	 String regid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_screen);
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			// onclicklistenser for the login button
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(i);
				// navigates from start screen to login screen
			}
		});
		final Button buttonReg = (Button) findViewById(R.id.button2);
		if (checkPlayServices()) {
		      gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
		            regid = getRegistrationId(getApplicationContext());
		            if(!regid.isEmpty()){
		             button.setEnabled(false);
		            }else{
		             button.setEnabled(true);
		            }
		  }
		buttonReg.setOnClickListener(new OnClickListener() {
			// onclicklistener for registration
			public void onClick(View v) {
				if (checkPlayServices()) {
			        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
			              regid = getRegistrationId(getApplicationContext());
			               
			              if (regid.isEmpty()) {
			            	  buttonReg.setEnabled(false);
			                  //new RegId(getApplicationContext(), gcm, getAppVersion(getApplicationContext()));
			                  Intent i = new Intent(MainActivity.this, RegActivity.class);
			                  i.putExtra("regid",regid);
			  				startActivity(i);
			  				// navigates from start screen to registration screen
			              }else{
			               //Toast.makeText(getApplicationContext(), "Device already Registered", Toast.LENGTH_SHORT).show();
			              }
			       } else {
			              Log.i(TAG, "No valid Google Play Services APK found.");
			       }
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// this adds items to the action bar if it is present.
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
	
	
	
	private boolean checkPlayServices() {
	     int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	     if (resultCode != ConnectionResult.SUCCESS) {
	         if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	             GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                     PLAY_SERVICES_RESOLUTION_REQUEST).show();
	         } else {
	             Log.i(TAG, "This device is not supported.");
	             finish();
	         }
	         return false;
	     }
	     return true;
	 }
	  
	 /**
	  * Gets the current registration ID for application on GCM service.
	  * <p>
	  * If result is empty, the app needs to register.
	  *
	  * @return registration ID, or empty string if there is no existing
	  *         registration ID.
	  */
	 private String getRegistrationId(Context context) {
	     final SharedPreferences prefs = getGCMPreferences(context);
	     String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	     if (registrationId.isEmpty()) {
	         Log.i(TAG, "Registration not found.");
	         return "";
	     }
	     // Check if app was updated; if so, it must clear the registration ID
	     // since the existing regID is not guaranteed to work with the new
	     // app version.
	     int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	     int currentVersion = getAppVersion(getApplicationContext());
	     if (registeredVersion != currentVersion) {
	         Log.i(TAG, "App version changed.");
	         return "";
	     }
	     return registrationId;
	 }
	  
	 /**
	  * @return Application's {@code SharedPreferences}.
	  */
	 private SharedPreferences getGCMPreferences(Context context) {
	  // This sample app persists the registration ID in shared preferences, but
	     // how you store the regID in your app is up to you.
	     return getSharedPreferences(MainActivity.class.getSimpleName(),
	             Context.MODE_PRIVATE);
	 }
	  
	 /**
	  * @return Application's version code from the {@code PackageManager}.
	  */
	 private static int getAppVersion(Context context) {
	     try {
	         PackageInfo packageInfo = context.getPackageManager()
	                 .getPackageInfo(context.getPackageName(), 0);
	         return packageInfo.versionCode;
	     } catch (NameNotFoundException e) {
	         // should never happen
	         throw new RuntimeException("Could not get package name: " + e);
	     }
	 }
	
	
	
	
	public void onBackPressed()
	{
		 Intent intent = new Intent(Intent.ACTION_MAIN);
		   intent.addCategory(Intent.CATEGORY_HOME);
		   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		   startActivity(intent);
	}
	
	
	
	
}