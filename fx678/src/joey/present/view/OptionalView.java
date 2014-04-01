package joey.present.view;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import joey.present.data.PriceData;
import joey.present.util.Const;
import joey.present.util.DensityUtil;
import joey.present.util.Util;
import joey.present.view.ui.PriceListView;
import joey.present.view.ui.SyncHorizontalScrollView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.umeng.analytics.MobclickAgent;

public class OptionalView extends Activity {

	// private DatagramSocket client;
	// private DatagramPacket recpacket;
	// private DatagramPacket sendpacket;
	private String[] ops;
	private String[] opsname;
	private String[] exs;
	private boolean[] opssel;
	// 本地配置保存对象
	private SharedPreferences preferences;

	private ImageButton editBtn;

	private int lastItem = 0;

	private LayoutInflater mInflater;

	private ListView priceListView;

	private ListView priceNameListView;

	private SyncHorizontalScrollView headScroll;

	private SyncHorizontalScrollView listScroll;

	private GestureDetector mGesture;

	// 更新数据
	private PriceData timeNow;

	// private Button shgoldBtn;
	// private Button hjxhBtn;
	// private Button stockindexBtn;
	// private Button whBtn;
	// // 最后选择类别按钮
	// private Button lastSelectBtn;
	// // 当前分类
	// private TextView selectedType;
	// 当前分类
	private TextView title;
	// 行情数据map
	private List<Map<String, Object>> mData;

	// 行情数据list
	private List<PriceData> listData;

	// 名字行情数据list
	private List<Map<String, Object>> listNameData;

	// 数据取得等待对话框组件
	private ProgressDialog progressDialog;

	// 数据取得线程
	private ProgressThread progressThread;

	// 自动更新线程
	private AutoUpdateThread autoUpdateThread;

	// // 自动发送active
	// private SendActiveThread sendActiveThread;

	// 自动更新线程启动标志
	private boolean running = false;

	private String selected;

	private String selectedEX;

	private String type;

	private Util util = new Util();

	private String errorMsg;

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

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		// if (client != null) {
		// client.close();
		// }

