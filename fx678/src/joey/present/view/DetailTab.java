package joey.present.view;

import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;

import java.lang.reflect.Field;

import com.umeng.analytics.MobclickAgent;

import joey.present.view.ui.AnimationTabHost;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class DetailTab extends TabActivity {

	private String code;
	private GestureDetector gestureDetector;
	private FrameLayout frameLayout;

	private AnimationTabHost tabHost;
	private TabWidget mTabWidget;

	/** 记录当前分页ID */
	private int currentTabID = 1;

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 谷歌分析统计代码
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 谷歌分析统计代码
		EasyTracker.getInstance().activityStop(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailtab);
		Bundle bundle = getIntent().getExtras();
		// code = bundle.getString("code");
		// Resources res = getResources(); // Resource object to get Drawables
		// R.drawable.xxx
		tabHost = (AnimationTabHost) findViewById(android.R.id.tabhost);
		mTabWidget = (TabWidget) findViewById(android.R.id.tabs);
		tabHost.setOpenAnimation(true);
		gestureDetector = new GestureDetector(new TabHostTouch());
		new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};

		TabSpec spec;
		Intent intent; // Reusable Intent for each tab
		try {
			Field idcurrent = tabHost.getClass().getDeclaredField("mCurrentTab");
			idcurrent.setAccessible(true);
			idcurrent.setInt(tabHost, -2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		intent = new Intent(this, StartView.class);
		intent.putExtras(bundle);
		spec = tabHost.newTabSpec("tab1").setIndicator(createTabView("首页")).setContent(intent);
		tabHost.addTab(spec);
		intent = new Intent(this, TimeDataView.class);
		intent.putExtras(bundle);
		spec = tabHost.newTabSpec("tab2").setIndicator(createTabView("分时")).setContent(intent);
		tabHost.addTab(spec);
		intent = new Intent(this, KLineView.class);
		intent.putExtras(bundle);
		spec = tabHost.newTabSpec("tab3").setIndicator(createTabView("K线")).setContent(intent);
		tabHost.addTab(spec);
		intent = new Intent(this, FenbiView.class);
		intent.putExtras(bundle);
		spec = tabHost.newTabSpec("tab4").setIndicator(createTabView("分笔")).setContent(intent);
		tabHost.addTab(spec);
		intent = new Intent(this, MainView.class);
		intent.putExtras(bundle);
		spec = tabHost.newTabSpec("tab5").setIndicator(createTabView("更多")).setContent(intent);
		tabHost.addTab(spec);
		// this.getCurrentActivity();
		try {
			Field idcurrent = tabHost.getClass().getDeclaredField("mCurrentTab");
			idcurrent.setAccessible(true);
			idcurrent.setInt(tabHost, 0);

		} catch (Exception e) {
			e.printStackTrace();
		}
		tabHost.setCurrentTab(currentTabID);
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			public void onTabChanged(String tabId) {
				currentTabID = tabHost.getCurrentTab();
				if ("tab1".equals(tabId)) {
					Intent in = new Intent(DetailTab.this, StartView.class);
					in.addCategory(Intent.CATEGORY_HOME);
					in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(in);
					finish();
				}
			}
		});
	}

	public boolean dispatchTouchEvent(MotionEvent event) {

		if (gestureDetector.onTouchEvent(event)) {
			event.setAction(MotionEvent.ACTION_CANCEL);
		}

		return super.dispatchTouchEvent(event);

	}

	private View createTabView(String text) {
		View view = LayoutInflater.from(this).inflate(R.layout.tabindicator, null);
		TextView tv = (TextView) view.findViewById(R.id.titletab);
		tv.setText(text);
		return view;
	}

	private class TabHostTouch extends SimpleOnGestureListener {
		/** 滑动翻页所需距离 */
		private static final int ON_TOUCH_DISTANCE = 200;

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (currentTabID == 1 || currentTabID == 2) {
				if (e1.getX() - e2.getX() <= (-ON_TOUCH_DISTANCE)) {
					currentTabID = tabHost.getCurrentTab() - 1;

					if (currentTabID <= 1) {
						currentTabID = 1;
					}
				} else if (e1.getX() - e2.getX() >= ON_TOUCH_DISTANCE) {
					currentTabID = tabHost.getCurrentTab() + 1;

					if (currentTabID > 2) {
						currentTabID = 2;
					}
				}
				if (e1.getY() - e2.getY() <= (-ON_TOUCH_DISTANCE)) {
					currentTabID = tabHost.getCurrentTab() - 1;

					if (currentTabID <= 1) {
						currentTabID = 1;
					}
				} else if (e1.getY() - e2.getY() >= ON_TOUCH_DISTANCE) {
					currentTabID = tabHost.getCurrentTab() + 1;

					if (currentTabID > 2) {
						currentTabID = 2;
					}
				}
				tabHost.setCurrentTab(currentTabID);
			}
			return false;
		}
	}

}
