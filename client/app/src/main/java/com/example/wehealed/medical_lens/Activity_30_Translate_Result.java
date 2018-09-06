package com.example.wehealed.medical_lens;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_30_Translate_Result extends AppCompatActivity {
    //static final String[] list = {"123","123"};
    static final String URL = "https://wehealedapi2.run.goorm.io/api/";
    private ArrayList<String> list;
    private SparseArray<TextBlock> items;
    private String fileName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_30__translate_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_translate_result);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼

        Intent intent = getIntent();
        ArrayList<String> list = intent.getStringArrayListExtra("items");
        //fileName = intent.getStringExtra("name");
        fileName = "test.jpg";

        Toast.makeText(getApplicationContext(), "파일명: " + fileName, Toast.LENGTH_LONG).show();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        ListView listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(adapter);
/*
        String text = "";
        for(int i=0; i<list.size(); i++) {
            text = text + list.get(i) + "\n";
        }*/

        Sentence[] sentences = new Sentence[list.size()];

        for(int i=0; i<list.size(); i++) {
            Sentence s = new Sentence(i+1,list.get(i),"");
            sentences[i] = s;
        }

        index2(sentences);




        //index(sentences);

        findViewById(R.id.button_go_human_translation_request_activity).setOnClickListener(mClickListener);
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

    // 서버 통신
    public void index(String text) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        //Call<TextItem> call = retrofitService.getIndex("mos");
        Call<TextItem> call = retrofitService.getIndex(text);
        call.enqueue(new Callback<TextItem>() {
            @Override
            public void onResponse(Call<TextItem> call, Response<TextItem> response) {
                TextItem repo = response.body();
                if(repo != null) {
                    //textViewIndex.setText(repo.getName());'
                    //Log.d(this.getClass().getName(), repo.getText());

                    Log.d("WeHealed Result", ":" + repo.getText());
                }
            }

            @Override
            public void onFailure(Call<TextItem> call, Throwable t) {

            }
        });
    }

    // Post + Json방식
    public void index2(Sentence[] sentences) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        MachineTranslationRequestJSON requestJSON = new MachineTranslationRequestJSON(fileName,sentences);
/*
        for(int i=0; i<requestJSON.getSentences().length; i++) {
            Log.d("WeHealed JSON", requestJSON.getSentences()[i].getOriginal_sentence());
        }*/
        Call<MachineTranslationResponseJSON> call = retrofitService.getJSON(requestJSON);
        call.enqueue(new Callback<MachineTranslationResponseJSON>() {
            @Override
            public void onResponse(Call<MachineTranslationResponseJSON> call, Response<MachineTranslationResponseJSON> response) {
                MachineTranslationResponseJSON repo = response.body();
                Log.d("WeHealed Response","Response");
                if(repo != null) {
                    Log.d("WeHealed Response",repo.getPicture_file_name());
                    Log.d("WeHealed Response",repo.getSentences()[0].getOriginal_sentence());
                    Log.d("WeHealed Response",repo.getDescribing_urls()[0].getKey() + "  :  " + repo.getDescribing_urls()[0].getUrl());

                }
            }

            @Override
            public void onFailure(Call<MachineTranslationResponseJSON> call, Throwable t) {

            }
        });
    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_go_human_translation_request_activity:
                    Intent intent = new Intent(getApplicationContext(), Activity_35_Human_Translation_Request.class);
                    startActivity(intent);
                    break;
            }
        }
    };
}
