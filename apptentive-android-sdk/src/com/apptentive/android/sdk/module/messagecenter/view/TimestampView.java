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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.model.Message;
import com.apptentive.android.sdk.util.Util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Sky Kelsey
 */
public class TimestampView extends LinearLayout implements AbsListView.OnScrollListener {

	protected TimestampTimerTask timerTask;
	protected Timer timer;
	protected TextView textView;
	protected ListView listView;

	public TimestampView(Context context) {
		super(context);
		init();
	}

	public TimestampView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void release() {
		timerTask = new TimestampTimerTask();
		timer.schedule(timerTask, getResources().getInteger(R.integer.apptentive_timestamp_display_timeout));
	}

	public void touch() {
		if (View.VISIBLE != getVisibility()) {
			show();
		}
		if (timerTask != null) {
			timerTask.cancel();
			timer.purge();
		}
	}

	public void setTime(String timeString) {
		textView.setText(timeString);
	}

	protected void init() {
		textView = new TextView(getContext());
		textView.setText("HELLO!");
		textView.setTextColor(Color.BLACK);
		textView.setBackgroundColor(Color.WHITE);
		textView.setTextSize(getResources().getDimension(R.dimen.apptentive_text_medium));
		addView(textView);
		timer = new Timer();
	}

	protected void show() {
		Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
		setAnimation(fadeIn);
		fadeIn.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		startAnimation(fadeIn);
	}

	protected void hide() {
		Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
		setAnimation(fadeOut);
		fadeOut.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				setVisibility(View.INVISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		startAnimation(fadeOut);
	}

	protected class TimestampTimerTask extends TimerTask {
		@Override
		public void run() {
			post(new Runnable() {
				@Override
				public void run() {
					hide();
				}
			});
		}
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}

	@Override
	public void onScrollStateChanged(AbsListView absListView, int scrollState) {
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			release();
		} else {
			touch();
		}

	}

	@Override
	public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		setTime("" + firstVisibleItem);

		int x = listView.getWidth() / 2;
		int y = listView.getHeight() / 2;
		int position = listView.pointToPosition(x, y);
		Message message = (Message) listView.getItemAtPosition(position);
		if (message != null) {
			Double seconds = message.getCreatedAt();
			if (seconds != null) {
				setTime(Util.secondsToDisplayString(getResources().getString(R.string.apptentive_message_sent_timestamp_format), seconds));
			}
		}
	}
}
