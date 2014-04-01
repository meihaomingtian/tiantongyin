package joey.present.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fx678.finace.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.umeng.analytics.MobclickAgent;

public class MainView extends Activity {

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
		setContentView(R.layout.mainview);
		ImageButton backBtn = (ImageButton) findViewById(R.id.backbtn);
		backBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				finish();
			}

		});
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.playout:
			popDialog();
			break;

		default:
			break;
		}
	}

	private void popDialog() {
		new AlertDialog.Builder(this).setMessage("请选择").setCancelable(false)
				.setPositiveButton("拨打热线", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent i = new Intent(Intent.ACTION_DIAL);
						i.setData(Uri.parse("tel:021-62172178"));
						startActivity(i);
					}
				}).setNegativeButton("复制QQ号", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
						cmb.setText("291154356");
						dialog.dismiss();
						Toast.makeText(MainView.this, "QQ号已复制", 0).show();
					}
				}).setCancelable(true).create().show();
	}

}
