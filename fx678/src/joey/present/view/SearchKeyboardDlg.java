package joey.present.view;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.view.View.OnClickListener;
import com.fx678.finace.R;
public class SearchKeyboardDlg extends Dialog {
	private EditText code;
	private SearchKeyboardDlg dlg;

	public SearchKeyboardDlg(Context context) {

		super(context);
		dlg = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated constructor stub
		setContentView(R.layout.keyboardview);
		setCanceledOnTouchOutside(true);
		LayoutParams a = getWindow().getAttributes();
		a.gravity = Gravity.CENTER;
		a.dimAmount = 0.0f; //
		// a.width = 440;
		// a.height = 600;
		getWindow().setAttributes(a);
		initView();

	}

	private void initView() {
		code = (EditText) findViewById(R.id.codetext);
		code.setMaxLines(6);
		Button btn = null;
		btn = (Button) findViewById(R.id.one);
		initCodeBtn(btn);
		btn = (Button) findViewById(R.id.two);
		initCodeBtn(btn);
		btn = (Button) findViewById(R.id.three);
		initCodeBtn(btn);
		btn = (Button) findViewById(R.id.four);
		initCodeBtn(btn);
		btn = (Button) findViewById(R.id.five);
		initCodeBtn(btn);
		btn = (Button) findViewById(R.id.six);
		initCodeBtn(btn);
		btn = (Button) findViewById(R.id.seven);
		initCodeBtn(btn);
		btn = (Button) findViewById(R.id.eight);
		initCodeBtn(btn);
		btn = (Button) findViewById(R.id.nine);
		initCodeBtn(btn);
		btn = (Button) findViewById(R.id.zero);
		initCodeBtn(btn);
		btn = (Button) findViewById(R.id.a);
		initCodeBtn(btn);
		btn = (Button) findViewById(R.id.c);
		initCodeBtn(btn);
		btn = (Button) findViewById(R.id.f);
		initCodeBtn(btn);
		btn = (Button) findViewById(R.id.ott);
		initCodeBtn(btn);
		btn = (Button) findViewById(R.id.soo);
		initCodeBtn(btn);
		btn = (Button) findViewById(R.id.ooo);
		initCodeBtn(btn);

		btn = (Button) findViewById(R.id.close);
		btn.setOnClickListener(new android.view.View.OnClickListener() {

			public void onClick(View v) {
				dlg.dismiss();
			}

		});
		btn = (Button) findViewById(R.id.del);
		btn.setOnClickListener(new android.view.View.OnClickListener() {

			public void onClick(View v) {

				String tmp = code.getText().toString();
				if (tmp.length() == 0) {
					return;
				}
				code.setText(tmp.substring(0, tmp.length() - 1));
			}

		});
		btn = (Button) findViewById(R.id.search);
		btn.setOnClickListener(new android.view.View.OnClickListener() {

			public void onClick(View v) {
				// start result list
			}

		});
	}

	private void initCodeBtn(Button btn) {
		btn.setOnClickListener(new android.view.View.OnClickListener() {

			public void onClick(View v) {
				Button btn = (Button) v;
				String tmp = btn.getText().toString().trim();
				code.setText(code.getText() + "" + tmp);
			}

		});

	}
}
