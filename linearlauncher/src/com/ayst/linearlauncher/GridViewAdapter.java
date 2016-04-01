package com.ayst.linearlauncher;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.FocusHighlightHelper;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.ShadowOverlayContainer;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ayst.linearlauncher.utils.ImageHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/25.
 */
public class GridViewAdapter extends RecyclerView.Adapter {
    private Context mContext = null;
    private List<LauncherItem> mData = null;
    private PackageManager mPkgManager = null;
    private OnItemClickListener mOnItemClickListener = null;
    private OnFocusChangeListener mOnFocusChangeListener = null;
    private HorizontalGridView mParent = null;
    private int mLayoutId = R.layout.grid_item;
    private FocusHighlightHelper.DefaultItemFocusHighlight mFocusHighlight = null;

    private RotateAnimation mAnimation = null;

    public GridViewAdapter(Context context, HorizontalGridView parent, int layoutId, List<LauncherItem> data) {
        mContext = context;
        mPkgManager = context.getPackageManager();
        mData = data;
        mParent = parent;
        mLayoutId = layoutId;
        mFocusHighlight = new FocusHighlightHelper.DefaultItemFocusHighlight(FocusHighlight.ZOOM_FACTOR_LARGE, false);

        /** 设置旋转动画 */
        mAnimation =new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setDuration(3000);//设置动画持续时间
        mAnimation.setRepeatCount(Animation.INFINITE);//设置重复次数
        //mAnimation.setFillAfter(boolean);//动画执行完后是否停留在执行完的状态
        //mAnimation.setStartOffset(long startOffset);//执行前的等待时间
        mAnimation.startNow();
    }

    public void update(List<LauncherItem> data) {
        mData.clear();
        mData = data;
        this.notifyDataSetChanged();
    }

    public LauncherItem getItem(int position) {
        if (position >= 0 && position < mData.size()) {
            return mData.get(position);
        }
        return null;
    }

    public LauncherItem getSelectedItem() {
        return mData.get(mParent.getSelectedPosition());
    }

    private View.OnFocusChangeListener mItemFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (mFocusHighlight != null) {
                mFocusHighlight.onItemFocused(v, hasFocus);
            }
            View iconBg = v.findViewById(R.id.iv_bg);
            if (hasFocus) {
                v.setAlpha(1f);
                if (iconBg != null) {
                    iconBg.setBackgroundColor(0x00000000);
                    iconBg.setAnimation(mAnimation);
                }
            } else {
                v.setAlpha(0.5f);
                if (iconBg != null) {
                    iconBg.setBackgroundResource(R.drawable.bg_grid_icon);
                    iconBg.clearAnimation();
                }
            }
            if (mOnFocusChangeListener != null) {
                mOnFocusChangeListener.onFocusChange(v, mParent.getSelectedPosition());
            }
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
        ShadowOverlayContainer wrapper = new ShadowOverlayContainer(parent.getContext(), R.drawable.bg_grid_item);
        wrapper.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        wrapper.initialize(true, false, true);
        wrapper.wrap(v);
        wrapper.setOnFocusChangeListener(mItemFocusChangeListener);
        wrapper.setOnClickListener(mItemClickListener);
        wrapper.setFocusable(true);
        ViewHolder vh = new ViewHolder(wrapper);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final ResolveInfo item = mData.get(i).mResolveInfo;
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.tv.setText(item.activityInfo.loadLabel(mPkgManager));
        Palette.generateAsync(ImageHelper.drawableToBitmap(item.activityInfo.loadIcon(mPkgManager)),
                new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int bgColor = palette.getMutedColor(mContext.getResources().getColor(R.color.default_bg));
                        holder.iv.setImageDrawable(ImageHelper.mergeColorBg(item.activityInfo.loadIcon(mPkgManager), bgColor));
                    }
                });
        holder.lv.setAlpha((mParent.getSelectedPosition()==i) ? 1f : 0.5f);
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
            iv = (ImageView) v.findViewById(R.id.iv);
            tv = (TextView) v.findViewById(R.id.tv);
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
         * <p/>
         * Implementers can call getItemAtPosition(position) if they need to
         * access the data associated with the selected item.
         *
         * @param parent   The AdapterView where the click happened.
         * @param view     The view within the AdapterView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id       The row id of the item that was clicked.
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
     * been clicked, or null id no callback has been set.
     */
    public final OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public interface OnFocusChangeListener {
        void onFocusChange(View view, int position);
    }

    public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        mOnFocusChangeListener = listener;
    }

}
