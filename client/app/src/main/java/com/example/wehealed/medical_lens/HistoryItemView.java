package com.example.wehealed.medical_lens;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HistoryItemView extends LinearLayout {

    ImageView imageView_pictureImage;
    TextView textView_pictureTime;
    TextView textView_pictureFileName;

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

        textView_pictureTime = (TextView) findViewById(R.id.picture_time);
        textView_pictureFileName = (TextView) findViewById(R.id.text_view_picture_file_name);
        imageView_pictureImage = (ImageView) findViewById(R.id.picture_image);
    }

    public void setHistoryId(int historyId) {
        textView_pictureTime.setText(String.valueOf(historyId));
    }

    public void setPictureFileName(String pictureFileName) {
        textView_pictureFileName.setText(pictureFileName);
        //imageView.setImageResource(resId);
    }
}
