package com.base.basepedo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.base.basepedo.R;
import com.base.basepedo.service.StepDcretor;
import com.base.basepedo.service.StepService;

public class MainActivity extends AppCompatActivity implements Handler.Callback{
	private TextView text_step;
	private final int STEPSHOW = 1;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHandler = new Handler(this);
		text_step = (TextView) findViewById(R.id.text_step);

		Intent intent = new Intent(this, StepService.class);
		startService(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessage(STEPSHOW);
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
			case STEPSHOW:
				mHandler.removeMessages(STEPSHOW);
				text_step.setText(StepDcretor.CURRENT_SETP + "");
				mHandler.sendEmptyMessageDelayed(STEPSHOW, 150);
				break;
		}
		return false;
	}
}
