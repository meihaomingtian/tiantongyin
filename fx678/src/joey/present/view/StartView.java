package joey.present.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import joey.present.util.Const;
import joey.present.util.MyLogger;
import joey.present.util.Util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.umeng.analytics.MobclickAgent;

public class StartView extends Activity {

	// private ImageButton searchBtn;
	private ImageButton goldmkbtn;
	private ImageButton globalbtn;
	private ImageButton newsbtn;
	private ImageButton mychoicebtn;
	private ImageButton tradingstrategybtn;
	private ImageButton onlinetradingbtn;
	private ImageButton customerservicebtn;
	private ImageButton sethelpbtn;
	private ImageButton exitsoftbtn;
	private String version = "";
	private Util util = new Util();

	private DatagramSocket client;
	private DatagramPacket recpacket;
	private DatagramPacket sendpacket;

	/** 解析出来的title */
	private String title;
	/** 解析出来的imgurl_android */
	private String imgurl_android;
	/** 解析出来的linkurl */
	private String linkurl;
	/** 解析出来的version */
	private String version_mintai;
	private MyLogger log = MyLogger.yLog();
	Bitmap mBitmap;
	SharedPreferences sharedPreferences;

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

	public void onDestroy() {
		super.onDestroy();
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
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startview);

		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

		initView();

		// 异步版本检查
		if (isNetworkConnected(this)) {
			checkVersion();
			// startService(new Intent(StartView.this, ImportService.class));
		} else {
			showNoConn();
		}

		// startService(new Intent(StartView.this, NewsOnTopService.class));
	}

	/**
	 * 初始化九宫格
	 */
	private void initView() {
		//行情中心
		goldmkbtn = (ImageButton) findViewById(R.id.goldmkbtn);
		goldmkbtn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent in = new Intent(StartView.this, SelectView.class);
					Bundle bundle = new Bundle();
					bundle.putString("started", "gold");
					in.putExtras(bundle);
					startActivity(in);
				}
				return false;
			}
		});
		//资讯中心
		globalbtn = (ImageButton) findViewById(R.id.globalbtn);
		globalbtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent in = new Intent(StartView.this, NewsView.class);
					Bundle bundle = new Bundle();
					bundle.putString("selected", "newstab");
					bundle.putString("btn", "0");
					in.putExtras(bundle);
					startActivity(in);
				}
				return false;
			}
		});
		//我的自选
		newsbtn = (ImageButton) findViewById(R.id.newsbtn);
		newsbtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent in = new Intent(StartView.this, MainTab.class);
					Bundle bundle = new Bundle();
					bundle.putString("selected", "mychoicetab");
					bundle.putString("btn", "0");
					in.putExtras(bundle);
					startActivity(in);
				}
				return false;
			}
		});
		//财经日历
		mychoicebtn = (ImageButton) findViewById(R.id.mychoicebtn);
		mychoicebtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent in = new Intent(StartView.this, CalendarView.class);
					startActivity(in);
					// Intent intent= new Intent(Intent.ACTION_VIEW);
					// intent.setData(Uri.parse("http://wap.fx678.com"));
					// startActivity(intent);
				}
				return false;
			}
		});
		//开户申请
		tradingstrategybtn = (ImageButton) findViewById(R.id.tradingstrategybtn);
		tradingstrategybtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent in = new Intent(StartView.this,
							khsqView.class);
					// Intent in = new Intent(StartView.this, MainTab.class);
					Bundle bundle = new Bundle();
					bundle.putString("selected", "newstab");
					bundle.putString("btn", "1");
					in.putExtras(bundle);
					startActivity(in);
				}
				return false;
			}
		});
		//实时解盘
		onlinetradingbtn = (ImageButton) findViewById(R.id.onlinetradingbtn);
		onlinetradingbtn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent in = new Intent(StartView.this, Ssjpview.class);
//					Bundle bundle = new Bundle();
//					bundle.putString("started", "global");
//					in.putExtras(bundle);
					startActivity(in);
				}	
				return false;
			}
		});
		// 客户服务
		customerservicebtn = (ImageButton) findViewById(R.id.customerservicebtn);
		customerservicebtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
				Intent intent = new Intent(StartView.this, MainView2.class);
				startActivity(intent);
				}
				return false;
			}
		});
		sethelpbtn = (ImageButton) findViewById(R.id.sethelpbtn);
		sethelpbtn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				MobclickAgent.onEvent(StartView.this, "EVENT_MT_0001");
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent in = new Intent(StartView.this, WebView_07.class);
					in.putExtra("linkurl", linkurl);
					in.putExtra("title", title);
					startActivity(in);
				}
				return false;
			}
		});
		exitsoftbtn = (ImageButton) findViewById(R.id.exitsoftbtn);
		exitsoftbtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent intent = new Intent(StartView.this,
							AnaltstsWebView.class);
					startActivity(intent);
				}
				return false;
			}
		});
	}

	// 退出确认对话框
	protected void exitDialog() {
		AlertDialog.Builder builder = new Builder(StartView.this);
		builder.setMessage("确认要退出天通銀吗？");
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

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String message = msg.getData().getString("message");
			Toast.makeText(getApplicationContext(), message, 3000).show();
		}
	};

	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * Dialog提示
	 */
	private void showNoConn() {

		new AlertDialog.Builder(StartView.this).setTitle("注意：")
				.setCancelable(false).setMessage("无法加载数据,请检查网络连接")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//
					}
				}).create().show();
	}

	private void checkVersion() {

		MyTask mytask = new MyTask();
		mytask.execute(null, null, null);
	}

	class MyTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			version = util.checkVersion(Const.APP_VersionCheckURL);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			String v = version.substring(0, 1);
			// v = "1";
			if ("1".equals(v)) {
				AlertDialog.Builder builder = new Builder(StartView.this);
				builder.setMessage("发现新版本，需要下载最新版吗？");
				builder.setTitle("提示");
				builder.setPositiveButton("更新",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent1 = new Intent(Intent.ACTION_VIEW);
								intent1.setData(Uri.parse(version.substring(2,
										version.length())));
								startActivity(intent1);
							}
						});
				builder.setNegativeButton("不更新",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			} else if ("2".equals(v)) {
				AlertDialog.Builder builder = new Builder(StartView.this);
				builder.setMessage("请更新到最新版使用！");
				builder.setTitle("提示");
				builder.setPositiveButton("更新",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent1 = new Intent(Intent.ACTION_VIEW);
								intent1.setData(Uri.parse(version.substring(2,
										version.length())));
								startActivity(intent1);
							}
						});
				builder.create().show();
			}
		}
	}

}
