package com.ayst.linearlauncher.http;

import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;

public class RequestTask extends AsyncTask<String[], Integer, String> {
    private static final String TAG = "RequestTask";
    private Context mContext;
    private RequestTaskInterface mTaskObserver;
    private String mUrl;
    private String mPostContent;
    private String mRequestType;
    
    public RequestTask(Context context, String url, String requestType, String postContent, RequestTaskInterface requestTaskInterface) {
        mContext = context;
        mUrl = url;
        mTaskObserver = requestTaskInterface;
        mRequestType = requestType;
        mPostContent = postContent;
    }

    @Override
    protected String doInBackground(String[]... params) {
        if(mRequestType.equals("POST")) {
            HttpEngine httpEngine = new HttpEngine(mRequestType, mUrl, mPostContent);
            return httpEngine.requestPost();
        } else if(mRequestType.equals("GET")) {
            HttpEngine httpEngine = new HttpEngine(mRequestType, mUrl, mPostContent);
            return httpEngine.requestGet();
        } else {
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "onPostExecute, result=" + result);
        mTaskObserver.postExecute(result);
    }
}
