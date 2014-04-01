package joey.present.view;

import java.util.List;
import java.util.Map;

import joey.present.data.InforPojo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.fx678.finace.R;
public class TnewsAdapter extends BaseAdapter {

	// 新闻数据list
	private List<InforPojo> newsData;
	// 新闻类型时间
	private final String DATA_NEWSTIME = "newsTypeTime";
	// 新闻类型
	private final String DATA_NEWSHEAD = "newsTypeHead";
	private NewsView main;
	private List<? extends Map<String, ?>> list;

	public TnewsAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super();
		main = (NewsView) context;
		list = data;
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

	public View getView(int position, View convertView, ViewGroup parent) {
		if (position < 0 || newsData.size() <= 0)
			return null;

		if (convertView == null)
			convertView = LayoutInflater.from(main).inflate(
					R.layout.newslistitems, null);

		TextView title = (TextView) convertView.findViewById(R.id.newsHead);
		TextView time = (TextView) convertView.findViewById(R.id.newsTime);
		//TextView newnew = (TextView) convertView.findViewById(R.id.xxdj);
		Map<String, ?> map = list.get(position);
		title.setText((String) map.get(DATA_NEWSHEAD));
		time.setText((String) map.get(DATA_NEWSTIME));
		title.getPaint().setFakeBoldText(true);
		// 设置ListView里面Item的字体颜色
		//title.setTextColor(R.color.black);

		//time.setTextColor(R.color.black);

		return convertView;
	}
}
