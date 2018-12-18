package com.nith.major.nithlogger.user;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.nith.major.nithlogger.CustomLocalStorage;
import com.nith.major.nithlogger.R;
import com.nith.major.nithlogger.authentication.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private User user;
    private Activity currentActivity;
    private TextView _name;
    private TextView _roll;
    private TextView _contact;
    private EditText _purpose;
    private Button _qenerateQr;
    private ImageView _logout;
    private ImageView qrcodeView;
    public final static int WIDTH = 400;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do your variables initialisations here except Views!!!
        currentActivity = getActivity();
        user = User.getInstance(currentActivity);

    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        // initialise your views
        _name =(TextView)view.findViewById(R.id.nameTextView);
        _roll =(TextView)view.findViewById(R.id.rollTextView);
        _contact =(TextView)view.findViewById(R.id.contactTextView);
        _qenerateQr =(Button)view.findViewById(R.id.btn_generate_qr);
        _purpose =(EditText)view.findViewById(R.id.input_purpose);
        _logout = (ImageView) view.findViewById(R.id.logout);
        qrcodeView = (ImageView) view.findViewById(R.id.qrcode);
        _name.setText(user.getName());
        _roll.setText(user.getRoll());
        _contact.setText(user.getContact());

        if(CustomLocalStorage.getString(currentActivity,"json_str") != null) {
            try {
                Bitmap bitmap = encodeAsBitmap(CustomLocalStorage.getString(currentActivity, "json_str"));
                qrcodeView.setImageBitmap(bitmap);
                qrcodeView.invalidate();
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
        _qenerateQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQr();
            }
        });

        _logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    public void generateQr()
    {
        Log.e("ProfileFragment","inFunction");
        if (!validate()) {
            _purpose.requestFocus();
            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String curTimestamp = simpleDateFormat.format(new Date());
        Log.e("DATE",""+curTimestamp);

        Gson gson = new Gson();

        Map<String, Object> future_json = new HashMap<>();
        future_json.put("roll",user.getRoll());
        future_json.put("purpose", _purpose.getText().toString());
        String json_str = gson.toJsonTree(future_json).toString();
        CustomLocalStorage.set(currentActivity,"json_str",json_str);
        Log.d("json qr", json_str);
        //////////////////// END of JSON generation //////////////////


        try {
            Bitmap bitmap = encodeAsBitmap(json_str);
            qrcodeView.setImageBitmap(bitmap);
            qrcodeView.invalidate();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public boolean validate() {
        boolean valid = true;

        String purpose = _purpose.getText().toString();

        if (purpose.isEmpty() ) {
            _purpose.setError("This field cannot be null");
            valid = false;
        } else {
            _purpose.setError(null);
        }


        return valid;
    }

    private Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void logout()
    {
        User.setInstance(null);
        CustomLocalStorage.set(currentActivity,"roll_no",null);
        CustomLocalStorage.set(currentActivity,"pass",null);
        Intent intent = new Intent(currentActivity, LoginActivity.class);
        startActivity (intent);
        currentActivity.finish();
    }
}
