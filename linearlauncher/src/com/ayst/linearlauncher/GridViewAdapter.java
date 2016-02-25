package com.ayst.linearlauncher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v17.leanback.widget.ShadowOverlayContainer;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/25.
 */
public class GridViewAdapter extends RecyclerView.Adapter {
    private Context mContext = null;
    private List<ResolveInfo> mData = null;
    private PackageManager mPkgManager = null;

    public GridViewAdapter(Context context, List<ResolveInfo> data) {
        mContext = context;
        mPkgManager = context.getPackageManager();
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.bottom_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ResolveInfo item = mData.get(i);
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.tv.setText(item.activityInfo.loadLabel(mPkgManager));
        holder.iv.setImageDrawable(item.activityInfo.loadIcon(mPkgManager));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tv;
        public ViewHolder(View v) {
            super(v);
            iv = (ImageView)v.findViewById(R.id.iv);
            tv = (TextView)v.findViewById(R.id.tv);
        }
    }
}
