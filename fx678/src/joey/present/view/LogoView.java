package joey.present.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import joey.present.util.MyLogger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.code.microlog4android.config.PropertyConfigurator;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.Window;
import android.widget.ImageView;
import com.fx678.finace.R;

public class LogoView extends Activity {

	private MyLogger log = MyLogger.yLog();
	Bitmap mBitmap;

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// 谷歌分析统计代码
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// 谷歌分析统计代码
		EasyTracker.getInstance().activityStop(this);
	}

	// 启动图片组件
	private ImageView logoImage;
	// 主画面handler
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			startActivity(new Intent(LogoView.this, StartView.class));
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}
	};

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/** 解析出来的title */
	private String title;
	/** 解析出来的imgurl_android */
	private String imgurl_android;
	/** 解析出来的linkurl */
	private String linkurl;
	/** 解析出来的version */
	private String version_mintai;

	SharedPreferences sharedPreferences;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		PropertyConfigurator.getConfigurator(this).configure();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logoview);

		// 拿民泰的广告 OEM版本都应该注释掉
		if (isNetworkConnected(this)) {
			getAD();
		}

		handler.sendEmptyMessageDelayed(111, 2500);

	}

	public void ReadHttpResponse(String url) {

		StringBuilder sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://m.fx678.com/mintai/ad_fx678.xml");
		InputStream inpst = null;
		try {
			HttpResponse response = client.execute(httpget);
			StatusLine sl = response.getStatusLine();
			int sc = sl.getStatusCode();
			if (sc == 200) {
				HttpEntity ent = response.getEntity();
				inpst = ent.getContent();
			} else {
				Log.e("log_tag", "I didn't  get the response!");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// log.i( sb.toString());

		XmlPullParser parser = Xml.newPullParser();
		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		try {
			parser.setInput(inpst, "utf-8");

			// 获取事件类型
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					// 当前事件是文档开始事件
					// 文档开始时初始化list
					break;

				case XmlPullParser.START_TAG:
					// 当前事件是标签元素开始事件
					if (parser.getName().equals("title")) {
						// 如果是person标签 则新建person对象 并获取id

						title = parser.nextText();
						// log.i("title" + title);
						editor.putString("title", title);
						editor.commit();

					} else if (parser.getName().equals("imgurl_android")) {
						imgurl_android = parser.nextText();
						// log.i("imgurl_android" + imgurl_android);
						editor.putString("imgurl_android", imgurl_android);
						editor.commit();

					} else if (parser.getName().equals("linkurl")) {

						linkurl = parser.nextText();
						// log.i("linkurl" + linkurl);
						editor.putString("linkurl", linkurl);
						editor.commit();
					} else if (parser.getName().equals("version")) {
						version_mintai = parser.nextText();
						// log.i("version" + version_mintai);
						editor.putString("version", version_mintai);
						editor.commit();
					}
					break;
				case XmlPullParser.TEXT:

					break;
				case XmlPullParser.END_TAG:
					// 当前事件是标签元素结束事件
					if (parser.getName().equals("row")) {
						// person标签结束 添加到list并设空person对象
					}
					break;
				default:
					break;
				}
				// 进入下一个元素并触发相应事件
				event = parser.next();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 判断是否有网络连接
	 * 
	 * @param context
	 * @return
	 */
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 民泰广告
	 */
	private void getAD() {

		MyTask mytask = new MyTask();
		mytask.execute(null, null, null);
	}

	class MyTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			ReadHttpResponse(null);
			linkurl = sharedPreferences.getString("linkurl", "http://www.pm166.com/web/pages/wapbs/M_BsReg.aspx?kind=cpmmAPP");

			imgurl_android = sharedPreferences.getString("imgurl_android", "http://m.fx678.com/img/mintai/mtht_android.png");
			try {
				mBitmap = loadImageFromUrl(imgurl_android);
				saveMyBitmap("ad");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			super.onPostExecute(result);

		}
	}

	/**
	 * url 下载图片
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public Bitmap loadImageFromUrl(String url) throws Exception {
		final DefaultHttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(url);

		HttpResponse response = client.execute(getRequest);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			Log.e("PicShow", "Request URL failed, error code =" + statusCode);
		}

		HttpEntity entity = response.getEntity();
		if (entity == null) {
			Log.e("PicShow", "HttpEntity is null");
		}
		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			is = entity.getContent();
			byte[] buf = new byte[1024];
			int readBytes = -1;
			while ((readBytes = is.read(buf)) != -1) {
				baos.write(buf, 0, readBytes);
			}
		} finally {
			if (baos != null) {
				baos.close();
			}
			if (is != null) {
				is.close();
			}
		}
		byte[] imageArray = baos.toByteArray();
		return BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
	}

	/**
	 * 保存图片
	 * 
	 * @param bitName
	 * @throws IOException
	 */
	public void saveMyBitmap(String bitName) throws IOException {
		File f = new File("/data/data/com.fx678.finace/files/" + bitName + ".png");
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}