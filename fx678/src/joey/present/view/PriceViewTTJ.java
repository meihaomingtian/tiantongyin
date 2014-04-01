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
import joey.present.view.ui.SyncHorizontalScrollView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.umeng.analytics.MobclickAgent;

public class PriceViewTTJ extends Activity {

	private DecimalFormat df4 = new DecimalFormat("0.0000");
	private DecimalFormat df2 = new DecimalFormat("0.00");
	// private static int colorA = Color.argb(255, 15, 15, 15);
	// private static int colorB = Color.argb(255, 21, 21, 21);
	// public static final int[] colors = new int[] { colorA, colorB };
	private int[] colors = new int[] { 0xe1151515, 0xe10f0f0f };
	private DatagramSocket client;
	private DatagramPacket recpacket;
	private DatagramPacket sendpacket;

	// private ImageButton searchBtn;

	private int lastItem = 0;

	private boolean refreshData = false;

	private LayoutInflater mInflater;

	private ListView priceListView;

	private ListView priceNameListView;

	private SyncHorizontalScrollView headScroll;

	private SyncHorizontalScrollView listScroll;

	private GestureDetector mGesture;

	// 更新数据
	private PriceData timeNow;
	// 更新数据list
	private List<PriceData> refreshListData;

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

	// 自动发送active
	private SendActiveThread sendActiveThread;

	// 自动更新线程启动标志
	private boolean running = false;

	private String selected;

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
		client.close();
		running = false;
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
					String sendStr = String.format(Const.UDP_PARA + selected);
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
		setContentView(R.layout.priceviewttj);
		Bundle bundle = getIntent().getExtras();
		String titleShow = bundle.getString("selectedname");
		selected = bundle.getString("selected");
		running = true;
		type = bundle.getString("selectedex");
		// log.i("temp", "ex -> " + type);
		// searchBtn = (ImageButton) findViewById(R.id.search);
		title = (TextView) findViewById(R.id.title);
		title.setText(titleShow);

