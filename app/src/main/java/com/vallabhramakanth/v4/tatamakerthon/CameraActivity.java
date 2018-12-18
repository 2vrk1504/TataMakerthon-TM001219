package com.vallabhramakanth.v4.tatamakerthon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CameraActivity extends AppCompatActivity {

    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("TataMakerthon", MODE_PRIVATE);
        state = sp.getInt("state", 0);
        if(state == 1){
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, CameraFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("state", state);
        super.onSaveInstanceState(outState);
    }

}
