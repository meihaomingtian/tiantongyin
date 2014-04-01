package joey.present.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import joey.present.data.InforPojo;
import joey.present.util.Util;
import joey.present.view.ui.MsgListView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.fx678.finace.R;

public class Ssjpview extends Activity {
	// 新闻地址前段
	private String newURLFront;
	private ListView TnewList;
	// 新闻数据list
	private List<InforPojo> newsData;
	// 数据取得工具类
	private Util util = new Util();
	// 数据源
	List<Map<String, Object>> list;
	// 新闻类型
	private final String DATA_NEWSHEAD = "newsTypeHead";
	// 新闻类型时间
	private final String DATA_NEWSTIME = "newsTypeTime";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ssjpview);
		TnewList = (ListView) findViewById(R.id.jpList);
		newsData = new ArrayList<InforPojo>();
		Toast.makeText(this, "dsf234", 0).show();
		try {
			newsData = util.getInforByXML1("http://tool.fx678.com/mob/source/gushixinwen.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "324436", 0).show();
			e.printStackTrace();
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
			//显示内容
			TMyadapter tadapter = new TMyadapter(Ssjpview.this, list, R.layout.newslistitems, new String[] { DATA_NEWSHEAD, DATA_NEWSTIME }, new int[] {
					R.id.newsHead, R.id.newsTime });
			TnewList.setAdapter(tadapter);
			TnewList.setVisibility(View.VISIBLE);
			TnewList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (newsData.size() > position && position >= 0) {
						InforPojo tmpInfor = newsData.get(position);
						Intent in = new Intent(Ssjpview.this, InforDetailView.class);
						Bundle bundle = new Bundle();
						bundle.putString("link", newURLFront + tmpInfor.getLink_());
						in.putExtras(bundle);
						startActivity(in);
					}
				}
			});
		}
}
