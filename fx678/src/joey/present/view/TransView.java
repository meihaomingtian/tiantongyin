package joey.present.view;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import joey.present.data.InforPojo;
import joey.present.data.TypePojo;
import joey.present.util.Const;
import joey.present.util.Util;
import joey.present.view.ui.ImageAndText;
import joey.present.view.ui.ImageAndTextListAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;

public class TransView extends Activity {

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

	// 程序启动标志
	static final int PROGRESS_DIALOG = 0;
	// 数据读取标志
	static final int LOADING_DIALOG = 1;
	// 异常对话框标志
	static final int ERROR_DIALOG = 2;
	// 异常语句
	private String errorMsg = "";
	// 新闻数据list
	private List<InforPojo> newsData;
	// 数据取得线程
	private ProgressThread progressThread;
	// 数据取得表示用dialog
	private ProgressDialog progressDialog;
	// private Gallery gallery;
	// 新闻list组件
	private ListView newList;
	// private TabAdapter textAdapter;
	// 数据取得工具类
	private Util util = new Util();

	// 启动状态
	private boolean started = false;

	// 画面初始化
	private void init() throws Exception {

		// gallery = (Gallery) findViewById(R.id.gallery);
		String isupdate = "";
		Document doc = null;
		try {
			newsData = new ArrayList<InforPojo>();
			URL inforUrl = new URL("http://m.fx678.com/android/ad.xml");
			URLConnection ucon = inforUrl.openConnection();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			doc = builder.parse(ucon.getInputStream());
			NodeList items = doc.getElementsByTagName("customer");
			for (int i = 0; i < items.getLength(); i++) {
				InforPojo inforPojo = new InforPojo();
				Element item = (Element) items.item(i);
				inforPojo.setTitle_(util.getData(item, "title"));
				inforPojo.setAuthor_(util.getChildData(item, "version", "phone", "imageurl"));
				inforPojo.setLink_(util.getChildData(item, "version", "phone", "apkurl"));
				inforPojo.setContent_(util.getChildData(item, "version", "phone", "EventID"));
				newsData.add(inforPojo);
			}
		} catch (Exception e) {
			throw e;
		}

	}

	// 显示取得数据
	public void showList() {
		try {
			dismissDialog(PROGRESS_DIALOG);
			List<ImageAndText> listforView = new ArrayList<ImageAndText>();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < newsData.size(); i++) {
				ImageAndText imgTxt = new ImageAndText(newsData.get(i).getAuthor_(), newsData.get(i).getTitle_());
				listforView.add(imgTxt);
			}
			ImageAndTextListAdapter adapter = new ImageAndTextListAdapter(TransView.this, listforView, newList);
			newList.setAdapter(adapter);
			newList.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (newsData.size() > position) {
						InforPojo tmpInfor = newsData.get(position);
						MobclickAgent.onEvent(TransView.this, tmpInfor.getContent_());

						Intent intent1 = new Intent(Intent.ACTION_VIEW);
						intent1.setData(Uri.parse(tmpInfor.getLink_()));
						startActivity(intent1);
					}
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transview);
		newsData = new ArrayList<InforPojo>();
		newList = (ListView) findViewById(R.id.transList);
		showDialog(PROGRESS_DIALOG);

		ImageButton backBtn = (ImageButton) findViewById(R.id.backbtn);
		backBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				finish();
			}

		});
	}

	// 菜单
	protected void authorDialog() {
		AlertDialog.Builder builder = new Builder(TransView.this);
		builder.setMessage("汇通财经" + Const.VERSIONCODE + " \n" + "www.fx678.com\n" + "www.gold678.com");
		builder.setTitle("关于");

		builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	protected void errorDialog(String title, String msg) {

		AlertDialog.Builder builder = new Builder(TransView.this);
		builder.setMessage(msg);
		builder.setTitle(title);

		builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(TransView.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			if (started) {
				progressDialog.setMessage("取得数据...");
			} else {
				progressDialog.setMessage("初始化数据...");
			}

			//progressDialog.setTitle("请等待");
			progressDialog.setCancelable(true);
			progressThread = new ProgressThread(handler);
			progressThread.setState(0);
			progressThread.start();
			return progressDialog;
		case LOADING_DIALOG:
			progressDialog = new ProgressDialog(TransView.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("取得数据...");
			//progressDialog.setTitle("请等待");
			progressDialog.setCancelable(true);
			progressThread = new ProgressThread(handler);
			progressThread.setState(1);
			progressThread.start();
			return progressDialog;
		default:
			return null;
		}
	}

	// Define the Handler that receives messages from the thread and update the
	// progress
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("total");
			if (total == 1) {
				showList();
			}
			if (total == 2) {
				dismissDialog(PROGRESS_DIALOG);

				if (!"".equals(errorMsg)) {
					new AlertDialog.Builder(TransView.this).setTitle("通信异常").setMessage(errorMsg)
							.setNeutralButton("确认", new DialogInterface.OnClickListener() {

								// 点击AlertDialog上的按钮的事件处理代码

								public void onClick(DialogInterface dialog, int which) {

								}
							}).show();
				}
			}

			if (total == 3) {

			}
			// new Thread() {
			// public void run() {
			// if (!"".equals(errorMsg)) {
			// new AlertDialog.Builder(MainView.this).setTitle("通信异常")
			// .setMessage("errorMsg").setNeutralButton("确认",
			// new DialogInterface.OnClickListener() {
			//
			// // 点击AlertDialog上的按钮的事件处理代码
			// @Override
			// public void onClick(
			// DialogInterface dialog,
			// int which) {
			//
			// }
			// }).show();
			// }
			// }
			// }.start();

			if (total == 4) {

			}
			// progressDialog.setProgress(total);
			// if (total >= 100) {
			// dismissDialog(PROGRESS_DIALOG);
			// if (initResult) {
			// // 启动自动更新若干线程
			// }
			// }
		}
	};

	/** Nested class that performs progress calculations (counting) */
	private class ProgressThread extends Thread {
		Handler mHandler;
		final static int STATE_INIT = 0;
		final static int STATE_INITLOADING = 2;
		final static int STATE_LOADING = 1;
		final static int MORE_LOADING = 3;
		int mState;

		@SuppressWarnings("unused")
		ProgressThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			if (mState == STATE_INIT) {
				try {
					init();
					errorMsg = "";
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 1);
					msg.setData(b);
					mHandler.sendMessage(msg);

				} catch (Exception e) {
					progressDialog.dismiss();
					errorMsg = "请检查网络问题，重启程序";
					// errorMsg = e.getMessage();
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 2);
					msg.setData(b);

					mHandler.sendMessage(msg);
					return;
				}
				// try {
				// Thread.sleep(200);
				// } catch (InterruptedException e) {
				// // Log.e("ERROR", "Thread Interrupted");
				// }

			} else if (mState == STATE_LOADING) {

			} else if (mState == STATE_INITLOADING) {

			} else if (mState == MORE_LOADING) {

			}

		}

		/*
		 * sets the current state for the thread, used to stop the thread
		 */
		public void setState(int state) {
			mState = state;
		}

	}

	protected void onDestroy() {
		super.onDestroy();
	}

}
