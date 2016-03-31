package com.ayst.linearlauncher;

import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ayst.linearlauncher.db.DBManager;
import com.ayst.linearlauncher.upgrade.UpgradeManager;
import com.ayst.linearlauncher.utils.*;
import com.ayst.linearlauncher.widget.CircleImageView;

import java.util.*;

public class MainActivity extends Activity {
    private final static String TAG = "MainActivity";
    private final static int MSG_UPDATE_BG = 1;

    private View mBgView = null;
    private HorizontalGridView mMainGridView = null;
    private GridViewAdapter mMainAdapter = null;
    private PackageManager mPkgManager = null;

    private BackDoor mBackDoorHide = null;
    private BackDoor mBackDoorReset = null;

    private ArrayList<String> mHidePackageList = null;

    private LinearLayout mBottomView = null;
    private LinearLayout mBottomSubView = null;
    private CircleImageView mBottomIcon = null;
    private TextView mBottomText = null;
    private Button mBottomHideBtn = null;
    private Button mBottomUninstallBtn = null;
    private Button mBottomAddBtn = null;
    private HorizontalGridView mBottomGridView = null;
    private GridViewAdapter mBottomAdapter = null;

    private LinearLayout mUpgradeView = null;
    private CircleImageView mUpgradeIv = null;
    private TextView mUpgradeTv = null;
    private Button mUpgradeOkBtn = null;
    private Button mUpgradeCancelBtn = null;

    private PackageChangedReceiver mPkgChangedReceiver = null;
    private DBManager mDBManager = null;
    private UpgradeManager mUpgradeManager = null;

