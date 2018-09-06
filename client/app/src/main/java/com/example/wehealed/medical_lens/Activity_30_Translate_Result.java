package com.example.wehealed.medical_lens;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_30_Translate_Result extends AppCompatActivity {
    static final String URL = "https://wehealedapi2.run.goorm.io/api/";

    //private SparseArray<TextBlock> items;

    DBHelper dbHelper;

    int historyId = 0;
    String picturePathAndFileName = "";
    String pictureFileName = "";
    int pictureTime = 0;
    String originalText = "";
    String summaryText = "";


    ImageView pictureImageView;
    TextView machineTranslateResultTextView;
    TextView pictureFileNameTextView;

    ListView translationResultListView;
    ArrayAdapter translationResultListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_30__translate_result);

        try {
            dbHelper = DBHelper.getInstance(getApplicationContext());
        }
        catch (Exception e) {
            Log.i(Constants.LOG_TAG, e.toString());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_translate_result);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼

        pictureImageView = (ImageView)findViewById(R.id.imageView_picture);
        machineTranslateResultTextView = (TextView) findViewById(R.id.machine_translation_response);
        pictureFileNameTextView = (TextView) findViewById(R.id.textView_picture_file_name);

        translationResultListView = (ListView)findViewById(R.id.listView_translation_result);
        translationResultListViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        translationResultListView.setAdapter(translationResultListViewAdapter);

        findViewById(R.id.button_go_human_translation_request_activity).setOnClickListener(mClickListener);

        initialize();
    }

    @Override
    protected void onNewIntent(Intent newIntent) {
        initialize();
    }

    private void initialize() {

        // Intent 정보로 넘어온 historyId 수신
        Intent intent = getIntent();
        historyId = intent.getIntExtra("historyId", 0);

        // historyId 로 클라이언트 DB 데이터 조회
        try {
            Cursor cursor = dbHelper.get("SELECT * FROM PICTURE_HISTORY_V5 " +
                    "WHERE HISTORY_ID='" + historyId + "'");

            if (cursor.moveToFirst()) {
                pictureTime = cursor.getInt(cursor.getColumnIndex("PICTURE_TIME"));
                picturePathAndFileName = cursor.getString(cursor.getColumnIndex("PICTURE_PATH_AND_FILE_NAME"));
                pictureFileName = cursor.getString(cursor.getColumnIndex("PICTURE_FILE_NAME"));
                originalText = cursor.getString(cursor.getColumnIndex("ORIGINAL_TEXT"));
                summaryText = cursor.getString(cursor.getColumnIndex("SUMMARY_TEXT"));

                Log.i(Constants.LOG_TAG, "HISTORY_ID " + historyId
                        + " PICTURE_TIME" + pictureTime
                        + " PICTURE_PATH_AND_FILE_NAME " + picturePathAndFileName
                        + " PICTURE_FILE_NAME " + pictureFileName
                        + " ORIGINAL_TEXT " + originalText
                        + " SUMMARY_TEXT " + summaryText
                );
            }
            cursor.close();
        }
        catch (Exception e) {
            Log.i(Constants.LOG_TAG, e.toString());
        }

        // 클라이언트 DB 데이터 -> 사진 파일 로딩
        File imgFile = new  File(picturePathAndFileName);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            pictureImageView.setImageBitmap(myBitmap);
        }

        // 클라이언트 DB 데이터 -> 사진 파일이름 표시
        pictureFileNameTextView.setText(pictureFileName);

        // 클라이언트 DB 데이터 -> originalText 표시
        translationResultListViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        ArrayList<String> arrayList = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(originalText, "|");
        while (st.hasMoreTokens()) {
            String sentence = st.nextToken();
            arrayList.add(sentence);
        }
        translationResultListViewAdapter.addAll(arrayList);
        translationResultListView.setAdapter(translationResultListViewAdapter);

        Sentence[] sentences = new Sentence[arrayList.size()];
        for(int i=0; i<arrayList.size(); i++) {
            Sentence s = new Sentence(i+1,arrayList.get(i),"");
            sentences[i] = s;
        }
        sendAndReceiveMachineTranslationResult(sentences);
    }

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

    /*
    // 서버로 번역을 요청한다
    // Get 방식
    public void sendAndReceiveMachineTranslationResult(String text) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        Call<TextItem> call = retrofitService.getIndex(text);
        call.enqueue(new Callback<TextItem>() {
            @Override
            public void onResponse(Call<TextItem> call, Response<TextItem> response) {
                TextItem repo = response.body();
                processResponse(repo);
            }

            @Override
            public void onFailure(Call<TextItem> call, Throwable t) {
                Log.d("WeHealed", "Machine Translation Failure");
                Toast.makeText(getApplicationContext(), "Machine Translation Failure", Toast.LENGTH_LONG).show();
            }
        });
    }
    */

    // 서버로 번역을 요청한다
    // Post + Json 방식
    public void sendAndReceiveMachineTranslationResult(Sentence[] sentences) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        MachineTranslationRequestJSON requestJSON = new MachineTranslationRequestJSON(pictureFileName, sentences);
