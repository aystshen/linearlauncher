package com.ayst.linearlauncher.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Administrator on 2016/2/24.
 */
public class HidePackageList {

    public final static String PREF_NAME = "main";
    public final static String HIDE_LIST = "hide_list";
    private static final String TAG = "HidePackageList";

    public static void save(Context context, ArrayList<String> array) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
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
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        ArrayList<String> array = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(prefs.getString(HIDE_LIST, "[]"));
            for (int i=0; i<jsonArray.length(); i++) {
                array.add(jsonArray.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "get, array=" + array.toString());
        return array;
    }
}
