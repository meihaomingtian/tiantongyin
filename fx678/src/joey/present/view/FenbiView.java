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
import joey.present.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.umeng.analytics.MobclickAgent;

public class FenbiView extends Activity {

	/**
	 * 颜色设置 0 无色 1红色 2绿色
	 */
	private int bgcolor = 0;
	/** 小数位约束 */
	private DecimalFormat df_base;

	private DecimalFormat df4 = new DecimalFormat("0.0000");
	private DecimalFormat df2 = new DecimalFormat("0.00");
	private DatagramSocket client;
	private DatagramPacket recpacket;
	private DatagramPacket sendpacket;

	// private int nowColor = Color.WHITE;
	private String log;
	private ImageButton addBtn;
	private String code;
	private String ex;
	private String name;
	private TextView detailname;
	private LayoutInflater mInflater;
	private ListView fenbiListView;

	private TextView nowvalue;
	private TextView updownpecent;
	private TextView updownvalue;
	private TextView evalue;
	private TextView closevalue;
	private TextView openvalue;
	private TextView highvalue;
	private TextView lowvalue;
	private TextView timenow;
	// 本地配置保存对象
	private SharedPreferences preferences;
	// 分时历史数据list
	private List<PriceData> listData;
	// 分时当前数据
	private PriceData timeNow;
	// 五档数据
	private List<Map<String, Object>> mData;

	private float lastClose = 0;// 最后收盘价

	// 数据取得等待对话框组件
	private ProgressDialog progressDialog;

	// 数据取得线程
	private ProgressThread progressThread;

	// 自动更新线程
	private AutoUpdateThread autoUpdateThread;

	// 自动发送active
	private SendActiveThread sendActiveThread;