		running = false;
		autoUpdateThread = null;
		// sendActiveThread = null;
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		Thread t = new Thread() {
			public void run() {
				try {
					// client.close();
					autoUpdateThread = null;
					// sendActiveThread = null;
					// client = new DatagramSocket();
					// String sendStr = String.format(Const.UDP_PARA +
					// selected);
					// byte[] sendBuf = sendStr.getBytes();
					// sendBuf = sendStr.getBytes();
					// InetAddress address =
					// InetAddress.getByName(Const.UDP_IP);
					// sendpacket = new DatagramPacket(sendBuf, sendBuf.length,
					// address, Const.UDP_PORT);
					// client.send(sendpacket);
					// byte[] recBuf = new byte[1000];
					// recpacket = new DatagramPacket(recBuf, recBuf.length);
					autoUpdateThread = new AutoUpdateThread(handler);
					running = true;
					autoUpdateThread.start();
					// sendActiveThread = new SendActiveThread();
					// sendActiveThread.start();
				} catch (Exception e) {

				}
			}
		};
		t.start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.optionalview);
		preferences = getSharedPreferences(Const.PREFERENCES_NAME, Activity.MODE_PRIVATE);
		String op = preferences.getString(Const.PREF_OPTIONAL, "");
		String opname = preferences.getString(Const.PREF_OPTIONAL_NAME, "");
		String opex = preferences.getString(Const.PREF_EX, "");
		//Log.i("temp", "op->" + op);
		//Log.i("temp", "opname->" + op);
		//Log.i("temp", "opex->" + op);
		if (!"".equals(op)) {
			if (op.indexOf(",") < 0) {
				ops = new String[] { op };
				exs = new String[] { opex };
				opsname = new String[] { opname };
			} else {
				ops = op.split(",");
				exs = opex.split(",");
				opsname = opname.split(",");
			}
			opssel = new boolean[ops.length];
		}

		selected = op;
		selectedEX = opex;
		running = true;
		editBtn = (ImageButton) findViewById(R.id.editbtn);
		editBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (ops == null || ops.length <= 0) {
					Message msg = handler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 3);
					msg.setData(b);
					handler.sendMessage(msg);
				} else {
					editDialog();
				}
			}
		});
		title = (TextView) findViewById(R.id.title);
		ImageButton backBtn = (ImageButton) findViewById(R.id.backbtn);
		backBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				OptionalView.this.finish();
			}

		});
		headScroll = (SyncHorizontalScrollView) findViewById(R.id.scrollListHead);
		listScroll = (SyncHorizontalScrollView) findViewById(R.id.scrollList);
		headScroll.setSmoothScrollingEnabled(true);
		listScroll.setSmoothScrollingEnabled(true);
		headScroll.setScrollView(listScroll);
		listScroll.setScrollView(headScroll);
		priceNameListView = (ListView) findViewById(R.id.priceNameListView);

		priceListView = (ListView) findViewById(R.id.priceListView);
		MyAdapter adapter = new MyAdapter();
		priceListView.setAdapter(adapter);
		priceListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (listData.size() > position) {
					PriceData pData = listData.get(position);
					Intent in = new Intent(OptionalView.this, DetailTab.class);
					Bundle bundle = new Bundle();
					bundle.putString("code", pData.getPrice_code());
					int sel = 0;
					for (int i = 0; i < ops.length; i++) {
						if (pData.getPrice_code().equals(ops[i])) {
							sel = i;
						}
					}
					bundle.putString("name", pData.getPrice_name());
					bundle.putString("selected", selected);
					bundle.putString("ex", exs[sel]);
					bundle.putString(Const.PRICE_DECIMAL, pData.getPrice_Decimal());
					in.putExtras(bundle);
					in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(in);
				}
			}
		});

		// searchBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// SearchKeyboardDlg dlg = new SearchKeyboardDlg(PriceView.this);
		// dlg.show();
		// }
		// });
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		if (ops == null || ops.length <= 0) {
			Message msg = handler.obtainMessage();
			Bundle b = new Bundle();
			b.putInt("total", 5);
			msg.setData(b);
			handler.sendMessage(msg);
		} else {
			progressDialog = new ProgressDialog(OptionalView.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("取得数据...");
			// progressDialog.setTitle("请等待");
			progressDialog.setCancelable(true);
			progressDialog.show();
			progressThread = new ProgressThread(handler);
			progressThread.setState(1);
			progressThread.start();
			autoUpdateThread = new AutoUpdateThread(handler);
			running = true;
			autoUpdateThread.start();
			// Thread t = new Thread() {
			// public void run() {
			// try {
			// client = new DatagramSocket();
			// String sendStr = String
			// .format(Const.UDP_DIY + selected);
			// byte[] sendBuf = sendStr.getBytes();
			// sendBuf = sendStr.getBytes();
			// InetAddress address = InetAddress
			// .getByName(Const.UDP_IP);
			// sendpacket = new DatagramPacket(sendBuf,
			// sendBuf.length, address, Const.UDP_PORT);
			// client.send(sendpacket);
			// byte[] recBuf = new byte[1000];
			// recpacket = new DatagramPacket(recBuf, recBuf.length);
			// autoUpdateThread = new AutoUpdateThread(handler);
			// running = true;
			// autoUpdateThread.start();
			// } catch (Exception e) {
			// }
			// }
			// };
			// t.start();
		}

	}

	private synchronized void updateData(String type) {
		// listData = util.getPriceDataOP("http://mhth.fx678.com/diy.aspx?code="
		// + type, type, selectedEX);
		listData = util.getPriceDataOP("http://mhth.fx678.com/diy.aspx?code=" + type, type, selectedEX);
		//Log.i("temp", "listSize->" + listData.size());
	}

	public void showData() {
		// if (Const.SHGOLD.equals(selected)) {
		// selectedType.setText(R.string.shgold);
		// } else if (Const.HJXH.equals(selected)) {
		// selectedType.setText(R.string.hjxh);
		// } else if (Const.STOCKINDEX.equals(selected)) {
		// selectedType.setText(R.string.stockindex);
		// } else if (Const.WH.equals(selected)) {
		// selectedType.setText(R.string.wh);
		// }
		try {
			progressDialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
		listNameData = new ArrayList<Map<String, Object>>();
		mData = new ArrayList<Map<String, Object>>();
		String decimal;

		for (int i = 0; i < listData.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> mapName = new HashMap<String, Object>();
			PriceData pData = listData.get(i);

			decimal = pData.getPrice_Decimal();
			DecimalFormat df_base = Const.df4;
			if (decimal != null) {
				if ("0".equals(decimal)) {
					df_base = Const.df0;
				} else if ("1".equals(decimal)) {
					df_base = Const.df1;
				} else if ("2".equals(decimal)) {
					df_base = Const.df2;
				} else if ("3".equals(decimal)) {
					df_base = Const.df3;
				} else if ("4".equals(decimal)) {
					df_base = Const.df4;
				}
			}

			map.put(Const.PRICE_QUOTETIME, pData.getPrice_quotetime());
			map.put(Const.PRICE_OPEN, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_open()))));
			map.put(Const.PRICE_HIGH, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_high()))));
			map.put(Const.PRICE_LAST, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_last()))));
			map.put(Const.PRICE_LOW, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_low()))));
			map.put(Const.PRICE_NAME, pData.getPrice_name());
			mapName.put(Const.PRICE_NAME, pData.getPrice_name());
			map.put(Const.PRICE_CODE, pData.getPrice_code());
			map.put(Const.PRICE_LASTCLOSE, pData.getPrice_lastclose());
			map.put(Const.PRICE_LASTSETTLE, pData.getPrice_lastsettle());
			map.put(Const.PRICE_UPDOWN, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_updown()))));
			map.put(Const.PRICE_UPDOWNRATE, pData.getPrice_updownrate());
			map.put(Const.PRICE_VOLUME, pData.getPrice_volume());
			map.put(Const.PRICE_TURNOVER, pData.getPrice_turnover());
			map.put(Const.PRICE_DECIMAL, pData.getPrice_Decimal());
			mData.add(map);
			listNameData.add(mapName);
		}
		ViewGroup.LayoutParams linearParams1 = priceListView.getLayoutParams();
		// LinearLayout.LayoutParams ff = new LinearLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT, 45 * listData.size());
		// priceNameListView.setLayoutParams(ff);
		// LinearLayout.LayoutParams ff2 = new LinearLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT, 45 * listData.size());
		// priceListView.setLayoutParams(ff2);
		linearParams1.height = DensityUtil.dip2px(this, 35) * listData.size();
		priceListView.setLayoutParams(linearParams1);
		ViewGroup.LayoutParams linearParams2 = priceNameListView.getLayoutParams();
		linearParams2.height = DensityUtil.dip2px(this, 35) * listData.size();
		priceNameListView.setLayoutParams(linearParams2);
		SimpleAdapter adapterS = new SimpleAdapter(this, listNameData, R.layout.pricenameitem, new String[] { Const.PRICE_NAME },
				new int[] { R.id.pricename });

		priceNameListView.setAdapter(adapterS);
		MyAdapter adapter = (MyAdapter) priceListView.getAdapter();
		adapter.notifyDataSetChanged();

	}

	private class ProgressThread extends Thread {
		Handler mHandler;
		int mState;

		@SuppressWarnings("unused")
		ProgressThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			if (1 == mState) {
				try {
					updateData(selected);

					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 1);
					msg.setData(b);
					errorMsg = "";
					mHandler.sendMessage(msg);
					return;
				} catch (Exception e) {
					progressDialog.dismiss();
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 2);
					msg.setData(b);
					errorMsg = "无法取得数据！请稍后再试。";
					mHandler.sendMessage(msg);
					return;
				}
			} else if (2 == mState) {
				try {
					updateData(selected);
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 4);
					msg.setData(b);
					errorMsg = "";
					mHandler.sendMessage(msg);
					return;
				} catch (Exception e) {
					progressDialog.dismiss();
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 2);
					msg.setData(b);
					errorMsg = "无法取得数据！请稍后再试。";
					mHandler.sendMessage(msg);
					return;
				}

			}

		}

		/*
		 * sets the current state for the thread, used to stop the thread
		 */
		public void setState(int state) {
			mState = state;
		}

	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			int total = msg.getData().getInt("total");
			if (total == 1) {
				// sendActiveThread = new SendActiveThread();
				// sendActiveThread.start();
				showData();
			}
			if (total == 2) {
				if (!"".equals(errorMsg)) {
					// errorDialog("异常", errorMsg);
				}
			}
			if (total == 3) {
				Toast.makeText(getApplicationContext(), "您还没有添加过自选行情", 2000).show();
			}
			if (total == 4 && running) {
				showData();
				Toast.makeText(getApplicationContext(), "数据已更新!", 1000).show();
			}
			if (total == 5) {
				Toast.makeText(getApplicationContext(), "您还没有添加过自选行情，请到行情详细画面中点击【+自选】添加自选行情数据", 7000).show();
			}
		}
	};

	private class ViewHolder {
		// public TextView name;
		public TextView last;
		public TextView low;
		public TextView close;
		public TextView settle;
		public TextView updown;
		public TextView updownrate;
		public TextView high;
		public TextView open;
		public TextView volume;
		public TextView average;
		public TextView turnover;
	}

	// 行情数据列表组件用数据adapter
	private class MyAdapter extends BaseAdapter {

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			ViewHolder vholder = null;
			if (paramView == null) {
				vholder = new ViewHolder();
				paramView = mInflater.inflate(R.layout.priceitem, null);
				// vholder.name = (TextView) paramView
				// .findViewById(R.id.pricename);
				vholder.last = (TextView) paramView.findViewById(R.id.last);
				vholder.low = (TextView) paramView.findViewById(R.id.low);
				vholder.close = (TextView) paramView.findViewById(R.id.close);
				vholder.settle = (TextView) paramView.findViewById(R.id.settle);
				vholder.updown = (TextView) paramView.findViewById(R.id.updown);
				vholder.updownrate = (TextView) paramView.findViewById(R.id.updownrate);
				vholder.high = (TextView) paramView.findViewById(R.id.high);
				vholder.open = (TextView) paramView.findViewById(R.id.open);
				vholder.volume = (TextView) paramView.findViewById(R.id.volume);
				vholder.average = (TextView) paramView.findViewById(R.id.average);
				vholder.turnover = (TextView) paramView.findViewById(R.id.turnover);
				paramView.setTag(vholder);
			} else {
				vholder = (ViewHolder) paramView.getTag();
			}

			// 校正（处理同时上下和左右滚动出现错位情况）
			// View child = ((ViewGroup) paramView).getChildAt(1);
			// int head = priceListView.getHeadScrollX();
			// if (child.getScrollX() != head) {
			// child.scrollTo(priceListView.getHeadScrollX(), 0);
			// }

			vholder.last.setTextColor(Color.WHITE);
			vholder.last.setBackgroundColor(Color.TRANSPARENT);
			vholder.low.setTextColor(Color.WHITE);
			vholder.close.setTextColor(Color.WHITE);
			vholder.settle.setTextColor(Color.WHITE);
			vholder.updown.setTextColor(Color.WHITE);
			vholder.updownrate.setTextColor(Color.WHITE);
			vholder.high.setTextColor(Color.WHITE);
			vholder.open.setTextColor(Color.WHITE);
			vholder.average.setTextColor(Color.WHITE);
			vholder.volume.setTextColor(Color.YELLOW);
			vholder.turnover.setTextColor(Color.YELLOW);
			String updown = (String) mData.get(paramInt).get(Const.PRICE_UPDOWN);
			String newCode = (String) mData.get(paramInt).get(Const.PRICE_CODE);
			if (util.getFloat(updown) != 0) {
				if (util.getFloat(updown) < 0) {
					vholder.last.setTextColor(Color.GREEN);
					vholder.low.setTextColor(Color.GREEN);
					vholder.updown.setTextColor(Color.GREEN);
					vholder.updownrate.setTextColor(Color.GREEN);
					vholder.high.setTextColor(Color.GREEN);
					vholder.open.setTextColor(Color.GREEN);

				} else {
					vholder.last.setTextColor(Color.RED);
					vholder.low.setTextColor(Color.RED);
					vholder.updown.setTextColor(Color.RED);
					vholder.updownrate.setTextColor(Color.RED);
					vholder.high.setTextColor(Color.RED);
					vholder.open.setTextColor(Color.RED);

				}
			}
			float lastclose = util.getFloat((String) mData.get(paramInt).get(Const.PRICE_LASTCLOSE));
			if (util.getFloat((String) mData.get(paramInt).get(Const.PRICE_OPEN)) != 0
					&& (util.getFloat((String) mData.get(paramInt).get(Const.PRICE_OPEN)) - lastclose) > 0) {
				vholder.open.setTextColor(Color.RED);
			} else {
				vholder.open.setTextColor(Color.GREEN);
			}
			if (util.getFloat((String) mData.get(paramInt).get(Const.PRICE_HIGH)) != 0
					&& (util.getFloat((String) mData.get(paramInt).get(Const.PRICE_HIGH)) - lastclose) > 0) {
				vholder.high.setTextColor(Color.RED);
			} else {
				vholder.high.setTextColor(Color.GREEN);
			}
			if (util.getFloat((String) mData.get(paramInt).get(Const.PRICE_LOW)) != 0
					&& (util.getFloat((String) mData.get(paramInt).get(Const.PRICE_LOW)) - lastclose) > 0) {
				vholder.low.setTextColor(Color.RED);
			} else {
				vholder.low.setTextColor(Color.GREEN);
			}

			// vholder.name.setText((String) mData.get(paramInt).get(
			// Const.PRICE_NAME));

			// vholder.code.setText((String) mData.get(paramInt).get(
			// Const.PRICE_CODE));
			vholder.last.setText((String) mData.get(paramInt).get(Const.PRICE_LAST));

			vholder.low.setText((String) mData.get(paramInt).get(Const.PRICE_LOW));

			vholder.close.setText((String) mData.get(paramInt).get(Const.PRICE_LASTCLOSE));

			vholder.settle.setText((String) mData.get(paramInt).get(Const.PRICE_LASTSETTLE));

			vholder.updown.setText((String) mData.get(paramInt).get(Const.PRICE_UPDOWN));

			vholder.updownrate.setText((String) mData.get(paramInt).get(Const.PRICE_UPDOWNRATE) + "%");

			vholder.high.setText((String) mData.get(paramInt).get(Const.PRICE_HIGH));

			vholder.open.setText((String) mData.get(paramInt).get(Const.PRICE_OPEN));

			vholder.volume.setText((String) mData.get(paramInt).get(Const.PRICE_VOLUME));
			vholder.average.setText((String) mData.get(paramInt).get(Const.PRICE_AVERAGE));
			String turnovertmp = (String) mData.get(paramInt).get(Const.PRICE_TURNOVER);
			float turnovertmpvalue = util.getFloat(turnovertmp) / 10000;
			turnovertmpvalue = (float) Math.round(turnovertmpvalue * 100) / 100;
			vholder.turnover.setText(String.valueOf(turnovertmpvalue) + "万");
			String lastValue = (String) mData.get(paramInt).get(Const.PRICE_LAST);
			if (util.getFloat(lastValue) == 0) {
				vholder.last.setTextColor(Color.WHITE);
				vholder.updown.setTextColor(Color.WHITE);
				vholder.updownrate.setTextColor(Color.WHITE);
				vholder.low.setTextColor(Color.WHITE);
				vholder.close.setTextColor(Color.WHITE);
				vholder.settle.setTextColor(Color.WHITE);
				vholder.high.setTextColor(Color.WHITE);
				vholder.open.setTextColor(Color.WHITE);
				vholder.updown.setText("0.00");
				vholder.updownrate.setText("0.00" + "%");
			}
			return paramView;
		}

		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		public int getCount() {
			if (mData != null) {
				return mData.size();
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
	}

	// 自动更新线程
	/** Nested class that performs progress calculations (counting) */
	private class AutoUpdateThread extends Thread {
		Handler mHandler;
		private long waitTime = 10000;

		AutoUpdateThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			while (running) {
				try {
					sleep(10000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				progressThread = new ProgressThread(handler);
				progressThread.setState(2);
				progressThread.start();
				// try {
				// client.receive(recpacket);
				// String str = new String(recpacket.getData(), 0, 1000);
				// if (str.indexOf("{") >= 0 && str.indexOf("}") >= 0) {
				// timeNow = util.getTimeNowUDPString(str, timeNow);
				// if (timeNow != null) {
				// Message msg = mHandler.obtainMessage();
				// Bundle b = new Bundle();
				// b.putInt("total", 4);
				// msg.setData(b);
				// errorMsg = "";
				// mHandler.sendMessage(msg);
				// }
				// }
				// } catch (IOException e) {
				// Message msg = mHandler.obtainMessage();
				// Bundle b = new Bundle();
				// b.putInt("total", 2);
				// msg.setData(b);
				// errorMsg = "无法取得数据！请稍后再试。";
				// mHandler.sendMessage(msg);
				// // return;
				// }
			}
		}

	}

	// 菜单
	protected void editDialog() {
		Builder b = new AlertDialog.Builder(this);
		b.setTitle("选择需要删除的自选行情");
		b.setMultiChoiceItems(opsname, opssel, new DialogInterface.OnMultiChoiceClickListener() {
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				opssel[which] = isChecked; // 设置选中标志位
			}
		});
		b.setPositiveButton("删除", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				String opssave = "";
				String opnamesave = "";
				String exsave = "";
				for (int i = 0; i < ops.length; i++) {
					if (!opssel[i]) {
						if ("".equals(opssave)) {
							opssave = ops[i];
							opnamesave = opsname[i];
							exsave = exs[i];
						} else {
							opssave = opssave + "," + ops[i];
							opnamesave = opnamesave + "," + opsname[i];
							exsave = exsave + "," + exs[i];
						}
					}
				}
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(Const.PREF_OPTIONAL, opssave);
				editor.putString(Const.PREF_OPTIONAL_NAME, opnamesave);
				editor.putString(Const.PREF_EX, exsave);
				selected = opssave;
				if (!"".equals(opssave)) {
					if (opssave.indexOf(",") < 0) {
						ops = new String[] { opssave };
						opsname = new String[] { opnamesave };
						exs = new String[] { exsave };
					} else {
						ops = opssave.split(",");
						opsname = opnamesave.split(",");
						exs = exsave.split(",");
					}
					opssel = new boolean[ops.length];
				}
				editor.commit();
				dialog.dismiss();
				if (!"".equals(selected)) {

					progressDialog = new ProgressDialog(OptionalView.this);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.setMessage("取得数据...");
					// progressDialog.setTitle("请等待");
					progressDialog.setCancelable(true);
					progressDialog.show();
					progressThread = new ProgressThread(handler);
					progressThread.setState(1);
					progressThread.start();
					try {
						// client.close();
						autoUpdateThread = null;
						// sendActiveThread = null;
						// client = new DatagramSocket();
						// String sendStr = String
						// .format(Const.UDP_DIY + selected);
						// byte[] sendBuf = sendStr.getBytes();
						// sendBuf = sendStr.getBytes();
						// InetAddress address = InetAddress
						// .getByName(Const.UDP_IP);
						// sendpacket = new DatagramPacket(sendBuf,
						// sendBuf.length, address, Const.UDP_PORT);
						// client.send(sendpacket);
						// byte[] recBuf = new byte[1000];
						// recpacket = new DatagramPacket(recBuf,
						// recBuf.length);
						autoUpdateThread = new AutoUpdateThread(handler);
						running = true;
						autoUpdateThread.start();
						// sendActiveThread = new SendActiveThread();
						// sendActiveThread.start();
					} catch (Exception e) {

					}
				} else {
					listNameData = new ArrayList<Map<String, Object>>();
					SimpleAdapter adapter = new SimpleAdapter(OptionalView.this, listNameData, R.layout.pricenameitem,
							new String[] { Const.PRICE_NAME }, new int[] { R.id.pricename });
					priceNameListView.setAdapter(adapter);
					mData = new ArrayList<Map<String, Object>>();
					MyAdapter adapters = (MyAdapter) priceListView.getAdapter();
					adapters.notifyDataSetChanged();
				}

			}
		});
		b.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		b.create().show();
	}

	protected void exitDialog() {
		AlertDialog.Builder builder = new Builder(OptionalView.this);
		builder.setMessage("确认要退出汇通金融吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	// 菜单选择
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == 1) {

		} else if (item.getItemId() == 2) {
			exitDialog();
		}
		return true;
	}

	// // 自动发送active
	// /** Nested class that performs progress calculations (counting) */
	// private class SendActiveThread extends Thread {
	// private long waitTime = 10000;
	//
	// public void run() {
	// while (running) {
	// try {
	// sleep(waitTime);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// String sendStr = String.format("active,active");
	// byte[] sendBuf = sendStr.getBytes();
	// sendBuf = sendStr.getBytes();
	// try {
	// sendpacket.setData(sendBuf);
	// client.send(sendpacket);
	// } catch (Exception e) {
	//
	// }
	// }
	// }
	//
	// }
}
