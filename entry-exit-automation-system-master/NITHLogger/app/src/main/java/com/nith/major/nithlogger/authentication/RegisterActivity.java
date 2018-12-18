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
import android.view.TextureView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nith.major.nithlogger.CustomLocalStorage;
import com.nith.major.nithlogger.R;
import com.nith.major.nithlogger.ServerRestClient;
import com.nith.major.nithlogger.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {

    private Context context;
    private Activity currentActivity = this;

    private EditText name;
    private EditText roll;
    private EditText contact;
    private EditText password;
    private Spinner gspinner;
    private Spinner yspinner;
    private Spinner bspinner;
    private TextView _login_link;
    private Button _signup;
    private Integer[] yearlist;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        context = getApplicationContext();

        name = (EditText) findViewById(R.id.input_name);
        roll = (EditText) findViewById(R.id.input_roll);
        contact = (EditText) findViewById(R.id.input_contact);
        password = (EditText) findViewById(R.id.input_password);
        _login_link = (TextView) findViewById(R.id.link_login);
        _signup = (Button) findViewById(R.id.btn_signup);

        gspinner = (Spinner) findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> gadapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        gadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gspinner.setAdapter(gadapter);

        getDate();

        yspinner = (Spinner) findViewById(R.id.year_spinner);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, yearlist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yspinner.setAdapter(adapter);

        bspinner = (Spinner) findViewById(R.id.branch_spinner);
        ArrayAdapter<CharSequence> badapter = ArrayAdapter.createFromResource(this,
                R.array.branch_array, android.R.layout.simple_spinner_item);
        badapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bspinner.setAdapter(badapter);

        _signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _login_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(currentActivity, LoginActivity.class);
                startActivity(intent);
                currentActivity.finish();
            }
        });
    }

    public void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signup.setEnabled(false);

        progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String Name = name.getText().toString();
        String Roll = roll.getText().toString();
        String Contact = contact.getText().toString();
        String Password = password.getText().toString();
        String Gender = gspinner.getSelectedItem().toString();
        String Branch = bspinner.getSelectedItem().toString();
        String Year = yspinner.getSelectedItem().toString();

        // TODO: Implement your own signup logic here.



        HashMap<String, String> user_params = new HashMap<>();
        RequestParams user = new RequestParams();

        user_params.put("name", Name);
        user_params.put("roll", Roll);
        user_params.put("contact", Contact);
        user_params.put("password", Password);
        user_params.put("gender", Gender);
        user_params.put("branch", Branch);
        user_params.put("year", Year);

        user.put("user",user_params);
        Log.e("signup",""+user);
        registerCall(user);

        /*new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);*/
    }

    private void registerCall(RequestParams user) {
        Log.e("RegCall",""+user);
        ServerRestClient.post("register", user, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try{
                    String error = response.get("error").toString();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                    //showProgress(false);
                    progressDialog.dismiss();
                    onSignupFailed();
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

                    Log.e("USER:", "creating user");
                    // SET SINGLETON USER
                    User.getInstance(currentActivity).setName(Name);
                    User.getInstance(currentActivity).setRoll(Roll);
                    User.getInstance(currentActivity).setGender(Gender);
                    User.getInstance(currentActivity).setYear(Year);
                    User.getInstance(currentActivity).setBranch(Branch);
                    User.getInstance(currentActivity).setContact(Contact);
                    Log.e("USER:", "saving user");
                    User.saveInstance(currentActivity);
                    Log.e("FAILURE:", "not entering onSingup");
                    onSignupSuccess();
                }
                catch(JSONException e){
                    Log.e("FAILURE:", "error parsing response JSON");
                    Toast.makeText(context, "Server error. Try again later.", Toast.LENGTH_LONG).show();
                   //showProgress(false);
                    progressDialog.dismiss();
                    onSignupFailed();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String error, Throwable throwable){
                Log.e("FAILURE:", error);
                Toast.makeText(context, error.toString()
                        , Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "Throwable Err...", Toast.LENGTH_SHORT).show();
                //showProgress(false);
                progressDialog.dismiss();
                onSignupFailed();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object){
                Log.e("FAILURE:", "some error I dont know how to handle. timeout?");
                Toast.makeText(context, "Reaching reg not db...", Toast.LENGTH_SHORT).show();
                //showProgress(false);
                progressDialog.dismiss();
                onSignupFailed();
            }
        });
    }


    public void onSignupSuccess() {
        progressDialog.dismiss();
        _signup.setEnabled(false);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _signup.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

       /* String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }*/

        return valid;
    }


    public void getDate()
    {
        yearlist = new Integer[5];
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for(int i=0;i<5;i++)
        {
            yearlist[i] = currentYear-i;
        }

    }

}
