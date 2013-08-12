/*
 * Copyright (c) 2013, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.R;
import com.apptentive.android.sdk.model.Message;
import com.apptentive.android.sdk.util.Util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Sky Kelsey
 */
public class TimestampView extends FrameLayout implements AbsListView.OnScrollListener {

	protected TimestampTimerTask timerTask;
	protected Timer timer;
	protected ListView listView;
	protected TextView textView;
	protected View spacer;

	protected boolean visible;

	protected boolean pendingShow;

	protected int listItems;
	protected int listViewHeight;

	public TimestampView(Context context) {
		super(context);
		init();
	}

	public TimestampView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void release() {
		Log.e("release()");
		pendingShow = false;
		timerTask = new TimestampTimerTask();
		timer.schedule(timerTask, getResources().getInteger(R.integer.apptentive_timestamp_display_timeout));
	}

	public void touch() {
		Log.e("touch()");
		pendingShow = true;
		if (View.VISIBLE != getVisibility()) {
			show();
		} else {
			visible = true;
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
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.apptentive_message_center_timestamp, this);
		textView = (TextView) findViewById(R.id.timestamp);
		//spacer = findViewById(R.id.spacer);
		timer = new Timer();
		visible = false;
		pendingShow = false;
		listItems = 0;
		listViewHeight = 0;
	}

	protected synchronized void show() {
		Log.e("show()");
		if (!visible) {
			visible = true;
			Log.e("Showing...");
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
	}

	protected synchronized void hide() {
		Log.e("hide()");
		if (visible) {
			visible = false;
			Log.e("Hiding...");
			Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
			setAnimation(fadeOut);
			fadeOut.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					if (!pendingShow) {
						setVisibility(View.INVISIBLE);
					}
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}
			});
			startAnimation(fadeOut);
		}
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

		//
		String state = "";
		switch (scrollState) {
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				state = "SCROLL_STATE_FLING";
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				state = "SCROLL_STATE_IDLE";
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				state = "SCROLL_STATE_TOUCH_SCROLL";
				break;
		}
		Log.e("onScrollStateChanged(%s)", state);
		//

		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
			touch();
		} else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			release();
		}
	}

	@Override
	public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		Log.e("onScroll(%s, %s, %s)", firstVisibleItem, visibleItemCount, totalItemCount);
		setTime("" + firstVisibleItem);

		int newItemCount = absListView.getCount();
		if (listItems != newItemCount) {
			listItems = newItemCount;
			listViewHeight = Util.getTotalHeightofListView(listView);
			Log.e("listViewHeight = %s", listViewHeight);
		}

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
