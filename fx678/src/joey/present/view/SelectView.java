package joey.present.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import joey.present.util.Const;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.umeng.analytics.MobclickAgent;

public class SelectView extends Activity {

	private LayoutInflater mInflater;
	private ListView cornerListView = null;
	private TextView title = null;
	private List<Map<String, String>> listData = null;
	private List<String> listDataForSend = null;
	private List<String> listDataName = null;
	private List<String> listDataEx = null;
	private SimpleAdapter adapter = null;
	private String selected = "";

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

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectview);
		Bundle bundle = getIntent().getExtras();
		selected = bundle.getString("started");
		cornerListView = (ListView) findViewById(R.id.setting_list);
		title = (TextView) findViewById(R.id.title);
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		setListData(selected);
		ImageButton backBtn = (ImageButton) findViewById(R.id.backbtn);
		backBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				SelectView.this.finish();
			}

		});
		MyAdapter adapter = new MyAdapter();
		cornerListView.setAdapter(adapter);
		cornerListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (listDataForSend.size() > position) {
					String temp = listDataForSend.get(position);
					String tempName = listDataName.get(position);
					String tempEx = listDataEx.get(position);
					Intent in = new Intent(SelectView.this, MainTab.class);
					Bundle bundle = new Bundle();
					bundle.putString("selected", temp);
					bundle.putString("selectedname", tempName);
					bundle.putString("selectedex", tempEx);
					bundle.putString("btn", "0");
					in.putExtras(bundle);
					startActivity(in);
				}
			}
		});
	}

	/**
	 * 设置列表数据
	 */
	private void setListData(String selected) {
		listData = new ArrayList<Map<String, String>>();
		listDataForSend = new ArrayList<String>();
		listDataName = new ArrayList<String>();
		listDataEx = new ArrayList<String>();
		if ("gold".equals(selected)) {
			title.setText("行情中心");
			Map<String, String> map = new HashMap<String, String>();
			// 津贵所
			map = new HashMap<String, String>();
			map.put("text", Const.HXCE_NAME);
			listData.add(map);
			listDataForSend.add(Const.TTJ);
			listDataName.add(Const.HXCE_NAME);
			listDataEx.add(Const.TTJ8);
//			Log.i("listDataEx", "listDataEx------->>>" +  listDataEx);
			// 国际黄金
			map = new HashMap<String, String>();
			map.put("text", Const.BS_NAME);
			listData.add(map);
			listDataForSend.add(Const.HJXH);
			listDataName.add(Const.BS_NAME);
			listDataEx.add(Const.HJXH2);

			// 外汇市场
			map = new HashMap<String, String>();
			map.put("text", Const.HF_NAME);
			listData.add(map);
			listDataForSend.add(Const.WH);
			listDataName.add(Const.HF_NAME);
			listDataEx.add(Const.WH4);

			// 全球股指
			map = new HashMap<String, String>();
			map.put("text", Const.QDCE_NAME);
			listData.add(map);
			listDataForSend.add(Const.STOCKINDEX);
			listDataName.add(Const.QDCE_NAME);
			listDataEx.add(Const.STOCKINDEX3);

		} else if ("global".equals(selected)) {
			title.setText("全球市场");

			Map<String, String> map = new HashMap<String, String>();
			map.put("text", Const.WH_NAME);
			listData.add(map);
			listDataForSend.add(Const.WH);
			listDataName.add(Const.WH_NAME);
			listDataEx.add(Const.WH4);
			map = new HashMap<String, String>();
			map.put("text", Const.STOCKINDEX_NAME);
			listData.add(map);
			listDataForSend.add(Const.STOCKINDEX);
			listDataName.add(Const.STOCKINDEX_NAME);
			listDataEx.add(Const.STOCKINDEX3);

			map = new HashMap<String, String>();
			map.put("text", Const.NYMEX_NAME);
			listData.add(map);
			listDataForSend.add(Const.NYMEX);
			listDataName.add(Const.NYMEX_NAME);
			listDataEx.add(Const.NYMEX5);
			map = new HashMap<String, String>();
			map.put("text", Const.IPE_NAME);
			listData.add(map);
			listDataForSend.add(Const.IPE);
			listDataName.add(Const.IPE_NAME);
			listDataEx.add(Const.IPE7);
			map = new HashMap<String, String>();
			map.put("text", Const.COMEX_NAME);
			listData.add(map);
			listDataForSend.add(Const.COMEX);
			listDataName.add(Const.COMEX_NAME);
			listDataEx.add(Const.COMEX6);
			map = new HashMap<String, String>();
			map.put("text", Const.SHQH_NAME);
			listData.add(map);
			listDataForSend.add(Const.SHQH);
			listDataName.add(Const.SHQH_NAME);
			listDataEx.add(Const.SHQH9);

		}

	}

	private final class ViewHolder {
		public TextView name;
	}

	// 行情数据列表组件用数据adapter
	private class MyAdapter extends BaseAdapter {

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			View view;
			if (paramView == null) {
				view = mInflater.inflate(R.layout.selectitem, null);
				View item = view.findViewById(R.id.selectitem);
				item.setBackgroundResource(getBackground(paramInt));
			} else {
				view = paramView;
			}
			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder == null) {
				holder = new ViewHolder();
				view.setTag(holder);
				holder.name = (TextView) view.findViewById(R.id.selectitem);
			}
			holder.name.setText((String) listData.get(paramInt).get("text"));
			return view;
		}

		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		public int getCount() {
			if (listData != null) {
				return listData.size();
			} else {
				return 0;
			}
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		private int getBackground(int position) {
			if (position == 0 && listData.size() == 1) {
				// 单个数据
				return R.drawable.corner_round;
			}
			if (position == 0) {
				// 头
				return R.drawable.corner_round_top;
			}
			if (position == listData.size() - 1) {
				// 尾
				return R.drawable.corner_round_bottom;
			}
			// 中间
			return R.drawable.corner_shape;
		}
	}
}