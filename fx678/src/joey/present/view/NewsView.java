package joey.present.view;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
import joey.present.view.ui.MsgListView;
import joey.present.view.ui.MsgListView.OnRefreshListener;
import joey.present.view.ui.NewService;

import org.w3c.dom.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.umeng.analytics.MobclickAgent;

public class NewsView extends Activity {
	private Button btn_i;

	// 数据源
	List<Map<String, Object>> list;
	// viewpager数据
	private String XML_URL = "http://tool.fx678.com/mob/source/httoutiao.xml";

	// 下标
	private int oldPosition = 0;

	private ViewPager viewPager; // android-support-4 的滑动图片组件

	private TextView tv_title;

	private int currentItem; // 当前图片索引

	private List<View> dots; // 图片正文的圆点

	private List<InforPojo> newlist = null; // 文字和图片的数据
	List<View> imageViews;

	List<Map<String, Object>> data;

	HashMap<String, Object> item;
	// 对象
	InforPojo inforPojo;
	private List<View> views;

	Bitmap bm;
	NewService service = new NewService();
	ImageView imageView;
	URL url;
	boolean flag1 = true, flag2 = true;
	private Handler nHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			System.out.println("viewPager.getChildCount()" + viewPager.getChildCount());
			switch (msg.what) {
			case 0:
				((ImageView) imageViews.get(0)).setImageBitmap(bm);
				// ((ImageView) ((ImageView) imageViews.get(0))
				// .findViewWithTag("image" + 0)).setImageBitmap(bm);
				tv_title.setText((String) data.get(0).get("title"));

				break;
			case 1:
				((ImageView) imageViews.get(1)).setImageBitmap(bm);
				tv_title.setText((String) data.get(1).get("title"));
				break;
			case 2:
				((ImageView) imageViews.get(2)).setImageBitmap(bm);
				tv_title.setText((String) data.get(2).get("title"));
				break;
			}
			super.handleMessage(msg);
		}

	};

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

	private void show() {
		data = new ArrayList<Map<String, Object>>();
		imageViews = new ArrayList<View>();
		System.out.println("newlist.size()" + newlist.size());
		for (int i = 0; i < newlist.size(); i++) {
			imageView = new ImageView(NewsView.this);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setTag("image" + i);
			imageViews.add(imageView);

			item = new HashMap<String, Object>();
			inforPojo = newlist.get(i);
			item.put("title", inforPojo.getTitle_());
			item.put("Img", inforPojo.getLink_());
			// item.put("ID", inforPojo.getLink_());
			data.add(item);

		}

		System.out.println("data.size()" + data.size());

		viewPager.setAdapter(new MyAdapter2(imageViews));
		viewPager.setCurrentItem(0);

		// 设置一个监听器，当ViewPager中的页面改变时调用
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		System.out.println("viewPager.getChildCount()" + viewPager.getChildCount());
		// System.out.println("data.size()" + data.size());
		// 初始化图片资源
		// for (int i = 0; i < data.size(); i++) {
		//
		// // 核心代码，解析显示大图片的方法
		// try {
		// URL url1 = new URL((String) data.get(i).get("Img"));
		// URLConnection conn = url1.openConnection();
		// conn.connect();
		// InputStream is = conn.getInputStream();
		// bm = BitmapFactory.decodeStream(is);
		// imageView.setImageBitmap(bm);
		// is.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// // 此方法只能打开小图片，不能打开大图片
		// // imageView.setImageURI(Uri.parse((String)
		// // data.get(i).get("Img")));
		// // imageView.setImageBitmap(BitmapFactory.decodeFile((String) data
		// // .get(i).get("Img")));
		// // imageView.setImageDrawable(Drawable.createFromPath((String) data
		// // .get(i).get("Img")));
		// // System.out.println((String) data.get(i).get("Img"));
		//
		// // 图片填充整个界面
		// // imageView.setScaleType(ScaleType.CENTER_CROP);
		// // imageViews.add(imageView);
		// }
		// 设置图片监听事件，跳转
		imageViews.get(0).setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent in = new Intent(NewsView.this, InforDetailView.class);
				Bundle bundle = new Bundle();
				bundle.putString("link", newURLFront + newlist.get(0).getImg());
				System.out.println(bundle);
				in.putExtras(bundle);
				startActivity(in);
			}

		});
		imageViews.get(1).setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent in = new Intent(NewsView.this, InforDetailView.class);
				Bundle bundle = new Bundle();
				bundle.putString("link", newURLFront + newlist.get(1).getImg());
				System.out.println(bundle);
				in.putExtras(bundle);
				startActivity(in);
			}

		});
		imageViews.get(2).setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent in = new Intent(NewsView.this, InforDetailView.class);
				Bundle bundle = new Bundle();
				bundle.putString("link", newURLFront + newlist.get(2).getImg());
				System.out.println(bundle);
				in.putExtras(bundle);
				startActivity(in);
			}

		});

	}

	private class MyPageChangeListener implements OnPageChangeListener {

		public void onPageScrollStateChanged(int position) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageSelected(int position) {
			currentItem = position;
			// tv_title.setText();
			// 取得圆点符号之前的索引，对应设置背景资源
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			// 取得圆点符号当前的索引，对应设置背景资源
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			tv_title.setText((String) data.get(position).get("title"));
			oldPosition = position;
			if (position == 1 && flag1) {
				Thread mThread = new Thread(new MyRunnable(position));
				mThread.start();
				flag1 = false;
			}
			if (position == 2 && flag2) {
				Thread mThread = new Thread(new MyRunnable(position));
				mThread.start();
				flag2 = false;
			}

		}

	}

	private class MyAdapter2 extends PagerAdapter {

		private List<View> views;

		public MyAdapter2(List<View> views) {
			this.views = views;
		}

		@Override
		public int getItemPosition(Object object) {

			return POSITION_NONE;
		}

		// 销毁arg1位置的界面
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {

		}

		// 获得当前界面数
		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}

			return 0;
		}

		// 初始化arg1位置的界面
		@Override
		public Object instantiateItem(View arg0, int arg1) {

			((ViewPager) arg0).addView(views.get(arg1), 0);

			return views.get(arg1);
		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {

			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}
	}

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
	// private Gallery gallery;
	// 新闻list组件
	private MsgListView newList;
	private ListView TnewList;
	// private TabAdapter textAdapter;
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

	private Myadapter adapter;
	private String lastId = "";

	// public void clearList(List<Map<String, Object>> list) {
	// int size = list.size();
	// if (size > 0) {
	// list.removeAll(list);
	// adapter.notifyDataSetChanged();
	// }
	// }

	private void initView() {
		if ("0".equals(btnType)) {
			// typeList = typeList.subList(0, typeList.size() - 5);
			title.setText("资讯中心");
		} else {
			// typeList = typeList.subList(typeList.size() - 5, typeList.size()
			// - 1);
			title.setText("交易策略");
		}
		for (int i = 0; i < typeList.size(); i++) {
			final TypePojo tmp = typeList.get(i);
			Button btn = new Button(NewsView.this);
			btn.setText(tmp.getTypename_());
			if ("滚动播报".equals(btn)) {
				btn.setTextColor(Color.WHITE);
			} else {
				btn.setTextColor(Color.BLACK);
			}

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
						progressDialog = new ProgressDialog(NewsView.this);
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

			// 设置初始新闻 按钮的背景颜色

			if (i == 0 && "0".equals(btnType)) {
				lastSelectBtn = btn;
				lastSelectBtn.setTextColor(Color.WHITE);
				lastSelectBtn.setBackgroundResource(R.drawable.android_menu_on);
				selectType = (String) btn.getText();
			}
			if ("1".equals(btnType) && "交易策略".equals(tmp.getTypename_())) {
				lastSelectBtn = btn;
				lastSelectBtn.setTextColor(Color.WHITE);
				lastSelectBtn.setBackgroundResource(R.drawable.android_menu_on);
				selectType = (String) btn.getText();
				title.setText(tmp.getTypename_());
				// title.setTextColor(Color.WHITE);
			}
			scrolllayout.addView(btn);
		}
		running = true;
		// AutoUpdateThread autoThread1 = new AutoUpdateThread(handler);
		// autoThread1.setAutoTime("1");
		// autoThread1.start();
		// AutoUpdateThread autoThread2 = new AutoUpdateThread(handler);
		// autoThread2.setAutoTime("2");
		// autoThread2.start();
		// AutoUpdateThread autoThread3 = new AutoUpdateThread(handler);
		// autoThread3.setAutoTime("3");
		// autoThread3.start();
		newList.setonRefreshListener(new OnRefreshListener() {

			public void onRefresh() {

				progressThread = new ProgressThread(handler);
				progressThread.setState(4);
				progressThread.start();
			}
		});

	}

	// 画面初始化
	private void init() throws Exception {

		// gallery = (Gallery) findViewById(R.id.gallery);
		String isupdate = "";
		Document doc = null;
		try {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(Const.VERSION, VERSION);
			editor.commit();
			URL inforUrl = new URL("http://tool.fx678.com/mob/upfirst.xml");
			URLConnection ucon = inforUrl.openConnection();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			doc = builder.parse(ucon.getInputStream());
			isupdate = doc.getElementsByTagName("isinfoupdate").item(0).getFirstChild().getNodeValue();
			// 取得新闻url前段地址
			newURLFront = doc.getElementsByTagName("newscontentURL").item(0).getFirstChild().getNodeValue();
		} catch (Exception e) {
			int typeCount = 0;
			typeCount = preferences.getInt(Const.NEWS_COUNT, 0);
			if (typeCount <= 0) {
				throw e;
			}
		}

		// String isupdate = "23";
		if (isupdate == null || "".equals(isupdate)) {
			int typeCount = 0;
			typeCount = preferences.getInt(Const.NEWS_COUNT, 0);
			for (int i = 0; i < typeCount; i++) {
				String typeValue = preferences.getString(Const.NEWS_NUM + i, "");
				String[] types = typeValue.split(";");
				if (types.length > 2) {
					TypePojo typePojo = new TypePojo();
					typePojo.setTypeno_(types[3]);
					typePojo.setTypename_(types[0]);
					typePojo.setAutoupdate_(types[1]);
					typePojo.setTypeupdateurl_(types[2]);
					tabNames.add(typePojo.getTypename_());
					typeList.add(typePojo);
				}
			}
		} else if (!isupdate.equals(preferences.getString(Const.UPDATE, ""))) {
			String infoupdateurl = doc.getElementsByTagName("infoupdateurl").item(0).getFirstChild().getNodeValue();
			// log.i("temp","url->" + infoupdateurl );
			typeList = util.getTypeByXML(infoupdateurl);
			// typeList = loadInfoUtil.getTypeByXMLTest("");
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(Const.UPDATE, isupdate);
			editor.putInt(Const.NEWS_COUNT, typeList.size());
			for (int i = 0; i < typeList.size(); i++) {
				TypePojo typePojo = typeList.get(i);
				tabNames.add(typePojo.getTypename_());
				String typeValue = typePojo.getTypename_() + ";" + typePojo.getAutoupdate_() + ";" + typePojo.getTypeupdateurl_() + ";" + typePojo.getTypeno_();
				editor.putString(Const.NEWS_NUM + i, typeValue);
			}
			editor.commit();
		} else {
			int typeCount = 0;
			typeCount = preferences.getInt(Const.NEWS_COUNT, 0);
			for (int i = 0; i < typeCount; i++) {
				String typeValue = preferences.getString(Const.NEWS_NUM + i, "");
				String[] types = typeValue.split(";");
				if (types.length > 2) {
					TypePojo typePojo = new TypePojo();
					typePojo.setTypeno_(types[3]);
					typePojo.setTypename_(types[0]);
					typePojo.setAutoupdate_(types[1]);
					typePojo.setTypeupdateurl_(types[2]);
					tabNames.add(typePojo.getTypename_());
					typeList.add(typePojo);
				}
			}
		}
		if ("0".equals(btnType)) {
			typeList = typeList.subList(0, typeList.size() - 5);
			// title.setText("资讯中心");
		} else {
			typeList = typeList.subList(typeList.size() - 5, typeList.size() - 1);
			// title.setText("交易策略");
		}
		// textAdapter = new TabAdapter(this, tabNames);
		// gallery.setAdapter(textAdapter);
		// gallery.setSelection(tabNames.size() * 5);
		// textAdapter.setSelectedTab(tabNames.size() * 5);
		// gallery.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// TabAdapter adapter = (TabAdapter) parent.getAdapter();
		// adapter.setSelectedTab(position);
		// selectType = (String) adapter.getItem(position);
		// // showDialog(LOADING_DIALOG);
		// progressDialog = new ProgressDialog(MainView.this);
		// progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// progressDialog.setMessage("初始化数据...");
		// progressDialog.setTitle("请等待");
		// progressDialog.show();
		// progressThread = new ProgressThread(handler);
		// progressThread.setState(1);
		// progressThread.start();
		// // try {
		// // updateNews(selectType);
		// // } catch (Exception e) {
		// // // TODO Auto-generated catch block
		// // errorDialog("更新异常", "无法取得数据！请稍后再试。");
		// // }
		//
		// // dismissDialog(LOADING_DIALOG);
		// }
		//
		// });
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
				// newsData = loadInfoUtil.getInforByXMLtest("");
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

	// 更新新闻 updateNews ---判断新闻类型 财经还是全球
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
				// newsData = loadInfoUtil.getInforByXMLtest("");
			}

		} else {
			typePojo = typeList.get(0);
			url = typePojo.getTypeupdateurl_();
			// gallery.setSelection(tabNames.size() * 5);
			newsData = util.getInforByXML(url);
			// newsData = loadInfoUtil.getInforByXMLtest("");
		}
		if (newsData != null && newsData.size() > 0) {
			lastId = newsData.get(0).getLink_();
		}
		// 此画面需要自动更新的情况下启动更新线程

	}

	// 显示取得数据
	public void showList() {
		list = new ArrayList<Map<String, Object>>();
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

		if (!"滚动播报".equals(selectType)) {
			newList.setVisibility(View.GONE);
			TMyadapter tadapter = new TMyadapter(NewsView.this, list, R.layout.newslistitems, new String[] { DATA_NEWSHEAD, DATA_NEWSTIME }, new int[] {
					R.id.newsHead, R.id.newsTime });
			TnewList.setAdapter(tadapter);
			TnewList.setVisibility(View.VISIBLE);
			TnewList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (newsData.size() > position && position >= 0) {
						InforPojo tmpInfor = newsData.get(position);
						Intent in = new Intent(NewsView.this, InforDetailView.class);
						Bundle bundle = new Bundle();
						bundle.putString("link", newURLFront + tmpInfor.getLink_());
						in.putExtras(bundle);
						startActivity(in);
					}
				}
			});
		} else {
			TnewList.setVisibility(View.GONE);

			// Map<String, Object> map = new HashMap<String, Object>();
			// map.put(DATA_NEWSHEAD, "点击查看更多资讯");
			// map.put(DATA_NEWSTIME, "");
			// list.add(map);
			adapter = new Myadapter(this, list, R.layout.newslistitems, new String[] { DATA_NEWSHEAD, DATA_NEWSTIME },
					new int[] { R.id.newsHead, R.id.newsTime });

			// View header = View.inflate(this, R.layout.viewpager, null);
			// newList.addHeaderView(header);
			// RelativeLayout layout = (RelativeLayout) adapter.getView(0, null,
			// null);
			// TextView t = (TextView)layout.findViewById(R.id.newsHead);
			// t.getPaint().setFakeBoldText(true);
			// t.setTextColor(Color.RED);
			// TextView t2 = (TextView)layout.findViewById(R.id.newsTime);
			// t2.getPaint().setFakeBoldText(true);
			// t2.setTextColor(Color.RED);
			// adapter.notifyDataSetChanged();

			newList.setAdapter(adapter);
			newList.setVisibility(View.VISIBLE);
			newList.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// if (newsData.size() == position - 1) {
					// // 查询更多资讯
					// progressDialog = new ProgressDialog(NewsView.this);
					// progressDialog
					// .setProgressStyle(ProgressDialog.STYLE_SPINNER);
					// progressDialog.setMessage("取得数据...");
					// progressDialog.setTitle("请等待");
					// progressDialog.setCancelable(true);
					// progressDialog.show();
					// progressThread = new ProgressThread(handler);
					// progressThread.setState(3);
					// progressThread.start();
					// return;
					// }
					if (newsData.size() >= position && position > 0) {

						InforPojo tmpInfor = newsData.get(position - 1);
						Intent in = new Intent(NewsView.this, InforDetailView.class);
						Bundle bundle = new Bundle();
						// bundle.putString("title", tmpInfor.getTitle_());
						// bundle.putString("time", tmpInfor.getTime_());
						// bundle.putString("detail", tmpInfor.getContent_());
						bundle.putString("link", newURLFront + tmpInfor.getLink_());
						in.putExtras(bundle);
						startActivity(in);

					}
				}

			});
			newList.onRefreshComplete();
			Toast.makeText(getApplicationContext(), "数据已更新!", 500).show();
		}
	}

	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsview);
		btn_i = (Button) findViewById(R.id.Btn_i);
		btn_i.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				progressDialog = new ProgressDialog(NewsView.this);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setMessage("取得数据...");
				//progressDialog.setTitle("请等待");
				progressDialog.setCancelable(true);
				progressDialog.show();
				progressThread = new ProgressThread(handler);
				progressThread.setState(1);
				progressThread.start();
				// lastSelectBtn = btn_i;
			}
		});
		dots = new ArrayList<View>();
		dots.add(findViewById(R.id.v_dot0));
		dots.add(findViewById(R.id.v_dot1));
		dots.add(findViewById(R.id.v_dot2));
		// 文字标题控件
		tv_title = (TextView) findViewById(R.id.tv_title);
		// ViewPager控件
		viewPager = (ViewPager) findViewById(R.id.vp);
		Bundle bundle = getIntent().getExtras();
		if (!"".equals(bundle.getString("started"))) {
			started = true;
		}

		btnType = bundle.getString("btn");
		typeList = new ArrayList<TypePojo>();
		tabNames = new ArrayList<String>();
		newsData = new ArrayList<InforPojo>();
		scrollView = (HorizontalScrollView) findViewById(R.id.hsvnav);

		newList = (MsgListView) findViewById(R.id.newsList);
		TnewList = (ListView) findViewById(R.id.TnewsList);
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

		// dismissDialog(PROGRESS_DIALOG);

		final Handler dataHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				show();
				Thread mThread = new Thread(new MyRunnable(0));
				mThread.start();
			}

		};
		new Thread() {

			@Override
			public void run() {
				try {
					url = new URL(XML_URL);
					HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
					httpCon.setRequestMethod("GET");
					newlist = service.getNews(url.openStream());
					System.out.println("newlist.size()" + newlist.size());
					dataHandler.sendEmptyMessage(1);
				} catch (Exception e) {
					System.out.println("异常");
					e.printStackTrace();
				}
			}

		}.start();

		// show();
		// Thread mThread = new Thread(new MyRunnable(0));
		// mThread.start();
	}

	class MyRunnable implements Runnable {
		private int position;

		public MyRunnable(int position) {
			this.position = position;
			System.out.println("Thread run" + position);
		}

		public void run() {
			try {
				URL url1 = new URL((String) data.get(position).get("Img"));
				URLConnection conn = url1.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				bm = BitmapFactory.decodeStream(is);
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			nHandler.sendEmptyMessage(position);
		}

	}

	// 菜单
	protected void authorDialog() {
		AlertDialog.Builder builder = new Builder(NewsView.this);
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

		AlertDialog.Builder builder = new Builder(NewsView.this);
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
			progressDialog = new ProgressDialog(NewsView.this);
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
			progressDialog = new ProgressDialog(NewsView.this);
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
					errorDialog("异常", errorMsg);
				}
			}
			if (total == 3) {

				showList();
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
				try {
					updateNews(selectType, false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showList();
				Toast.makeText(getApplicationContext(), "数据已更新!", 2000).show();
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

	private class Myadapter extends BaseAdapter {

		private NewsView main;
		private List<? extends Map<String, ?>> list;

		public Myadapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
			super();
			main = (NewsView) context;
			list = data;
			// TODO Auto-generated constructor stub
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
			title.getPaint().setAntiAlias(true);

			// 字体加粗
			// title.getPaint().setFakeBoldText(true);
			// 设置ListView里面Item的字体颜色
			// title.setTextColor(getResources().getColor(R.color.huise));
			//
			// time.setTextColor(getResources().getColor(R.color.huise));

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

	private class TMyadapter extends BaseAdapter {

		private NewsView main;
		private List<? extends Map<String, ?>> list;

		public TMyadapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
			super();
			main = (NewsView) context;
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

			// if (position <= 2) {
			//
			// title.getPaint().setFakeBoldText(true);
			// title.setTextColor(getResources().getColor(R.color.orange));
			// time.setTextColor(getResources().getColor(R.color.orange));
			// // newnew.setText("NEW");
			// }

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

	// 自动更新线程
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
				// try {
				// Thread.sleep(200);
				// } catch (InterruptedException e) {
				// // Log.e("ERROR", "Thread Interrupted");
				// }
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
				// try {
				// Thread.sleep(100);
				// } catch (InterruptedException e) {
				// Log.e("ERROR", "Thread Interrupted");
				// }
				// msg = mHandler.obtainMessage();
				// b = new Bundle();
				// b.putInt("total", 4);
				// msg.setData(b);
				// mHandler.sendMessage(msg);
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
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						try {
							refreshNews(selectType, true);
						} catch (Exception e) {
							// TODO Auto-generated catch block
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
