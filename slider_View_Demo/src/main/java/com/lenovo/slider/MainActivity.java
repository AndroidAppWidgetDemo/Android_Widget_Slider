package com.lenovo.slider;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.lenovo.focus.R;

public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = MainActivity.class.getName();

	ViewGroup contentView = null;

	SliderViewOnDraw sliderViewOnDraw = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 父容器布局
		contentView = (ViewGroup) findViewById(R.id.content);
		for (int i = 0; i < contentView.getChildCount(); i++) {

			contentView.getChildAt(i).setOnClickListener(this);
		}

		sliderViewOnDraw = (SliderViewOnDraw) findViewById(R.id.slider);

		sliderViewOnDraw.moveToDestin(contentView.getChildAt(0),
				SliderViewOnDraw.SHOW_TRANSLATE_AND_SCALE_ANIMATION);

	}

	@Override
	public void onClick(View v) {

		sliderViewOnDraw.moveToDestin(v,
				SliderViewOnDraw.SHOW_TRANSLATE_AND_SCALE_ANIMATION);

	}

}
