package com.letv.jjfa_focus;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.android_test.R;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();

    FocusViewOnDraw focusViewOnDraw = null;

    /*
     * 数据
     */
    private int viewId[] = {R.id.button1, R.id.Button2, R.id.Button3,
            R.id.Button4, R.id.button5, R.id.Button6, R.id.Button7};

    private int currentPosition = 0;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 父容器布局
        //
        focusViewOnDraw = (FocusViewOnDraw) findViewById(R.id.FocusViewOnDraw);

        // 上一个
        findViewById(R.id.pre_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (currentPosition > 0) {
                    currentPosition--;

                    focusViewOnDraw.moveToDestin(
                            //
                            findViewById(viewId[currentPosition]),
                            //
                            FocusViewOnDraw.SHOW_TRANSLATE_AND_SCALE_ANIMATION);
                }

            }
        });

        // 下一个
        findViewById(R.id.next_btn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //
                if ((currentPosition + 1) < viewId.length) {
                    currentPosition++;

                    focusViewOnDraw.moveToDestin(
                            //
                            findViewById(viewId[currentPosition]),
                            //
                            FocusViewOnDraw.SHOW_TRANSLATE_AND_SCALE_ANIMATION);
                }

            }
        });


        focusViewOnDraw.moveToDestin(
                //
                findViewById(viewId[currentPosition]),
                //
                FocusViewOnDraw.SHOW_TRANSLATE_AND_SCALE_ANIMATION);

    }

}