/*
        for(int i=0; i<requestJSON.getSentences().length; i++) {
            Log.d("WeHealed JSON", requestJSON.getSentences()[i].getOriginal_sentence());
        }*/
        Call<MachineTranslationResponseJSON> call = retrofitService.getJSON(requestJSON);
        call.enqueue(new Callback<MachineTranslationResponseJSON>() {
            @Override
            public void onResponse(Call<MachineTranslationResponseJSON> call, Response<MachineTranslationResponseJSON> response) {
                MachineTranslationResponseJSON repo = response.body();
                processResponse(repo);
            }

            @Override
            public void onFailure(Call<MachineTranslationResponseJSON> call, Throwable t) {
                Log.d("WeHealed", "Machine Translation Failure");
                Toast.makeText(getApplicationContext(), "Machine Translation Failure", Toast.LENGTH_LONG).show();
            }
        });
    }

    // 서버로부터 번역 결과를 받아 처리한다
    protected void processResponse(MachineTranslationResponseJSON responseJSON) {

        String machineTranslateResponse = "";

        if(responseJSON != null) {

            // TODO : 서버 수신 데이터 -> DB 에 기록한다
            machineTranslateResponse = responseJSON.getPicture_file_name();
            try {
                dbHelper.exec("UPDATE PICTURE_HISTORY_V5 SET " +
                        "MACHINE_TRANSLATION_RESULT = '" + machineTranslateResponse + "' " +
                        "WHERE HISTORY_ID = '" + historyId + "';");

                Log.d("WeHealed Response", "Machine Translation Result Saved :" + machineTranslateResponse);
                Log.d("WeHealed Response", responseJSON.getSentences()[0].getOriginal_sentence());
                Log.d("WeHealed Response",responseJSON.getDescribing_urls()[0].getKey() + "  :  " + responseJSON.getDescribing_urls()[0].getUrl());
            } catch (Exception e) {
                e.printStackTrace();

                Log.d("WeHealed", "Machine Translation Result Save Failed");
            }

            // 서버 수신 데이터 -> 임시로 원본 표시
            Log.d("WeHealed Response",responseJSON.toString());
            //machineTranslateResultTextView.setText(responseJSON.getPicture_file_name());
            machineTranslateResultTextView.setText(responseJSON.toString());

            // TODO : 서버 수신 데이터 -> 파싱해서 표시
            String machineTranslateResponseSentences = "안녕|세계";
            translationResultListViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

            ArrayList<String> arrayList = new ArrayList<String>();
            StringTokenizer st = new StringTokenizer(machineTranslateResponseSentences, "|");
            while (st.hasMoreTokens()) {
                String sentence = st.nextToken();
                arrayList.add(sentence);
                translationResultListViewAdapter.add(sentence);
            }
            translationResultListView.setAdapter(translationResultListViewAdapter);
        }
    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_go_human_translation_request_activity:
                    Intent intent = new Intent(getApplicationContext(), Activity_35_Human_Translation_Request.class);
                    intent.putExtra("historyId", historyId);
                    intent.putExtra("picturePathAndFileName", picturePathAndFileName);
                    intent.putExtra("pictureFileName", pictureFileName);
                    startActivity(intent);
                    break;
            }
        }
    };
}
