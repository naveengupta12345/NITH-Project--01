package com.nith.major.nithlogger.events;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nith.major.nithlogger.CustomLocalStorage;
import com.nith.major.nithlogger.R;
import com.nith.major.nithlogger.ServerRestClient;
import com.nith.major.nithlogger.authentication.RegisterActivity;
import com.nith.major.nithlogger.user.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComplaintFragment extends Fragment {

    private User user;
    private Activity currentActivity;
    private EditText _feedback;
    private Spinner _cspinner;
    private Button _fb;
    private Context context;
    private ProgressDialog progressDialog;

    public ComplaintFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_complaint, container, false);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!
        currentActivity = getActivity();
        user = User.getInstance(currentActivity);
        context = getContext();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _feedback = (EditText) view.findViewById(R.id.input_fb);
        _cspinner = (Spinner) view.findViewById(R.id.cType_spinner);
        _fb = (Button) view.findViewById(R.id.btn_complaint);
        ArrayAdapter<CharSequence> gadapter = ArrayAdapter.createFromResource(context,
                R.array.cType_array, android.R.layout.simple_spinner_item);
        gadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _cspinner.setAdapter(gadapter);

        _fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
    }


    public void send() {


        _fb.setEnabled(false);

        progressDialog = new ProgressDialog(context,
                R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending Feedback...");
        progressDialog.show();

        String c = _feedback.getText().toString();
        String cType = _cspinner.getSelectedItem().toString();



        HashMap<String, String> user_params = new HashMap<>();
        RequestParams user = new RequestParams();

        user_params.put("type", cType);
        user_params.put("fb", c);

        user.put("user",user_params);
        Log.e("feedback",""+user);
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
        ServerRestClient.post("feedback", user, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try{
                    String error = response.get("error").toString();
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                    //showProgress(false);
                    progressDialog.dismiss();
                    onFeedbackFailed();
                    return;
                }
                catch(JSONException e){
                    //normal behaviour when there are no errors.
                }

                try{
                    String result = response.get("response").toString();
                    onFeedbackSuccess();
                }
                catch(JSONException e){
                    Log.e("FAILURE:", "error parsing response JSON");
                    Toast.makeText(context, "Server error. Try again later.", Toast.LENGTH_LONG).show();
                    //showProgress(false);
                    progressDialog.dismiss();
                    onFeedbackFailed();
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
                onFeedbackFailed();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object){
                Log.e("FAILURE:", "some error I dont know how to handle. timeout?");
                Toast.makeText(context, "Reaching reg not db...", Toast.LENGTH_SHORT).show();
                //showProgress(false);
                progressDialog.dismiss();
                onFeedbackFailed();
            }
        });
    }


    public void onFeedbackSuccess() {
        progressDialog.dismiss();
        _feedback.setText("");
        _fb.setEnabled(true);
        Toast.makeText(context, "Feedback sent.", Toast.LENGTH_LONG).show();
    }

    public void onFeedbackFailed() {
        Toast.makeText(context, "Sending failed", Toast.LENGTH_LONG).show();
        _fb.setEnabled(true);
    }
}
