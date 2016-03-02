package com.ayst.linearlauncher.utils;

import android.content.ComponentName;
import android.util.Log;
import com.ayst.linearlauncher.UsageStats;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/3/1.
 */
public class PkgUsageStatsUtil {
    private final static String TAG = "PkgUsageStatsUtil";
    private static Object mIUsageStatsObject = null;
    private static Method mGetPkgUsageStatsMethod = null;

    public static UsageStats getPkgUsageStats(ComponentName cn) {
        try {
            if (mIUsageStatsObject == null || mGetPkgUsageStatsMethod == null) {
                //获得ServiceManager类
                Class<?> ServiceManager = Class
                        .forName("android.os.ServiceManager");

                //获得ServiceManager的getService方法
                Method getService = ServiceManager.getMethod("getService", java.lang.String.class);

                //调用getService获取RemoteService
                Object oRemoteService = getService.invoke(null, "usagestats");

                //获得IUsageStats.Stub类
                Class<?> cStub = Class
                        .forName("com.android.internal.app.IUsageStats$Stub");
                //获得asInterface方法
                Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);
                //调用asInterface方法获取IUsageStats对象
                mIUsageStatsObject = asInterface.invoke(null, oRemoteService);
                //获得getPkgUsageStats(ComponentName)方法
                mGetPkgUsageStatsMethod = mIUsageStatsObject.getClass().getMethod("getPkgUsageStats", ComponentName.class);
            }
            //调用getPkgUsageStats 获取PkgUsageStats对象
            Object stats = mGetPkgUsageStatsMethod.invoke(mIUsageStatsObject, cn);

            //获得PkgUsageStats类
            Class<?> PkgUsageStats = Class.forName("com.android.internal.os.PkgUsageStats");

            int launchCount = PkgUsageStats.getDeclaredField("launchCount").getInt(stats);
            long useTime = PkgUsageStats.getDeclaredField("usageTime").getLong(stats);

            Log.i(TAG, "getPkgUsageStats, " + cn.toString() + " launchCount=" + launchCount + " useTime=" + useTime);
            return new UsageStats(launchCount, useTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new UsageStats(0, 0);
    }
}
