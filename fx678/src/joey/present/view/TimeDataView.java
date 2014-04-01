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

import joey.present.data.KData;
import joey.present.data.PriceData;
import joey.present.util.Const;
import joey.present.util.Util;
import android.R.interpolator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.umeng.analytics.MobclickAgent;

public class TimeDataView extends Activity {

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

	private String log;
	// private int nowColor = Color.WHITE;
	private ImageButton addBtn;
	private String code;
	private String ex;
	private String name;
	private TextView detailname;
	private LinearLayout drawLayout;
	private LayoutInflater mInflater;
	private ListView abListView;
	private SlidingDrawer mDialerDrawer;
	private ImageButton slidingButton;

	private TextView nowvalue;
	private TextView updownpecent;
	private TextView updownvalue;
	private TextView evalue;
	private TextView closevalue;
	private TextView openvalue;
	private TextView highvalue;
	private TextView lowvalue;
	private TextView timenow;
	// ttj
	private TextView buylabel;
	private TextView ttjbuy;
	private TextView selllabel;
	private TextView ttjsell;
	// 本地配置保存对象
	private SharedPreferences preferences;
	// 分时历史数据list
	private List<KData> listData;
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

	private TimeDataDraw timeDataDraw;

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

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timedataview);
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

				TimeDataView.this.finish();
			}

		});
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		drawLayout = (LinearLayout) findViewById(R.id.timeviewlayout);
		abListView = (ListView) findViewById(R.id.timeABlist);
		MyAdapter adapter = new MyAdapter();
		abListView.setAdapter(adapter);
		nowvalue = (TextView) findViewById(R.id.nowvalue);
		updownpecent = (TextView) findViewById(R.id.updownpecent);
		updownvalue = (TextView) findViewById(R.id.updownvalue);
		evalue = (TextView) findViewById(R.id.evalue);
		openvalue = (TextView) findViewById(R.id.openvalue);
		closevalue = (TextView) findViewById(R.id.closevalue);
		highvalue = (TextView) findViewById(R.id.highvalue);
		lowvalue = (TextView) findViewById(R.id.lowvalue);
		timenow = (TextView) findViewById(R.id.timenow);
		// ttj
		buylabel = (TextView) findViewById(R.id.ttjbuylabel);
		ttjbuy = (TextView) findViewById(R.id.ttjbuy);
		selllabel = (TextView) findViewById(R.id.ttjselllabel);
		ttjsell = (TextView) findViewById(R.id.ttjsell);
		if (!Const.TTJ8.equals(ex)) {
			buylabel.setVisibility(View.INVISIBLE);
			ttjbuy.setVisibility(View.INVISIBLE);
			selllabel.setVisibility(View.INVISIBLE);
			ttjsell.setVisibility(View.INVISIBLE);
		}

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		timeDataDraw = new TimeDataDraw(TimeDataView.this, dm.widthPixels - 10, dm.widthPixels - 10, ex);
		drawLayout.addView(timeDataDraw);
		timeDataDraw.setCode(code);
		slidingButton = (ImageButton) findViewById(R.id.handle);
		slidingButton.setImageResource(R.drawable.shows);
		mDialerDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawer);
		if (!Const.SHGOLD1.equals(ex)) {
			mDialerDrawer.setVisibility(View.INVISIBLE);
		}
		// mDialerDrawer
		// .setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
		//
		// public void onDrawerOpened() {
		// slidingButton.setImageResource(R.drawable.hide);
		// }
		// });
		// mDialerDrawer.setOnDrawerScrollListener(new OnDrawerScrollListener()
		// {
		//
		// public void onScrollStarted() {
		//
		// slidingButton.setImageResource(R.drawable.hide);
		//
		// }
		//
		// public void onScrollEnded() {
		//
		// slidingButton.setImageResource(R.drawable.hide);
		//
		// }
		// });
		// mDialerDrawer
		// .setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
		//
		// public void onDrawerClosed() {
		// slidingButton.setImageResource(R.drawable.shows);
		// }
		//
		// });
		progressDialog = new ProgressDialog(TimeDataView.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("取得数据...");
		// progressDialog.setTitle("请等待");
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
		String listDataURL = Const.URL_TIMEDATA;
		listDataURL = listDataURL.replaceFirst(Const.URL_EX, ex);
		listDataURL = listDataURL.replaceFirst(Const.URL_CODE, code);
		listData = util.getTimeData(listDataURL);
		String timeNowURL = Const.URL_NOW;
		timeNowURL = timeNowURL.replaceFirst(Const.URL_EX, ex);
		timeNowURL = timeNowURL.replaceFirst(Const.URL_CODE, code);
		timeNowURL = timeNowURL.replaceFirst(Const.URL_DATE, "1333605270");
		timeNowURL = timeNowURL.replaceFirst(Const.URL_COUNT, "3");
		timeNow = util.getTimeNow(timeNowURL, ex);
		lastClose = util.getFloat(timeNow.getPrice_lastclose());
		if (Const.AUT_D.equals(code) || Const.AGT_D.equals(code)) {
			lastClose = util.getFloat(timeNow.getPrice_lastsettle());
		}
		timeDataDraw.setLastClose(lastClose);
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
		// ttj
		if (Const.TTJ8.equals(ex)) {
			ttjbuy.setText(String.valueOf(df_base.format(Float.parseFloat(timeNow.getPriceTTJbuy()))));
			ttjsell.setText(String.valueOf(df_base.format(Float.parseFloat(timeNow.getPriceTTJsell()))));
		}
		// ygy
		if (Const.YGY10.equals(ex) && (timeNow.getPriceTTJbuy() != null || timeNow.getPriceTTJsell() != null)) {
			ttjbuy.setText(String.valueOf(df_base.format(Float.parseFloat(timeNow.getPriceTTJbuy()))));
			ttjsell.setText(String.valueOf(df_base.format(Float.parseFloat(timeNow.getPriceTTJsell()))));
		}

		// 详细数据更新
		if ((util.getFloat(timeNow.getPrice_last()) - util.getFloat(timeNow.getPrice_lastclose())) > 0) {
			nowvalue.setTextColor(Color.BLACK);
			nowvalue.setBackgroundColor(Color.RED);
			bgcolor = 1;
			updownpecent.setTextColor(Color.RED);
			updownvalue.setTextColor(Color.RED);
		} else {
			nowvalue.setTextColor(Color.BLACK);
			nowvalue.setBackgroundColor(Color.GREEN);
			bgcolor = 2;
			updownpecent.setTextColor(Color.GREEN);
			updownvalue.setTextColor(Color.GREEN);
		}
		if ((util.getFloat(timeNow.getPrice_last()) - util.getFloat(timeNow.getPrice_lastclose())) == 0) {
			nowvalue.setTextColor(Color.WHITE);
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
		if ((util.getFloat(timeNow.getPrice_open()) - util.getFloat(timeNow.getPrice_lastclose())) > 0) {
			openvalue.setTextColor(Color.RED);
		} else {
			openvalue.setTextColor(Color.GREEN);
		}
		if ((util.getFloat(timeNow.getPrice_open()) - util.getFloat(timeNow.getPrice_lastclose())) == 0) {
			openvalue.setTextColor(Color.WHITE);
		}
		openvalue.setText(String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_open()))));
		closevalue.setText(String.valueOf(df_base.format(lastClose)));
		if ((util.getFloat(timeNow.getPrice_high()) - util.getFloat(timeNow.getPrice_lastclose())) > 0) {
			highvalue.setTextColor(Color.RED);
		} else {
			highvalue.setTextColor(Color.GREEN);
		}
		if ((util.getFloat(timeNow.getPrice_high()) - util.getFloat(timeNow.getPrice_lastclose())) == 0) {
			highvalue.setTextColor(Color.WHITE);
		}
		highvalue.setText(String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_high()))));
		if ((util.getFloat(timeNow.getPrice_low()) - util.getFloat(timeNow.getPrice_lastclose())) > 0) {
			lowvalue.setTextColor(Color.RED);
		} else {
			lowvalue.setTextColor(Color.GREEN);
		}
		if ((util.getFloat(timeNow.getPrice_low()) - util.getFloat(timeNow.getPrice_lastclose())) == 0) {
			lowvalue.setTextColor(Color.WHITE);
		}
		lowvalue.setText(String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_low()))));
		timenow.setText(util.formatTimeHms(timeNow.getPrice_quotetime()));
		// 五档数据更新
		if (!"".equals(timeNow.getPrice_ask5()) && !"".equals(timeNow.getPrice_ask4())
				&& !"".equals(timeNow.getPrice_ask3())&& !"".equals(timeNow.getPrice_ask2())
				&& !"".equals(timeNow.getPrice_ask1())) {
			mData = new ArrayList<Map<String, Object>>();
			mData.add(getABData("卖五", String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_ask5()))),
					timeNow.getPrice_asklot5()));
			mData.add(getABData("卖四", String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_ask4()))),
					timeNow.getPrice_asklot4()));
			mData.add(getABData("卖三", String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_ask3()))),
					timeNow.getPrice_asklot3()));
			mData.add(getABData("卖二", String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_ask2()))),
					timeNow.getPrice_asklot2()));
			mData.add(getABData("卖一", String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_ask1()))),
					timeNow.getPrice_asklot1()));
			mData.add(getABData("  ", "  ", "  "));
			mData.add(getABData("买一", String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_bid1()))),
					timeNow.getPrice_bidlot1()));
			mData.add(getABData("买二", String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_bid2()))),
					timeNow.getPrice_bidlot2()));
			mData.add(getABData("买三", String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_bid3()))),
					timeNow.getPrice_bidlot3()));
			mData.add(getABData("买四", String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_bid4()))),
					timeNow.getPrice_bidlot4()));
			mData.add(getABData("买五", String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_bid5()))),
					timeNow.getPrice_bidlot5()));

			MyAdapter adapter = (MyAdapter) abListView.getAdapter();
			adapter.notifyDataSetChanged();
		}

		if (listData != null && listData.size() > 0) {
			timeDataDraw.setInitFlag(true);
			timeDataDraw.updateData(listData);
			KData data = new KData();
			data.setK_average(timeNow.getPrice_average());
			data.setK_close(timeNow.getPrice_last());
			data.setK_date(util.formatTimeSec(timeNow.getPrice_quotetime()));
			data.setK_timeLong(timeNow.getPrice_quotetime());
			data.setK_high(timeNow.getPrice_high());
			data.setK_low(timeNow.getPrice_low());
			data.setK_open(timeNow.getPrice_open());
			data.setK_volume(timeNow.getPrice_volume());
			timeDataDraw.updateNew(data);
		}

		//
		handler.sendEmptyMessageDelayed(112, 300);

	}

	private Map<String, Object> getABData(String abname, String ab, String ablot) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Const.ABNAME, abname);
		map.put(Const.AB, ab);
		map.put(Const.ABLOT, ablot);
		return map;
	}

	// public void showData() {
	// timeDataDraw.setInitFlag(true);
	// timeDataDraw.updateData(listData);
	// Toast.makeText(getApplicationContext(), "数据已更新!" +
	// timeNow.getPrice_updown()+ timeNow.getPrice_updownrate() + log,
	// 6000).show();
	// int nowNum = listData.size() - 1;
	// nowvalue.setText(listData.get(nowNum).getK_close());
	// updownpecent.setText(listData.get(listData.size() -
	// 1).getPrice_updownrate());
	// updownvalue.setText(ls.get(ls.size() - 1).getPrice_updown());
	// cjvalue.setText(ls.get(ls.size() - 1).getPrice_volume());
	// avgvalue.setText(ls.get(ls.size() - 1).getPrice_average());
	// timenow.setText(listData.get(nowNum).getK_date());
	// }

	// 自动发送active
	/** Nested class that performs progress calculations (counting) */
	private class SendActiveThread extends Thread {
		private long waitTime = 10000;

		public void run() {

			while (running) {

				try {
					SendActiveThread.sleep(waitTime);
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
					AutoUpdateThread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					client.receive(recpacket);
					String str = new String(recpacket.getData(), 0, 1000);
					if (str.indexOf("{") >= 0 && str.indexOf("}") >= 0 && str.indexOf(code) >= 0) {
						timeNow = util.getTimeNowUDPString(str, timeNow);

						if (timeNow != null) {
							Message msg = mHandler.obtainMessage();
							Bundle b = new Bundle();
							b.putInt("total", 4);
							msg.setData(b);
							errorMsg = "";
							mHandler.sendMessage(msg);
						}

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

			if (msg.what == 112) {
				if (bgcolor == 1) {// 红色背景
					nowvalue.setTextColor(Color.RED);
					nowvalue.setBackgroundColor(Color.BLACK);
				} else if (bgcolor == 2) {// 绿色背景
					nowvalue.setTextColor(Color.GREEN);
					nowvalue.setBackgroundColor(Color.BLACK);
				}
			}
		}
	};

	private final class ViewHolder {
		public TextView abname;
		public TextView ab;
		public TextView ablot;
	}

	// 行情数据列表组件用数据adapter
	private class MyAdapter extends BaseAdapter {

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			ViewHolder holder = null;
			if (paramView == null) {
				paramView = mInflater.inflate(R.layout.timeabitem, null);
				holder = new ViewHolder();
				holder.abname = (TextView) paramView.findViewById(R.id.abname);
				holder.ab = (TextView) paramView.findViewById(R.id.ab);
				holder.ablot = (TextView) paramView.findViewById(R.id.ablot);

				paramView.setTag(holder);
			} else {
				holder = (ViewHolder) paramView.getTag();
			}
			String ab = (String) mData.get(paramInt).get(Const.AB);
			holder.ab.setTextColor(Color.WHITE);
			holder.ablot.setTextColor(Color.WHITE);
			if (util.getFloat(ab) - lastClose > 0) {
				holder.ab.setTextColor(Color.RED);
				holder.ablot.setTextColor(Color.RED);
			} else if (util.getFloat(ab) - lastClose < 0) {
				holder.ab.setTextColor(Color.GREEN);
				holder.ablot.setTextColor(Color.GREEN);
			}
			holder.abname.setText((String) mData.get(paramInt).get(Const.ABNAME));
			holder.ab.setText((String) mData.get(paramInt).get(Const.AB));
			holder.ablot.setText((String) mData.get(paramInt).get(Const.ABLOT));
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
		AlertDialog.Builder builder = new Builder(TimeDataView.this);
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
							String[] tmp = opTemp.split(",");
							for (int i = 0; i < tmp.length; i++) {
								if (code.equals(tmp[i])) {
									Message msg = handler.obtainMessage();
									Bundle b = new Bundle();
									b.putInt("total", 6);
									msg.setData(b);
									handler.sendMessage(msg);
									return;
								}
							}
							opTemp = opTemp + "," + code;
							exTemp = exTemp + "," + ex;
							opNameTemp = opNameTemp + "," + name;
							editor.putString(Const.PREF_OPTIONAL, opTemp);
							editor.putString(Const.PREF_EX, exTemp);
							editor.putString(Const.PREF_OPTIONAL_NAME, opNameTemp);
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
