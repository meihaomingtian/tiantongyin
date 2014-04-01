package joey.present.view;

import com.fx678.finace.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

public class khsqView extends Activity {
	private ImageButton mnkh;
	private ImageButton spkh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yhsq);
		ImageButton backBtn = (ImageButton) findViewById(R.id.backbtua);
		backBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				khsqView.this.finish();
			}
		});
		mnkh = (ImageButton) findViewById(R.id.mnkh);
		mnkh.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent in = new Intent(khsqView.this, xxkhview.class);
					Bundle bundle = new Bundle();
					bundle.putString("khpd", "mnkh");
					in.putExtras(bundle);
					startActivity(in);
				}
				return false;
			}
		});
		spkh = (ImageButton) findViewById(R.id.spkh);
		spkh.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent in = new Intent(khsqView.this, xxkhview.class);
					Bundle bundle = new Bundle();
					bundle.putString("khpd", "spkh");
					in.putExtras(bundle);
					startActivity(in);
				}
				return false;
			}
		});
	}
}