		// shgoldBtn = (Button) findViewById(R.id.shgold);
		// selectedType.setText(R.string.shgold);
		// shgoldBtn.setBackgroundResource(R.drawable.nbtnbg_pressed);
		// shgoldBtn.setTextColor(Color.BLACK);
		// lastSelectBtn = shgoldBtn;
		// shgoldBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// Button btn = (Button) v;
		//
		// selectedType.setText(R.string.shgold);
		//
		// if (!btn.equals(lastSelectBtn)) {
		// running = false;
		// autoUpdateThread = null;
		// btn.setBackgroundResource(R.drawable.nbtnbg_pressed);
		// btn.setTextColor(Color.BLACK);
		// lastSelectBtn.setBackgroundResource(R.layout.button);
		// lastSelectBtn.setTextColor(Color.WHITE);
		// selected = Const.SHGOLD;
		// type = Const.SHGOLD1;
		// progressDialog = new ProgressDialog(PriceView.this);
		// progressDialog
		// .setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// progressDialog.setMessage("取得数据...");
		// progressDialog.setTitle("请等待");
		// progressDialog.setCancelable(true);
		// progressDialog.show();
		// progressThread = new ProgressThread(handler);
		// progressThread.setState(1);
		// progressThread.start();
		// lastSelectBtn = btn;
		// }
		// }
		// });
		// hjxhBtn = (Button) findViewById(R.id.hjxh);
		// hjxhBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// Button btn = (Button) v;
		//
		// selectedType.setText(R.string.hjxh);
		//
		// if (!btn.equals(lastSelectBtn)) {
		// running = false;
		// autoUpdateThread = null;
		// btn.setBackgroundResource(R.drawable.nbtnbg_pressed);
		// btn.setTextColor(Color.BLACK);
		// lastSelectBtn.setBackgroundResource(R.layout.button);
		// lastSelectBtn.setTextColor(Color.WHITE);
		// selected = Const.HJXH;
		// type = Const.HJXH2;
		// progressDialog = new ProgressDialog(PriceView.this);
		// progressDialog
		// .setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// progressDialog.setMessage("取得数据...");
		// progressDialog.setTitle("请等待");
		// progressDialog.setCancelable(true);
		// progressDialog.show();
		// progressThread = new ProgressThread(handler);
		// progressThread.setState(1);
		// progressThread.start();
		// lastSelectBtn = btn;
		//
		// }
		// }
		// });
		// stockindexBtn = (Button) findViewById(R.id.stockindex);
		// stockindexBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// Button btn = (Button) v;
		//
		// selectedType.setText(R.string.stockindex);
		//
		// if (!btn.equals(lastSelectBtn)) {
		// running = false;
		// autoUpdateThread = null;
		// btn.setBackgroundResource(R.drawable.nbtnbg_pressed);
		// btn.setTextColor(Color.BLACK);
		// lastSelectBtn.setBackgroundResource(R.layout.button);
		// lastSelectBtn.setTextColor(Color.WHITE);
		// selected = Const.STOCKINDEX;
		// type = Const.STOCKINDEX3;
		// progressDialog = new ProgressDialog(PriceView.this);
		// progressDialog
		// .setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// progressDialog.setMessage("取得数据...");
		// progressDialog.setTitle("请等待");
		// progressDialog.setCancelable(true);
		// progressDialog.show();
		// progressThread = new ProgressThread(handler);
		// progressThread.setState(1);
		// progressThread.start();
		// lastSelectBtn = btn;
		// }
		// }
		// });
		//
		// whBtn = (Button) findViewById(R.id.wh);
		// whBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// Button btn = (Button) v;
		//
		// selectedType.setText(R.string.wh);
		//
		// if (!btn.equals(lastSelectBtn)) {
		// running = false;
		// autoUpdateThread = null;
		// btn.setBackgroundResource(R.drawable.nbtnbg_pressed);
		// btn.setTextColor(Color.BLACK);
		// lastSelectBtn.setBackgroundResource(R.layout.button);
		// lastSelectBtn.setTextColor(Color.WHITE);
		// selected = Const.WH;
		// type = Const.WH4;
		// progressDialog = new ProgressDialog(PriceView.this);
		// progressDialog
		// .setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// progressDialog.setMessage("取得数据...");
		// progressDialog.setTitle("请等待");
		// progressDialog.setCancelable(true);
		// progressDialog.show();
		// progressThread = new ProgressThread(handler);
		// progressThread.setState(1);
		// progressThread.start();
		// lastSelectBtn = btn;
		// }
		// }
		// });
		ImageButton backBtn = (ImageButton) findViewById(R.id.backbtn);
		backBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				PriceViewTTJ.this.finish();
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
		// priceListView.mListHead = (LinearLayout)
		// findViewById(R.id.headcolumnheadmov);
		MyAdapter adapter = new MyAdapter();
		priceListView.setAdapter(adapter);
		// priceListView.setOnScrollListener(new OnScrollListener() {
		//
		// public void onScroll(AbsListView v, int firstVisibleItem,
		// int visibleItemCount, int totalItemCount) {
		// int Pos[] = { -1, -1 };
		// v.getLocationOnScreen(Pos);
		// oldListY = Pos[1];
		// }
		//
		// public void onScrollStateChanged(AbsListView view, int scrollState) {
		//
		// int Pos[] = { -1, -1 };
		// view.getLocationOnScreen(Pos);
		// priceListView.getScrollY();
		// }
		// });
		priceListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (listData.size() > position) {
					PriceData pData = listData.get(position);
					Intent in = new Intent(PriceViewTTJ.this, DetailTab.class);
					Bundle bundle = new Bundle();
					bundle.putString("code", pData.getPrice_code());
					bundle.putString("name", pData.getPrice_name());
					bundle.putString("selected", selected);
					bundle.putString("ex", type);
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

		progressDialog = new ProgressDialog(PriceViewTTJ.this);
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
					String sendStr = String.format(Const.UDP_PARA + selected);
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

	private void updateData(String type) {
		// listData =
		// util.getPriceDataTTJ("http://mhth.fx678.com/quotelist.aspx?key=" +
		// type,
		// type);
		listData = util.getPriceDataTTJ("http://mhth.fx678.com/quotelist.aspx?key=" + type, type);
	}

	public void showNew() {

		if (refreshListData.size() <= 0) {
			return;
		}

		PriceData timeNow = refreshListData.get(refreshListData.size() - 1);

		// String code = timeNow.getPrice_code();
		// if (code != null && mData != null) {
		for (int i = 0; i < mData.size(); i++) {
			Map<String, Object> map = mData.get(i);
			String code = (String) map.get(Const.PRICE_CODE);
			String decimal = (String) map.get(Const.PRICE_DECIMAL);
			if (code.equals(timeNow.getPrice_code())) {

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

				if ("".equals(timeNow.getPrice_lastclose())) {
					float lastClose = util.getFloat((String) map.get(Const.PRICE_LASTCLOSE));
					if (Const.AUT_D.equals(code) || Const.AGT_D.equals(code)) {
						if (map.get(Const.PRICE_SETTLE) != null && !"".equals(map.get(Const.PRICE_SETTLE))) {
							lastClose = util.getFloat((String) map.get(Const.PRICE_SETTLE));
						}
					}
					float updown = util.getFloat(timeNow.getPrice_last()) - lastClose;
					timeNow.setPrice_lastclose(String.valueOf(lastClose));
					float updownrate = updown / lastClose * 100;
					if (Const.WH4.equals(type) && !Const.USD.equals(timeNow.getPrice_code())
							&& !Const.USDJPY.equals(timeNow.getPrice_code())) {
						timeNow.setPrice_updown(String.valueOf(df4.format(updown)));
					} else {
						timeNow.setPrice_updown(String.valueOf(df2.format(updown)));
					}
					timeNow.setPrice_updownrate(String.valueOf(df2.format(updownrate)));

				}
				map.put(Const.PRICE_TTJSELL, String.valueOf(df_base.format(Float.parseFloat(timeNow.getPriceTTJsell()))));
				map.put(Const.PRICE_TTJBUY, String.valueOf(df_base.format(Float.parseFloat(timeNow.getPriceTTJbuy()))));
				// //log.i("temp", "amp ->" + timeNow.getPriceTTJAmplitude());
				if (timeNow.getPriceTTJAmplitude() != null) {
					if(!"".equals(timeNow.getPriceTTJAmplitude())){
						map.put(Const.PRICE_TTJAMLITUDE,
								String.valueOf(df_base.format(Float.parseFloat(timeNow.getPriceTTJAmplitude()))));
					}
				}
				map.put(Const.PRICE_QUOTETIME, timeNow.getPrice_quotetime());
				map.put(Const.PRICE_OPEN, String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_open()))));
				map.put(Const.PRICE_HIGH, String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_high()))));
				map.put(Const.PRICE_LAST, String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_last()))));
				map.put(Const.PRICE_LOW, String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_low()))));
				// map.put(Const.PRICE_NAME, timeNow.getPrice_name());
				map.put(Const.PRICE_CODE, timeNow.getPrice_code());
				// map.put(Const.PRICE_LASTCLOSE,
				// timeNow.getPrice_lastclose());
				// map.put(Const.PRICE_LASTSETTLE,
				// timeNow.getPrice_lastsettle());
				map.put(Const.PRICE_UPDOWN, String.valueOf(df_base.format(Float.parseFloat(timeNow.getPrice_updown()))));
				map.put(Const.PRICE_UPDOWNRATE, timeNow.getPrice_updownrate());
			}
		}

		this.timeNow = timeNow;
		MyAdapter adapter = (MyAdapter) priceListView.getAdapter();
		adapter.notifyDataSetChanged();
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
		progressDialog.dismiss();
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
			map.put(Const.PRICE_DECIMAL, pData.getPrice_Decimal());
			map.put(Const.PRICE_LASTCLOSE, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_lastclose()))));
			map.put(Const.PRICE_UPDOWN, String.valueOf(df_base.format(Float.parseFloat(pData.getPrice_updown()))));
			map.put(Const.PRICE_UPDOWNRATE, pData.getPrice_updownrate());
			map.put(Const.PRICE_TTJBUY, String.valueOf(df_base.format(Float.parseFloat(pData.getPriceTTJbuy()))));
			map.put(Const.PRICE_TTJSELL, String.valueOf(df_base.format(Float.parseFloat(pData.getPriceTTJsell()))));
			map.put(Const.PRICE_TTJAMLITUDE, String.valueOf(df_base.format(Float.parseFloat(pData.getPriceTTJAmplitude()))));
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
		CustomAdapter adapterS = new CustomAdapter(this, listNameData, R.layout.pricenameitem, new String[] { Const.PRICE_NAME },
				new int[] { R.id.pricename });

		priceNameListView.setAdapter(adapterS);
		MyAdapter adapter = (MyAdapter) priceListView.getAdapter();
		adapter.notifyDataSetChanged();
		refreshListData = new ArrayList<PriceData>();
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
				sendActiveThread = new SendActiveThread();
				sendActiveThread.start();
				showData();
			}
			if (total == 2) {
				if (!"".equals(errorMsg)) {
					// errorDialog("异常", errorMsg);
				}
			}

			if (total == 4 && running) {
				showNew();
				// Toast.makeText(getApplicationContext(), "数据已更新!",
				// 1000).show();
			}

		}
	};

	private class ViewHolder {
		// public TextView name;
		public TextView last;
		public TextView low;
		public TextView close;
		public TextView updown;
		public TextView updownrate;
		public TextView high;
		public TextView open;
		public TextView buy;
		public TextView sell;
		public TextView amplitude;
	}

	// 行情数据列表组件用数据adapter
	private class MyAdapter extends BaseAdapter {

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			ViewHolder vholder = null;
			if (paramView == null) {
				vholder = new ViewHolder();
				paramView = mInflater.inflate(R.layout.priceitemttj, null);
				// vholder.name = (TextView) paramView
				// .findViewById(R.id.pricename);
				vholder.last = (TextView) paramView.findViewById(R.id.last);
				vholder.low = (TextView) paramView.findViewById(R.id.low);
				vholder.close = (TextView) paramView.findViewById(R.id.close);
				vholder.updown = (TextView) paramView.findViewById(R.id.updown);
				vholder.updownrate = (TextView) paramView.findViewById(R.id.updownrate);
				vholder.high = (TextView) paramView.findViewById(R.id.high);
				vholder.open = (TextView) paramView.findViewById(R.id.open);
				vholder.buy = (TextView) paramView.findViewById(R.id.buy);
				vholder.sell = (TextView) paramView.findViewById(R.id.sell);
				vholder.amplitude = (TextView) paramView.findViewById(R.id.amplitude);
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
			vholder.buy.setTextColor(Color.WHITE);
			vholder.sell.setTextColor(Color.WHITE);
			vholder.amplitude.setTextColor(Color.WHITE);
			vholder.last.setTextColor(Color.WHITE);
			if (!refreshData) {
				vholder.last.setBackgroundColor(Color.TRANSPARENT);
			}
			vholder.low.setTextColor(Color.WHITE);
			vholder.close.setTextColor(Color.WHITE);
			vholder.updown.setTextColor(Color.WHITE);
			vholder.updownrate.setTextColor(Color.WHITE);
			vholder.high.setTextColor(Color.WHITE);
			vholder.open.setTextColor(Color.WHITE);
			String updown = (String) mData.get(paramInt).get(Const.PRICE_UPDOWN);
			String newCode = (String) mData.get(paramInt).get(Const.PRICE_CODE);
			if (util.getFloat(updown) != 0) {
				if (util.getFloat(updown) < 0) {
					vholder.buy.setTextColor(Color.GREEN);
					vholder.sell.setTextColor(Color.GREEN);
					vholder.amplitude.setTextColor(Color.GREEN);
					vholder.low.setTextColor(Color.GREEN);
					vholder.updown.setTextColor(Color.GREEN);
					vholder.updownrate.setTextColor(Color.GREEN);
					vholder.high.setTextColor(Color.GREEN);
					vholder.open.setTextColor(Color.GREEN);
					if (timeNow != null && newCode.equals(timeNow.getPrice_code())) {
						vholder.last.setBackgroundColor(Color.GREEN);
						vholder.last.setTextColor(Color.BLACK);
					} else {
						vholder.last.setBackgroundColor(Color.TRANSPARENT);
						vholder.last.setTextColor(Color.GREEN);
					}
				} else {
					vholder.buy.setTextColor(Color.RED);
					vholder.sell.setTextColor(Color.RED);
					vholder.amplitude.setTextColor(Color.RED);
					vholder.last.setTextColor(Color.RED);
					vholder.low.setTextColor(Color.RED);
					vholder.updown.setTextColor(Color.RED);
					vholder.updownrate.setTextColor(Color.RED);
					vholder.high.setTextColor(Color.RED);
					vholder.open.setTextColor(Color.RED);
					if (timeNow != null && newCode.equals(timeNow.getPrice_code())) {
						vholder.last.setBackgroundColor(Color.RED);
						vholder.last.setTextColor(Color.BLACK);
					} else {
						vholder.last.setBackgroundColor(Color.TRANSPARENT);
						vholder.last.setTextColor(Color.RED);
					}
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

			vholder.buy.setText((String) mData.get(paramInt).get(Const.PRICE_TTJBUY));

			vholder.sell.setText((String) mData.get(paramInt).get(Const.PRICE_TTJSELL));

			if (!"".equals((String) mData.get(paramInt).get(Const.PRICE_TTJAMLITUDE))) {
				vholder.amplitude.setText((String) mData.get(paramInt).get(Const.PRICE_TTJAMLITUDE));
			}

			vholder.last.setText((String) mData.get(paramInt).get(Const.PRICE_LAST));

			vholder.low.setText((String) mData.get(paramInt).get(Const.PRICE_LOW));

			vholder.close.setText((String) mData.get(paramInt).get(Const.PRICE_LASTCLOSE));

			vholder.updown.setText((String) mData.get(paramInt).get(Const.PRICE_UPDOWN));

			vholder.updownrate.setText((String) mData.get(paramInt).get(Const.PRICE_UPDOWNRATE) + "%");

			vholder.high.setText((String) mData.get(paramInt).get(Const.PRICE_HIGH));

			vholder.open.setText((String) mData.get(paramInt).get(Const.PRICE_OPEN));

			String lastValue = (String) mData.get(paramInt).get(Const.PRICE_LAST);
			if (util.getFloat(lastValue) == 0) {
				vholder.last.setTextColor(Color.WHITE);
				vholder.updown.setTextColor(Color.WHITE);
				vholder.updownrate.setTextColor(Color.WHITE);
				vholder.low.setTextColor(Color.WHITE);
				vholder.close.setTextColor(Color.WHITE);
				vholder.high.setTextColor(Color.WHITE);
				vholder.open.setTextColor(Color.WHITE);
				vholder.updown.setText("0.00");
				vholder.updownrate.setText("0.00" + "%");
			}
			int colorPos = paramInt % colors.length;
			paramView.setBackgroundColor(colors[colorPos]);
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
					AutoUpdateThread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				refreshData = true;
				try {
					byte[] recBuf = new byte[500];
					DatagramPacket recpacket = new DatagramPacket(recBuf, recBuf.length);
					client.receive(recpacket);
					String str = new String(recpacket.getData(), 0, 500);
					if (str.indexOf("{") >= 0 && str.indexOf("}") >= 0) {
						// log.i("temp", "json->" + str.trim());
						timeNow = util.getTimeNowUDPStringTTJ(str, null);
						//Log.i("temp", "str->" + str);
						if (refreshListData != null && timeNow != null) {
							refreshListData.add(timeNow);
						}
						if (timeNow != null) {
							// //log.i("log", "TTJ UDP 刷新");
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

	// 菜单
	protected void authorDialog() {
		AlertDialog.Builder builder = new Builder(PriceViewTTJ.this);
		builder.setMessage("汇通财经" + Const.VERSIONCODE + " \n" + "www.fx678.com\n" + "www.gold678.com");
		builder.setTitle("关于");

		builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	protected void exitDialog() {
		AlertDialog.Builder builder = new Builder(PriceViewTTJ.this);
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
			authorDialog();
		} else if (item.getItemId() == 2) {
			exitDialog();
		}
		return true;
	}

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
				refreshData = false;
				String sendStr = String.format("active,active");
				byte[] sendBuf = sendStr.getBytes();
				sendBuf = sendStr.getBytes();
				try {
					sendpacket.setData(sendBuf);
					client.send(sendpacket);
				} catch (Exception e) {

				}
				if (refreshListData.size() > 50) {
					refreshListData.clear();
				}
			}
		}

	}

	private class CustomAdapter extends SimpleAdapter {

		/*
		 * 以数字方式传入时，需按ARGB格式；若按RGB格式，不生效或数组中成员为 android.graphics.Color.rgb(230,
		 * 230, 230)
		 */

		public CustomAdapter(Context context, List<Map<String, Object>> items, int resource, String[] from, int[] to) {
			super(context, items, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			int colorPos = position % colors.length;
			view.setBackgroundColor(colors[colorPos]);
			return view;
		}
	}
}
