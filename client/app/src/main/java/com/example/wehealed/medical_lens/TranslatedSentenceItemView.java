package com.example.wehealed.medical_lens;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.regex.Pattern;

public class TranslatedSentenceItemView extends LinearLayout {

    TextView textView_original_sentence;
    TextView textView_translated_sentence_by_wehealed;
    TextView textView_translated_sentence_by_google;

    public TranslatedSentenceItemView(Context context) {
        super(context);

        init(context);
    }

    public TranslatedSentenceItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_translated_sentence, this, true);

        textView_original_sentence = (TextView) findViewById(R.id.textView_original_sentence);
        textView_translated_sentence_by_google= (TextView) findViewById(R.id.textView_translated_sentence_by_google);
        textView_translated_sentence_by_wehealed= (TextView) findViewById(R.id.textView_translated_sentence_by_wehealed);
    }

    public void setValue(String original_sentence, String translated_sentence_by_google, String translated_sentence_by_wehealed) {
        textView_original_sentence.setText(original_sentence);
        textView_translated_sentence_by_google.setText(translated_sentence_by_google);
        textView_translated_sentence_by_wehealed.setText(translated_sentence_by_wehealed);
    }

    public void applyLink(Pattern pattern, String url, Linkify.TransformFilter filter) {
        Linkify.addLinks(textView_translated_sentence_by_wehealed, pattern, url, null, filter);
    }

}
