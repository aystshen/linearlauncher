package com.ayst.linearlauncher;


import android.content.pm.ResolveInfo;

public class LauncherItem {
	public ResolveInfo mResolveInfo = null;
	public int mLaunchCnt = 0;

	public LauncherItem(ResolveInfo resolveInfo, int launchCnt) {
		mResolveInfo = resolveInfo;
		mLaunchCnt = launchCnt;
	}
}
