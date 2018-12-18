package com.vallabhramakanth.v4.tatamakerthon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class SecondActivity extends AppCompatActivity {

    private Button btn;
    private int btnstate;
    private static final String TAG = "SecondActivityTAG";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        btnstate = 0;
        SharedPreferences sp = getSharedPreferences("TataMakerthon", MODE_PRIVATE);
        btn = findViewById(R.id.sa_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnstate == 0) {
                    new SendHttpRequestTask().execute();
                }
                else{
                    Intent intent = new Intent(btn.getContext(), CameraActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        ((Button)findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("TataMakerthon", MODE_PRIVATE);
                SharedPreferences.Editor e = sp.edit();
                e.putInt("state", 0);
                e.apply();
                Intent intent = new Intent(btn.getContext(), CameraActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private class SendHttpRequestTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                Log.d(TAG, "started");
                URL url = new URL("http://192.168.4.1/output/output0.jpg");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.d(TAG, "started");
                connection.setDoInput(true);
                connection.connect();
                Log.d(TAG, "started");
                InputStream input = connection.getInputStream();
                Log.d(TAG, "input stream ok");
                return BitmapFactory.decodeStream(input);
            }catch (Exception e){
                Log.d(TAG,e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(result != null){
                ImageView imageView = findViewById(R.id.output_image);
                imageView.setVisibility(View.VISIBLE);
                findViewById(R.id.please_wait).setVisibility(View.GONE);
                imageView.setImageBitmap(result);
                btn.setText("OK");
                btnstate = 1;
                SharedPreferences sp = getSharedPreferences("TataMakerthon", MODE_PRIVATE);
                SharedPreferences.Editor e = sp.edit();
                e.putInt("state", 0);
                e.apply();
            }
            else{
                Toast.makeText(getApplicationContext(), "Image not available yet. Please wait for a while.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
