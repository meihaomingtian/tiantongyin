package joey.present.view;

import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import joey.present.util.Const;
import joey.present.util.ZoomTextView;

import org.w3c.dom.Document;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;

public class InforDetailView extends Activity {
	private TextView autherText;
	// 数据取得用线程
	private ProgressThread progressThread;
	// 数据取得用dialog
	private ProgressDialog progressDialog;
	// 详细内容地址
	private String url;
	// 详细内容标题组件
	private TextView title;
	// 详细内容发表时间组件
	private TextView time;
	// 详细内容组件
	private TextView detail;
	// 详细内容标题
	private String titleValue;
	// 详细内容发表时间
	private String timeValue;
	// 详细内容
	private String detailValue;
	// 详细内容
	private String author;
	// 异常语句
	private String errorMsg = "";
	// 放大缩小组件
	private ZoomControls zoomControls;
	// 论坛监听按钮
	private Button btn_i;
	// 字体大小
	private float size;
	// 字体大小默认值
	private float sizeDef;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.infordetailview);

		Bundle bundle = getIntent().getExtras();
		url = bundle.getString("link");
		title = (TextView) findViewById(R.id.newsDetailTitle);
		time = (TextView) findViewById(R.id.newsDetailTime);
		detail = (TextView) findViewById(R.id.newsDetail);
		autherText = (TextView) findViewById(R.id.author);
		// 放大缩小控件
		// zoomControls = (ZoomControls) findViewById(R.id.zoomcontrols);
		// detail.setMovementMethod(ScrollingMovementMethod.getInstance());
		// size = detail.getTextSize() - 2;
		// sizeDef = detail.getTextSize();
		// detail.setTextSize(sizeDef - 4);

		// 监听跳转论坛按钮
		btn_i = (Button) findViewById(R.id.Btn_i);
		btn_i.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 内嵌游览器WebView,跳转到fx678论坛
				Intent intent = new Intent(InforDetailView.this, WebView_forum.class);
				// Intent intent = new Intent(Intent.ACTION_VIEW);
				// intent.setData(Uri.parse("http://my.fx678.com/"));
				startActivity(intent);

			}
		});

		// 头条返回实现
		Button btn = (Button) findViewById(R.id.Btn_fanhui);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				InforDetailView.this.finish();
			}
		});

		// 放大缩小控件的监听实现
		// zoomControls.setOnZoomInClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// if (size <= sizeDef + 8) {
		// size = size + 2;
		// detail.setTextSize(size);
		// }
		//
		// }
		//
		// });
		// zoomControls.setOnZoomOutClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// if (size > sizeDef - 4) {
		// size = size - 2;
		// detail.setTextSize(size);
		// }
		// }
		//
		// });

		// ImageButton backBtn = (ImageButton) findViewById(R.id.backbtn);
		// backBtn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		//
		// InforDetailView.this.finish();
		// }
		//
		// });

		progressDialog = new ProgressDialog(InforDetailView.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("取得数据...");
		//progressDialog.setTitle("请等待");
		progressDialog.setCancelable(true);
		progressDialog.show();
		progressThread = new ProgressThread(handler);
		progressThread.start();

	}

	// Define the Handler that receives messages from the thread and update the
	// progress
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			if ("".equals(errorMsg)) {
				title.setText(titleValue);
				time.setText(timeValue + "   " + author);
				detail.setText(detailValue);

				title.setText(titleValue);
				time.setText(timeValue + " ");
				// + author.replace("汇通网 www.fx678.com", "汇金网 gold678.com"));
				autherText.setText(author);
				detail.setText(detailValue);
				//float zoomScale = 0.5f;// 缩放比例
				//new ZoomTextView(detail, zoomScale);

			} else {
				errorDialog("异常", errorMsg);
			}
		}
	};

	// 取得详细数据
	public void getDetail() throws Exception {
		Document doc = null;
		try {
			URL inforUrl = new URL(url);
			URLConnection ucon = inforUrl.openConnection();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			doc = builder.parse(ucon.getInputStream());
		} catch (Exception e) {
			throw e;
		}
		titleValue = doc.getElementsByTagName("NewsTitle").item(0).getFirstChild().getNodeValue();
		timeValue = doc.getElementsByTagName("NewsTime").item(0).getFirstChild().getNodeValue();
		author = doc.getElementsByTagName("author").item(0).getFirstChild().getNodeValue();
		detailValue = doc.getElementsByTagName("NewsContent").item(0).getFirstChild().getNodeValue();
		detailValue = detailValue.replace("&nbsp;", " ");
		detailValue = detailValue.replace("<br>", "\n");
		// detailValue = detailValue.replace("<br><br>", "1111");

		if (detailValue.length() > 4) {
			if (detailValue.substring(0, 3).indexOf("\n") > 0) {
				detailValue = detailValue.substring(4);
			}
			detailValue = "        " + detailValue;
		}
	}

	protected void errorDialog(String title, String msg) {

		AlertDialog.Builder builder = new Builder(InforDetailView.this);
		builder.setMessage(msg);
		builder.setTitle(title);

		builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/** Nested class that performs progress calculations (counting) */
	private class ProgressThread extends Thread {
		Handler mHandler;

		@SuppressWarnings("unused")
		ProgressThread(Handler h) {
			mHandler = h;
		}

		public void run() {
			try {
				getDetail();
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				msg.setData(b);
				errorMsg = "";
				mHandler.sendMessage(msg);
			} catch (Exception e) {

				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				msg.setData(b);
				errorMsg = "无法取得数据！请稍后再试。";
				mHandler.sendMessage(msg);
			}

		}

	}
}
