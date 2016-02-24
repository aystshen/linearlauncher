package com.ayst.linearlauncher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import com.ayst.linearlauncher.utils.BackDoor;
import com.ayst.linearlauncher.utils.HidePackageList;
import com.ayst.linearlauncher.widget.twoway.TwoWayAdapterView;
import com.ayst.linearlauncher.widget.twoway.TwoWayGridView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private final static String TAG = "MainActivity";
    private TwoWayGridView mGridWay = null;
    private MainAdapter mAdapter = null;
    private PackageManager mPkgManager = null;

    private BackDoor mBackDoorHide = null;
    private BackDoor mBackDoorReset = null;

    private ArrayList<String> mHidePackageList = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mPkgManager = this.getPackageManager();
        initData();
        initView();
    }

    private void initData() {
        mHidePackageList = HidePackageList.get(this);

        mAdapter = new MainAdapter(this);
        mAdapter.addAll(getAllApps(this));

        mBackDoorHide = new BackDoor(BackDoor.DOORKEY_HIDE);
        mBackDoorReset = new BackDoor(BackDoor.DOORKEY_RESET);
    }

    private void initView() {
        mGridWay = (TwoWayGridView) findViewById(R.id.twoway_gridview);
        mGridWay.setOnItemClickListener(new TwoWayAdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(TwoWayAdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i(TAG, "mGridWay onItemClick position=" + position);
                if (position >= 0 && position < mAdapter.getCount()) {
                    ResolveInfo item = mAdapter.getItem(position);
                    if (item != null) {
                        //该应用的包名
                        String pkg = item.activityInfo.packageName;
                        //应用的主activity类
                        String cls = item.activityInfo.name;
                        ComponentName componet = new ComponentName(pkg, cls);
                        try {
                            Intent intent = new Intent();
                            intent.setComponent(componet);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.i(TAG, "Item is null");
                    }
                }
            }
        });
        mAdapter.setFixMargin(mGridWay.getMarginLeftOrRight());
        mGridWay.setAdapter(mAdapter);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mBackDoorHide.isAsRule(event)) {
            ResolveInfo selectedItem = (ResolveInfo) mGridWay.getSelectedItem();
            Log.i(TAG, "dispatchKeyEvent, BACKDOOR_HIDE selected package=" + selectedItem.activityInfo.packageName);
            mHidePackageList.add(selectedItem.activityInfo.packageName);
            HidePackageList.save(this, mHidePackageList);
            mAdapter.addAll(getAllApps(this));
        } else if (mBackDoorReset.isAsRule(event)) {
            Log.i(TAG, "dispatchKeyEvent, BACKDOOR_RESET");
            mHidePackageList.clear();
            HidePackageList.save(this, mHidePackageList);
            mAdapter.addAll(getAllApps(this));
        }

        return super.dispatchKeyEvent(event);
    }

    public List<ResolveInfo> getAllApps(Context context) {
        List<ResolveInfo> apps = new ArrayList<ResolveInfo>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> appsAll = context.getPackageManager().queryIntentActivities(intent, 0);

        boolean isHide = false;
        for (ResolveInfo item : appsAll) {
            isHide = false;
            for (String pkg : mHidePackageList) {
                if (item.activityInfo.packageName.equals(pkg)) {
                    isHide = true;
                    break;
                }
            }
            if (!isHide) {
                apps.add(item);
            }
        }

        return apps;
    }
}
