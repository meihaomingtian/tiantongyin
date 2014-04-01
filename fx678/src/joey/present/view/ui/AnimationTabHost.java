package joey.present.view.ui;

import com.fx678.finace.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;

public class AnimationTabHost extends TabHost {
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;

	/** 记录是否打开动画效果 */
	private boolean isOpenAnimation;
	/** 记录当前标签页的总数 */
	private int mTabCount;

	public AnimationTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);

		slideLeftIn = AnimationUtils.loadAnimation(context,
				R.anim.slide_in_left);
		slideLeftOut = AnimationUtils.loadAnimation(context,
				R.anim.slide_out_left);
		slideRightIn = AnimationUtils.loadAnimation(context,
				R.anim.slide_in_right);
		slideRightOut = AnimationUtils.loadAnimation(context,
				R.anim.slide_out_right);

		isOpenAnimation = true;
	}

	public boolean setTabAnimation(int[] animationResIDs) {
		if (3 == animationResIDs.length) {
			slideLeftIn = AnimationUtils.loadAnimation(getContext(),
					animationResIDs[0]);
			slideLeftOut = AnimationUtils.loadAnimation(getContext(),
					animationResIDs[1]);
			slideRightIn = AnimationUtils.loadAnimation(getContext(),
					animationResIDs[2]);
			slideRightOut = AnimationUtils.loadAnimation(getContext(),
					animationResIDs[3]);

			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置是否打开动画效果
	 * 
	 * @param isOpenAnimation
	 *            true：打开
	 */
	public void setOpenAnimation(boolean isOpenAnimation) {
		this.isOpenAnimation = isOpenAnimation;
	}

	/**
	 * @return 返回当前标签页的总数
	 */
	public int getTabCount() {
		return mTabCount;
	}

	@Override
	public void addTab(TabSpec tabSpec) {
		mTabCount++;
		super.addTab(tabSpec);
	}

	@Override
	public void setCurrentTab(int index) {
		int mCurrentTabID = getCurrentTab();

		if (null != getCurrentView()) {
			// 第一次设置 Tab 时，该值为 null。

			if (isOpenAnimation) {
				if (mCurrentTabID == (mTabCount - 1) && index == 0) {
					getCurrentView().startAnimation(slideLeftOut);
				} else if (mCurrentTabID == 0 && index == (mTabCount - 1)) {
					getCurrentView().startAnimation(slideRightOut);
				} else if (index > mCurrentTabID) {
					getCurrentView().startAnimation(slideLeftOut);
				} else if (index < mCurrentTabID) {
					getCurrentView().startAnimation(slideRightOut);
				}
			}
		}

		super.setCurrentTab(index);

		if (isOpenAnimation) {
			if (mCurrentTabID == (mTabCount - 1) && index == 0) {
				getCurrentView().startAnimation(slideLeftIn);
			} else if (mCurrentTabID == 0 && index == (mTabCount - 1)) {
				getCurrentView().startAnimation(slideRightIn);
			} else if (index > mCurrentTabID) {
				getCurrentView().startAnimation(slideLeftIn);
			} else if (index < mCurrentTabID) {
				getCurrentView().startAnimation(slideRightIn);
			}
		}
	}

}
