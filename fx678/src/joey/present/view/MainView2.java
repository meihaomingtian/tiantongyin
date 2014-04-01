package joey.present.view;

import java.util.Random;

import joey.present.util.Const;
import joey.present.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.umeng.analytics.MobclickAgent;

public class MainView2 extends Activity {

	TextView app_version;

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

	Random random;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainview2);

		app_version = (TextView) findViewById(R.id.app_version);
		app_version.setText("版本号：" + Const.APP_Version);

		random = new Random();
		changeColor();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about_01:
			startActivity(new Intent(MainView2.this, TransView.class));
			break;
		case R.id.about_02:
			startActivity(new Intent(MainView2.this, MainView.class));
			break;
		case R.id.about_03:
			support();
			break;
		case R.id.about_04:
			email();
			break;
		case R.id.about_05:
			// 异步版本检查
			if (isNetworkConnected(this)) {
				checkVersion();
			} else {
				showNoConn();
			}
			break;

		case R.id.backbtn:
			this.finish();
			break;
		case R.id.app_version:

			changeColor();

			break;
		}
	}

	private String suppoerPhone = "021-62313339";

	private void support() {
		new AlertDialog.Builder(this).setMessage("确定拨打技术支持电话：" + "\n" + suppoerPhone + " 吗？").setCancelable(false)
				.setPositiveButton("好", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent i = new Intent(Intent.ACTION_DIAL);
						i.setData(Uri.parse("tel:" + suppoerPhone));
						startActivity(i);
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).setCancelable(true).create().show();
	}

	/**
	 * 意见反馈
	 */
	private void email() {
		try {

			// SENDTO方式
			Uri uri = Uri.parse("mailto:3g@fx678.cn");
			String[] email = { "3g@fx678.cn" };
			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
			intent.putExtra(Intent.EXTRA_SUBJECT, "汇通财经 安卓客户端意见反馈"); // 主题
			intent.putExtra(Intent.EXTRA_TEXT, "意见反馈内容"); // 正文
			startActivity(Intent.createChooser(intent, "请选择邮件类应用"));

		} catch (Exception e) {
			new AlertDialog.Builder(MainView2.this).setTitle("提示").setMessage("无邮件账户，请设置邮件账户来发送电子邮件")
					.setNegativeButton("确认", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
			e.printStackTrace();
		}
	}

	/**
	 * 版本号随机变色
	 */
	private void changeColor() {
		app_version.setTextColor(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
	}

	@Deprecated
	/**
	 * 已经废弃
	 */
	private void popVersion() {

		new AlertDialog.Builder(MainView2.this).setCancelable(false).setMessage("版本号：" + Const.APP_Version).setCancelable(true)
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

	private String version = "";
	private Util util = new Util();

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
				AlertDialog.Builder builder = new Builder(MainView2.this);
				builder.setMessage("发现新版本，需要下载最新版吗？");
				builder.setTitle("提示");
				builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Intent intent1 = new Intent(Intent.ACTION_VIEW);
						intent1.setData(Uri.parse(version.substring(2, version.length())));
						startActivity(intent1);
					}
				});
				builder.setNegativeButton("不更新", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			} else if ("2".equals(v)) {
				AlertDialog.Builder builder = new Builder(MainView2.this);
				builder.setMessage("请更新到最新版使用！");
				builder.setTitle("提示");
				builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Intent intent1 = new Intent(Intent.ACTION_VIEW);
						intent1.setData(Uri.parse(version.substring(2, version.length())));
						startActivity(intent1);
					}
				});
				builder.create().show();
			} else if ("0".equals(v)) {
				Toast toast = Toast.makeText(MainView2.this, "已是最新版本", 0);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.setDuration(0);
				toast.show();
			}
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
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 无网络连接提示
	 */
	private void showNoConn() {

		new AlertDialog.Builder(MainView2.this).setTitle("注意：").setCancelable(false).setMessage("无法检查更新,请检查网络连接")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//
					}
				}).create().show();
	}

}
