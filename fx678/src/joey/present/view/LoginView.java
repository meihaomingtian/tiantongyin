package joey.present.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import com.fx678.finace.R;
public class LoginView extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginview);
		
		
		}
}