	// 自动更新线程启动标志
	private boolean running = false;

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
		running = false;
		client.close();
		autoUpdateThread = null;
		sendActiveThread = null;

	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		Thread t = new Thread() {
			public void run() {
				try {
					client.close();
					autoUpdateThread = null;
					sendActiveThread = null;
					client = new DatagramSocket();
					String sendStr = String.format(Const.UDP_PARA + code);
					byte[] sendBuf = sendStr.getBytes();
					sendBuf = sendStr.getBytes();
					InetAddress address = InetAddress.getByName(Const.UDP_IP);
					sendpacket = new DatagramPacket(sendBuf, sendBuf.length, address, Const.UDP_PORT);
					client.send(sendpacket);
					byte[] recBuf = new byte[1000];
					recpacket = new DatagramPacket(recBuf, recBuf.length);
					autoUpdateThread = new AutoUpdateThread(handler);
					running = true;
					autoUpdateThread.start();
					sendActiveThread = new SendActiveThread();
					sendActiveThread.start();
				} catch (Exception e) {

				}
			}
		};
		t.start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.fenbiview);
		Bundle bundle = getIntent().getExtras();
		code = bundle.getString("code");
		ex = bundle.getString("ex");
		name = bundle.getString("name");
		String df_str = bundle.getString(Const.PRICE_DECIMAL);
		//Log.i("temp", "df_str->" + df_str);

		df_base = Const.df4;

		if (df_str != null) {
			if ("0".equals(df_str)) {
				df_base = Const.df0;
			} else if ("1".equals(df_str)) {
				df_base = Const.df1;
			} else if ("2".equals(df_str)) {
				df_base = Const.df2;
			} else if ("3".equals(df_str)) {
				df_base = Const.df3;
			} else if ("4".equals(df_str)) {
				df_base = Const.df4;
			}
		}

		addBtn = (ImageButton) findViewById(R.id.addbtn);
		detailname = (TextView) findViewById(R.id.detailname);
		detailname.setText(name);
		preferences = getSharedPreferences(Const.PREFERENCES_NAME, Activity.MODE_PRIVATE);
		addBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				addDialog();
			}
		});
		ImageButton backBtn = (ImageButton) findViewById(R.id.backbtn);
		backBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				FenbiView.this.finish();
			}

		});
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		fenbiListView = (ListView) findViewById(R.id.fenbiListView);
		MyAdapter adapter = new MyAdapter();
		fenbiListView.setAdapter(adapter);
		nowvalue = (TextView) findViewById(R.id.nowvalue);
		updownpecent = (TextView) findViewById(R.id.updownpecent);
		updownvalue = (TextView) findViewById(R.id.updownvalue);
		evalue = (TextView) findViewById(R.id.evalue);
		openvalue = (TextView) findViewById(R.id.openvalue);
		closevalue = (TextView) findViewById(R.id.closevalue);
		highvalue = (TextView) findViewById(R.id.highvalue);
		lowvalue = (TextView) findViewById(R.id.lowvalue);
		timenow = (TextView) findViewById(R.id.timenow);
		progressDialog = new ProgressDialog(FenbiView.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("取得数据...");
		//progressDialog.setTitle("请等待");
		progressDialog.setCancelable(true);
		progressDialog.show();
		progressThread = new ProgressThread(handler);
		progressThread.setState(1);
		progressThread.start();
		Thread t = new Thread() {
			public void run() {
				try {
					client = new DatagramSocket();
					String sendStr = String.format(Const.UDP_PARA + code);
					byte[] sendBuf = sendStr.getBytes();
					sendBuf = sendStr.getBytes();
					InetAddress address = InetAddress.getByName(Const.UDP_IP);
					sendpacket = new DatagramPacket(sendBuf, sendBuf.length, address, Const.UDP_PORT);
					client.send(sendpacket);
					byte[] recBuf = new byte[1000];
					recpacket = new DatagramPacket(recBuf, recBuf.length);
					autoUpdateThread = new AutoUpdateThread(handler);
					running = true;
					autoUpdateThread.start();
				} catch (Exception e) {

				}
			}
		};
		t.start();

	}

	private synchronized void updateData(String code) {
		String timeNowURL = Const.URL_NOW;
		timeNowURL = timeNowURL.replaceFirst(Const.URL_EX, ex);
		timeNowURL = timeNowURL.replaceFirst(Const.URL_CODE, code);
		timeNowURL = timeNowURL.replaceFirst(Const.URL_DATE, "1333605270");
		timeNowURL = timeNowURL.replaceFirst(Const.URL_COUNT, "40");
		listData = util.getFenbi(timeNowURL, ex);
		String timeNowU = Const.URL_NOW;
		timeNowU = timeNowU.replaceFirst(Const.URL_EX, ex);
		timeNowU = timeNowU.replaceFirst(Const.URL_CODE, code);
		timeNowU = timeNowU.replaceFirst(Const.URL_DATE, "1333605270");
		timeNowU = timeNowU.replaceFirst(Const.URL_COUNT, "1");
		timeNow = util.getTimeNow(timeNowU, ex);
		lastClose = util.getFloat(timeNow.getPrice_lastclose());
		if (Const.AUT_D.equals(code) || Const.AGT_D.equals(code)) {
			lastClose = util.getFloat(timeNow.getPrice_lastsettle());
		}
		// log = timeNowURL+ timeNow.getPrice_last() +
		// timeNow.getPrice_lastclose();
	}

	public void showNew() {
		progressDialog.dismiss();
		if (lastClose != 0) {
			float updown = util.getFloat(timeNow.getPrice_last()) - lastClose;
			float updownrate = updown / lastClose * 100;
			timeNow.setPrice_updown(String.valueOf(df4.format(updown)));
			timeNow.setPrice_updownrate(String.valueOf(df2.format(updownrate)));
			timeNow.setPrice_lastclose(String.valueOf(lastClose));
		}
		if (listData.size() > 0 && !listData.get(0).getPrice_quotetime().equals(timeNow.getPrice_quotetime())) {
			listData.add(listData.get(listData.size() - 1));
			for (int i = listData.size() - 1; i > 0; i--) {
				listData.set(i, listData.get(i - 1));
			}
			listData.set(0, timeNow);
		}

		// listData = listDataTemp;
		mData = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < listData.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			PriceData pData = listData.get(i);
			map.put(Const.FENBI_PRICE, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_last()))));
			map.put(Const.FENBI_TIME, util.formatTimeHms(pData.getPrice_quotetime()));
			map.put(Const.FENBI_VOLUME, pData.getPrice_volume());
			map.put(Const.PRICE_UPDOWN, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_updown()))));
			mData.add(map);
		}

		// 详细数据更新
		if (util.getFloat(timeNow.getPrice_last()) - lastClose > 0) {
			nowvalue.setTextColor(Color.BLACK);
			nowvalue.setBackgroundColor(Color.RED);
			// nowColor = Color.RED;
			bgcolor = 1;
			updownpecent.setTextColor(Color.RED);
			updownvalue.setTextColor(Color.RED);
		} else {
			nowvalue.setBackgroundColor(Color.GREEN);
			nowvalue.setTextColor(Color.BLACK);
			// nowColor = Color.RED;
			bgcolor = 2;
			updownpecent.setTextColor(Color.GREEN);
			updownvalue.setTextColor(Color.GREEN);
		}
		if (util.getFloat(timeNow.getPrice_last()) - lastClose == 0) {
			nowvalue.setTextColor(Color.WHITE);
			// nowColor = Color.WHITE;
			updownpecent.setTextColor(Color.WHITE);
			updownvalue.setTextColor(Color.WHITE);
		}
		nowvalue.setText(String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_last()))));
		updownpecent.setText(timeNow.getPrice_updownrate() + "%");
		updownvalue.setText(String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_updown()))));
		evalue.setTextColor(Color.YELLOW);
		String turnovertmp = timeNow.getPrice_turnover();
		float turnovertmpvalue = util.getFloat(turnovertmp) / 10000;
		turnovertmpvalue = (float) Math.round(turnovertmpvalue * 100) / 100;
		evalue.setText(String.valueOf(turnovertmpvalue) + "万");
		if (util.getFloat(timeNow.getPrice_open()) - lastClose > 0) {
			openvalue.setTextColor(Color.RED);
		} else {
			openvalue.setTextColor(Color.GREEN);
		}
		if (util.getFloat(timeNow.getPrice_open()) - lastClose == 0) {
			openvalue.setTextColor(Color.WHITE);
		}
		openvalue.setText(String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_open()))));
		closevalue.setText(String.valueOf(df_base.format(lastClose)));
		if (util.getFloat(timeNow.getPrice_high()) - lastClose > 0) {
			highvalue.setTextColor(Color.RED);
		} else {
			highvalue.setTextColor(Color.GREEN);
		}
		if (util.getFloat(timeNow.getPrice_high()) - lastClose == 0) {
			highvalue.setTextColor(Color.WHITE);
		}
		highvalue.setText(String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_high()))));
		if (util.getFloat(timeNow.getPrice_low()) - lastClose > 0) {
			lowvalue.setTextColor(Color.RED);
		} else {
			lowvalue.setTextColor(Color.GREEN);
		}
		if (util.getFloat(timeNow.getPrice_low()) - lastClose == 0) {
			lowvalue.setTextColor(Color.WHITE);
		}
		lowvalue.setText(String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_low()))));
		timenow.setText(util.formatTimeHms(timeNow.getPrice_quotetime()));
		MyAdapter adapter = (MyAdapter) fenbiListView.getAdapter();
		adapter.notifyDataSetChanged();

		handler.sendEmptyMessageDelayed(112, 300);
	}

	// 自动发送active
	/** Nested class that performs progress calculations (counting) */
	private class SendActiveThread extends Thread {
		private long waitTime = 10000;

		public void run() {

			while (running) {
				try {
					sleep(waitTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String sendStr = String.format("active,active");
				byte[] sendBuf = sendStr.getBytes();
				sendBuf = sendStr.getBytes();
				try {
					sendpacket.setData(sendBuf);
					client.send(sendpacket);
				} catch (Exception e) {

				}
			}
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
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					client.receive(recpacket);
					String str = new String(recpacket.getData(), 0, 1000);
					if (str.indexOf("{") >= 0 && str.indexOf("}") >= 0 && str.indexOf(code) >= 0) {
						timeNow = util.getTimeNowUDPString(str, timeNow);

						Message msg = mHandler.obtainMessage();
						Bundle b = new Bundle();
						b.putInt("total", 4);
						msg.setData(b);
						errorMsg = "";
						mHandler.sendMessage(msg);
					}
				} catch (IOException e) {
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 2);
					msg.setData(b);
					errorMsg = "无法取得数据！请稍后再试。";
					mHandler.sendMessage(msg);
					// return;
				}
			}
		}

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
					updateData(code);
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
					updateData(code);
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 4);
					msg.setData(b);
					errorMsg = "";
					progressDialog.dismiss();
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

			if (msg.what == 112) {
				if (bgcolor == 1) {// 红色背景
					nowvalue.setTextColor(Color.RED);
					nowvalue.setBackgroundColor(Color.BLACK);
				} else if (bgcolor == 2) {// 绿色背景
					nowvalue.setTextColor(Color.GREEN);
					nowvalue.setBackgroundColor(Color.BLACK);
				}
			}

			int total = msg.getData().getInt("total");
			if (total == 1) {
				sendActiveThread = new SendActiveThread();
				sendActiveThread.start();
				showNew();
			}
			if (total == 2) {
				if (!"".equals(errorMsg)) {
					// errorDialog("异常", errorMsg);
				}
			}
			if (total == 3 && running) {
				Toast.makeText(getApplicationContext(), "已添加到我的自选中。", 4000).show();
			}
			if (total == 4 && running) {
				showNew();
			}
			if (total == 6 && running) {
				Toast.makeText(getApplicationContext(), "我的自选中已有此项。", 4000).show();
			}
			if (total == 7 && running) {
				// nowvalue.setTextColor(nowColor);
				// nowvalue.setBackgroundColor(Color.TRANSPARENT);
			}
		}
	};

	private final class ViewHolder {
		public TextView fenbitime;
		public TextView fenbiprice;
		public TextView fenbivolume;
	}

	// 行情数据列表组件用数据adapter
	private class MyAdapter extends BaseAdapter {

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			ViewHolder vholder = null;
			if (paramView == null) {
				vholder = new ViewHolder();
				paramView = mInflater.inflate(R.layout.fenbiitem, null);
				// vholder.name = (TextView) paramView
				// .findViewById(R.id.pricename);
				vholder.fenbitime = (TextView) paramView.findViewById(R.id.fenbi_time);
				vholder.fenbiprice = (TextView) paramView.findViewById(R.id.fenbi_price);
				vholder.fenbivolume = (TextView) paramView.findViewById(R.id.fenbi_volume);
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

			vholder.fenbiprice.setTextColor(Color.WHITE);
			String updown = (String) mData.get(paramInt).get(Const.PRICE_UPDOWN);
			if (util.getFloat(updown) != 0) {
				if (util.getFloat(updown) < 0) {
					vholder.fenbiprice.setTextColor(Color.GREEN);
				} else {
					vholder.fenbiprice.setTextColor(Color.RED);
				}
			}

			vholder.fenbitime.setText((String) mData.get(paramInt).get(Const.FENBI_TIME));

			vholder.fenbiprice.setText((String) mData.get(paramInt).get(Const.FENBI_PRICE));

			vholder.fenbivolume.setText((String) mData.get(paramInt).get(Const.FENBI_VOLUME));

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

	protected void addDialog() {
		AlertDialog.Builder builder = new Builder(FenbiView.this);
		builder.setMessage("确认要添加到我的自选吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				Thread t = new Thread() {
					public void run() {

						SharedPreferences.Editor editor = preferences.edit();
						String opTemp = preferences.getString(Const.PREF_OPTIONAL, "");
						String opNameTemp = preferences.getString(Const.PREF_OPTIONAL_NAME, "");
						String exTemp = preferences.getString(Const.PREF_EX, "");
						if ("".equals(opTemp)) {
							editor.putString(Const.PREF_OPTIONAL, code);
							editor.putString(Const.PREF_OPTIONAL_NAME, name);
							editor.putString(Const.PREF_EX, ex);
						} else {
							if (opTemp.indexOf(code) >= 0) {
								Message msg = handler.obtainMessage();
								Bundle b = new Bundle();
								b.putInt("total", 6);
								msg.setData(b);
								handler.sendMessage(msg);
								return;
							} else {
								opTemp = opTemp + "," + code;
								exTemp = exTemp + "," + ex;
								opNameTemp = opNameTemp + "," + name;
								editor.putString(Const.PREF_OPTIONAL, opTemp);
								editor.putString(Const.PREF_EX, exTemp);
								editor.putString(Const.PREF_OPTIONAL_NAME, opNameTemp);
							}
						}
						editor.commit();
						Message msg = handler.obtainMessage();
						Bundle b = new Bundle();
						b.putInt("total", 3);
						msg.setData(b);
						handler.sendMessage(msg);
					}
				};
				t.start();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}
