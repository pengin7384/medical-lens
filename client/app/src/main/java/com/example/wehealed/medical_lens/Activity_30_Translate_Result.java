package com.example.wehealed.medical_lens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class Activity_30_Translate_Result extends AppCompatActivity {

    //private SparseArray<TextBlock> items;

    DBHelper dbHelper;

    int historyId = 0;
    String picturePathAndFileName = "";
    String pictureFileName = "";
    int pictureTime = 0;
    String originalText = "";
    String summaryText = "";

    ArrayList<String> originalSentenceArrayList;
    boolean isToRetryTranslation = false;

    Button buttonRequestAgain;

    ImageView pictureImageView;
//    TextView machineTranslateResultTextView;
//    TextView pictureFileNameTextView;

//    ListView originalTextListView;
//    ArrayAdapter originalTextListViewAdapter;

    ListView translationResultListView;
    ArrayAdapter translationResultListViewAdapter;

    HorizontalScrollView horizontalScrollView = null;

    TextView textView;
    CustomView customView;
    Boolean treeSwitch = false;

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
//        machineTranslateResultTextView = (TextView) findViewById(R.id.machine_translation_response);
//        pictureFileNameTextView = (TextView) findViewById(R.id.textView_picture_file_name);

//        originalTextListView = (ListView)findViewById(R.id.listView_original_text);
//        originalTextListViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
//        originalTextListView.setAdapter(originalTextListViewAdapter);

        //photoView = (PhotoView)findViewById(R.id.photo_view);

        translationResultListView = (ListView)findViewById(R.id.listView_translation_result);
        translationResultListViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        translationResultListView.setAdapter(translationResultListViewAdapter);

        findViewById(R.id.button_go_human_translation_request_activity).setOnClickListener(mClickListener);
        findViewById(R.id.button_translation_warning).setOnClickListener(mClickListener);
        findViewById(R.id.button_request_again).setOnClickListener(mClickListener);

        buttonRequestAgain = (Button)findViewById(R.id.button_request_again);

        horizontalScrollView = (HorizontalScrollView)findViewById(R.id.horizontalScrollView);
        //vv.requestDisallowInterceptTouchEvent(true);
        //vv.addView(customView);
        //customView.getParent().requestDisallowInterceptTouchEvent(true);
        customView = (CustomView) findViewById(R.id.customView);

        // TODO: Set up the Text To Speech engine.

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
            Bitmap originalBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0,0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);

            pictureImageView.setImageBitmap(rotatedBitmap);

            //photoView.setImageBitmap(rotatedBitmap);
            PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(pictureImageView);
            photoViewAttacher.setScaleType(ImageView.ScaleType.FIT_XY);
        }


        // 클라이언트 DB 데이터 -> 사진 파일이름 표시
//        pictureFileNameTextView.setText(pictureFileName);


        // 클라이언트 DB 데이터 -> originalText 표시
        StringTokenizer st = new StringTokenizer(originalText, "|");
        originalSentenceArrayList = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String sentence = st.nextToken();
            originalSentenceArrayList.add(sentence);
        }
//        originalTextListViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
//        originalTextListViewAdapter.addAll(originalSentenceArrayList);
//        originalTextListView.setAdapter(originalTextListViewAdapter);

        translationResultListViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        translationResultListViewAdapter.addAll(originalSentenceArrayList);
        translationResultListView.setAdapter(translationResultListViewAdapter );

        isToRetryTranslation = false;
        buttonRequestAgain.setEnabled(isToRetryTranslation);

        sendAndReceiveMachineTranslationResult();
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

    public void sendAndReceiveMachineTranslationResult() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.requestBaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        Sentence[] sentences = new Sentence[originalSentenceArrayList.size()];
        for(int i=0; i<originalSentenceArrayList.size(); i++) {
            Sentence s = new Sentence(i+1,originalSentenceArrayList.get(i),"", "");
            sentences[i] = s;
            Log.d("WeHealed Trans", s.getOriginal_sentence());
        }

        MachineTranslationRequestJSON requestJSON = new MachineTranslationRequestJSON(pictureFileName, sentences);
