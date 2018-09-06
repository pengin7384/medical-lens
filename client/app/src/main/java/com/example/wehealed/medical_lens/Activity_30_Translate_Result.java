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

import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_30_Translate_Result extends AppCompatActivity {
    //static final String[] list = {"123","123"};
    static final String URL = "https://wehealedapi.run.goorm.io/api/";
    private ArrayList<String> list;
    private SparseArray<TextBlock> items;

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

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        ListView listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(adapter);

        String text = "";
        for(int i=0; i<list.size(); i++) {
            text = text + list.get(i) + "\n";
        }

        index(text);

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
