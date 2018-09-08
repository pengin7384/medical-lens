/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.wehealed.medical_lens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Url;

/**
 * Activity for the Ocr Detecting app.  This app detects text and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each TextBlock.
 */
public final class Activity_20_Camera extends AppCompatActivity {
    private static final String TAG = "OcrCaptureActivity";

    DBHelper dbHelper;

    int historyId = 0;
    String picturePathAndFileName = "";
    String pictureFileName = "";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";

    private CameraSource cameraSource;
    private CameraSourcePreview preview;
    private GraphicOverlay<OcrGraphic> graphicOverlay;

    // Helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    // A TextToSpeech engine for speaking a String value.
    private TextToSpeech tts;
    private Button btnCapture;
    private OcrDetectorProcessor processor;
    private String fileName="";

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_20__camera);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_camera);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼

        try {
            dbHelper = DBHelper.getInstance(getApplicationContext());
        }
        catch (Exception e) {
            Log.i(Constants.LOG_TAG, e.toString());
        }

        preview = (CameraSourcePreview) findViewById(R.id.preview);
        graphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.graphicOverlay);
        btnCapture = findViewById(R.id.button_main_capture);
        btnCapture.setOnClickListener(mClickListener);

        // preview의 사이즈 조절 : 세로 길이 = 가로 길이 * 4 / 3 이 되도록 한다
        // preview의 사이즈를 240dp * 320dp 로 함으로써, 실제 카메라 촬영 해상도 3:4 비율과 동일하도록 한다
        int previewHeight = preview.getHeight();
        int previewWidth = preview.getWidth();
        preview.setMinimumHeight((int)((previewWidth * 4) / 3));

        // Set good defaults for capturing text.
        boolean autoFocus = true;
        boolean useFlash = false;

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    // Button Event
    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_main_capture:  // 캡처 버튼
                    onTakePictureButtonClick();
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void takePicture(byte[] data, Camera camera) {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Log.d("WeHealed TakePicture", "length = " + data.length);

            long pictureTime = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.KOREA);
            String currentDateTimeString = sdf.format(new Date(pictureTime));
            String timeStampString = TimeUnit.MILLISECONDS.toSeconds(pictureTime) + "";

            historyId = 0;
            picturePathAndFileName = "";
            pictureFileName = "";

            // 파일을 스마트폰 내장 메모리 -> DCIM/Medical-Lens/ 폴더에 저장한다
            File externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            //File externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!externalStorageDirectory.exists()) {
                externalStorageDirectory.mkdirs();
            }

            String pictureStorageDirectoryPath = externalStorageDirectory.getAbsolutePath() + "/Medical-Lens";
            File pictureStorageDirectory = new File(pictureStorageDirectoryPath);
            if (!pictureStorageDirectory.exists()) {
                pictureStorageDirectory.mkdirs();
            }


            File fileItem = new File(pictureStorageDirectoryPath, currentDateTimeString + ".jpg");
            fileName = fileItem.getName();

            Log.d("WeHealed TakePicture", "Filename = " + pictureStorageDirectoryPath + "/" + fileItem.getName() );

            FileOutputStream output = null;
            try {
                // 사진을 스마트폰의 MediaStore DB (갤러리DB)에 저장한다
                //pictureStorageDirectory.createNewFile();

                output = new FileOutputStream(fileItem);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);

                picturePathAndFileName = pictureStorageDirectoryPath + "/" + fileItem.getName();
                pictureFileName = fileItem.getName();

                // 사진을 앨범에 저장한다
                // TODO : Pictures 가 아니라 Medical-Lens 에 저장
                String outUriStr = MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        bitmap,
                        fileItem.getName(), "Medical-Lens Captured Image"
                );

                if (outUriStr == null) {
                    Log.d("WeHealed Capture", "MediaStore Image Save Failed");
                    return;
                }
                else {
                    Uri outUri = Uri.parse(outUriStr);
                    Log.d("WeHealed Capture", "WeHealed Image Save Result : " + outUri);
                    Toast.makeText(getApplicationContext(),"Medical-Lens 앨범에 사진을 저장했습니다.",Toast.LENGTH_LONG).show();
//                    sendBroadcast(new Intent(ACTION_MEDIA_SCANNER_SCAN_FILE, outUri));
                }

                // 사진으로부터 텍스트 추출 한다
                ArrayList<String> list = new ArrayList<String >();
                SparseArray<TextBlock> items = processor.getItems();

                ArrayList<TextData> arrayList = new ArrayList<TextData>();

                if(items != null) {
                    for (int i = 0; i < items.size(); ++i) {
                        TextBlock item = items.valueAt(i);
                        if (item != null && item.getValue() != null) {
                            List<? extends Text> texts = item.getComponents();
                            for (Text line : texts) { // Line 단위
                                arrayList.add(new TextData(line.getBoundingBox().centerY(), line.getValue()));

                        /*
                        for (Text element : line.getComponents()) { // Word 단위
                            arrayList.add(new TextData(element.getBoundingBox().centerY() , element.getValue()));
                        }*/
                            }
                            //arrayList.add(new TextData(item.getBoundingBox().centerY(),item.getValue())); // 블럭단위로 가져오기
                        }
                    }

                    Collections.sort(arrayList);
                    for (int i = 0; i < arrayList.size(); ++i) {
                        list.add(arrayList.get(i).getText());
                    }

                }

                String originalText = "";
                // TODO : 추출된 텍스트들을 전처리한다. 일단은 | 로 묶어서 DB에 저장함
                for (int i = 0; i < list.size(); ++i) {
                    originalText += list.get(i) + "|";
                }
                originalText = originalText.replaceAll("\'", "\''");
                originalText = originalText.replaceAll("\"", "\\\"");

                // DB 에 기록한다
                String sql = "INSERT INTO PICTURE_HISTORY_V5 (" +
                        "PICTURE_PATH_AND_FILE_NAME" +
                        ", PICTURE_FILE_NAME" +
                        ", PICTURE_TIME" +
                        ", ORIGINAL_TEXT" +
                        ", MACHINE_TRANSLATION_RESULT" +
                        ", HUMAN_TRANSLATION_REQUESTED, HUMAN_TRANSLATION_REQUEST_TIME, HUMAN_TRANSLATION_RESPONSE_TIME, HUMAN_TRANSLATION_RESULT, HUMAN_TRANSLATION_CONFIRMED" +
                        ", SUMMARY_TEXT, SUMMARY_SENTENCE_NUMBER " +
                        ") " +
                        "VALUES (" +
                        "'" + pictureStorageDirectoryPath + "/" + fileItem.getName() + "'" +
                        ", '" + fileItem.getName() + "'" +
                        ", '" + timeStampString + "'" +
                        ", '" + originalText + "'" +
                        ", ''" +
                        ", '', '', '', '', 'N'" +
                        ", '', 0" +
                        ");";
                dbHelper.exec(sql);
                Log.d(Constants.LOG_TAG, sql);

                Cursor cursor = dbHelper.get("SELECT MAX(HISTORY_ID) FROM PICTURE_HISTORY_V5;");
                try {
                    if (cursor.moveToFirst()) {
                        historyId = cursor.getInt(0);

                        // 번역 화면으로 전환한다
                        moveToTranslateResultActivity();
                    }
                }
                catch (Exception e) {
                    Log.i(Constants.LOG_TAG, e.toString());

                }
                finally {
                    try {
                        cursor.close();
                    }
                    catch (Exception ignore) {
                    }
                }
            } catch (Exception e) {
                Log.i(Constants.LOG_TAG, e.toString());
                e.printStackTrace();
            } finally {
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        Log.i(Constants.LOG_TAG, e.toString());
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("WeHealed", "Failed to capture the image", e);
        }
    }

    private void moveToTranslateResultActivity() {
        // 번역 Activity 실행한다
        Intent intent = new Intent(getApplicationContext(), Activity_30_Translate_Result.class);
        intent.putExtra("historyId", historyId);
        intent.putExtra("name",fileName);
        startActivity(intent);
        finish();

    }

    private void onTakePictureButtonClick() {
        // 카메라 사진 촬영 이미지 저장
        cameraSource.takePicture(null, new com.example.wehealed.medical_lens.CameraSource.PictureCallback(){
            public void onPictureTaken(byte[] data, Camera camera) {
                takePicture(data, camera);

                camera.startPreview();
            }
        });
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(graphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // TODO: Create the TextRecognizer
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        // TODO: Set the TextRecognizer's Processor.
        processor = new OcrDetectorProcessor(graphicOverlay);

        // OCR processor
        textRecognizer.setProcessor(processor);

        // TODO: Check if the TextRecognizer is operational.
        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }
        // TODO: Create the cameraSource using the TextRecognizer.
        cameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(15.0f)
                        .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO : null)
                        .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (preview != null) {
            preview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preview != null) {
            preview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (cameraSource != null) {
            try {
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }

    }

    /**
     * onTap is called to speak the tapped TextBlock, if any, out loud.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the tap was on a TextBlock
     */
    private boolean onTap(float rawX, float rawY) {
        // TODO: Speak the text when the user taps on screen.
        return false;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (cameraSource != null) {
                cameraSource.doZoom(detector.getScaleFactor());
            }
        }
    }

    /**
     * Generate a string containing a formatted timestamp with the current date and time.
     *
     * @return a {@link String} representing a time.
     */
    private static String generateTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.KOREA);
        return sdf.format(new Date());
    }

}
