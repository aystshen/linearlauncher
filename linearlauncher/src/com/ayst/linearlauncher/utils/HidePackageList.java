package com.ayst.linearlauncher.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import com.ayst.linearlauncher.LauncherItem;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/2/24.
 */
public class HidePackageList {

    public final static String PREF_NAME = "main";
    public final static String HIDE_LIST = "hide_list";
    private static final String TAG = "HidePackageList";

    public static void init(Context context) {
        ArrayList<String> array = new ArrayList<String>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appsAll = context.getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo item : appsAll) {
            String pkg = item.activityInfo.packageName;
            if (pkg.contains("com.android")) {
                Log.i(TAG, "init, android pkg=" + pkg);
                array.add(pkg);
            }
        }
        save(context, array);
    }

    public static void save(Context context, ArrayList<String> array) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        JSONArray jsonArray = new JSONArray();
        for (String str : array) {
            jsonArray.put(str);
        }
        Log.i(TAG, "save, jsonArray=" + jsonArray.toString());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(HIDE_LIST, jsonArray.toString());
        editor.commit();
    }

    public static ArrayList<String> get(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<String> array = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(prefs.getString(HIDE_LIST, "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                array.add(jsonArray.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "get, array=" + array.toString());
        return array;
    }
}
