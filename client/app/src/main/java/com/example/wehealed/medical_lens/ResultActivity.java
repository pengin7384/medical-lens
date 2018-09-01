package com.example.wehealed.medical_lens;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultActivity extends AppCompatActivity {
    //static final String[] list = {"123","123"};
    static final String URL = "https://wehealedapi.run.goorm.io/api/";
    private ArrayList<String> list;
    private SparseArray<TextBlock> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        ArrayList<String> list = intent.getStringArrayListExtra("items");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        ListView listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(adapter);

        index();
    }


    // 서버 통신
    public void index() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        //Call<TextItem> call = retrofitService.getIndex("mos");
        Call<TextItem> call = retrofitService.getIndex("asdf");
        call.enqueue(new Callback<TextItem>() {
            @Override
            public void onResponse(Call<TextItem> call, Response<TextItem> response) {
                TextItem repo = response.body();
                if(repo != null) {
                    //textViewIndex.setText(repo.getName());'
                    //Log.d(this.getClass().getName(), repo.getText());

                    Log.d("Result", ":" + repo.getText());
                }
            }

            @Override
            public void onFailure(Call<TextItem> call, Throwable t) {

            }
        });
    }

}
