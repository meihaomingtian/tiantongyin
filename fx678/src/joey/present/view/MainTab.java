package joey.present.view;

import java.lang.reflect.Field;

import joey.present.util.Const;

import com.umeng.analytics.MobclickAgent;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;

public class MainTab extends TabActivity {

	private TabHost tabHost;

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
		setContentView(R.layout.maintab);
		Bundle bundle = getIntent().getExtras();
		String sel = bundle.getString("selected");
		String ex = bundle.getString("selectedex");
		// Resources res = getResources(); // Resource object to get Drawables
		// R.drawable.xxx
		tabHost = getTabHost(); // The activity TabHost
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
		intent = new Intent(this, OptionalView.class);
		intent.putExtras(bundle).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		spec = tabHost.newTabSpec("tab2").setIndicator(createTabView("自选")).setContent(intent);
		tabHost.addTab(spec);
		// 分页判断显示标题,买入/卖出..还是 最高/最低
		if (!"newstab".equals(sel) && !"mychoicetab".equals(sel)) {
			if (Const.TTJ8.equals(ex)) {
				intent = new Intent(this, PriceViewTTJ.class);
			} else if (Const.SHGOLD1.equals(ex)) {
				intent = new Intent(this, PriceView.class);
			} else if (Const.YGY10.equals(ex)) {
				intent = new Intent(this, PriceViewTTJ.class);
			} else {
				intent = new Intent(this, PriceViewGG.class);
			}
			intent.putExtras(bundle);
			spec = tabHost.newTabSpec("tab3").setIndicator(createTabView("市场")).setContent(intent);
			tabHost.addTab(spec);
		}
		intent = new Intent(this, NewsView.class);
		intent.putExtras(bundle);
		spec = tabHost.newTabSpec("tab4").setIndicator(createTabView("资讯")).setContent(intent);
		tabHost.addTab(spec);
		intent = new Intent(this, TransView.class);
		spec = tabHost.newTabSpec("tab5").setIndicator(createTabView("交易")).setContent(intent);
		tabHost.addTab(spec);
		try {
			Field idcurrent = tabHost.getClass().getDeclaredField("mCurrentTab");
			idcurrent.setAccessible(true);
			idcurrent.setInt(tabHost, 0);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// if (!"newstab".equals(sel) && !"mychoicetab".equals(sel)) {
		// tabHost.setCurrentTab(2);
		// }
		// if ("newstab".equals(sel)) {
		//
		// }
		if ("mychoicetab".equals(sel)) {
			tabHost.setCurrentTab(1);
		} else {
			tabHost.setCurrentTab(2);
		}

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			public void onTabChanged(String tabId) {
				if ("tab1".equals(tabId)) {
					Intent in = new Intent(MainTab.this, StartView.class);
					in.addCategory(Intent.CATEGORY_HOME);
					in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(in);
					finish();
				}
			}
		});
	}

	private View createTabView(String text) {
		View view = LayoutInflater.from(this).inflate(R.layout.tabindicator, null);
		TextView tv = (TextView) view.findViewById(R.id.titletab);
		tv.setText(text);
		return view;
	}

}
