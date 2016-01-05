package com.base.basepedo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.base.basepedo.R;
import com.base.basepedo.service.StepDcretor;
import com.base.basepedo.service.StepService;

public class MainActivity extends ActionBarActivity {
	private TextView text_step;
	private final int STEPSHOW = 1;

	Handler mHandler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case STEPSHOW:
				mHandler.removeMessages(STEPSHOW);
				text_step.setText(StepDcretor.CURRENT_SETP + "");
				mHandler.sendEmptyMessageDelayed(STEPSHOW, 150);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		text_step = (TextView) findViewById(R.id.text_step);

		Intent intent = new Intent(this, StepService.class);
		startService(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessage(STEPSHOW);
	}
}
