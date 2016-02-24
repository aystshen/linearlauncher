package com.ayst.linearlauncher;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainAdapter extends ArrayAdapter<ResolveInfo> {
    private LayoutInflater mInflater;
    private int mFixSide = 70;
    private PackageManager mPkgManager = null;

    public MainAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
        mPkgManager = context.getPackageManager();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResolveInfo item = getItem(position);
        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = (RelativeLayout) mInflater.inflate(
                    R.layout.main_item, parent, false);
            viewHolder.mIvTitle = (ImageView) convertView
                    .findViewById(R.id.iv_title);
            viewHolder.mTvTitle = (TextView) convertView
                    .findViewById(R.id.tv_title);
            viewHolder.mInnerRelative = (RelativeLayout) convertView
                    .findViewById(R.id.item_main_inner);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (item != null) {
            viewHolder.mIvTitle.setImageDrawable(item.activityInfo.loadIcon(mPkgManager));
            viewHolder.mTvTitle.setText(item.activityInfo.loadLabel(mPkgManager));
        }
        changeMargin(position, viewHolder, convertView);

        return convertView;
    }

    /**
     * 最后一个多画marginright 第一个画marginleft
     *
     * @param position
     * @param viewHolder
     * @param convertView
     */
    private void changeMargin(int position, ViewHolder viewHolder,
                              View convertView) {
        if (position == 0) {
            if (!viewHolder.isAddLeft) {
                convertView.getLayoutParams().width = (convertView
                        .getLayoutParams().width + mFixSide);
                ((RelativeLayout.LayoutParams) viewHolder.mInnerRelative
                        .getLayoutParams()).leftMargin = mFixSide;
                viewHolder.isAddLeft = true;
            }
            if (viewHolder.isAddRight) {
                convertView.getLayoutParams().width = (convertView
                        .getLayoutParams().width - mFixSide);
                ((RelativeLayout.LayoutParams) viewHolder.mInnerRelative
                        .getLayoutParams()).rightMargin = 0;
                viewHolder.isAddRight = false;
            }
        } else if (position > 0 && position == (getCount() - 1)) {
            if (!viewHolder.isAddRight) {
                convertView.getLayoutParams().width = (convertView
                        .getLayoutParams().width + mFixSide);
                ((RelativeLayout.LayoutParams) viewHolder.mInnerRelative
                        .getLayoutParams()).rightMargin = mFixSide;
                viewHolder.isAddRight = true;
            }
            if (viewHolder.isAddLeft) {
                convertView.getLayoutParams().width = (convertView
                        .getLayoutParams().width - mFixSide);
                ((RelativeLayout.LayoutParams) viewHolder.mInnerRelative
                        .getLayoutParams()).leftMargin = 0;
                viewHolder.isAddLeft = false;
            }
        } else {
            if (viewHolder.isAddLeft) {
                convertView.getLayoutParams().width = (convertView
                        .getLayoutParams().width - mFixSide);
                ((RelativeLayout.LayoutParams) viewHolder.mInnerRelative
                        .getLayoutParams()).leftMargin = 0;
                viewHolder.isAddLeft = false;
            }
            if (viewHolder.isAddRight) {
                convertView.getLayoutParams().width = (convertView
                        .getLayoutParams().width - mFixSide);
                ((RelativeLayout.LayoutParams) viewHolder.mInnerRelative
                        .getLayoutParams()).rightMargin = 0;
                viewHolder.isAddRight = false;
            }
        }
    }

    class ViewHolder {
        boolean isAddLeft = false;
        boolean isAddRight = false;
        ImageView mIvTitle;
        TextView mTvTitle;
        RelativeLayout mInnerRelative = null;
    }

    public void setFixMargin(int margin) {
        mFixSide = margin;
    }
}