    private final static int UPGRADE_CNT = 20;
    private final static int STAND_CNT = 2;
    private int mUpgradeCountDown = UPGRADE_CNT;
    private int mStandCountDown = STAND_CNT;

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_BG:
                    Palette.generateAsync(ImageHelper.drawableToBitmap(mMainAdapter.getItem(mMainGridView.getSelectedPosition()).mResolveInfo.activityInfo.loadIcon(mPkgManager)),
                            new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    int mutedColor = getResources().getColor(R.color.transparent);
                                    int vibrantColor = getResources().getColor(R.color.transparent);

                                    Palette.Swatch mutedSwatch = palette.getDarkMutedSwatch();
                                    if (mutedSwatch != null) {
                                        mutedColor = mutedSwatch.getRgb();
                                    } else if ((mutedSwatch = palette.getMutedSwatch()) != null) {
                                        mutedColor = mutedSwatch.getRgb();
                                    } else if ((mutedSwatch = palette.getLightMutedSwatch()) != null) {
                                        mutedColor = mutedSwatch.getRgb();
                                    }

                                    Palette.Swatch vibrantSwatch = palette.getLightVibrantSwatch();
                                    if (vibrantSwatch != null) {
                                        vibrantColor = vibrantSwatch.getRgb();
                                    } else if ((vibrantSwatch = palette.getVibrantSwatch()) != null) {
                                        vibrantColor = vibrantSwatch.getRgb();
                                    } else if ((vibrantSwatch = palette.getDarkVibrantSwatch()) != null) {
                                        vibrantColor = vibrantSwatch.getRgb();
                                    }
                                    GradientDrawable bgDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{vibrantColor, mutedColor});
                                    mBgView.setBackgroundDrawable(bgDrawable);
                                }
                            });
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mDBManager = new DBManager(this);
        mPkgManager = this.getPackageManager();
        initData();
        initView();

        mUpgradeManager = new UpgradeManager(this, new UpgradeManager.OnFoundNewVersionInterface() {
            @Override
            public void onFoundNewVersionInterface(String version, String introduction, String url) {
                mUpgradeIv.setImageResource(R.drawable.ic_launcher);
                mUpgradeTv.setText(introduction);
                mUpgradeView.setVisibility(View.VISIBLE);
                mUpgradeOkBtn.requestFocus();
                if (mBottomView.getVisibility() == View.VISIBLE) {
                    mBottomView.setVisibility(View.GONE);
                }
                mMainGridView.setFocusable(false);
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mUpgradeCountDown > 0) {
                    mUpgradeCountDown--;
                } else if (mUpgradeCountDown == 0) {
                    mUpgradeCountDown = -1;
                    mUpgradeManager.checkUpdate();
                }

                if (mStandCountDown > 0) {
                    mStandCountDown--;
                } else if (mStandCountDown == 0) {
                    mStandCountDown = -1;
                    mHandler.sendEmptyMessage(MSG_UPDATE_BG);
                }

                mHandler.postDelayed(this, 1000);
            }
        }, 0);

        mPkgChangedReceiver = new PackageChangedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        registerReceiver(mPkgChangedReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //update();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPkgChangedReceiver != null) {
            unregisterReceiver(mPkgChangedReceiver);
        }
    }

    private void initData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int lastVersion = prefs.getInt("version_key", 0);
        if (lastVersion == 0) {
            prefs.edit().putInt("version_key", Utils.getVersionCode(this)).apply();
            HidePackageList.init(this);
        }
        mHidePackageList = HidePackageList.get(this);

        mBackDoorHide = new BackDoor(BackDoor.DOORKEY_HIDE);
        mBackDoorReset = new BackDoor(BackDoor.DOORKEY_RESET);
    }

    private void initView() {
        mBgView = (View) findViewById(R.id.bg_view);

        mMainGridView = (HorizontalGridView) findViewById(R.id.main_gridview);
        mMainAdapter = new GridViewAdapter(this, mMainGridView, R.layout.grid_item, getAllApps(this, true));
        mMainGridView.setAdapter(mMainAdapter);
        mMainAdapter.setOnItemClickListener(new GridViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HorizontalGridView parent, View view, int position, long id) {
                Log.i(TAG, "mMainGridView onItemClick position=" + position);
                if (position >= 0 && position < mMainAdapter.getItemCount()) {
                    ResolveInfo item = mMainAdapter.getItem(position).mResolveInfo;
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
                            mDBManager.increase(pkg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.i(TAG, "Item is null");
                    }
                }
            }
        });
        mMainAdapter.setOnFocusChangeListener(new GridViewAdapter.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, int position) {
                mStandCountDown = STAND_CNT;
            }
        });

        mBottomView = (LinearLayout) findViewById(R.id.ll_bottom);
        mBottomIcon = (CircleImageView) findViewById(R.id.bottom_icon);
        mBottomText = (TextView) findViewById(R.id.bottom_text);
        mBottomHideBtn = (Button) findViewById(R.id.btn_hide);
        mBottomUninstallBtn = (Button) findViewById(R.id.btn_del);
        mBottomAddBtn = (Button) findViewById(R.id.btn_add);
        mBottomSubView = (LinearLayout) findViewById(R.id.ll_sub);
        mBottomGridView = (HorizontalGridView) findViewById(R.id.bottom_gridview);
        mBottomAdapter = new GridViewAdapter(this, mBottomGridView, R.layout.bottom_item, getAllApps(this, false));
        mBottomGridView.setAdapter(mBottomAdapter);
        mBottomAdapter.setOnItemClickListener(new GridViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HorizontalGridView parent, View view, int position, long id) {
                Log.i(TAG, "mBottomGridView onClick position=" + position);
                if (position >= 0 && position < mBottomAdapter.getItemCount()) {
                    ResolveInfo item = mBottomAdapter.getItem(position).mResolveInfo;
                    if (item != null) {
                        String pkg = item.activityInfo.packageName;
                        mHidePackageList.remove(pkg);
                        HidePackageList.save(MainActivity.this, mHidePackageList);
                        update();
                    } else {
                        Log.i(TAG, "Item is null");
                    }
                }
            }
        });
        mBottomAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSubView.getVisibility() == View.GONE) {
                    mBottomSubView.setVisibility(View.VISIBLE);
                    mBottomGridView.requestFocus();
                }
            }
        });
        if (mBottomAdapter.getItemCount() > 0) {
            mBottomAddBtn.setVisibility(View.VISIBLE);
        }

        // upgrade
        mUpgradeView = (LinearLayout) findViewById(R.id.ll_upgrade);
        mUpgradeIv = (CircleImageView) findViewById(R.id.iv_upgrade);
        mUpgradeTv = (TextView) findViewById(R.id.tv_upgrade);
        mUpgradeOkBtn = (Button) findViewById(R.id.btn_ok);
        mUpgradeOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUpgradeManager.downloadAPK();
                mUpgradeView.setVisibility(View.GONE);
                mMainGridView.setFocusable(true);
            }
        });
        mUpgradeCancelBtn = (Button) findViewById(R.id.btn_cancel);
        mUpgradeCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUpgradeView.setVisibility(View.GONE);
                mMainGridView.setFocusable(true);
            }
        });

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mBackDoorHide.isAsRule(event)) {
            ResolveInfo selectedItem = (ResolveInfo) mMainAdapter.getSelectedItem().mResolveInfo;
            Log.i(TAG, "dispatchKeyEvent, BACKDOOR_HIDE selected package=" + selectedItem.activityInfo.packageName);
            mHidePackageList.add(selectedItem.activityInfo.packageName);
            HidePackageList.save(this, mHidePackageList);
            update();
        } else if (mBackDoorReset.isAsRule(event)) {
            Log.i(TAG, "dispatchKeyEvent, BACKDOOR_RESET");
            mHidePackageList.clear();
            HidePackageList.save(this, mHidePackageList);
            update();
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU
                && event.getAction() == KeyEvent.ACTION_UP) {
            if (mBottomView.getVisibility() == View.GONE) {
                final ResolveInfo selectedItem = (ResolveInfo) mMainAdapter.getSelectedItem().mResolveInfo;
                mBottomIcon.setImageDrawable(selectedItem.loadIcon(mPkgManager));
                mBottomText.setText(selectedItem.loadLabel(mPkgManager));
                mBottomHideBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mHidePackageList.add(selectedItem.activityInfo.packageName);
                        HidePackageList.save(MainActivity.this, mHidePackageList);
                        showMenu(false);
                        update();
                    }
                });
                mBottomUninstallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uninstall(selectedItem.activityInfo.packageName);
                        showMenu(false);
                    }
                });
                showMenu(true);
            } else {
                showMenu(false);
            }
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP) {
            if (mBottomView.getVisibility() == View.VISIBLE) {
                showMenu(false);
            }
            if (mUpgradeView.getVisibility() == View.VISIBLE) {
                mUpgradeView.setVisibility(View.GONE);
                mMainGridView.setFocusable(true);
            }
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    public List<LauncherItem> getAllApps(Context context, boolean isGetShow) {
        List<LauncherItem> apps = new ArrayList<LauncherItem>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> appsAll = context.getPackageManager().queryIntentActivities(intent, 0);

        boolean isHide = false;
        for (ResolveInfo item : appsAll) {
            isHide = false;
            for (String pkg : mHidePackageList) {
                if (item.activityInfo.packageName.equals(pkg)) {
                    Log.i(TAG, "getAllApps, isHide pkg=" + pkg);
                    if (!isGetShow) {
                        apps.add(new LauncherItem(item, mDBManager.get(item.activityInfo.packageName)));
                    }
                    isHide = true;
                    break;
                }
            }
            if (!isHide && isGetShow) {
                apps.add(new LauncherItem(item, mDBManager.get(item.activityInfo.packageName)));
            }
        }

        Collections.sort(apps, new PkgComparator());

        return apps;
    }

    private void update() {
        mMainAdapter.update(getAllApps(this, true));
        mBottomAdapter.update(getAllApps(this, false));
        mMainGridView.invalidate();
        mBottomSubView.invalidate();
        if (mBottomAdapter.getItemCount() > 0) {
            mBottomAddBtn.setVisibility(View.VISIBLE);
        } else {
            mBottomAddBtn.setVisibility(View.GONE);
            mBottomSubView.setVisibility(View.GONE);
            mBottomView.setVisibility(View.GONE);
        }
    }

    private void showMenu(boolean isShow) {
        if (isShow) {
            mBottomView.setVisibility(View.VISIBLE);
            mBottomHideBtn.requestFocus();
            mMainGridView.setFocusable(false);
        } else {
            mBottomView.setVisibility(View.GONE);
            mBottomSubView.setVisibility(View.GONE);
            mMainGridView.setFocusable(true);
        }
    }

    private void uninstall(String pkgName) {
        Uri uri = Uri.parse("package:" + pkgName);//获取删除包名的URI
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DELETE);//设置我们要执行的卸载动作
        intent.setData(uri);//设置获取到的URI
        startActivity(intent);
    }

    class PackageChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收安装广播
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
                String packageName = intent.getDataString();
                Log.i(TAG, "install:" + packageName);
                update();
            }
            //接收卸载广播
            if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
                String packageName = intent.getDataString();
                Log.i(TAG, "uninstall:" + packageName);
                update();
            }
        }
    }

    class PkgComparator implements Comparator {
        @Override
        public int compare(Object lhs, Object rhs) {
            LauncherItem a = (LauncherItem) lhs;
            LauncherItem b = (LauncherItem) rhs;

            return (b.mLaunchCnt - a.mLaunchCnt);
        }
    }
}
