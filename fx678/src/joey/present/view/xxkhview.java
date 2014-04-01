package joey.present.view;

import joey.present.view.WebView_forum.MyWebChromeClient;

import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class xxkhview extends Activity {
	private String khpd = "";
	private WebView webview;
	private TextView textview;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xxkhview);
		Bundle bundle = getIntent().getExtras();
		khpd = bundle.getString("khpd");
		webview = (WebView) findViewById(R.id.wy);
		// 后退按钮
		ImageButton backBtn = (ImageButton) findViewById(R.id.backbtub);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				xxkhview.this.finish();
			}
		});

		// 支持网页有js
		webview.getSettings().setJavaScriptEnabled(true);
		// 点击继续停留在当前的browser中响应，而不是新开一个browser
		webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("tel:")) {
					startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
					return true;
				} else if (url.startsWith("mailto:")) {
					url = url.replaceFirst("mailto:", "");
					url = url.trim();
					Intent i = new Intent(Intent.ACTION_SEND);
					i.setType("plain/text").putExtra(Intent.EXTRA_EMAIL,
							new String[] { url });
					startActivity(i);
					return true;
				} else if (url.startsWith("geo:")) {
					Intent searchAddress = new Intent(Intent.ACTION_VIEW, Uri
							.parse(url));
					startActivity(searchAddress);
					return true;
				} else {
					view.loadUrl(url);
					return true;
				}
			}
		});

		webview.setWebChromeClient(new MyWebChromeClient());
		webview.getSettings().setSupportZoom(true);
		webview.getSettings().setBuiltInZoomControls(true);
		// awebview.getSettings().setUseWideViewPort(true);
		webview.getSettings().setLoadWithOverviewMode(true);

		webHtml();
	}

	// 内嵌游览器WebView
	private void webHtml() {
		try {
			if (khpd.equals("mnkh")) {
				webview.loadUrl("http://srgjs.com/app/zhuce.htm");
				textview = (TextView) findViewById(R.id.titel);
				textview.setText("模拟账号申请");
			} else if (khpd.equals("spkh")) {
				webview.loadUrl("http://srgjs.com/app/yuyue.htm");
				textview = (TextView) findViewById(R.id.titel);
				textview.setText("实盘账号申请");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 系统的back回退键重写，不会退处Activity
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			webview.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	final Context myApp = this;

	final class MyWebChromeClient extends WebChromeClient {
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			new AlertDialog.Builder(myApp)
					.setTitle("提示：")
					.setMessage(message)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									result.confirm();
								}
							}).create().show();
			return true;
		};

		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {
			new AlertDialog.Builder(myApp)
					.setTitle("App Titler")
					.setMessage(message)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									result.confirm();
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									result.cancel();
								}
							}).create().show();

			return true;
		}
	}
}