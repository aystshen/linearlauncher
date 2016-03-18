package com.ayst.linearlauncher.upgrade;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.test.suitebuilder.TestMethod;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.ayst.linearlauncher.R;
import com.ayst.linearlauncher.http.Download;
import com.ayst.linearlauncher.http.RequestTask;
import com.ayst.linearlauncher.http.RequestTaskInterface;
import com.ayst.linearlauncher.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Administrator on 2016/3/9.
 */
public class UpgradeManager implements RequestTaskInterface {
    private final static String TAG = "UpgradeManager";
    private final static int REQUEST_DESCRIPTOR = 1;
    private final static int REQUEST_DOWNLOAD = 2;
    private final static String REQUEST_DESCRIPTOR_URL = "http://7xroxp.dl1.z0.glb.clouddn.com/description.json";

    private Context mContext = null;
    private UpgradeManager mUpgradeManager = null;
    private int mRequestType = REQUEST_DESCRIPTOR;
    private int mIgnoreVersion = 0;
    private String mDownloadUrl = "";
    private OnFoundNewVersionInterface mOnFoundNewVersionInterface = null;


    public UpgradeManager(Context context, OnFoundNewVersionInterface onFoundNewVersionInterface) {
        mContext = context;
        mOnFoundNewVersionInterface = onFoundNewVersionInterface;
    }

    public void checkUpdate() {
        Log.i(TAG, "checkUpdate start...");
        checkUpdate(REQUEST_DESCRIPTOR_URL);
    }

    private void checkUpdate(String url) {
        if (Utils.isConnNetWork(mContext)) {
            mRequestType = REQUEST_DESCRIPTOR;
            new RequestTask(mContext, url, "POST", null, this)
                    .execute(new String[]{url});
        }
    }

    public void downloadAPK() {
        if (!TextUtils.isEmpty(mDownloadUrl)) {
            mRequestType = REQUEST_DOWNLOAD;
            new Download(mContext, mDownloadUrl, getUpgradeFilePath(), this, null).execute(new String[]{mDownloadUrl});
        } else {
            Log.e(TAG, "downloadAPK, mDownloadUrl is null");
        }
    }

    private String getUpgradeFilePath() {
        return getUpgradeDir() + "/upgrade.apk";
    }

    private String getUpgradeDir() {
        String state = Environment.getExternalStorageState();
        File sdcardDir = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                sdcardDir = Environment.getExternalStorageDirectory();
                Log.i(TAG, "Environment.MEDIA_MOUNTED :" + sdcardDir.getAbsolutePath() + " R:" + sdcardDir.canRead() + " W:" + sdcardDir.canWrite());
                if (!sdcardDir.canWrite()) {
                    sdcardDir = mContext.getFilesDir();
                }
            } else {
                sdcardDir = mContext.getFilesDir();
            }
            return sdcardDir.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mContext.getFilesDir().getAbsolutePath();
    }

    @Override
    public void postExecute(String result) {
        Log.i(TAG, "postExecute, result=" + result);
        if (mRequestType == REQUEST_DESCRIPTOR) {
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject data = new JSONObject(result);
                    if ("com.ayst.linearlauncher".equals(data.getString("package"))) {
                        String subDescriptionUrl = data.getString("subDescriptionUrl");
                        if (TextUtils.isEmpty(subDescriptionUrl)) {
                            int versionCode = data.getInt("versionCode");
                            if (versionCode > Utils.getVersionCode(mContext)
                                    && versionCode != mIgnoreVersion) {
                                mDownloadUrl = data.getString("downloadUrl");
                                if (mOnFoundNewVersionInterface != null) {
                                    mOnFoundNewVersionInterface.onFoundNewVersionInterface(
                                            data.getString("versionName"),
                                            data.getString("introduction"),
                                            mDownloadUrl);
                                }
                            }
                        } else {
                            Log.i(TAG, "postExecute, request subDescriptionUrl...");
                            checkUpdate(subDescriptionUrl);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (mRequestType == REQUEST_DOWNLOAD) {
            if (!TextUtils.isEmpty(result)) {
                Log.i(TAG, "postExecute, install apk...");
                //Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS, 1);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(result)), "application/vnd.android.package-archive");
                intent.putExtra("run_after_install", true);
                mContext.startActivity(intent);
            } else {
                Toast.makeText(mContext, R.string.main_download_failed, Toast.LENGTH_LONG).show();
            }
        }
    }

    public interface OnFoundNewVersionInterface {
        public abstract void onFoundNewVersionInterface(String version, String introduction, String url);
    }
}
