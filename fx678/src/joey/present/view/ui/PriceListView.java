package joey.present.view.ui;

import java.util.List;
import java.util.Map;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;

public class PriceListView extends ListView {

	private GestureDetector mGesture;

	public LinearLayout mListHead;

	private int mOffsetX = 0;
	private int mOffsetY = 0;
	private int screenWidth;

	private int firstOutX;

	private int mScrollX;
	private int mScrollY;

	private int distanY;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mTouchSlop;
	private float mLastMotionY;
	private float mLastMotionX;
	private boolean mIsInEdge;
	private int mMinimumVelocity;
	private int mMaximumVelocity;
	private static final int FLING_MIN_DISTANCE = 50;
	private static final int FLING_MIN_VELOCITY = 100;

	private boolean outBound = false;

	public PriceListView(Context context, AttributeSet attrs) {

		super(context, attrs);
		mScroller = new Scroller(context);
		setFocusable(true);
		setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
		setWillNotDraw(false);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = configuration.getScaledTouchSlop();
		mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
//		mGesture = new GestureDetector(context, mOnGesture);

	}
//
//	public void fling(int velocityX, int velocityY) {
//		if (getChildCount() > 0) {
//			int curX = mListHead.getScrollX();
//			mScroller.fling(getScrollX(), getScrollY(), velocityX, 0, 0,
//					getScreenWidth() - curX, 0, 0);
//			final boolean movingDown = velocityX > 0;
//			awakenScrollBars(mScroller.getDuration());
//			invalidate();
//		}
//	}
//
//	private void obtainVelocityTracker(MotionEvent event) {
//		if (mVelocityTracker == null) {
//			mVelocityTracker = VelocityTracker.obtain();
//		}
//		mVelocityTracker.addMovement(event);
//	}
//
//	private void releaseVelocityTracker() {
//		if (mVelocityTracker != null) {
//			mVelocityTracker.recycle();
//			mVelocityTracker = null;
//		}
//	}
//
//	public boolean onTouchEvent(MotionEvent event) {
//		if (event.getAction() == MotionEvent.ACTION_DOWN
//				&& event.getEdgeFlags() != 0) {
//			return false;
//		}
//
//		obtainVelocityTracker(event);
//		int curX = mListHead.getScrollX();
//		int scrollWidth = getWidth();
//		final int action = event.getAction();
//		final float x = event.getX();
//		final float y = event.getY();
//
//		switch (action) {
//		case MotionEvent.ACTION_DOWN:
//			if (!mScroller.isFinished()) {
//				mScroller.abortAnimation();
//			}
//			mLastMotionY = y;
//			break;
//
//		case MotionEvent.ACTION_MOVE:
//			final int deltaY = (int) (mLastMotionY - y);
//			final int deltaX = (int) (mLastMotionX - x);
//			mLastMotionX = x;
//			mLastMotionY = y;
//
//			int dx = deltaX;
//			int dy = deltaY;
//			// 越界处理
//			if (curX + deltaX < 0)
//				dx = 0;
//			if (curX + deltaX + getScreenWidth() > scrollWidth)
//				dx = scrollWidth - getScreenWidth() - curX;
//			mOffsetX += dx;
//			// 根据收拾滚动item
//			for (int i = 0, j = getChildCount(); i < j; i++) {
//				View child = ((ViewGroup) getChildAt(i)).getChildAt(1);
////				child.scrollTo(mOffset, 0);
//				if (child.getScrollX() != mOffsetX)
//				child.scrollBy(mOffsetX, 0);
//			}
//			mListHead.scrollBy(dx, 0);
//			scrollBy(0, dy);
//			// if (deltaY < 0) {
//			// if (getScrollY() > 0) {
//			// scrollBy(0, deltaY);
//			// }
//			// } else if (deltaY > 0) {
//			// mIsInEdge = getScrollY() <= childTotalHeight - height;
//			// if (mIsInEdge) {
//			// scrollBy(0, deltaY);
//			// }
//			// }
//			break;
//
//		case MotionEvent.ACTION_UP:
//			final VelocityTracker velocityTracker = mVelocityTracker;
//			velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
//			int yVelocity = (int) velocityTracker.getYVelocity();
//			int xVelocity = (int) velocityTracker.getXVelocity();
//			if ((Math.abs(yVelocity) > mMinimumVelocity) && (Math.abs(xVelocity) > mMinimumVelocity) 
//					&& getChildCount() > 0) {
////				fling(xVelocity, yVelocity);
//			} else {
//				
//			}
//			releaseVelocityTracker();
//			break;
//		}
//
//		return true;
//	}
//
//	public void computeScroll() {
//		if (mScroller.computeScrollOffset()) {
//
//			int scrollX = getScrollX();
//			int scrollY = getScrollY();
//			int oldX = scrollX;
//			int oldY = scrollY;
//			int x = mScroller.getCurrX();
//			int y = mScroller.getCurrY();
//			scrollX = x;
//			scrollY = y;
//			scrollY = scrollY + 10;
//			int dx = scrollX;
//			int curX = mListHead.getScrollX();
//			int scrollWidth = getWidth();
//			// 越界处理
//			if (curX + scrollX < 0)
//				dx = 0;
//			if (curX + scrollX + getScreenWidth() > scrollWidth)
//				dx = scrollWidth - getScreenWidth() - curX;
//			mOffsetX += dx;
//			// 根据收拾滚动item
//			for (int i = 0, j = getChildCount(); i < j; i++) {
//				View child = ((ViewGroup) getChildAt(i)).getChildAt(1);
//				if (child.getScrollX() != mOffsetX)
//					child.scrollTo(mOffsetX, 0);
//			}
//			mListHead.scrollBy(dx, 0);
////			scrollTo(scrollX, scrollY);
//			postInvalidate();
//		}
//	}
//
//	public void scrollTo(int x, int y) {
//		if (mScrollX != x || mScrollY != y) {
//			int oldX = mScrollX;
//			int oldY = mScrollY;
//			mScrollX = x;
//			mScrollY = y;
//			onScrollChanged(mScrollX, mScrollY, oldX, oldY);
//			if (!awakenScrollBars()) {
//				invalidate();
//			}
//		}
//	}
//
////	public boolean dispatchTouchEvent(MotionEvent ev) {
////		// int act = ev.getAction();
////		// if ((act == MotionEvent.ACTION_UP || act ==
////		// MotionEvent.ACTION_CANCEL)
////		// && outBound) {
////		// outBound = false;
////		// // scroll back
////		// }
////		// if (!mGesture.onTouchEvent(ev)) {
////		// outBound = false;
////		// } else {
////		// outBound = true;
////		// }
////		// Rect rect = new Rect();
////		// getLocalVisibleRect(rect);
////		// TranslateAnimation am = new TranslateAnimation(0, 0, -rect.top, 0);
////		// am.setDuration(300);
////		// startAnimation(am);
////		// scrollTo(0, 0);
////		super.dispatchTouchEvent(ev);
////		return mGesture.onTouchEvent(ev);
////	}
//
////	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {
////
////		@Override
////		public boolean onDown(MotionEvent e) {
////			
////			return true;
////		}
////
////		@Override
////		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
////				float velocityY) {
////			return true;
////		}
////
////		@Override
////		public boolean onScroll(MotionEvent e1, MotionEvent e2,
////				float distanceX, float distanceY) {
////			synchronized (PriceListView.this) {
////				// int firstPos = getFirstVisiblePosition();
////				// int lastPos = getLastVisiblePosition();
////				// int itemCount = getCount();
////				//
////				// // outbound Top
////				// if (outBound && firstPos != 0 && lastPos != (itemCount - 1))
////				// {
////				// scrollTo(0, 0);
////				// // return false;
////				// }
////				// View firstView = getChildAt(firstPos);
////				// if (!outBound)
////				// firstOutY = (int) e2.getRawY();
////				// if (firstView != null
////				// && (outBound || (firstPos == 0
////				// && firstView.getTop() == 0 && distanceY < 0))) {
////				// // Record the length of each slide
////				// distanY = firstOutY - (int) e2.getRawY();
////				// scrollTo(0, distanY / 2);
////				// // return true;
////				// }
////				int moveX = (int) distanceX;
////				int curX = mListHead.getScrollX();
////				int scrollWidth = getWidth();
////				int dx = moveX;
////				// 越界处理
////				if (curX + moveX < 0)
////					dx = 0;
////				if (curX + moveX + getScreenWidth() > scrollWidth)
////					dx = scrollWidth - getScreenWidth() - curX;
////				mOffset += dx;
////				// 根据收拾滚动item
////				for (int i = 0, j = getChildCount(); i < j; i++) {
////					View child = ((ViewGroup) getChildAt(i)).getChildAt(1);
////					if (child.getScrollX() != mOffset)
////						child.scrollTo(mOffset, 0);
////				}
////				mListHead.scrollBy(dx, 0);
////			}
////
////			requestLayout();
////			return true;
////		}
////
////	};
//
//	public int getScreenWidth() {
//		if (screenWidth == 0) {
//			screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
//
//			if (getChildAt(0) != null) {
//				screenWidth -= ((ViewGroup) getChildAt(0)).getChildAt(0)
//						.getMeasuredWidth();
//			} else if (mListHead != null) {
//				screenWidth -= mListHead.getChildAt(0).getMeasuredWidth();
//			}
//		}
//		return screenWidth;
//	}
//
//	public int getHeadScrollX() {
//		return mListHead.getScrollX();
//	}
//
//	public final class ViewHolder {
//		public TextView name;
//		public TextView newM;
//		public TextView pchange;
//	}

}
