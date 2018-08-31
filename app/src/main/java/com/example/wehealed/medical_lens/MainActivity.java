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
                    //requestRead(); 
                    break;
                case R.id.button_url:

                    break;

            }

        }
    };


    public void requestRead() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            openMedia();
        }
    }

    public void openMedia() {
        Intent intent2 = new Intent(MainActivity.this, ScanActivity.class);
        intent2.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_MEDIA);
        startActivityForResult(intent2, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openMedia();
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
