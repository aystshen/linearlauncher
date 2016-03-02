package com.ayst.linearlauncher;

/**
 * Created by Administrator on 2016/3/2.
 */
public class UsageStats {
    int launchCount = 0;
    long useTime = 0;

    public UsageStats(int cnt, long time) {
        launchCount = cnt;
        useTime = time;
    }
}
