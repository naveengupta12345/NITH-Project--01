package com.nith.major.nithlogger.authentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nith.major.nithlogger.CustomLocalStorage;
import com.nith.major.nithlogger.MainActivity;
import com.nith.major.nithlogger.R;
import com.nith.major.nithlogger.ServerRestClient;
import com.nith.major.nithlogger.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private Context context;
    private Activity currentActivity = this;

    private EditText _roll;
    private EditText _password;
    private TextView _signup_link;
    private Button _login;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (CustomLocalStorage.getString(this, "roll_no") != null)
        {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity (intent);
            this.finish();
            return;
        }

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        context = getApplicationContext();

        _roll = (EditText) findViewById(R.id.input_rollno);
        _password = (EditText) findViewById(R.id.input_pass);
        _login = (Button) findViewById(R.id.btn_login);
        _signup_link = (TextView) findViewById(R.id.link_signup);

        _login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("Login Button","Pressed");
                login();
            }
        });

        _signup_link.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(currentActivity, RegisterActivity.class);
                startActivity (intent);
                currentActivity.finish();
            }
        });

    }

    public void login ()
    {
        attemptLogin();

    }

    public void attemptLogin()
    {
        Log.d("TAG", "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _login.setEnabled(false);

         progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String roll = _roll.getText().toString();
        String password = _password.getText().toString();

        // TODO: Implement your own authentication logic here.

        HashMap<String, String> user_params = new HashMap<>();
        RequestParams user = new RequestParams();

        user_params.put("roll", roll);
        user_params.put("pass", password);

        user.put("user",user_params);


        ServerRestClient.post("login", user, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try{
                    String error = response.get("error").toString();
                    Toast.makeText(currentActivity.getApplicationContext(), error, Toast.LENGTH_LONG).show();
                    //FAIL LOGIN
                    progressDialog.dismiss();
                    onLoginFailed();
                    return;
                }
                catch(JSONException e){
                    //normal behaviour when there are no errors.
                }

                try{
                    String Name = response.get("name").toString();
                    String Roll = response.get("roll_no").toString();
                    String Contact = response.get("contact_number").toString();
                    String Password = response.get("pass").toString();
                    String Gender = response.get("gender").toString();
                    String Branch = response.get("branch").toString();
                    String Year = response.get("year").toString();


                    //SUCCESS
                    CustomLocalStorage.set(currentActivity, "roll_no", Roll);
                    CustomLocalStorage.set(currentActivity, "pass", Password);
                    Toast.makeText(context, "Registed Successfully!", Toast.LENGTH_LONG).show();

                    Log.e("USER:", "Creating user");
                    // SET SINGLETON USER
                    User.getInstance(currentActivity).setName(Name);
                    User.getInstance(currentActivity).setRoll(Roll);
                    User.getInstance(currentActivity).setGender(Gender);
                    User.getInstance(currentActivity).setYear(Year);
                    User.getInstance(currentActivity).setBranch(Branch);
                    User.getInstance(currentActivity).setContact(Contact);
                    Log.e("USER:", "Saving user");
                    User.saveInstance(currentActivity);
                    onLoginSuccess();

                    //SUCCESS LOGIN
                    Toast.makeText(currentActivity.getApplicationContext(), "Successful login!", Toast.LENGTH_LONG).show();
                    Log.e("user",response.toString());
                }
                catch(JSONException e){
                    Log.e("FAILURE:", "error parsing response JSON");
                    e.printStackTrace();
                    progressDialog.dismiss();
                    onLoginFailed();
                    Toast.makeText(currentActivity.getApplicationContext(), "Server error. Try again later.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String error, Throwable throwable){
                Log.e("FAILURE:", error);
                progressDialog.dismiss();
                onLoginFailed();
                Toast.makeText(currentActivity.getApplicationContext(), "Server not available...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                Log.e("FAILURE:", "some error I dont know how to handle. timeout?");
                progressDialog.dismiss();
                onLoginFailed();
                Toast.makeText(currentActivity.getApplicationContext(), "Server not available...", Toast.LENGTH_SHORT).show();
            }

        });

        /*new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);*/
    }

    public boolean validate() {
        boolean valid = true;

        String roll = _roll.getText().toString();
        String password = _password.getText().toString();

        if (roll.isEmpty() ) {
            _roll.setError("This field cannot be null");
            valid = false;
        } else {
            _roll.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _password.setError(null);
        }

        return valid;
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _login.setEnabled(true);
    }

    public void onLoginSuccess() {
        _login.setEnabled(false);
        progressDialog.dismiss();
        Intent intent = new Intent(currentActivity, MainActivity.class);
        startActivity (intent);
        currentActivity.finish();

    }
}


