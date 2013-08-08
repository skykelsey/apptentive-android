/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.apptentive.android.sdk.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Sky Kelsey
 */
public class TimestampView extends LinearLayout {

	TimestampTimerTask timerTask;
	Timer timer;
	TextView textView;

	public TimestampView(Context context) {
		super(context);
		init(context);
	}

	public TimestampView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void release(Context context) {
		Log.e("release()");
		timerTask = new TimestampTimerTask(context);
		timer.schedule(timerTask, 1000);
	}

	public void touch(Context context) {
		Log.e("touch()");
		if (View.VISIBLE != getVisibility()) {
			show(context);
		}
		if (timerTask != null) {
			timerTask.cancel();
			timer.purge();
		}
	}

	public void setTime(String timeString) {
		textView.setText(timeString);
	}

	protected void init(Context context) {
		textView = new TextView(context);
		textView.setText("HELLO!");
		textView.setTextColor(Color.YELLOW);
		addView(textView);
		timer = new Timer();
	}

	protected void show(Context context) {
		Log.e("show()");
		setVisibility(View.VISIBLE);
	}

	protected void hide(Context context) {
		Log.e("hide()");
		setVisibility(View.INVISIBLE);
	}

	protected class TimestampTimerTask extends TimerTask {
		private final Context context;

		public TimestampTimerTask(Context context) {
			this.context = context;
		}

		@Override
		public void run() {
			Log.e("TIMER RUNNING");
			post(new Runnable() {
				@Override
				public void run() {
					hide(context);
				}
			});
		}
	}
}
