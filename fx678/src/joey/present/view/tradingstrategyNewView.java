package joey.present.view;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import joey.present.data.InforPojo;
import joey.present.data.TypePojo;
import joey.present.util.Const;
import joey.present.util.Util;
import org.w3c.dom.Document;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.umeng.analytics.MobclickAgent;

public class tradingstrategyNewView extends Activity {
	// 是否是交易策略
	private String btnType = "2";
	// 自动更新线程标志位
	private boolean running = false;
	// list数据列名
	private final String DATA_NEWSTYPE = "newsType";
	// 新闻类型
	private final String DATA_NEWSHEAD = "newsTypeHead";
	// 新闻类型时间
	private final String DATA_NEWSTIME = "newsTypeTime";
	private final String VERSION = "1.00";
	// 程序启动标志
	static final int PROGRESS_DIALOG = 0;
	// 数据读取标志
	static final int LOADING_DIALOG = 1;
	// 异常对话框标志
	static final int ERROR_DIALOG = 2;
	// 初始化标志
	private boolean initResult = false;
	// 异常语句
	private String errorMsg = "";
	// 本地配置保存对象
	private SharedPreferences preferences;
	// 新闻类别list
	private List<TypePojo> typeList;
	// 新闻数据list
	private List<InforPojo> newsData;
	// 刷新按钮组件
	private ImageButton btnReflash;
	// 最后一次选择的类别
	private Button lastSelectBtn;
	// 行情按钮组件
	private ImageButton anotherViewBtn;
	// 汇通手机网站按钮组件
	private ImageButton wapBtn;
	// 选择的类别
	private String selectType = "";
	// 数据取得线程
	private ProgressThread progressThread;
	// 数据取得表示用dialog
	private ProgressDialog progressDialog;
	// 软件更新地址
	private String softupdateurl = "";
	// 新闻list组件
	private ListView newList;
	// 数据取得工具类
	private Util util = new Util();
	// 类别名list
	private List<String> tabNames;
	// 滚动条组件
	private LinearLayout scrolllayout;
	// 新闻地址前段
	private String newURLFront;
	// 启动状态
	private boolean started = false;

	private HorizontalScrollView scrollView;

	private TextView title;

	private String lastId = "";

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

