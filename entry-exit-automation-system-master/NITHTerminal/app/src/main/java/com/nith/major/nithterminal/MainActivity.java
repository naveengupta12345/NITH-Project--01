package com.nith.major.nithterminal;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private final String LOGTAG = "QRCScanner-MainActivity";
    private ProgressDialog insertOrderRequestProgressDialog;
    private Activity currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button_start_scan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start the qr scan activity
                Intent i = new Intent(MainActivity.this, QrCodeActivity.class);
                startActivityForResult(i, REQUEST_CODE_QR_SCAN);
            }
        });

        currentActivity = this;

        insertOrderRequestProgressDialog = new ProgressDialog(this);
        insertOrderRequestProgressDialog.setTitle("Loading");
        insertOrderRequestProgressDialog.setMessage("Wait while loading...");
        insertOrderRequestProgressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            Log.d(LOGTAG, "COULD NOT GET A GOOD RESULT.");
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d(LOGTAG, "Have scan result in your app activity :" + result);
            try {
                JSONObject j = new JSONObject(result);
                sendtoserver(j);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendtoserver(final JSONObject res) throws JSONException {
        insertOrderRequestProgressDialog.show();
        HashMap<String, String> order_params = new HashMap<>();
        RequestParams order = new RequestParams();

        order_params.put("roll", res.getString("roll"));
        order_params.put("purp", res.getString("purpose"));
        order.put("order", order_params);

        ServerRestClient.post("record", order, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String error = response.get("error").toString();
                    insertOrderRequestProgressDialog.dismiss();
                    showWarningDialog("warning", error);
                    return;
                } catch (JSONException e) {
                    //normal behaviour when there are no errors.
                }

                try {
                    String result = response.get("response").toString();
                    insertOrderRequestProgressDialog.dismiss();
                    showWarningDialog("success", result+" successful");

                } catch (JSONException e) {
                    //normal behaviour when there are no errors.
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String error, Throwable throwable) {
                Log.e("FAILURE:", "~JSON OBJECT - status: " + statusCode);
                Log.e("FAILURE:", error);
                insertOrderRequestProgressDialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject object) {
                Log.e("FAILURE:", "some error I dont know how to handle. timeout?");
                Log.e("FAILURE:", "JSON OBJECT - status: " + statusCode);
                insertOrderRequestProgressDialog.dismiss();

            }

        });

    }

    private void showWarningDialog(String type, String msg) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(currentActivity);
        switch (type) {
            case "warning": //orange warning
                builder.setTitle("Warning");
                builder.setIcon(R.drawable.ic_warning_orange_24dp);
                break;
            case "error": //red warning
                builder.setTitle("Error");
                builder.setIcon(R.drawable.ic_warning_red_24dp);
                break;
            case "success": //success
                builder.setTitle("Success");
                builder.setIcon(R.drawable.ic_check_circle_green_24dp);
                break;
            default:
                break;
        }
        builder.setMessage(msg);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}