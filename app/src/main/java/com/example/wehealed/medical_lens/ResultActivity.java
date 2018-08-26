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

public class ResultActivity extends AppCompatActivity {
    //static final String[] list = {"123","123"};
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




    }
}
