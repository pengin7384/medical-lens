package com.example.wehealed.medical_lens;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class CustomView extends View {
    public Paint pnt = null;
    public TokenResponseJSON data = null;
    public int textSize;
    public int base_x = 200;
    public int base_Y = 300;

    public String pos_tag[] = {
        "UNKNOWN", "ADJ", "ADP", "ADV", "CONJ", "DET", "NOUN", "NUM",
                "PRON", "PRT", "PUNCT", "VERB", "X", "AFFIX"   };
    public String label[] = {
            "UNKNOWN", "ABBREV", "ACOMP", "ADVCL", "ADVMOD", "AMOD", "APPOS", "ATTR", "AUX", "AUXPASS", "CC", "CCOMP", "CONJ", "CSUBJ", "CSUBJPASS", "DEP", "DET", "DISCOURSE", "DOBJ",
            "EXPL", "GOESWITH", "IOBJ", "MARK", "MWE", "MWV", "NEG", "NN", "NPADVMOD", "NSUBJ", "NSUBJPASS", "NUM", "NUMBER", "P", "PARATAXIS", "PARTMOD", "PCOMP", "POBJ", "POSS",
            "POSTNEG", "PRECOMP", "PRECONJ", "PREDET", "PREF", "PREP", "PRONL", "PRT", "PS", "QUANTMOD", "RCMOD", "RCMODREL", "RDROP", "REF", "REMNANT", "REPARANDUM", "ROOT", "SNUM",
            "SUFF", "TMOD", "TOPIC", "VMOD", "VOCATIVE", "XCOMP", "SUFFIX", "TITLE", "ADVPHMOD", "AUXCAUS", "AUXVV", "DTMOD", "FOREIGN", "KW", "LIST", "NOMC", "NOMCSUBJ", "NOMCSUBJPASS",
            "NUMC", "COP", "DISLOCATED", "ASP", "GMOD", "GOBJ", "INFMOD", "MES", "NCOMP" };


    public CustomView(Context context) {
        super(context);

    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void drawTree(TokenResponseJSON input) {
        data = input;
        textSize = 70;

        invalidate();
    }

    public void drawPosText(Canvas canvas, String text, int x, int y, int size, int color) {
        int length = text.length();

        //canvas.drawText(tl[i].getText().getContent(),base_x+index*textSize
        // ,base_Y+200,pnt);
        pnt.setTextSize((float)size);
        pnt.setColor(color);
        canvas.drawText(text, x-length/2*pxToDp(size), y-textSize/2,pnt);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        pnt = new Paint();
        Log.d("WeHealed CustomView", "OnDraw");

        canvas.drawColor(Color.WHITE);
        if(data != null) {
            if(data.tokens[0].getText().getContent()==null)
                return;
            Token[] tl = data.getTokens();
            pnt.setAntiAlias(true);
            pnt.setColor(Color.BLACK);

            pnt.setTextSize((float)textSize);

            Log.d("WeHealed CustomView", ""+tl[0].getText().getContent());
           // textView.setText(tl[0].getText().getContent());
            //canvas.drawText(tl[0].getText().getContent(),base_x,base_Y,pnt);
            int index=0;

            int[] start_x = new int[tl.length];
            int[] len = new int[tl.length];



            for(int i=0; i<tl.length; i++) {
                start_x[i] = base_x+index*textSize;
                len[i] = tl[i].getText().getContent().length();


                //canvas.drawText(pos_tag[tl[i].getPart_of_speech().getTag()],base_x+index*textSize,base_Y+300,pnt);
                //canvas.drawText(tl[i].getText().getContent(),base_x+index*textSize,base_Y+200,pnt);
                int num = Integer.parseInt(tl[i].getDependency_edge().getLabel());

                drawPosText(canvas, label[num], base_x+index*textSize,base_Y+100,50,Color.YELLOW);
                drawPosText(canvas, tl[i].getText().getContent(), base_x+index*textSize,base_Y+200,70, Color.BLACK);
                drawPosText(canvas, pos_tag[tl[i].getPart_of_speech().getTag()], base_x+index*textSize,base_Y+300,30, Color.RED);



                index += len[i]/2 + 2;
            }

            for(int i=0; i<tl.length; i++) {
                int target_index = tl[i].getDependency_edge().getHeadTokenIndex();
                if(i!=target_index) {
                    //drawFunc(canvas, start_x[i], 300, start_x[target_index], 300);
                    int distance = target_index-i;
                    if(distance<0) {
                        distance = -1 * distance;
                    }
                    drawFunc(canvas, start_x[i]+pxToDp(tl[i].getText().getContent().length()/2), 400, start_x[target_index]+pxToDp(tl[target_index].getText().getContent().length()/2), 400, distance);
                }

            }
        }
        /*
        canvas.drawColor(Color.RED);
        pnt.setAntiAlias(true);
        pnt.setColor(Color.WHITE);
        canvas.drawCircle(150, 250, 15, pnt);*/
    }

    public void drawFunc(Canvas canvas, int x1, int y1, int x2, int y2, int distance) {
        float cx1, cy1, cx2, cy2;
        cx1 = x1 + ((x2-x1)/2);
        cx2 = x2 - ((x2-x1)/2);
        cy1 = y2;
        cy2 = y1;


        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        // 굵기
        paint.setStrokeWidth(6);
        // 선
        paint.setStyle(Paint.Style.STROKE);

        // 점선

        DashPathEffect dashPath = new DashPathEffect(new float[]{5,5}, 2);
        paint.setPathEffect(dashPath);

        Path path = new Path();
        path.reset();
        path.moveTo(x1, y1);
        //path.cubicTo(cx1, cy1, cx2, cy2, x2, y2);
        path.cubicTo(x1, y1, (x1+x2)/2, y1-150-distance*50, x2, y2);
        canvas.drawPath(path, paint);

        canvas.drawLine(x2,y2+15,x2-20,y2-20+15,paint);
        canvas.drawLine(x2,y2+15,x2+20,y2-20+15,paint);

    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
