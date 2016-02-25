package com.ayst.linearlauncher;

import android.text.TextUtils;

public class LauncherItem {

	public static final int TYPE_START_ACTIVITY_START_ACTIVITY = 1;
	public static final int TYPE_START_ACTIVITY_SEND_BROADCAST = 2;
	public static final int TYPE_START_ACTIVITY_START_PACKAGE = 3;

	private String name;
	private String action;
	private int drawableResId;
	private int textResId;
	private String mPackage;
	private String mExtra;
	private boolean isReplaceSrc = false;
	private String mImgSrc = "";

	private int mStartType = TYPE_START_ACTIVITY_START_ACTIVITY;// 1、startactivity()
																// ||
																// 2.sendBroadcast();

	/**
	 * @param name
	 *            item name
	 * @param action
	 *            item action
	 * @param drawbleId
	 *            item drawable id
	 * @param discription
	 *            item description
	 * @param appearanceCount
	 *            item count on right-top corner
	 * @param state
	 *            different state of appearance
	 */
	public LauncherItem(String pack, String action, int drawableResId,
			int textResId, String extra) {
		this.action = action;
		this.drawableResId = drawableResId;
		this.textResId = textResId;
		this.mPackage = pack;
		this.mExtra = extra;
	}

	public String getPackage() {
		return mPackage;
	}

	public String getExtra() {
		return mExtra;
	}

	public String getName() {
		return name;
	}

	public String getAction() {
		return action;
	}

	public int getDrawableResId() {
		return drawableResId;
	}

	public int getTextResId() {
		return textResId;
	}

	public int getStartType() {
		return mStartType;
	}

	public void setStartType(int mStartType) {
		this.mStartType = mStartType;
	}

	public void setImgSrc(String src) {
		if (!TextUtils.isEmpty(src)) {
			isReplaceSrc = true;
			mImgSrc = src;
		}
	}

	public String getImgSrc() {
		return mImgSrc;
	}

	public boolean isImgReplace() {
		return isReplaceSrc;
	}
}
