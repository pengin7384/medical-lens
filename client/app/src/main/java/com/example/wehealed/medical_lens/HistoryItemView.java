package com.example.wehealed.medical_lens;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HistoryItemView extends LinearLayout {

    ImageView imageView_pictureImage;
    TextView textView_pictureFileName;
    TextView textView_summary;

    public HistoryItemView(Context context) {
        super(context);

        init(context);
    }

    public HistoryItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_history, this, true);

        textView_pictureFileName = (TextView) findViewById(R.id.textView_picture_file_name);
        textView_summary= (TextView) findViewById(R.id.textView_summary);

        imageView_pictureImage = (ImageView) findViewById(R.id.picture_image);
    }

    public void setSummary(String summaryText) {
        textView_summary.setText(summaryText);
    }

    public void setPicture(String picturePathAndFileName, String pictureFileName, int pictureTime) {

        // 사진 파일
        textView_pictureFileName.setText(pictureFileName);

        File imgFile = new  File(picturePathAndFileName);
        if(imgFile.exists()){
            Bitmap originalBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0,0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);

            imageView_pictureImage.setImageBitmap(rotatedBitmap);
        }

        //long pictureTimeLong = (long)pictureTime;
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

        // 사진 촬영 시각 timestamp
        //String pictureTimeStampString = TimeUnit.MILLISECONDS.toSeconds(pictureTimeLong) + "";
        //textView_pictureTimeStamp.setText(pictureTimeStampString);

        // 사진 촬영 시각 텍스트
        //String pictureTimeString = sdf.format(new Date(pictureTimeLong));
        //textView_pictureTime.setText(pictureTimeString);

    }
}
