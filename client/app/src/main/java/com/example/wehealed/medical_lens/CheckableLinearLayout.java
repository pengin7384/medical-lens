package com.example.wehealed.medical_lens;

/*
0906 choi sanghee
http://recipes4dev.tistory.com/68
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements Checkable {

    // 만약 CheckBox가 아닌 View를 추가한다면 아래의 변수 사용 가능.
    // private boolean mIsChecked ;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        // mIsChecked = false ;
    }

    @Override
    public boolean isChecked() {
        //CheckBox cb = (CheckBox) findViewById(R.id.checkBox1) ;
        //return cb.isChecked() ;
         return false ;
    }

    @Override
    public void toggle() {
//        Log.d("list_toggle",String.valueOf("토글"));
//        CheckBox cb = (CheckBox) findViewById(R.id.checkBox1) ;
//
//        setChecked(cb.isChecked() ? false : true) ;
//        setChecked(mIsChecked ? false : true) ;
    }

    @Override
    public void setChecked(boolean checked) {
//        CheckBox cb = (CheckBox) findViewById(R.id.checkBox1) ;

//        if (cb.isChecked() != checked) {
//            cb.setChecked(checked) ;
//        }

        // CheckBox 가 아닌 View의 상태 변경.
    }
}
