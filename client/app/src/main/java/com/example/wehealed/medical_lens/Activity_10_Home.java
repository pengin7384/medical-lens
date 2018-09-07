package com.example.wehealed.medical_lens;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.util.ArrayList;


public class Activity_10_Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE = 99;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    DBHelper dbHelper;

    ListView historyListView;
    HistoryListAdapter historyListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_10__home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        // 왼쪽에서 열리는 서랍
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try {
            dbHelper = DBHelper.getInstance(getApplicationContext());
        }
        catch (Exception e) {
            Log.i(Constants.LOG_TAG, e.toString());
        }

        findViewById(R.id.button_camera).setOnClickListener(mClickListener);
        findViewById(R.id.button_gallery).setOnClickListener(mClickListener);
        findViewById(R.id.button_clear_history).setOnClickListener(mClickListener);

        historyListView = (ListView) findViewById(R.id.history_listview);
        historyListAdapter = new HistoryListAdapter(this); // android.R.layout.simple_list_item_multiple_choice
        historyListView.setAdapter(historyListAdapter);


        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HistoryItem item = (HistoryItem) historyListAdapter.getItem(position);

                Intent intent = new Intent(getApplicationContext(), Activity_30_Translate_Result.class);
                intent.putExtra("historyId", item.getHistoryId());

                Toast.makeText(getApplicationContext(), "선택 : " + item.getPictureFileName(), Toast.LENGTH_LONG).show();
                startActivity(intent);
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareInformation();
        loadHistoryList();
    }

    protected void prepareInformation() {

    }

    protected void loadHistoryList() {
        // 클라이언트 DB 에서 히스토리를 읽어들여서 화면에 표시한다
        try {
            Cursor cursor = dbHelper.get("SELECT * FROM PICTURE_HISTORY_V5 ORDER BY HISTORY_ID DESC");

            try {
                if (cursor.moveToFirst()) {

                    historyListAdapter = new HistoryListAdapter(this);

                    do {
                        int historyId = cursor.getInt(cursor.getColumnIndex("HISTORY_ID"));
                        int pictureTime = cursor.getInt(cursor.getColumnIndex("PICTURE_TIME"));
                        String picturePathAndFileName = cursor.getString(cursor.getColumnIndex("PICTURE_PATH_AND_FILE_NAME"));
                        String pictureFileName = cursor.getString(cursor.getColumnIndex("PICTURE_FILE_NAME"));
                        String summaryText = cursor.getString(cursor.getColumnIndex("SUMMARY_TEXT"));

                        Log.i(Constants.LOG_TAG, "HISTORY_ID " + historyId
                                + " PICTURE_TIME" + pictureTime
                                + " PICTURE_PATH_AND_FILE_NAME " + picturePathAndFileName
                                + " PICTURE_FILE_NAME " + pictureFileName
                                + " SUMMARY_TEXT " + summaryText
                        );

                        HistoryItem newItem = new HistoryItem(historyId, picturePathAndFileName, pictureFileName, pictureTime, summaryText);
                        historyListAdapter.addItem(newItem);

                    } while (cursor.moveToNext());

                    historyListView.setAdapter(historyListAdapter);
                    historyListAdapter.notifyDataSetChanged();
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
        }
        catch (Exception e) {
            Log.i(Constants.LOG_TAG, e.toString());
        }
    }

    /**
     * Drawer 메뉴 눌렀을 때 처리
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Drawer 눌렀다가 배경 눌렀을 때 처리
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_camera:
                    Intent intent = new Intent(Activity_10_Home.this, Activity_20_Camera.class);
                    startActivity(intent);
                    break;
                case R.id.button_gallery:
                    requestRead();
                    break;
                case R.id.button_clear_history:
                    clearHistory();
                    break;
            }

        }
    };

    public void clearHistory() {

//        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//        builder.setTitle("Clear History")
//                .setMessage(R.string.clear_history_confirm)
//                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        clearHistory();
//                        Toast.makeText(getApplicationContext(),"삭제했습니다",Toast.LENGTH_LONG).show();
//                    }
//                })
//                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                })
//                .show();

        try {
            dbHelper.get("DELETE FROM PICTURE_HISTORY_V5;");

            Log.i(Constants.LOG_TAG, "DELETE FROM PICTURE_HISTORY_V5;");

            historyListAdapter.clear();
            historyListAdapter.notifyDataSetChanged();
//            historyListAdapter = new HistoryListAdapter(this); // android.R.layout.simple_list_item_multiple_choice
//            historyListView.setAdapter(historyListAdapter);
        }
        catch (Exception e) {
            Log.i(Constants.LOG_TAG, e.toString());
        }
    }

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
        Intent intent2 = new Intent(Activity_10_Home.this, ScanActivity.class);
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
                Toast.makeText(Activity_10_Home.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // 의무기록 촬영 및 해석 히스토리
    public class HistoryListAdapter extends BaseAdapter {

        private Context mContext;

        ArrayList<HistoryItem> items = new ArrayList<HistoryItem>();

        public HistoryListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return Math.max(items.size(), 10);
            //return items.size();
        }

        public void clear() {
            items.clear();
        }

        public void addItem(HistoryItem item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {

            HistoryItemView itemView;
            if (convertView == null) {
                itemView = new HistoryItemView(mContext);
            }
            else {
                itemView = (HistoryItemView) convertView;
            }

            try {
                HistoryItem item = items.get(position);

                if (item != null) {
                    itemView.setPicture(
                            item.getPicturePathAndFileName(),
                            item.getPictureFileName(),
                            item.getPictureTime());
                    itemView.setSummary(item.getSummaryText());
                }
            }
            catch(Exception e) {
                Log.d("WeHealed", e.toString());
            }

            return itemView;
        }
    }
}
