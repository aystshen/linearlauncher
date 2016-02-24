package com.ayst.linearlauncher.widget.twoway;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import com.ayst.linearlauncher.R;

public class ScaleItemView extends RelativeLayout {

	private Animator[] mFocusAnimators = new Animator[2];

	private RelativeLayout mRelativeLayout;
	private Context mContext;

	public ScaleItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public ScaleItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
	}

	public ScaleItemView(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mRelativeLayout = (RelativeLayout) findViewById(R.id.item_main_inner);
		initAnimation(mContext);
	}

	private void initAnimation(Context ctx) {
		int time = 100;
		mFocusAnimators[0] = getAnimator(1f, 1.2f, 1f, 1.2f, 0.5f, 1f, time);
		mFocusAnimators[1] = getAnimator(1.2f, 1f, 1.2f, 1f, 1f, 0.5f, time);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		executeTo(gainFocus);
	}

	private Animator getAnimator(float fx, float tx, float fy, float ty,
			float fa, float ta, final int duration) {
		PropertyValuesHolder px = PropertyValuesHolder
				.ofFloat("scaleX", fx, tx);
		PropertyValuesHolder py = PropertyValuesHolder
				.ofFloat("scaleY", fy, ty);
		PropertyValuesHolder pa = PropertyValuesHolder.ofFloat("alpha", fa, ta);
		ObjectAnimator oa = ObjectAnimator.ofPropertyValuesHolder(
				mRelativeLayout, px, py, pa);
		oa.setDuration(duration);

		return oa;
	}

	private void executeTo(boolean enlarger) {
		Animator a = mFocusAnimators[enlarger ? 0 : 1];
		Animator b = mFocusAnimators[enlarger ? 1 : 0];
		if (b != null)
			b.cancel();
		if (a != null)
			a.start();
	}

	@Override
	public View focusSearch(int direction) {
		ViewParent parent = getParent();
		if (parent != null) {
			return parent.focusSearch(this, direction);
		} else {
			return null;
		}
	}
}
