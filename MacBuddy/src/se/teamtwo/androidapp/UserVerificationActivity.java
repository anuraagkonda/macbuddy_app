package se.teamtwo.androidapp;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserVerificationActivity extends Activity 
{
	Button code_submission;
	Button resend_code;
	EditText verificationcode;
	String entered_code, sent_code;
	Bundle extras;
	
	String t_string_email;
	String t_string_userName;
	String t_string_resendcode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{	
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verification_screen);
		extras = getIntent().getExtras();
		if (extras != null) {
			t_string_userName = extras.getString("username");
			   t_string_email=extras.getString("email");
			   t_string_resendcode=extras.getString("verificationcode");
			}
		code_submission = (Button)findViewById(R.id.VerificationButton);
		
		
		//Button resend_code;
		resend_code= (Button)findViewById(R.id.resendCodebutton);
		
		verificationcode = (EditText)findViewById(R.id.codetext);
		sent_code = RegActivity.string_code;
		
		code_submission.setOnClickListener(new View.OnClickListener() 
		{
	        @Override
			public void onClick(View v) 
		    {
		        entered_code = verificationcode.getText().toString().trim();
		        System.out.println(sent_code);
		        if(entered_code.equals(sent_code))
		        {
		        	System.out.println("Working :D");
		        	Toast t = Toast.makeText(UserVerificationActivity.this,"Registration Successful", Toast.LENGTH_SHORT);
		        	t.show();
		        	Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
		        	i.putExtra("username",t_string_userName);
					startActivity(i);
		        }
		        else{
		        	Toast t = Toast.makeText(UserVerificationActivity.this,"Incorrect code! Please try again!", Toast.LENGTH_LONG);
		        	t.show();
		        	System.out.println("It ain't workin :(");
		        }
		    }
		});
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		//getData();
		
		
		resend_code.setOnClickListener(new View.OnClickListener() 
		{
	        @Override
			public void onClick(View v) 
		    {
	        	Toast tt = Toast.makeText(UserVerificationActivity.this,"Code Sent Again", Toast.LENGTH_SHORT);
	        	tt.show();
	        	JSONParser jsonParser1 = new JSONParser();
	        	List<NameValuePair> passParams1 = new ArrayList<NameValuePair>();
	        	passParams1.add(new BasicNameValuePair("name", t_string_userName));
	        	passParams1.add(new BasicNameValuePair("email", t_string_email));
	        	passParams1.add(new BasicNameValuePair("verificationCode", t_string_resendcode));
	        	
	        	System.out.println("before sending");
	        	System.out.println("name "+t_string_userName);
	        	System.out.println("email "+t_string_email);
	        	System.out.println("code "+t_string_resendcode);
	        	//Object jsonParser;
				//JSONObject json1 =
						jsonParser1.makeHttpRequest(
						"http://omega.uta.edu/~akk1814/resendcode.php", "POST",
						passParams1);
						
						System.out.println("after sending");
						
						verificationcode = (EditText)findViewById(R.id.codetext);
	        	
		        entered_code = verificationcode.getText().toString().trim();
		        System.out.println(sent_code);
		        if(entered_code.equals(t_string_resendcode))
		        {
		        	System.out.println("Working :D");
		        	Toast t = Toast.makeText(UserVerificationActivity.this,"Registration Successful", Toast.LENGTH_SHORT);
		        	t.show();
		        	Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
		        	i.putExtra("username",t_string_userName);
					startActivity(i);
		        }
		        else{
		        	Toast t = Toast.makeText(UserVerificationActivity.this,"Incorrect code! Please try again!", Toast.LENGTH_LONG);
		        	t.show();
		        	System.out.println("It ain't workin :(");
		        }
		    }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onBackPressed()
	{ 
		finish();
	}
}