/*
        for(int i=0; i<requestJSON.getSentences().length; i++) {
            Log.d("WeHealed JSON", requestJSON.getSentences()[i].getOriginal_sentence());
        }*/

        // TODO : 최신버전의 서버 API 를 호출한다
        Call<MachineTranslationResponseJSON> call = retrofitService.getJSON(requestJSON);
        call.enqueue(new Callback<MachineTranslationResponseJSON>() {
            @Override
            public void onResponse(Call<MachineTranslationResponseJSON> call, Response<MachineTranslationResponseJSON> response) {

                if (response.isSuccessful()) {
                    MachineTranslationResponseJSON repo = response.body();
                    processResponse(repo);
                }
                else {
                    isToRetryTranslation = true;
                    buttonRequestAgain.setEnabled(isToRetryTranslation);
                }
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

        String responseSentences = "";
        String originalSentences = "";
        String translatedSentencesByGoogle = "";
        String translatedSentencesByWeHealed = "";

        if(responseJSON != null) {

            // 서버 수신 데이터 -> DB 에 기록한다
            String responsePictureFileName = responseJSON.getPicture_file_name();
            String responseTime = responseJSON.getResponse_time();

            Sentence[] sentences = responseJSON.getSentences();
            for(int i=0; i<sentences.length; i++) {
                responseSentences += sentences[i].getSentence_number() + "|";

                responseSentences += sentences[i].getOriginal_sentence() + "|";
                originalSentences += sentences[i].getOriginal_sentence() + "\n";

                responseSentences += sentences[i].getTranslated_sentence_by_wehealed() + "`";
                translatedSentencesByWeHealed += sentences[i].getTranslated_sentence_by_wehealed() + "\n";

                responseSentences += sentences[i].getTranslated_sentence_by_google() + "`";
                translatedSentencesByGoogle += sentences[i].getTranslated_sentence_by_google() + "\n";
            }

            Summary[] summaries = responseJSON.getSummaries();
            String summaryText = new String();
            int summarySentenceNumber = 0;
            for(int i=0; i<summaries.length; i++) {
                summaryText = summaries[i].getSummary_text();
                summarySentenceNumber = summaries[i].getSummary_number();
            }


            try {
                dbHelper.exec("UPDATE PICTURE_HISTORY_V5 SET " +
                        "MACHINE_TRANSLATION_RESULT = '" + responseSentences + "' " +
                        ", SUMMARY_TEXT = '" + summaryText + "' " +
                        ", SUMMARY_SENTENCE_NUMBER = '" + summarySentenceNumber + "' " +
                        "WHERE HISTORY_ID = '" + historyId + "';");

                Log.d("WeHealed Response", "Machine Translation HistoryId " + historyId);
                Log.d("WeHealed Response", "originalSentences : " + originalSentences);
                Log.d("WeHealed Response", "translatedSentencesByGoogle : " + translatedSentencesByGoogle);
                Log.d("WeHealed Response", "translatedSentencesByWeHealed : " + translatedSentencesByWeHealed);
                Log.d("WeHealed Response", "summaryText : " + summaryText);
                Log.d("WeHealed Response", "summarySentenceNumber : " + summarySentenceNumber);
                Log.d("WeHealed Response", responseJSON.getSentences()[0].getOriginal_sentence());
                Log.d("WeHealed Response",responseJSON.getDescribing_urls()[0].getKey() + "  :  " + responseJSON.getDescribing_urls()[0].getUrl());
            } catch (Exception e) {
                e.printStackTrace();

                Log.d("WeHealed", "Machine Translation Result Save Failed");
            }

            // 서버 수신 데이터 -> 원본 표시
//            originalTextListViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
//            for(int i=0; i<sentences.length; i++) {
//                String sentence = new String();
//                sentence += sentences[i].getOriginal_sentence() + "\n";
//                originalTextListViewAdapter.add(sentence);
//            }
//            originalTextListView.setAdapter(originalTextListViewAdapter);

            // 서버 수신 데이터 -> 번역 결과 표시
            translationResultListViewAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
            for(int i=0; i<sentences.length; i++) {
                String sentence = new String();
                sentence += sentences[i].getOriginal_sentence() + "\n";
                sentence += sentences[i].getTranslated_sentence_by_google() + "\n";
                sentence += sentences[i].getTranslated_sentence_by_wehealed() + "";
                translationResultListViewAdapter.add(sentence);
            }
            translationResultListView.setAdapter(translationResultListViewAdapter);
            translationResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String selectedItem = (String) parent.getItemAtPosition(position);
                    //Log.d("WeHealed Click", "\n"+selectedItem);
                    //Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_LONG).show();

                    if(treeSwitch==false) {
                        String[] data = selectedItem.split("\n");
                        sendAndReceiveToken(data[0]);
                        Log.d("WeHealed Click", ":::"+data[0]);
                        Log.d("WeHealed Click", ":::"+data[1]);
                        Log.d("WeHealed Click", ":::"+data[2]);


                        treeSwitch=true;

                    } else {
                        horizontalScrollView.setVisibility(View.INVISIBLE);
                        treeSwitch=false;
                    }

                }
            });

            // TODO : 서버 수신 데이터 -> Summary 표시
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
                case R.id.button_translation_warning :
                    showTranslationWarning();
                    break;
                case R.id.button_request_again :
                    isToRetryTranslation = false;
                    buttonRequestAgain.setEnabled(isToRetryTranslation);
                    sendAndReceiveMachineTranslationResult();
                    break;
            }
        }
    };

    protected void showTranslationWarning() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Warning")
                .setMessage(R.string.translation_warning_detail)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    public void sendAndReceiveToken(final String text) {
        String URL = "https://wehealedapi2.run.goorm.io/api/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        Call<TokenResponseJSON> call = retrofitService.getJSON(text);
        call.enqueue(new Callback<TokenResponseJSON>() {
            @Override
            public void onResponse(Call<TokenResponseJSON> call, Response<TokenResponseJSON> response) {
                TokenResponseJSON repo = response.body();
                //Log.d("WeHealed", String.valueOf(repo.tokens[0].getText().getContent()));
                Token[] tl = repo.getTokens();

                //Toast.makeText(getApplicationContext(),String.valueOf(tl.length),Toast.LENGTH_LONG).show();
                customView.drawTree(repo);
                horizontalScrollView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<TokenResponseJSON> call, Throwable t) {
                Log.d("WeHealed", "Token Failure");
                Toast.makeText(getApplicationContext(), "Token Failure", Toast.LENGTH_LONG).show();
            }
        });
    }



}
