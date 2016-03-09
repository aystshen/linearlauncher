package com.ayst.linearlauncher.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpEngine {
    protected static final String TAG = "HttpEngine";
    public static final String DOWNLOADFILE = "DOWNLOADFILE";
    public static final String GET = "GET";
    public static final String HTTP = "http://";
    public static final int MAX_TIME_OUT_COUNT = 0x3;
    public static final int MAX_TIME_OUT_TIME = 0x1388;
    public static final String POST = "POST";
    public static final String UPLOADFILE = "UPLOADFILE";
    private HttpURLConnection connect;
    private String mPostContent;
    private String mRequestType;
    private String mRequestURL;
    private static String ERROR = "<?xml";

    public HttpEngine() {
    }
    
    public HttpEngine(String requestType, String url, String postContent) {
        init(requestType, url, postContent);
    }
    
    private void init(String requestType, String url, String postContent) {
        mRequestType = requestType;
        mRequestURL = url;
        mPostContent = postContent;
    }
    
    public String requestPost() {
        try {
            Log.i(TAG, "requestPost..., url=" + mRequestURL);
            connect = (HttpURLConnection) (new URL(mRequestURL)).openConnection();
            connect.setRequestMethod("POST");
            connect.setConnectTimeout(60000);
            connect.setRequestProperty("contentType", "utf-8");
            connect.setRequestProperty("connection", "keep-alive");
            connect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (mPostContent != null) {
                connect.setRequestProperty("Content-Length", String.valueOf(mPostContent.getBytes().length));
            }
            connect.setDoOutput(true);
            connect.setDoInput(true);
            connect.setUseCaches(false);
            connect.connect();
            if (mPostContent != null) {
                OutputStream outputstream = connect.getOutputStream();
                outputstream.write(mPostContent.getBytes());
                outputstream.flush();
                outputstream.close();
            }
            if (connect.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connect.getInputStream();
                connect.getContentEncoding();
                connect.getContentType();
                StringBuilder strBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                do {
                    String str = bufferedReader.readLine();
                    if (str != null) {
                        if (str.length() > 0) {
                            strBuilder.append(str);
                        }
                    } else {
                        bufferedReader.close();
                        String str1 = strBuilder.toString();
                        if(connect != null) {
                            connect.disconnect();
                        }
                        return str1;
                    }
                } while (true);
            }
            if(connect != null)
                connect.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String requestGet() {
        try {
            Log.i(TAG, "requestGet..., url=" + mRequestURL);
            connect = (HttpURLConnection)(new URL(mRequestURL)).openConnection();
            connect.setRequestMethod("GET");
            connect.setConnectTimeout(60000);
            connect.setRequestProperty("contentType", "utf-8");
            connect.setRequestProperty("connection", "keep-alive");
            connect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connect.setDoOutput(true);
            connect.setDoInput(true);
            connect.setUseCaches(false);
            connect.connect();
            if(connect.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connect.getInputStream();
                String contentType = connect.getContentType();
                Log.i("HttpEngine", (new StringBuilder("type = ")).append(contentType).toString());
                StringBuilder strBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                do {
                    String str = bufferedReader.readLine();
                    if(str != null) {
                        if(str.length() > 0) {
                            strBuilder.append(str);
                        }
                    } else {
                        bufferedReader.close();
                        String str1 = strBuilder.toString();
                        if(connect != null) {
                            connect.disconnect();
                        }
                        return str1;
                    }
                } while (true);
            }
            if(connect != null) {
                connect.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