	private void initView() {
		// if ("0".equals(btnType)) {
		// title.setText("资讯中心");
		// } else {
		title.setText("交易策略");
		String urll = "http://tool.fx678.com/mob/jyclNewskind.xml";
		try {
			typeList = util.getTypeByXML(urll);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// }
		for (int i = 0; i < typeList.size(); i++) {
			TypePojo tmp = typeList.get(i);
			Button btn = new Button(tradingstrategyNewView.this);
			btn.setText(tmp.getTypename_());
			btn.setTextColor(Color.BLACK);
			btn.setBackgroundResource(R.layout.newsbtn);
			btn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) 16);
			btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			btn.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Button btn = (Button) v;
					if (!btn.equals(lastSelectBtn)) {
						// 设置按下选中时的背景
						btn.setBackgroundResource(R.drawable.android_menu_on);
						// 设置按下选中时的字体颜色
						btn.setTextColor(Color.WHITE);
						// 设置按下选中时的布局资源
						lastSelectBtn.setBackgroundResource(R.layout.newsbtn);
						lastSelectBtn.setTextColor(Color.BLACK);
						selectType = (String) btn.getText();
						progressDialog = new ProgressDialog(tradingstrategyNewView.this);
						progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						progressDialog.setMessage("取得数据...");
						//progressDialog.setTitle("请等待");
						progressDialog.setCancelable(true);
						progressDialog.show();
						progressThread = new ProgressThread(handler);
						progressThread.setState(1);
						progressThread.start();
						lastSelectBtn = btn;
					}
				}
			});

			// if (i == 0 && "0".equals(btnType)) {
			// lastSelectBtn = btn;
			// lastSelectBtn.setTextColor(Color.BLACK);
			// lastSelectBtn.setBackgroundResource(R.drawable.android_menu_on);
			// selectType = (String) btn.getText();
			// }
			if ("1".equals(btnType) && "交易策略".equals(tmp.getTypename_())) {
				lastSelectBtn = btn;
				lastSelectBtn.setTextColor(Color.WHITE);
				lastSelectBtn.setBackgroundResource(R.drawable.android_menu_on);
				selectType = (String) btn.getText();
				title.setText(tmp.getTypename_());
			}
			scrolllayout.addView(btn);
		}
		running = true;

		// 不知道啥功能 暂时不用。。代码节余
		// AutoUpdateThread autoThread1 = new AutoUpdateThread(handler);
		// autoThread1.setAutoTime("1");
		// autoThread1.start();
		// AutoUpdateThread autoThread2 = new AutoUpdateThread(handler);
		// autoThread2.setAutoTime("2");
		// autoThread2.start();
		// AutoUpdateThread autoThread3 = new AutoUpdateThread(handler);
		// autoThread3.setAutoTime("3");
		// autoThread3.start();

	}

	// 画面初始化
	private void init() throws Exception {

		String isupdate = "";
		Document doc = null;
		// try {
		// SharedPreferences.Editor editor = preferences.edit();
		// editor.putString(Const.VERSION, VERSION);
		// editor.commit();
		URL inforUrl = new URL("http://tool.fx678.com/mob/upfirst.xml");
		URLConnection ucon = inforUrl.openConnection();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		doc = builder.parse(ucon.getInputStream());
		// isupdate = doc.getElementsByTagName("isinfoupdate").item(0)
		// .getFirstChild().getNodeValue();
		// 取得新闻url前段地址
		newURLFront = doc.getElementsByTagName("newscontentURL").item(0).getFirstChild().getNodeValue();
		// } catch (Exception e) {
		// int typeCount = 0;
		// typeCount = preferences.getInt(Const.NEWS_COUNT, 0);
		// if (typeCount <= 0) {
		// throw e;
		// }
		// }

		// if (isupdate == null || "".equals(isupdate)) {
		// int typeCount = 0;
		// typeCount = preferences.getInt(Const.NEWS_COUNT, 0);
		// for (int i = 0; i < typeCount; i++) {
		// String typeValue = preferences
		// .getString(Const.NEWS_NUM + i, "");
		// String[] types = typeValue.split(";");
		// if (types.length > 2) {
		// TypePojo typePojo = new TypePojo();
		// typePojo.setTypeno_(types[3]);
		// typePojo.setTypename_(types[0]);
		// typePojo.setAutoupdate_(types[1]);
		// typePojo.setTypeupdateurl_(types[2]);
		// tabNames.add(typePojo.getTypename_());
		// typeList.add(typePojo);
		// }
		// }
		// } else if (!isupdate.equals(preferences.getString(Const.UPDATE, "")))
		// {
		// String infoupdateurl = doc.getElementsByTagName("infoupdateurl")
		// .item(0).getFirstChild().getNodeValue();
		// typeList = util.getTypeByXML(infoupdateurl);
		// SharedPreferences.Editor editor = preferences.edit();
		// editor.putString(Const.UPDATE, isupdate);
		// editor.putInt(Const.NEWS_COUNT, typeList.size());
		// for (int i = 0; i < typeList.size(); i++) {
		// TypePojo typePojo = typeList.get(i);
		// tabNames.add(typePojo.getTypename_());
		// String typeValue = typePojo.getTypename_() + ";"
		// + typePojo.getAutoupdate_() + ";"
		// + typePojo.getTypeupdateurl_() + ";"
		// + typePojo.getTypeno_();
		// editor.putString(Const.NEWS_NUM + i, typeValue);
		// }
		// editor.commit();
		// } else {
		// int typeCount = 0;
		// typeCount = preferences.getInt(Const.NEWS_COUNT, 0);
		// for (int i = 0; i < typeCount; i++) {
		// String typeValue = preferences.getString(Const.NEWS_NUM + i, "");
		// String[] types = typeValue.split(";");
		// if (types.length > 2) {
		// TypePojo typePojo = new TypePojo();
		// typePojo.setTypeno_(types[3]);
		// typePojo.setTypename_(types[0]);
		// typePojo.setAutoupdate_(types[1]);
		// typePojo.setTypeupdateurl_(types[2]);
		// tabNames.add(typePojo.getTypename_());
		// typeList.add(typePojo);
		// }
		// }

		TypePojo typePojo = new TypePojo();
		typePojo.setTypeno_("12");
		typePojo.setTypename_("交易策略");
		typePojo.setAutoupdate_("0");
		typePojo.setTypeupdateurl_("http://tool.fx678.com/mob/source/jiaoyi.xml");
		tabNames.add(typePojo.getTypename_());
		typeList.add(typePojo);

		typePojo = new TypePojo();
		typePojo.setTypeno_("13");
		typePojo.setTypename_("银行评论");
		typePojo.setAutoupdate_("0");
		typePojo.setTypeupdateurl_("http://tool.fx678.com/mob/source/yinhang.xml");
		tabNames.add(typePojo.getTypename_());
		typeList.add(typePojo);

		typePojo = new TypePojo();
		typePojo.setTypeno_("14");
		typePojo.setTypename_("专家评论");
		typePojo.setAutoupdate_("0");
		typePojo.setTypeupdateurl_("http://tool.fx678.com/mob/source/zhuanjia.xml");
		tabNames.add(typePojo.getTypename_());
		typeList.add(typePojo);

		typePojo = new TypePojo();
		typePojo.setTypeno_("15");
		typePojo.setTypename_("机构评论");
		typePojo.setAutoupdate_("0");
		typePojo.setTypeupdateurl_("http://tool.fx678.com/mob/source/jigou.xml");
		tabNames.add(typePojo.getTypename_());
		typeList.add(typePojo);

		// }
		if ("0".equals(btnType)) {
			typeList = typeList.subList(0, typeList.size() - 5);
		} else {
			typeList = typeList;
		}

	}

	// 更新新闻
	private void refreshNews(String typeName, boolean jycl) throws Exception {

		TypePojo typePojo = null;
		String no = "";
		for (int i = 0; i < typeList.size(); i++) {
			TypePojo tmp = typeList.get(i);
			if (typeName.equals(tmp.getTypename_())) {
				typePojo = tmp;
			}
		}
		if (typePojo != null) {
			if (jycl) {
				no = typePojo.getTypeno_();
				newsData = util.getInforByXML("http://tool.fx678.com/mob/source/jiaoyi.xml");
			} else {
				no = typePojo.getTypeno_();
				List<InforPojo> refreshData = util.getInforByXML("http://tool.fx678.com/mob/source/newRefresh.asp?type=" + no + "&id=" + lastId);
				for (int i = 0; i < newsData.size(); i++) {
					refreshData.add(newsData.get(i));
				}
				newsData = refreshData;
				if (newsData != null && newsData.size() > 0) {
					lastId = newsData.get(0).getLink_();
				}
			}

		}

	}

	// 更新新闻
	private void updateNews(String typeName, boolean moreLoad) throws Exception {

		TypePojo typePojo = null;
		String url = "";
		for (int i = 0; i < typeList.size(); i++) {
			TypePojo tmp = typeList.get(i);
			if (typeName.equals(tmp.getTypename_())) {
				typePojo = tmp;
			}
		}

		if (typePojo != null) {
			if (moreLoad) {
				// developing
				List<InforPojo> tmp = util.getInforByXML(url);
				for (int i = 0; i < tmp.size(); i++) {
					newsData.add(tmp.get(i));
				}
			} else {
				url = typePojo.getTypeupdateurl_();
				newsData = util.getInforByXML(url);
			}

		} else {
			typePojo = typeList.get(0);
			url = typePojo.getTypeupdateurl_();
			newsData = util.getInforByXML(url);
		}
		if (newsData != null && newsData.size() > 0) {
			lastId = newsData.get(0).getLink_();
		}
		// 此画面需要自动更新的情况下启动更新线程

	}

	// 显示取得数据
	public void showList() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < newsData.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			InforPojo inforPojo = newsData.get(i);
			// 格式化时间处理
			Date inforPojoTime = new Date(inforPojo.getTime_());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String DateInforPojoTime = format.format(inforPojoTime);
			map.put(DATA_NEWSHEAD, inforPojo.getTitle_());
			map.put(DATA_NEWSTIME, DateInforPojoTime);
			list.add(map);
		}
		Myadapter adapter = new Myadapter(this, list, R.layout.newslistitems, new String[] { DATA_NEWSHEAD, DATA_NEWSTIME }, new int[] { R.id.newsHead,
				R.id.newsTime });
		newList.setAdapter(adapter);
		newList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// 判断前三条交易咨询。position>=0 否则 会显示不全
				if (newsData.size() >= position && position >= 0) {

					InforPojo tmpInfor = newsData.get(position);
					Intent in = new Intent(tradingstrategyNewView.this, InforDetailView.class);
					Bundle bundle = new Bundle();
					bundle.putString("link", newURLFront + tmpInfor.getLink_());
					in.putExtras(bundle);
					startActivity(in);

				}
			}

		});

		Toast.makeText(getApplicationContext(), "数据已更新!", 500).show();
	}

	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tradingstrategynewview);

		Bundle bundle = getIntent().getExtras();
		if (!"".equals(bundle.getString("started"))) {
			started = true;
		}
		btnType = bundle.getString("btn");
		typeList = new ArrayList<TypePojo>();
		tabNames = new ArrayList<String>();
		newsData = new ArrayList<InforPojo>();
		scrollView = (HorizontalScrollView) findViewById(R.id.hsvnav);

		newList = (ListView) findViewById(R.id.tradingstrategynewsList);
		title = (TextView) findViewById(R.id.title);
		scrolllayout = (LinearLayout) findViewById(R.id.scrolllayout);
		preferences = getSharedPreferences(Const.PREFERENCESNEWS_NAME, Activity.MODE_PRIVATE);
		showDialog(PROGRESS_DIALOG);

		ImageButton backBtn = (ImageButton) findViewById(R.id.backbtn);
		backBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}

		});

	}

	// 菜单
	// protected void authorDialog() {
	// AlertDialog.Builder builder = new Builder(tradingstrategyNewView.this);
	// builder.setMessage("汇通财经" + Const.VERSIONCODE + " \n"
	// + "www.fx678.com\n" + "www.gold678.com");
	// builder.setTitle("关于");
	//
	// builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
	//
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// }
	// });
	// builder.create().show();
	// }

	// protected void errorDialog(String title, String msg) {
	//
	// AlertDialog.Builder builder = new Builder(tradingstrategyNewView.this);
	// builder.setMessage(msg);
	// builder.setTitle(title);
	//
	// builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
	//
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// }
	// });
	// builder.create().show();
	// }

	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(tradingstrategyNewView.this);
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
			progressDialog = new ProgressDialog(tradingstrategyNewView.this);
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

				initView();
			}
			if (total == 2) {

				if (!"".equals(errorMsg)) {
					// errorDialog("异常", errorMsg);
				}
			}
			if (total == 3) {

				showList();
			}

			if (total == 4) {
				try {
					updateNews(selectType, false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showList();
				Toast.makeText(getApplicationContext(), "数据已更新!", 2000).show();
			}
		}
	};

	private class Myadapter extends BaseAdapter {

		private tradingstrategyNewView main;
		private List<? extends Map<String, ?>> list;

		public Myadapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
			super();
			main = (tradingstrategyNewView) context;
			list = data;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (position < 0 || newsData.size() <= 0)
				return null;

			if (convertView == null)
				convertView = LayoutInflater.from(main).inflate(R.layout.newslistitems, null);

			TextView title = (TextView) convertView.findViewById(R.id.newsHead);
			TextView time = (TextView) convertView.findViewById(R.id.newsTime);
			// TextView newnew = (TextView) convertView.findViewById(R.id.xxdj);
			Map<String, ?> map = list.get(position);
			title.setText((String) map.get(DATA_NEWSHEAD));
			time.setText((String) map.get(DATA_NEWSTIME));
			// title.getPaint().setFakeBoldText(true);
			// 设置ListView里面Item的字体颜色
			title.setTextColor(getResources().getColor(R.color.black));

			time.setTextColor(getResources().getColor(R.color.black));

			return convertView;
		}

		public int getCount() {

			return newsData.size();
		}

		public Object getItem(int position) {
			return newsData.get(position);
		}

		public long getItemId(int position) {
			return 0;
		}

	}

	// 自动更新线程 - 暂时不用
	/** Nested class that performs progress calculations (counting) */
	// private class AutoUpdateThread extends Thread {
	// Handler mHandler;
	// final static long AUTO1 = 15000;
	// final static long AUTO2 = 300000;
	// final static long AUTO3 = 1800000;
	// private long waitTime = 1000;
	// private String waitType = "";
	//
	// @SuppressWarnings("unused")
	// AutoUpdateThread(Handler h) {
	// mHandler = h;
	// }
	//
	// public void setAutoTime(String autoTime) {
	// waitType = autoTime;
	// if ("1".equals(autoTime)) {
	// waitTime = AUTO1;
	// }
	// if ("2".equals(autoTime)) {
	// waitTime = AUTO2;
	// }
	// if ("3".equals(autoTime)) {
	// waitTime = AUTO3;
	// }
	// }
	//
	// public void run() {
	// try {
	// AutoUpdateThread.sleep(7000);
	// } catch (InterruptedException e) {
	// // Log.e("ERROR", "Thread Interrupted");
	// }
	// while (running) {
	// try {
	// AutoUpdateThread.sleep(waitTime);
	// } catch (InterruptedException e) {
	// // Log.e("ERROR", "Thread Interrupted");
	// }
	//
	// TypePojo typePojo = null;
	// for (int i = 0; i < typeList.size(); i++) {
	// TypePojo tmp = typeList.get(i);
	// if (selectType.equals(tmp.getTypename_())) {
	// typePojo = tmp;
	// }
	// }
	// if (typePojo != null
	// && waitType.equals(typePojo.getAutoupdate_())) {
	//
	// Message msg = mHandler.obtainMessage();
	// Bundle b = new Bundle();
	// b.putInt("total", 4);
	// msg.setData(b);
	// errorMsg = "";
	// mHandler.sendMessage(msg);
	// }
	// }
	// }
	//
	// }

	/** Nested class that performs progress calculations (counting) */
	private class ProgressThread extends Thread {
		Handler mHandler;
		final static int STATE_INIT = 0;
		final static int STATE_INITLOADING = 2;
		final static int STATE_LOADING = 1;
		final static int MORE_LOADING = 3;
		final static int REFRESH = 4;
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
					errorMsg = "无法初始化程序！请检查网络问题，重启程序";
					// errorMsg = e.getMessage();
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 2);
					msg.setData(b);

					mHandler.sendMessage(msg);
					return;
				}
				try {
					updateNews("", false);
					progressDialog.dismiss();
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 3);
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
			} else if (mState == STATE_LOADING) {
				try {
					updateNews(selectType, false);
					progressDialog.dismiss();
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 3);
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
			} else if (mState == STATE_INITLOADING) {
				try {
					updateNews(selectType, false);
					progressDialog.dismiss();
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 4);
					msg.setData(b);
					errorMsg = "";
					mHandler.sendMessage(msg);
					return;
				} catch (Exception e) {
					return;
				}
			} else if (mState == MORE_LOADING) {
				try {
					updateNews(selectType, true);
					progressDialog.dismiss();
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 3);
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
			} else if (mState == REFRESH) {
				try {

					if ("0".equals(btnType)) {
						try {
							refreshNews(selectType, false);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						try {
							refreshNews(selectType, true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					Message msg = mHandler.obtainMessage();
					Bundle b = new Bundle();
					b.putInt("total", 3);
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

	protected void onDestroy() {
		super.onDestroy();
		running = false;
	}

}
