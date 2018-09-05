package com.example.wehealed.medical_lens;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class Activity_01_Logo extends AppCompatActivity {

    private TimerTask mTask;
    private Timer mTimer;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_01__logo);

        //Common.initialize(getApplicationContext());
        try {
            // 데이터베이스
            DBHelper dbHelper = DBHelper.getInstance(this);

            Cursor csr = dbHelper.get("SELECT * FROM MY_INFO_V5");
            if (csr != null && csr.isClosed() == false) {
                if (csr.moveToNext()) {
                    Log.i(Constants.LOG_TAG, "MY_INFO_V5 " + csr.getString(csr.getColumnIndex("EMAIL_ADDRESS")));
                    dbHelper.myEmailAddress = csr.getString(csr.getColumnIndex("EMAIL_ADDRESS"));
                    dbHelper.myName = csr.getString(csr.getColumnIndex("EMAIL_ADDRESS"));
                }
            }
            else {
                Log.i(Constants.LOG_TAG, "MY_INFO_V5 " + "empty");
            }
        }
        catch (Exception e) {
            Log.i(Constants.LOG_TAG, e.toString());
        }

        mTask = new TimerTask() {
            @Override
            public void run() {

                //if ( dbHelper.myEmailAddress.equals("") ) {
                if ( true ) {
                    Intent intent = new Intent(getApplicationContext(), Activity_10_Home.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTask, 1000);
    }

    @Override
    protected void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
    }
}
