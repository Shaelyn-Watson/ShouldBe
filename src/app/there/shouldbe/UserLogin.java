package app.there.shouldbe;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class UserLogin extends Activity {
	
	Button signUp;
	Button login;
	EditText usernameET;
	EditText passwordET;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_login);
		
		signUp = (Button) findViewById(R.id.signupButton);
		signUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserLogin.this, SignUp.class);
				startActivity(intent);
				finish();
			}
		});
		
		login = (Button) findViewById(R.id.loginButton);
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkCredentials();
			}
		});
		
		usernameET = (EditText) findViewById(R.id.usernameET);
		passwordET = (EditText) findViewById(R.id.passwordET);
		
		// ========================
        // ======== Parse =========
        // ========================
        Parse.initialize(this, "3wJJsTSZovcJZreXDjYVeJi3e1AOqAZEA8e2S860", "y1SZ9RtY8wuv9sOaXTIHrapLK5uk6LrehEEYylZd");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void checkCredentials() {
		try {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Profiles");
			query.whereEqualTo("username", usernameET);
			query.setLimit(1);
			List<ParseObject> temp = query.find();
			if (temp.get(0).equals(usernameET)) {
				query.whereEqualTo("password", passwordET);
				query.setLimit(1);
				temp = query.find();
				if (temp.get(0).equals(passwordET)) {
					System.out.println("Success signed in!"); // start here next time to handle code for signing in
				}
			}
			else {
				System.out.println("Username/password combination not found"); // fix to print to screen
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
		
		
		
	}

}
