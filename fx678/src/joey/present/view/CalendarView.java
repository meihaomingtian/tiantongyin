package joey.present.view;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.umeng.analytics.MobclickAgent;

import joey.present.data.CalData;
import joey.present.data.PriceData;
import joey.present.util.Const;
import joey.present.util.DensityUtil;
import joey.present.util.Util;
import joey.present.view.ui.PriceListView;
import joey.present.view.ui.SyncHorizontalScrollView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CalendarView extends Activity {

	private int year, month, day;
	private Calendar c;
	// // 本地配置保存对象
	// private SharedPreferences preferences;

	private Button searchBtn;

	private int lastItem = 0;

	private LayoutInflater mInflater;

	private ListView calListView;

	private ListView ctimeListView;

	private SyncHorizontalScrollView headScroll;

	private SyncHorizontalScrollView listScroll;

	private GestureDetector mGesture;

	// 当前分类
	private TextView title;

	// 行情数据map
	private List<Map<String, Object>> mData;

	// 行情数据list
	private List<CalData> listData;

	// 名字行情数据list
	private List<Map<String, Object>> listTimeData;

	// 数据取得等待对话框组件
	private ProgressDialog progressDialog;

	// 数据取得线程
	private ProgressThread progressThread;
	//
	// // 自动更新线程
	// private AutoUpdateThread autoUpdateThread;

	// // 自动发送active
	// private SendActiveThread sendActiveThread;

	// 自动更新线程启动标志
	private boolean running = false;

	private String date = "";

	private Util util = new Util();

	private String errorMsg;
	// 时间控件
	private Button calvalue;

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

	// protected void onPause() {
	// super.onPause();
	// // if (client != null) {
	// // client.close();
	// // }
	//
	// running = false;
	// // autoUpdateThread = null;
	// // sendActiveThread = null;
	// }
	//
	// protected void onResume() {
	// super.onResume();
	// Thread t = new Thread() {
	// public void run() {
	// try {
	// // client.close();
	// autoUpdateThread = null;
	// // sendActiveThread = null;
	// // client = new DatagramSocket();
	// // String sendStr = String.format(Const.UDP_PARA +
	// // selected);
	// // byte[] sendBuf = sendStr.getBytes();
	// // sendBuf = sendStr.getBytes();
	// // InetAddress address =
	// // InetAddress.getByName(Const.UDP_IP);
	// // sendpacket = new DatagramPacket(sendBuf, sendBuf.length,
	// // address, Const.UDP_PORT);
	// // client.send(sendpacket);
	// // byte[] recBuf = new byte[1000];
	// // recpacket = new DatagramPacket(recBuf, recBuf.length);
	// autoUpdateThread = new AutoUpdateThread(handler);
	// running = true;
	// autoUpdateThread.start();
	// // sendActiveThread = new SendActiveThread();
	// // sendActiveThread.start();
	// } catch (Exception e) {
	//
	// }
	// }
	// };
	// t.start();
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendarview);
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		running = true;
		searchBtn = (Button) findViewById(R.id.searchbtn);
		searchBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if ("".equals(date)) {
					return;
				}
				progressDialog = new ProgressDialog(CalendarView.this);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setMessage("取得数据...");
				//progressDialog.setTitle("请等待");
				progressDialog.setCancelable(true);
				progressDialog.show();
				progressThread = new ProgressThread(handler);
				progressThread.setState(1);
				progressThread.start();
			}
		});
		calvalue = (Button) findViewById(R.id.calvalue);
		// calvalue.setInputType(InputType.TYPE_NULL);
		calvalue.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showDialog(0);
			}
		});
		ImageButton backBtn = (ImageButton) findViewById(R.id.backbtn);
		backBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				CalendarView.this.finish();
			}

		});
		headScroll = (SyncHorizontalScrollView) findViewById(R.id.scrollListHead);
		listScroll = (SyncHorizontalScrollView) findViewById(R.id.scrollList);
		headScroll.setSmoothScrollingEnabled(true);
		listScroll.setSmoothScrollingEnabled(true);
		headScroll.setScrollView(listScroll);
		listScroll.setScrollView(headScroll);
		ctimeListView = (ListView) findViewById(R.id.ctimeListView);

		calListView = (ListView) findViewById(R.id.calListView);
		MyAdapter adapter = new MyAdapter();
		calListView.setAdapter(adapter);

		// searchBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// SearchKeyboardDlg dlg = new SearchKeyboardDlg(PriceView.this);
		// dlg.show();
		// }
		// });
		c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		if (month < 9) {
			date = year + "-0" + (month + 1);
		} else {
			date = year + "-" + (month + 1);
		}
		if (day < 10) {
			date = date + "-0" + day;
		} else {
			date = date + "-" + day;
		}
		calvalue.setText(date);
		progressDialog = new ProgressDialog(CalendarView.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("取得数据...");
		//progressDialog.setTitle("请等待");
		progressDialog.setCancelable(true);
		progressDialog.show();
		progressThread = new ProgressThread(handler);
		progressThread.setState(1);
		progressThread.start();
	}

	private synchronized void updateData(String date) {
		// listData =
		// util.getCalData("http://mhth.fx678.com/Calendar.aspx?date=" + date);
		listData = util.getCalData("http://mhth.fx678.com/Calendar.aspx?date=" + date);
	}

	public void showData() {
		progressDialog.dismiss();
		if (listData == null || listData.size() == 0) {
			progressDialog.dismiss();
		}
		// if (Const.SHGOLD.equals(selected)) {
		// selectedType.setText(R.string.shgold);
		// } else if (Const.HJXH.equals(selected)) {
		// selectedType.setText(R.string.hjxh);
		// } else if (Const.STOCKINDEX.equals(selected)) {
		// selectedType.setText(R.string.stockindex);
		// } else if (Const.WH.equals(selected)) {
		// selectedType.setText(R.string.wh);
		// }
		// Toast.makeText(getApplicationContext(), listData.size()
		// + date, 5000)
		// .show();
		listTimeData = new ArrayList<Map<String, Object>>();
		mData = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < listData.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> mapName = new HashMap<String, Object>();
			CalData calData = listData.get(i);
			map.put(Const.CAL_COUNTRY, calData.getCalCountry());
			map.put(Const.CAL_ITEM, calData.getCalItem());
			map.put(Const.CAL_IMPORTANCE, calData.getCalImportance());
			map.put(Const.CAL_LASTVALUE, calData.getCalLastValue());
			map.put(Const.CAL_PREDICTION, calData.getCalPrediction());
			map.put(Const.CAL_ACTUAL, calData.getCalActual());
			mapName.put(Const.CAL_TIME, calData.getCalTime());
			mData.add(map);
			listTimeData.add(mapName);
		}
		ViewGroup.LayoutParams linearParams1 = calListView.getLayoutParams();
		// LinearLayout.LayoutParams ff = new LinearLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT, 45 * listData.size());
		// priceNameListView.setLayoutParams(ff);
		// LinearLayout.LayoutParams ff2 = new LinearLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT, 45 * listData.size());
		// priceListView.setLayoutParams(ff2);
		linearParams1.height = DensityUtil.dip2px(this, 35) * listData.size();
		calListView.setLayoutParams(linearParams1);
		ViewGroup.LayoutParams linearParams2 = ctimeListView.getLayoutParams();
		linearParams2.height = DensityUtil.dip2px(this, 35) * listData.size();
		ctimeListView.setLayoutParams(linearParams2);
		SimpleAdapter adapterS = new SimpleAdapter(this, listTimeData, R.layout.calendartimeitem, new String[] { Const.CAL_TIME }, new int[] { R.id.caltime });

		ctimeListView.setAdapter(adapterS);
		MyAdapter adapter = (MyAdapter) calListView.getAdapter();
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
					updateData(date);

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
					updateData(date);
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
				if (listData == null || listData.size() <= 0) {
					Toast.makeText(getApplicationContext(), "今日无重要数据公布", 4000).show();

				}
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
				if (listData == null || listData.size() <= 0) {
					Toast.makeText(getApplicationContext(), "今日无重要数据公布", 4000).show();

				}
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
		public TextView country;
		public TextView item;
		public TextView importance;
		public TextView lastValue;
		public TextView prediction;
		public TextView actual;
	}

	// 行情数据列表组件用数据adapter
	private class MyAdapter extends BaseAdapter {

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			ViewHolder vholder = null;
			if (paramView == null) {
				vholder = new ViewHolder();
				paramView = mInflater.inflate(R.layout.calendaritem, null);
				// vholder.name = (TextView) paramView
				// .findViewById(R.id.pricename);
				vholder.country = (TextView) paramView.findViewById(R.id.calcountry);
				vholder.item = (TextView) paramView.findViewById(R.id.calitem);
				vholder.importance = (TextView) paramView.findViewById(R.id.calimportance);
				vholder.lastValue = (TextView) paramView.findViewById(R.id.callastvalue);
				vholder.prediction = (TextView) paramView.findViewById(R.id.calprediction);
				vholder.actual = (TextView) paramView.findViewById(R.id.calactual);
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

			vholder.item.setTextColor(Color.WHITE);
			vholder.importance.setTextColor(Color.WHITE);
			String imp = (String) mData.get(paramInt).get(Const.CAL_IMPORTANCE);
			if ("高".equals(imp)) {
				vholder.item.setTextColor(Color.RED);
				vholder.importance.setTextColor(Color.RED);
			}

			vholder.country.setText((String) mData.get(paramInt).get(Const.CAL_COUNTRY));

			vholder.item.setText((String) mData.get(paramInt).get(Const.CAL_ITEM));

			vholder.importance.setText((String) mData.get(paramInt).get(Const.CAL_IMPORTANCE));

			vholder.lastValue.setText((String) mData.get(paramInt).get(Const.CAL_LASTVALUE));

			vholder.prediction.setText((String) mData.get(paramInt).get(Const.CAL_PREDICTION));

			vholder.actual.setText((String) mData.get(paramInt).get(Const.CAL_ACTUAL));
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
					AutoUpdateThread.sleep(10000);
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

	protected void exitDialog() {
		AlertDialog.Builder builder = new Builder(CalendarView.this);
		builder.setMessage("确认要退出汇通财经吗？");
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

	@Override
	protected Dialog onCreateDialog(int id) {

		return new DatePickerDialog(this, new OnDateSetListener() {

			public void onDateSet(DatePicker view, int yearr, int monthOfYear, int dayOfMonth) {

				year = yearr;
				month = monthOfYear;
				day = dayOfMonth;
				if (month < 9) {
					date = year + "-0" + (month + 1);
				} else {
					date = year + "-" + (month + 1);
				}
				if (day < 10) {
					date = date + "-0" + day;
				} else {
					date = date + "-" + day;
				}

				calvalue.setText(date);
			}

		}, year, month, day);

	}

	// // 自动发送active
	// /** Nested class that performs progress calculations (counting) */
	// private class SendActiveThread extends Thread {
	// private long waitTime = 10000;
	//
	// public void run() {
	// while (running) {
	// try {
	// Thread.sleep(waitTime);
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
