package com.example.wehealed.medical_lens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 99;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.button_camera).setOnClickListener(mClickListener);
        findViewById(R.id.button_gallery).setOnClickListener(mClickListener);
        findViewById(R.id.button_url).setOnClickListener(mClickListener);



    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_camera:
                    Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                    startActivity(intent);
                    break;
                case R.id.button_gallery:

                    break;
                case R.id.button_url:

                    break;

            }

        }
    };


}
