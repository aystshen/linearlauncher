package com.ayst.linearlauncher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.ShadowOverlayContainer;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
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
    private OnItemClickListener mOnItemClickListener = null;
    private HorizontalGridView mParent = null;
    private int mLayoutId = R.layout.main_item;

    public GridViewAdapter(Context context, HorizontalGridView parent, int layoutId, List<ResolveInfo> data) {
        mContext = context;
        mPkgManager = context.getPackageManager();
        mData = data;
        mParent = parent;
        mLayoutId = layoutId;
    }

    public void update(List<ResolveInfo> data) {
        mData.clear();
        mData = data;
        this.notifyDataSetChanged();
    }

    public ResolveInfo getItem(int position) {
        if (position >= 0 && position < mData.size()) {
            return mData.get(position);
        }
        return null;
    }

    public ResolveInfo getSelectedItem() {
        return mData.get(mParent.getSelectedPosition());
    }

    private View.OnFocusChangeListener mItemFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

        }
    };

    private View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("GridViewAdapter", "onClick");
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mParent, mParent, mParent.getSelectedPosition(), 0);
            }
        }
    };

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ResolveInfo item = mData.get(i);
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.tv.setText(item.activityInfo.loadLabel(mPkgManager));
        holder.iv.setImageDrawable(item.activityInfo.loadIcon(mPkgManager));
        holder.lv.setOnFocusChangeListener(mItemFocusChangeListener);
        holder.lv.setOnClickListener(mItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tv;
        View lv;
        public ViewHolder(View v) {
            super(v);
            lv = v;
            iv = (ImageView)v.findViewById(R.id.iv);
            tv = (TextView)v.findViewById(R.id.tv);
        }
    }

    /**
     * Interface definition for a callback to be invoked when an item in this
     * AdapterView has been clicked.
     */
    public interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need to
         * access the data associated with the selected item.
         *
         * @param parent The AdapterView where the click happened.
         * @param view The view within the AdapterView that was clicked (this
         *            will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id The row id of the item that was clicked.
         */
        void onItemClick(HorizontalGridView parent, View view, int position,
                         long id);
    }

    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * @return The callback to be invoked with an item in this AdapterView has
     *         been clicked, or null id no callback has been set.
     */
    public final OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }
}
