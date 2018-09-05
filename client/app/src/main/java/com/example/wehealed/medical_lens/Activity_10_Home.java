package com.example.wehealed.medical_lens;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

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

        historyListView = (ListView) findViewById(R.id.history_listview);
        historyListAdapter = new HistoryListAdapter(this); // android.R.layout.simple_list_item_multiple_choice
        historyListView.setAdapter(historyListAdapter);


        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HistoryItem item = (HistoryItem) historyListAdapter.getItem(position);
                Toast.makeText(getApplicationContext(), "선택 : " + item.getPictureFileName(), Toast.LENGTH_LONG).show();
                
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            Cursor cursor = dbHelper.get("SELECT * FROM PICTURE_HISTORY_V5 ORDER BY HISTORY_ID ASC");

            try {
                if (cursor.moveToFirst()) {
                    do {
                        int historyId = cursor.getInt(cursor.getColumnIndex("HISTORY_ID"));
                        String pictureFileName = cursor.getString(cursor.getColumnIndex("PICTURE_FILE_NAME"));

                        Log.i(Constants.LOG_TAG, "historyId " + historyId + " pictureFileName " + pictureFileName);

                        historyListAdapter.addItem(new HistoryItem(historyId, pictureFileName));
                        //historyListAdapter.notifyDataSetChanged();
                    } while (cursor.moveToNext());
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
            return items.size();
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


            itemView.setHistoryId(items.get(position).getHistoryId());
            itemView.setPictureFileName(items.get(position).getPictureFileName());


            return itemView;
        }
    }
}
