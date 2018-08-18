package com.example.wehealed.medical_lens;

import android.Manifest;
import android.content.Intent;
import android.graphics.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

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
