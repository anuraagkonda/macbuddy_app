package se.teamtwo.androidapp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class UserProfileActivity extends Activity{
	
	List<NameValuePair> nameValuePairs;
	//TextView textview_password = null;
	String username;
	EditText edittext_username;
	Button button_save;
	TextView textview_username;
	
	TextView textview_password;
	EditText edittext_password;
	TextView textview_retypepwd;
	EditText edittext_retypepwd;
	
	HttpPost httpPost;
	HttpClient httpClient;
	HttpResponse response;

protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.user_profile);
Bundle extras = getIntent().getExtras();
if (extras != null) {
    username = extras.getString("username");
}

	button_save = (Button) findViewById(R.id.saveButton);
	 textview_username = (TextView) findViewById(R.id.up_username);
	edittext_username = (EditText) findViewById(R.id.username_edit_text);
	 textview_password = (TextView) findViewById(R.id.up_password);
	 edittext_password = (EditText) findViewById(R.id.pwd_up);
	 textview_retypepwd = (TextView) findViewById(R.id.up_retypepwd);
	 edittext_retypepwd = (EditText) findViewById(R.id.repassword);
	 setdata();
	

	/*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	StrictMode.setThreadPolicy(policy);*/
    
   
    button_save.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
        	
        	
		  new Thread(new Runnable() {
				public void run() {
					edituser();
				}
			}).start();
     }
});
}

    
    public void edituser(){
    	try{
    		
            httpClient = new DefaultHttpClient();
			
			// Send the HTTP request
			httpPost = new HttpPost("http://omega.uta.edu/~akk1814/LoginActions/editUser.php");
			nameValuePairs = new ArrayList<NameValuePair>(2);
			
			nameValuePairs.add(new BasicNameValuePair("username", edittext_username
					.getText().toString().trim())); 
			nameValuePairs.add(new BasicNameValuePair("password",
					edittext_password.getText().toString().trim()));
			
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			response = httpClient.execute(httpPost);
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			
			final String responseLogin = httpClient.execute(httpPost,
					responseHandler);
			if (responseLogin.equalsIgnoreCase("you have succesfully updated user details")) {
				runOnUiThread(new Runnable() {
					public void run() {
						/*Toast.makeText(UserProfileActivity.this, "you have succesfully updated user details",
								Toast.LENGTH_SHORT).show();*/
						//joinEventButton.setText("Unjoin Event");
						Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
						i.putExtra("username",username);
						//i.putExtra("buttonValueUnjoin","Unjoin Event");
						//i.putExtra("eventIdForUnjoin",eventId);
						//i.putExtra("flag",true);
						startActivity(i);
						
					}
				});
			}
			else {
				UserProfileActivity.this.runOnUiThread(new Runnable() {
					public void run() {
					}
				});
				//responseLogin.getEntity().consumeContent();
			}

			// Progress Dialog should be dismissed
			
    	}
    	catch(Exception e){
    		
    	}
    }
   public void setdata(){
    	 System.out.println("user is" + username);
    	 edittext_username.setText(username);
    }

    }
