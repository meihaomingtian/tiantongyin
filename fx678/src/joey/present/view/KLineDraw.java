package joey.present.view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import joey.present.data.KData;
import joey.present.data.PriceData;
import joey.present.util.Const;
import joey.present.util.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class KLineDraw extends View implements Runnable {
	private Util util = new Util();
	private String ex = Const.SHGOLD1;
	private String index = "";
	private boolean indexMA = true;
	private boolean initFlag = false;

	public boolean isInitFlag() {
		return initFlag;
	}

	public void setInitFlag(boolean initFlag) {
		this.initFlag = initFlag;
	}

	private int drawLineCount = 0;
	private final int CMA5 = 5;
	private final int CMA10 = 10;
	private final int CMA20 = 20;
	private final int macdParam[] = { 12, 26, 9 };
	private final int volParam[] = { 5, 10, 20 };
	private final int kdjParam[] = { 9, 3, 3 };
	private final int rsiParam[] = { 6, 12, 24 };
	private final int bollParam[] = { 26, 2 };
	private final int obvParam[] = { 12 };
	private final int cciParam[] = { 14 };
	private final int psyParam[] = { 12, 24 };
	private float macdData[][];
	private float volData[][];
	private float kdjData[][];
	private float rsiData[][];
	private float bollData[][];
	private float obvData[][];
	private float cciData[][];
	private float psyData[][];
	private List<Float> maSet1list;
	private List<Float> maSet2list;
	private List<Float> maSet3list;
	private int points = 38;// 点数
	private float lastClose = 0;// 最后收盘价
	private String code;

	private Paint mPaint = null;
	private float width; // 屏幕宽度
	private float height;// 屏幕高度
	private float maxH; // 绘制图标横坐标的高度
	private float minH;
	private float tableWidth; // 表格宽度
	private float tableHeight;// 表格高度
	private float leftWhite;// 左边预留空白宽度
	private float rightWhite;// 右边预留空白
	private float topWhite; // 上留空白
	private float kHeight; // K线主图高度
	private float interval; // 每一天k线柱状宽度
	private float maxPrice = 0; // 最高价
	private float minPrice = 1000000;// 最低价
	private float maxZB = -1000000; // 最高价
	private float minZB = 1000000;// 最低价
	private float tradeHeight;// 交易量高度
	private float maxTradeVolume = 0;// 最大交易量
	private float midHeight;// 交易量 显示日期 行的宽度
	private DecimalFormat df4 = new DecimalFormat("0.0000");
	private DecimalFormat df2 = new DecimalFormat("0.00");
	private DecimalFormat df0 = new DecimalFormat("");
	public float x = -1;// 指示线的横坐标位置 初始化时不显示
	public List<KData> ls = null;
	private int n = 1;// 第N天的数据
	private float kButton;// k线下边线坐标

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	private float kRight;// k线右边线坐标

	// 设置指标
	public void setIndex(String index) {
		macdData = null;
		volData = null;
		kdjData = null;
		rsiData = null;
		bollData = null;
		obvData = null;
		cciData = null;
		psyData = null;
		if (!Const.INDEX_BOLL.equals(index)) {
			this.index = index;

			indexMA = true;
			// maSet1list = new ArrayList<Float>();
			// maSet2list = new ArrayList<Float>();
			// maSet3list = new ArrayList<Float>();
			// // ma计算
			// for (int i = 0; i < ls.size(); i++) {
			// float ma5temp = 0;
			// float ma10temp = 0;
			// float ma20temp = 0;
			// if (i >= CMA5) {
			// for (int j = 0; j < CMA5; j++) {
			// ma5temp = ma5temp
			// + util.getFloat(ls.get(i - j).getK_close());
			// }
			// ma5temp = ma5temp / CMA5;
			// }
			// if (i >= CMA10) {
			// for (int j = 0; j < CMA10; j++) {
			// ma10temp = ma10temp
			// + util.getFloat(ls.get(i - j).getK_close());
			// }
			// ma10temp = ma10temp / CMA10;
			//
			// }
			// if (i >= CMA20) {
			// for (int j = 0; j < CMA20; j++) {
			// ma20temp = ma20temp
			// + util.getFloat(ls.get(i - j).getK_close());
			// }
			// ma20temp = ma20temp / CMA20;
			// }
			// maSet1list.add(ma5temp);
			// maSet2list.add(ma10temp);
			// maSet3list.add(ma20temp);
			// }
		} else {
			this.index = index;
			indexMA = false;
			bollData = new float[3][ls.size()];
			float average[] = bollData[0];
			float up[] = bollData[1];
			float down[] = bollData[2];
			float sum = 0.0F;
			for (int i = 0; i < ls.size(); i++) {
				float aveTemp = 0;
				if (i >= bollParam[0] - 1) {
					for (int j = 0; j < bollParam[0]; j++) {
						aveTemp += util.getFloat(ls.get(i - j).getK_close());
					}
					aveTemp = aveTemp / bollParam[0];
				}
				average[i] = aveTemp;
			}
			for (int i = bollParam[0] - 1; i < (bollParam[0] + bollParam[0]) - 2; i++) {
				float value = util.getFloat(ls.get(i).getK_close()) - average[i];
				sum += value * value;
			}

			float prevalue = 0.0F;
			for (int i = (bollParam[0] + bollParam[0]) - 2; i < ls.size(); i++) {
				sum -= prevalue;
				float value = util.getFloat(ls.get(i).getK_close()) - average[i];
				sum += value * value;
				value = (float) Math.sqrt(sum / (float) bollParam[0]) * bollParam[1];
				up[i] = average[i] + value;
				down[i] = average[i] - value;
				if (i > points) {
					if (maxPrice < up[i]) {
						maxPrice = up[i];
					}
					if (minPrice > down[i]) {
						minPrice = down[i];
					}
				}
				value = util.getFloat(ls.get((i - bollParam[0]) + 1).getK_close()) - average[(i - bollParam[0]) + 1];
				prevalue = value * value;
			}

		}
		if (Const.INDEX_MACD.equals(index)) {
			macdData = new float[3][ls.size()];
			maxZB = -1000000; // 最高价
			minZB = 1000000;// 最低价
			macdData = new float[3][ls.size()];
			for (int i = 0; i < 3; i++) {
				if (macdParam[i] > ls.size() || macdParam[i] < 1)
					return;
			}
			float dif[] = macdData[0];
			float macd[] = macdData[1];
			float d_m[] = macdData[2];
			float di = 0.0F;
			float a = 0.0F;
			float b = 0.0F;
			float para[] = new float[3];
			float sum[] = new float[3];
			int n[] = new int[3];
			for (int i = 0; i < 3; i++) {
				n[i] = macdParam[i];
				para[i] = 2.0F / (float) (n[i] + 1);
				sum[i] = 0.0F;
			}
			for (int i = 0; i < ls.size(); i++) {
				di = util.getFloat(ls.get(i).getK_close());
				if (i < n[0]) {
					sum[0] += di;
					a = i != n[0] - 1 ? 0.0F : sum[0] / (float) n[0];
				} else {
					a = (di - a) * para[0] + a;
				}
				if (i < n[1]) {
					sum[1] += di;
					b = i != n[1] - 1 ? 0.0F : sum[1] / (float) n[1];
				} else {
					b = (di - b) * para[1] + b;
				}
				dif[i] = i < n[0] - 1 || i < n[1] ? 0.0F : a - b;
				if (i > points) {
					if (maxZB <= dif[i]) {
						maxZB = dif[i];
					}
					if (minZB >= dif[i]) {
						minZB = dif[i];
					}
				}
				if (i < n[1] + n[2]) {
					sum[2] += dif[i];
					macd[i] = i != (n[1] + n[2]) - 1 ? 0.0F : sum[2] / (float) n[2];
				} else {
					macd[i] = (float) ((double) (dif[i] - macd[i - 1]) * 0.20000000000000001D) + macd[i - 1];
				}
				if (i > points) {
					if (maxZB <= macd[i]) {
						maxZB = macd[i];
					}
					if (minZB >= macd[i]) {
						minZB = macd[i];
					}
				}

				d_m[i] = dif[i] - macd[i];
				if (i > points) {
					if (maxZB <= d_m[i]) {
						maxZB = d_m[i];
					}
					if (minZB >= d_m[i]) {
						minZB = d_m[i];
					}
				}
			}

		}
		// VOL
		if (Const.INDEX_VOL.equals(index)) {
			volData = new float[4][ls.size()];
			maxZB = -1000000;
			float total[] = volData[3];
			float ma5[] = volData[0];
			float ma10[] = volData[1];
			float ma20[] = volData[2];
			for (int i = 0; i < ls.size(); i++) {

				KData tmp = ls.get(i);
				total[i] = util.getFloat(tmp.getK_volume());
				if (i >= points) {
					if (maxZB < total[i]) {
						maxZB = total[i];
					}
				}
				float ma5temp = 0;
				float ma10temp = 0;
				float ma20temp = 0;
				if (i >= volParam[0] - 1) {
					for (int j = 0; j < volParam[0]; j++) {
						ma5temp = ma5temp + total[i - j];
					}
					ma5temp = ma5temp / volParam[0];
				}
				if (i >= volParam[1] - 1) {
					for (int j = 0; j < volParam[1]; j++) {
						ma10temp = ma10temp + total[i - j];
					}
					ma10temp = ma10temp / volParam[1];

				}
				if (i >= volParam[2] - 1) {
					for (int j = 0; j < volParam[2]; j++) {
						ma20temp = ma20temp + total[i - j];
					}
					ma20temp = ma20temp / volParam[2];
				}
				ma5[i] = ma5temp;
				ma10[i] = ma10temp;
				ma20[i] = ma20temp;

			}
		}
		// KDJ
		if (Const.INDEX_KDJ.equals(index)) {
			kdjData = new float[3][ls.size()];
			maxZB = -1000000;
			minZB = 1000000;
			float maxPrice = 0;
			float minPrice = 0;
			int n1 = kdjParam[0];
			int n2 = kdjParam[1];
			int n3 = kdjParam[2];
			float kvalue[] = kdjData[0];
			float dvalue[] = kdjData[1];
			float jvalue[] = kdjData[2];
			n2 = n2 > 0 ? n2 : 3;
			n3 = n3 > 0 ? n3 : 3;
			maxPrice = util.getFloat(ls.get(n1 - 1).getK_high());
			minPrice = util.getFloat(ls.get(n1 - 1).getK_low());
			for (int j = n1 - 1; j >= 0; j--) {
				if (maxPrice < util.getFloat(ls.get(j).getK_high()))
					maxPrice = util.getFloat(ls.get(j).getK_high());
				if (minPrice > util.getFloat(ls.get(j).getK_low()))
					minPrice = util.getFloat(ls.get(j).getK_low());
			}

			float rsv;
			if (maxPrice <= minPrice)
				rsv = 50F;
			else
				rsv = ((util.getFloat(ls.get(n1 - 1).getK_close()) - minPrice) / (maxPrice - minPrice)) * 100F;
			float prersv;
			kvalue[n1 - 1] = dvalue[n1 - 1] = jvalue[n1 - 1] = prersv = rsv;
			for (int i = 0; i < n1; i++) {
				kvalue[i] = 0.0F;
				dvalue[i] = 0.0F;
				jvalue[i] = 0.0F;
			}

			for (int i = n1; i < ls.size(); i++) {
				maxPrice = util.getFloat(ls.get(i).getK_high());
				minPrice = util.getFloat(ls.get(i).getK_low());
				for (int j = i - 1; j > i - n1; j--) {
					if (maxPrice < util.getFloat(ls.get(j).getK_high()))
						maxPrice = util.getFloat(ls.get(j).getK_high());
					if (minPrice > util.getFloat(ls.get(j).getK_low()))
						minPrice = util.getFloat(ls.get(j).getK_low());
				}

				if (maxPrice <= minPrice) {
					rsv = prersv;
				} else {
					prersv = rsv;
					rsv = ((util.getFloat(ls.get(i).getK_close()) - minPrice) / (maxPrice - minPrice)) * 100F;
				}
				kvalue[i] = (kvalue[i - 1] * (float) (n2 - 1)) / (float) n2 + rsv / (float) n2;
				dvalue[i] = kvalue[i] / (float) n3 + (dvalue[i - 1] * (float) (n3 - 1)) / (float) n3;
				jvalue[i] = 3F * kvalue[i] - 2.0F * dvalue[i];
				if (i > points) {
					if (maxZB < kvalue[i]) {
						maxZB = kvalue[i];
					}
					if (maxZB < dvalue[i]) {
						maxZB = dvalue[i];
					}
					if (maxZB < jvalue[i]) {
						maxZB = jvalue[i];
					}
					if (minZB > kvalue[i]) {
						minZB = kvalue[i];
					}
					if (minZB > dvalue[i]) {
						minZB = dvalue[i];
					}
					if (minZB > jvalue[i]) {
						minZB = jvalue[i];
					}
				}
			}
		}
		// RSI
		if (Const.INDEX_RSI.equals(index)) {
			rsiData = new float[3][ls.size()];
			maxZB = 0;
			minZB = 10000;
			float[] up = new float[3];
			float[] down = new float[3];

			for (int k = 0; k < 3; k++) {
				for (int i = 1; i < rsiParam[k]; i++) {
					if (util.getFloat(ls.get(i).getK_close()) > util.getFloat(ls.get(i - 1).getK_close())) {
						up[k] += (util.getFloat(ls.get(i).getK_close()) - util.getFloat(ls.get(i - 1).getK_close()));
					} else {
						down[k] += (util.getFloat(ls.get(i - 1).getK_close()) - util.getFloat(ls.get(i).getK_close()));
					}
				}
				if (up[k] + down[k] == 0.0F) {
					rsiData[k][rsiParam[k] - 1] = 50F;
				} else {
					rsiData[k][rsiParam[k] - 1] = (up[k] / (up[k] + down[k])) * 100F;
				}
			}
			float[] predown = new float[3];
			float[] preup = new float[3];
			for (int i = 1; i < ls.size(); i++) {
				for (int k = 0; k < 3; k++) {
					if (i < rsiParam[k]) {
						break;
					}
					up[k] -= preup[k];
					down[k] -= predown[k];
					if (util.getFloat(ls.get(i).getK_close()) > util.getFloat(ls.get(i - 1).getK_close())) {
						up[k] += (util.getFloat(ls.get(i).getK_close()) - util.getFloat(ls.get(i - 1).getK_close()));
					} else {
						down[k] += (util.getFloat(ls.get(i - 1).getK_close()) - util.getFloat(ls.get(i).getK_close()));
					}
					if (up[k] + down[k] == 0.0F) {
						rsiData[k][i] = rsiData[k][i - 1];
					}

					else {
						rsiData[k][i] = ((up[k]) / (up[k] + down[k])) * 100F;
					}

					preup[k] = predown[k] = 0.0F;
					if (util.getFloat(ls.get((i - rsiParam[k]) + 1).getK_close()) > util.getFloat(ls.get((i - rsiParam[k]))
							.getK_close())) {
						preup[k] = util.getFloat(ls.get((i - rsiParam[k]) + 1).getK_close())
								- util.getFloat(ls.get((i - rsiParam[k])).getK_close());
					} else {
						predown[k] = util.getFloat(ls.get((i - rsiParam[k]) + 1).getK_close())
								- util.getFloat(ls.get((i - rsiParam[k])).getK_close());
					}
					if (i > points) {
						if (maxZB < rsiData[k][i]) {
							maxZB = rsiData[k][i];
						}
						if (minZB > rsiData[k][i]) {
							minZB = rsiData[k][i];
						}
					}
				}
			}
		}
		// OBV
		if (Const.INDEX_OBV.equals(index)) {
			obvData = new float[2][ls.size()];
			maxZB = -10000;
			minZB = 10000;
			float obv[] = obvData[0];
			obv[0] = 0.0F;
			float total = util.getFloat(ls.get(0).getK_close()) * util.getFloat(ls.get(0).getK_volume());
			for (int i = 1; i < ls.size(); i++) {
				total += util.getFloat(ls.get(i).getK_close()) * util.getFloat(ls.get(i).getK_volume());
				if (util.getFloat(ls.get(i).getK_close()) > util.getFloat(ls.get(i - 1).getK_close())) {
					obv[i] = obv[i - 1] + (float) (total / 1000L);
				} else {
					if (util.getFloat(ls.get(i).getK_close()) < util.getFloat(ls.get(i - 1).getK_close())) {
						obv[i] = obv[i - 1] - (float) (total / 1000L);
					} else {
						obv[i] = obv[i - 1];
					}
				}
				if (i > points) {
					if (maxZB < obv[i]) {
						maxZB = obv[i];
					}
					if (minZB > obv[i]) {
						minZB = obv[i];
					}
				}
			}
		}
		// CCI
		if (Const.INDEX_CCI.equals(index)) {
			cciData = new float[2][ls.size()];
			maxZB = -10000;
			minZB = 10000;
			float cci[] = cciData[0];
			float ma[] = cciData[1];
			cci[0] = 0.0F;
			double sum = 0.0D;
			for (int i = 0; i < cciParam[0] - 1; i++) {
				sum += (util.getFloat(ls.get(0).getK_high()) + util.getFloat(ls.get(0).getK_low()) + util.getFloat(ls.get(0)
						.getK_close())) / 3F;
			}
			float prec = 0.0F;
			for (int i = cciParam[0] - 1; i < ls.size(); i++) {
				sum -= prec;
				sum += (util.getFloat(ls.get(i).getK_high()) + util.getFloat(ls.get(i).getK_low()) + util.getFloat(ls.get(i)
						.getK_close())) / 3F;
				ma[i] = (float) (sum / (double) cciParam[0]);
				prec = (util.getFloat(ls.get(i - cciParam[0] + 1).getK_high())
						+ util.getFloat(ls.get(i - cciParam[0] + 1).getK_low()) + util.getFloat(ls.get(i - cciParam[0] + 1)
						.getK_close())) / 3F;
			}
			cci[cciParam[0] - 2] = 0.0F;
			for (int i = cciParam[0] - 1; i < ls.size(); i++) {
				sum = 0.0D;
				for (int j = (i - cciParam[0]) + 1; j <= i; j++)
					sum += Math.abs((util.getFloat(ls.get(j).getK_high()) + util.getFloat(ls.get(j).getK_low()) + util
							.getFloat(ls.get(j).getK_close())) / 3F - ma[i]);

				if (sum == 0.0D)
					cci[i] = cci[i - 1];
				else
					cci[i] = (float) ((double) ((util.getFloat(ls.get(i).getK_high()) + util.getFloat(ls.get(i).getK_low()) + util
							.getFloat(ls.get(i).getK_close())) / 3F - ma[i]) / ((0.014999999999999999D * sum) / (double) cciParam[0]));

				if (i > points) {
					if (maxZB < cci[i]) {
						maxZB = cci[i];
					}
					if (minZB > cci[i]) {
						minZB = cci[i];
					}
				}
			}

		}
		// PSY
		if (Const.INDEX_PSY.equals(index)) {
			psyData = new float[2][ls.size()];
			maxZB = -10000;
			minZB = 10000;
			float psy[] = psyData[0];
			double sum = 0.0D;
			for (int i = 1; i < psyParam[0]; i++) {
				if (util.getFloat(ls.get(i).getK_close()) > util.getFloat(ls.get(i - 1).getK_close())) {
					sum++;
				}

			}
			for (int i = psyParam[0]; i < ls.size(); i++) {
				if (util.getFloat(ls.get(i).getK_close()) > util.getFloat(ls.get(i - 1).getK_close()))
					sum++;
				psy[i] = (float) ((sum / (double) psyParam[0]) * 100D);
				int j = (i - psyParam[0]) + 1;
				if (util.getFloat(ls.get(j).getK_close()) > util.getFloat(ls.get(j - 1).getK_close()))
					sum--;

				if (i > points) {
					if (maxZB < psy[i]) {
						maxZB = psy[i];
					}
					if (minZB > psy[i]) {
						minZB = psy[i];
					}
				}
			}

		}

		Message msg = handler.obtainMessage();
		handler.dispatchMessage(msg);
	}

	/** 小数位约束 */
	private DecimalFormat df_base;

	public KLineDraw(Context context, float wid, float hei, String ex, DecimalFormat df_base) {
		super(context);
		mPaint = new Paint();
		this.width = wid + 0.1f;
		this.height = hei + 0.1f;
		this.df_base = df_base;
		maxH = hei - 10.1f;
		leftWhite = 80.1f;
		rightWhite = 1.1f;
		topWhite = 22.1f;
		midHeight = 22.1f;
		kHeight = (maxH - topWhite) * 0.7f;
		tradeHeight = maxH - kHeight - topWhite - midHeight;
		tableWidth = width - rightWhite - leftWhite;
		this.ex = ex;
		this.setBackgroundColor(Color.BLACK);
		new Thread(this).start();
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setLastClose(float lastclose) {
		lastClose = lastclose;
	}

	public void updateData(List<KData> data, int point) {
		ls = data;
		maSet1list = new ArrayList<Float>();
		maSet2list = new ArrayList<Float>();
		maSet3list = new ArrayList<Float>();
		points = ls.size() - point;
		interval = tableWidth / point;
		// KData tmp = new KData();
		// tmp.setK_close(timeNow.getPrice_last());
		// tmp.setK_date(timeNow.getPrice_quotetime());
		// tmp.setK_high(timeNow.getPrice_high());
		// tmp.setK_low(timeNow.getPrice_low());
		// tmp.setK_open(timeNow.getPrice_open());
		// tmp.setK_total(timeNow.getPrice_total());
		// ls.add(tmp);
		// ma计算
		for (int i = 0; i < ls.size(); i++) {
			float ma5temp = 0;
			float ma10temp = 0;
			float ma20temp = 0;
			if (i >= CMA5 - 1) {
				for (int j = 0; j < CMA5; j++) {
					ma5temp = ma5temp + util.getFloat(ls.get(i - j).getK_close());
				}
				ma5temp = ma5temp / CMA5;
			}
			if (i >= CMA10 - 1) {
				for (int j = 0; j < CMA10; j++) {
					ma10temp = ma10temp + util.getFloat(ls.get(i - j).getK_close());
				}
				ma10temp = ma10temp / CMA10;

			}
			if (i >= CMA20 - 1) {
				for (int j = 0; j < CMA20; j++) {
					ma20temp = ma20temp + util.getFloat(ls.get(i - j).getK_close());
				}
				ma20temp = ma20temp / CMA20;
			}
			maSet1list.add(ma5temp);
			maSet2list.add(ma10temp);
			maSet3list.add(ma20temp);
		}

		float totalValue = 0f;
		float totalTrade = 0f;
		maxPrice = 0; // 最高价
		minPrice = 100000;// 最低价

		maxTradeVolume = 0;// 最大交易量
		for (int i = 0; i < ls.size(); i++) {
			totalValue = totalValue + util.getFloat(ls.get(i).getK_close()) * util.getFloat(ls.get(i).getK_volume());
			totalTrade = totalTrade + util.getFloat(ls.get(i).getK_volume());
			float tmpMinprice = util.getFloat(ls.get(i).getK_low());
			float tmpMaxprice = util.getFloat(ls.get(i).getK_high());
			float tmpMaxTradeVol = util.getFloat(ls.get(i).getK_volume());
			float tmpMaxMa = maSet1list.get(i);
			float tmpMaxMa2 = maSet2list.get(i);
			float tmpMaxMa3 = maSet3list.get(i);
			if (i >= points) {
				if (maxPrice < tmpMaxprice) {
					maxPrice = tmpMaxprice;
				}
				if (maxPrice < tmpMaxMa) {
					maxPrice = tmpMaxMa;
				}
				if (maxPrice < tmpMaxMa2) {
					maxPrice = tmpMaxMa2;
				}
				if (maxPrice < tmpMaxMa3) {
					maxPrice = tmpMaxMa3;
				}

				if (minPrice > tmpMinprice) {
					minPrice = tmpMinprice;
				}
				if (minPrice > tmpMaxMa) {
					minPrice = tmpMaxMa;
				}
				if (minPrice > tmpMaxMa2) {
					minPrice = tmpMaxMa2;
				}
				if (minPrice > tmpMaxMa3) {
					minPrice = tmpMaxMa3;
				}
				if (maxTradeVolume < tmpMaxTradeVol) {
					maxTradeVolume = tmpMaxTradeVol;
				}
			}
		}
		if ("".equals(index)) {
			setIndex(Const.INDEX_MACD);
			return;
		}
		if (indexMA) {
			setIndex(index);
		} else {
			setIndex(Const.INDEX_BOLL);
		}
	}

	int nextRed1 = Color.argb(255, 100, 0, 0);
	int nextRed2 = Color.argb(255, 100, 0, 0);
	int nextRed3 = Color.argb(255, 120, 0, 0);

	public void draw(Canvas canvas) {
		try {
			super.draw(canvas);
			canvas.drawColor(Color.BLACK);
			if (initFlag && ls.size() > points + 1) {
				// if (false) {
				try {
					List<KData> lsDraw = new ArrayList<KData>();
					List<Float> maSet1listDraw = new ArrayList<Float>();
					List<Float> maSet2listDraw = new ArrayList<Float>();
					List<Float> maSet3listDraw = new ArrayList<Float>();
					float macdDataDraw[][] = null;
					float volDataDraw[][] = null;
					float kdjDataDraw[][] = null;
					float rsiDataDraw[][] = null;
					float bollDataDraw[][] = null;
					float obvDataDraw[][] = null;
					float cciDataDraw[][] = null;
					float psyDataDraw[][] = null;
					if (macdData != null) {
						macdDataDraw = new float[3][ls.size() - points];
					}
					if (volData != null) {
						volDataDraw = new float[4][ls.size() - points];
					}
					if (kdjData != null) {
						kdjDataDraw = new float[3][ls.size() - points];
					}
					if (rsiData != null) {
						rsiDataDraw = new float[3][ls.size() - points];
					}
					if (bollData != null) {
						bollDataDraw = new float[3][ls.size() - points];
					}
					if (obvData != null) {
						obvDataDraw = new float[2][ls.size() - points];
					}
					if (cciData != null) {
						cciDataDraw = new float[2][ls.size() - points];
					}
					if (psyData != null) {
						psyDataDraw = new float[2][ls.size() - points];
					}
					for (int i = points; i < ls.size(); i++) {
						lsDraw.add(ls.get(i));
						maSet1listDraw.add(maSet1list.get(i));
						maSet2listDraw.add(maSet2list.get(i));
						maSet3listDraw.add(maSet3list.get(i));
						if (macdData != null && macdData[0].length > i) {
							macdDataDraw[0][i - points] = macdData[0][i];
							macdDataDraw[1][i - points] = macdData[1][i];
							macdDataDraw[2][i - points] = macdData[2][i];
						}
						if (volData != null) {
							volDataDraw[0][i - points] = volData[0][i];
							volDataDraw[1][i - points] = volData[1][i];
							volDataDraw[2][i - points] = volData[2][i];
							volDataDraw[3][i - points] = volData[3][i];
						}
						if (kdjData != null) {
							kdjDataDraw[0][i - points] = kdjData[0][i];
							kdjDataDraw[1][i - points] = kdjData[1][i];
							kdjDataDraw[2][i - points] = kdjData[2][i];
						}
						if (rsiData != null) {
							rsiDataDraw[0][i - points] = rsiData[0][i];
							rsiDataDraw[1][i - points] = rsiData[1][i];
							rsiDataDraw[2][i - points] = rsiData[2][i];
						}
						if (bollData != null) {
							bollDataDraw[0][i - points] = bollData[0][i];
							bollDataDraw[1][i - points] = bollData[1][i];
							bollDataDraw[2][i - points] = bollData[2][i];
						}
						if (obvData != null) {
							obvDataDraw[0][i - points] = obvData[0][i];
							obvDataDraw[1][i - points] = obvData[1][i];
						}
						if (cciData != null) {
							cciDataDraw[0][i - points] = cciData[0][i];
							cciDataDraw[1][i - points] = cciData[1][i];
						}
						if (psyData != null) {
							psyDataDraw[0][i - points] = psyData[0][i];
							psyDataDraw[1][i - points] = psyData[1][i];
						}
					}

					// 决定刻度线的值
					// float qujian = 0l;
					// if ((maxPrice - lastClose) > (lastClose - minPrice)) {
					// qujian = (float) ((maxPrice - lastClose) * 1.5);
					// } else {
					// qujian = (float) ((lastClose - minPrice) * 1.5);
					// }
					// qujian = Math.round(qujian * 100) / 100;
					// if (qujian < 0) {
					// qujian = 0 - qujian;
					// }
					double rul = kHeight / (maxPrice - minPrice);
					// 指标参考值
					float zbrul = 0f;
					// 开始画线，上面是预备参数
					mPaint.setStyle(Style.FILL);

					// 画实线坐标
					mPaint.setColor(nextRed1);
					mPaint.setStrokeWidth(1.0f);
					// 横坐标
					canvas.drawLine(1.1f, maxH, width - rightWhite, maxH, mPaint);
					canvas.drawLine(1.1f, 1.1f, width - rightWhite, 1.1f, mPaint);
					canvas.drawLine(1.1f, topWhite, width - rightWhite, topWhite, mPaint);
					// mid
					canvas.drawLine(1.1f, topWhite + kHeight, width - rightWhite, topWhite + kHeight, mPaint);
					canvas.drawLine(1.1f, topWhite + kHeight + midHeight, width - rightWhite, topWhite + kHeight + midHeight,
							mPaint);
					// 纵坐标
					canvas.drawLine(1.1f, maxH, 1.1f, 1.1f, mPaint);
					canvas.drawLine(width - rightWhite, maxH, width - rightWhite, 1.1f, mPaint);
					canvas.drawLine(leftWhite, maxH, leftWhite, topWhite + kHeight + midHeight, mPaint);
					canvas.drawLine(leftWhite, topWhite + kHeight, leftWhite, topWhite, mPaint);

					// K线 中值水平线
					mPaint.setStyle(Style.STROKE);
					mPaint.setColor(nextRed3);
					PathEffect effects = new DashPathEffect(new float[] { 4, 6, 4, 6 }, 1);
					mPaint.setPathEffect(effects);
					mPaint.setStrokeWidth(2.0f);
					Path path;
					for (int i = 1; i < 4; i++) {
						// canvas.drawLine(leftWhite, topWhite + kHeight * i /
						// 4, width - rightWhite, topWhite + kHeight * i / 4,
						// mPaint);
						path = new Path();
						path.moveTo(leftWhite, topWhite + kHeight * i / 4);
						path.lineTo(width - rightWhite, topWhite + kHeight * i / 4);
						canvas.drawPath(path, mPaint);
					}
					mPaint.setAntiAlias(true);
					mPaint.setStyle(Style.FILL);
					mPaint.setPathEffect(null);
					mPaint.setStrokeWidth(1.0f);
					mPaint.setTextSize(18);
					mPaint.setTextAlign(Paint.Align.RIGHT);
					// 标记K线 图左侧 数值刻度
					mPaint.setColor(Color.RED);
					DecimalFormat dfforview;
					if (Const.WH4.equals(ex) && !Const.USD.equals(code) && !Const.USDJPY.equals(code)) {
						dfforview = df4;
					} else {
						dfforview = df2;
					}
					canvas.drawText(String.valueOf(dfforview.format(maxPrice)), leftWhite, topWhite + 15, mPaint);
					canvas.drawText(String.valueOf(dfforview.format(maxPrice - (maxPrice - minPrice) / 4)), leftWhite, topWhite
							+ 8 + kHeight * 1 / 4, mPaint);
					mPaint.setColor(Color.WHITE);
					canvas.drawText(String.valueOf(dfforview.format((maxPrice + minPrice) / 2)), leftWhite, topWhite + 6
							+ kHeight * 1 / 2, mPaint);
					mPaint.setColor(Color.GREEN);
					canvas.drawText(String.valueOf(dfforview.format(minPrice + (maxPrice - minPrice) / 4)), leftWhite, topWhite
							+ 6 + kHeight * 3 / 4, mPaint);
					canvas.drawText(String.valueOf(dfforview.format(minPrice)), leftWhite, topWhite - 5 + kHeight, mPaint);

					// moveDate(canvas, lsDraw);
					// canvas.drawText(stockName, leftWhite, topWhite - 3,
					// mPaint);
					// canvas.drawText(beginDay, leftWhite+1,
					// maxH-tradeHeight-2,
					// mPaint);
					// canvas.drawText(endDay, width-rightWhite-64,
					// maxH-tradeHeight-2,
					// mPaint);
					if (indexMA) {
						// MA数值
						mPaint.setTextAlign(Paint.Align.LEFT);
						mPaint.setColor(Color.YELLOW);
						canvas.drawText("MA", 2.1f, topWhite - 3, mPaint);
						mPaint.setColor(Color.WHITE);
						canvas.drawText("5=" + String.valueOf(df2.format(maSet1list.get(maSet1list.size() - 1))), 30.1f,
								topWhite - 3, mPaint);
						mPaint.setColor(Color.YELLOW);
						canvas.drawText("10=" + String.valueOf(df2.format(maSet2list.get(maSet2list.size() - 1))), 135.1f,
								topWhite - 3, mPaint);
						mPaint.setColor(Const.MA_ZISE);
						canvas.drawText("20=" + String.valueOf(df2.format(maSet3list.get(maSet3list.size() - 1))), 260.1f,
								topWhite - 3, mPaint);

					} else {
						// BOLL数值
						mPaint.setTextAlign(Paint.Align.LEFT);
						mPaint.setColor(Color.YELLOW);
						canvas.drawText("BOLL", 2.1f, topWhite - 3, mPaint);
						mPaint.setColor(Color.WHITE);
						canvas.drawText("MID=" + String.valueOf(util.formatFloat00((bollDataDraw[0][lsDraw.size() - 1]))), 30.1f,
								topWhite - 3, mPaint);
						mPaint.setColor(Color.YELLOW);
						canvas.drawText("UPPER=" + String.valueOf(util.formatFloat00((bollDataDraw[1][lsDraw.size() - 1]))),
								135.1f, topWhite - 3, mPaint);
						mPaint.setColor(Const.MA_ZISE);
						canvas.drawText("LOWER=" + String.valueOf(util.formatFloat00((bollDataDraw[2][lsDraw.size() - 1]))),
								260.1f, topWhite - 3, mPaint);
					}
					int l = lsDraw.size();
					float lowMacd = 0;
					// 画MACD指标
					if (Const.INDEX_MACD.equals(index)) {
						zbrul = tradeHeight / (maxZB - minZB);

						if (Math.abs(maxZB) > Math.abs(minZB)) {
							zbrul = tradeHeight / (maxZB * 2);
							if (maxZB > 0) {
								lowMacd = 0 - maxZB;
							} else {
								lowMacd = maxZB;
							}
						} else {
							zbrul = tradeHeight / (minZB * 2);
							if (minZB > 0) {
								lowMacd = 0 - minZB;
							} else {
								lowMacd = minZB;
							}
						}
						zbrul = Math.abs(zbrul);
						// macd 指标
						mPaint.setColor(Color.YELLOW);
						mPaint.setTextAlign(Paint.Align.RIGHT);
						canvas.drawText(String.valueOf(util.formatFloat00(maxZB)), leftWhite, maxH - tradeHeight + 15, mPaint);
						canvas.drawText(String.valueOf(util.formatFloat00(minZB)), leftWhite, maxH - 5, mPaint);
						mPaint.setTextAlign(Paint.Align.LEFT);
						canvas.drawText("MACD", 2.1f, maxH - tradeHeight - 5, mPaint);
						mPaint.setColor(Color.YELLOW);
						canvas.drawText("DIF=" + String.valueOf(util.formatFloat00((macdDataDraw[0][lsDraw.size() - 1]))), 60.1f,
								maxH - tradeHeight - 5, mPaint);
						mPaint.setColor(Const.MA_ZISE);
						canvas.drawText("DEA=" + String.valueOf(util.formatFloat00(macdDataDraw[1][lsDraw.size() - 1])), 150.1f,
								maxH - tradeHeight - 5, mPaint);
						mPaint.setColor(Color.GREEN);
						canvas.drawText("MACD=" + String.valueOf(util.formatFloat00(macdDataDraw[2][lsDraw.size() - 1])), 240.1f,
								maxH - tradeHeight - 5, mPaint);

					}
					// 画VOL指标
					if (Const.INDEX_VOL.equals(index)) {
						zbrul = tradeHeight / maxZB;
						// vol刻度
						mPaint.setColor(Color.YELLOW);
						mPaint.setTextAlign(Paint.Align.RIGHT);
						canvas.drawText(String.valueOf(util.formatFloat00(maxZB)), leftWhite, maxH - tradeHeight + 15, mPaint);
						canvas.drawText("0", leftWhite, maxH - 5, mPaint);
						mPaint.setTextAlign(Paint.Align.LEFT);
						canvas.drawText("VOL", 2.1f, maxH - tradeHeight - 5, mPaint);
						mPaint.setColor(Color.WHITE);
						canvas.drawText("MA1=" + String.valueOf(util.formatFloat00((volDataDraw[0][lsDraw.size() - 1]))), 50.1f,
								maxH - tradeHeight - 5, mPaint);
						mPaint.setColor(Color.YELLOW);
						canvas.drawText("MA2=" + String.valueOf(util.formatFloat00(volDataDraw[1][lsDraw.size() - 1])), 165.1f,
								maxH - tradeHeight - 5, mPaint);
						mPaint.setColor(Const.MA_ZISE);
						canvas.drawText("MA3=" + String.valueOf(util.formatFloat00(volDataDraw[2][lsDraw.size() - 1])), 280.1f,
								maxH - tradeHeight - 5, mPaint);

					}
					// 画KDJ指标
					if (Const.INDEX_KDJ.equals(index)) {
						zbrul = tradeHeight / (maxZB - minZB);
						zbrul = Math.abs(zbrul);
						// DKJ刻度
						mPaint.setColor(Color.YELLOW);
						mPaint.setTextAlign(Paint.Align.RIGHT);
						canvas.drawText(String.valueOf(util.formatFloat00(maxZB)), leftWhite, maxH - tradeHeight + 15, mPaint);
						canvas.drawText(String.valueOf(util.formatFloat00(minZB)), leftWhite, maxH - 5, mPaint);
						mPaint.setTextAlign(Paint.Align.LEFT);
						canvas.drawText("KDJ", 2.1f, maxH - tradeHeight - 5, mPaint);
						mPaint.setColor(Color.WHITE);
						canvas.drawText("K=" + String.valueOf(util.formatFloat00((kdjDataDraw[0][lsDraw.size() - 1]))), 50.1f,
								maxH - tradeHeight - 5, mPaint);
						mPaint.setColor(Color.YELLOW);
						canvas.drawText("D=" + String.valueOf(util.formatFloat00(kdjDataDraw[1][lsDraw.size() - 1])), 165.1f,
								maxH - tradeHeight - 5, mPaint);
						mPaint.setColor(Const.MA_ZISE);
						canvas.drawText("J=" + String.valueOf(util.formatFloat00(kdjDataDraw[2][lsDraw.size() - 1])), 280.1f,
								maxH - tradeHeight - 5, mPaint);
					}

					// 画RSI指标
					if (Const.INDEX_RSI.equals(index)) {
						zbrul = tradeHeight / (maxZB - minZB);
						zbrul = Math.abs(zbrul);
						// RSI刻度
						mPaint.setColor(Color.YELLOW);
						mPaint.setTextAlign(Paint.Align.RIGHT);
						canvas.drawText(String.valueOf(util.formatFloat00(maxZB)), leftWhite, maxH - tradeHeight + 15, mPaint);
						canvas.drawText(String.valueOf(util.formatFloat00(minZB)), leftWhite, maxH - 5, mPaint);
						mPaint.setTextAlign(Paint.Align.LEFT);
						canvas.drawText("RSI", 2.1f, maxH - tradeHeight - 5, mPaint);
						mPaint.setColor(Color.WHITE);
						canvas.drawText("RSI1=" + String.valueOf(util.formatFloat00((rsiDataDraw[0][lsDraw.size() - 1]))), 50.1f,
								maxH - tradeHeight - 5, mPaint);
						mPaint.setColor(Color.YELLOW);
						canvas.drawText("RSI2=" + String.valueOf(util.formatFloat00(rsiDataDraw[1][lsDraw.size() - 1])), 165.1f,
								maxH - tradeHeight - 5, mPaint);
						mPaint.setColor(Const.MA_ZISE);
						canvas.drawText("RSI3=" + String.valueOf(util.formatFloat00(rsiDataDraw[2][lsDraw.size() - 1])), 280.1f,
								maxH - tradeHeight - 5, mPaint);
					}
					// 画OBV指标
					if (Const.INDEX_OBV.equals(index)) {
						zbrul = tradeHeight / (maxZB - minZB);
						zbrul = Math.abs(zbrul);
						// OBV刻度
						mPaint.setColor(Color.YELLOW);
						mPaint.setTextAlign(Paint.Align.RIGHT);
						canvas.drawText(String.valueOf(util.formatFloat00(maxZB)), leftWhite, maxH - tradeHeight + 15, mPaint);
						canvas.drawText(String.valueOf(util.formatFloat00(minZB)), leftWhite, maxH - 5, mPaint);
						mPaint.setTextAlign(Paint.Align.LEFT);
						canvas.drawText("OBV", 2.1f, maxH - tradeHeight - 5, mPaint);
						canvas.drawText(String.valueOf(util.formatFloat00((obvDataDraw[0][lsDraw.size() - 1]))), 50.1f, maxH
								- tradeHeight - 5, mPaint);
					}
					// 画CCI指标
					if (Const.INDEX_CCI.equals(index)) {
						zbrul = tradeHeight / (maxZB - minZB);
						zbrul = Math.abs(zbrul);
						// CCI刻度
						mPaint.setColor(Color.YELLOW);
						mPaint.setTextAlign(Paint.Align.RIGHT);
						canvas.drawText(String.valueOf(util.formatFloat00(maxZB)), leftWhite, maxH - tradeHeight + 15, mPaint);
						canvas.drawText(String.valueOf(util.formatFloat00(minZB)), leftWhite, maxH - 5, mPaint);
						mPaint.setTextAlign(Paint.Align.LEFT);
						canvas.drawText("CCI", 2.1f, maxH - tradeHeight - 5, mPaint);
						canvas.drawText(String.valueOf(util.formatFloat00((cciDataDraw[0][lsDraw.size() - 1]))), 50.1f, maxH
								- tradeHeight - 5, mPaint);
					}
					// 画PSY指标
					if (Const.INDEX_PSY.equals(index)) {
						zbrul = tradeHeight / (maxZB - minZB);
						zbrul = Math.abs(zbrul);
						// CCI刻度
						mPaint.setColor(Color.YELLOW);
						mPaint.setTextAlign(Paint.Align.RIGHT);
						canvas.drawText(String.valueOf(util.formatFloat00(maxZB)), leftWhite, maxH - tradeHeight + 15, mPaint);
						canvas.drawText(String.valueOf(util.formatFloat00(minZB)), leftWhite, maxH - 5, mPaint);
						mPaint.setTextAlign(Paint.Align.LEFT);
						canvas.drawText("PSY", 2.1f, maxH - tradeHeight - 5, mPaint);
						canvas.drawText(String.valueOf(util.formatFloat00((psyDataDraw[0][lsDraw.size() - 1]))), 50.1f, maxH
								- tradeHeight - 5, mPaint);
					}
					for (int i = 0; i < l; i++) {
						KData s = lsDraw.get(i);
						float open = util.getFloat(s.getK_open());
						float close = util.getFloat(s.getK_close());
						if (open - close < 0) {
							mPaint.setColor(Color.RED);
						} else {
							mPaint.setColor(Const.K_DOWN);
						}

						mPaint.setStrokeWidth(1);
						canvas.drawLine((float) (interval * i + interval / 2 + leftWhite),
								(float) (kHeight + topWhite - (util.getFloat(s.getK_high()) - minPrice) * rul), (float) (interval
										* i + interval / 2 + leftWhite),
								(float) (kHeight + topWhite - (util.getFloat(s.getK_low()) - minPrice) * rul), mPaint);
						mPaint.setStrokeWidth(interval - 2);
						canvas.drawLine((float) (interval * i + interval / 2 + leftWhite),
								(float) (kHeight + topWhite - (util.getFloat(s.getK_open()) - minPrice) * rul), (float) (interval
										* i + interval / 2 + leftWhite),
								(float) (kHeight + topWhite - (util.getFloat(s.getK_close()) - minPrice) * rul), mPaint);

						// 画MACD指标
						if (Const.INDEX_MACD.equals(index)) {
							// 画macd
							mPaint.setStrokeWidth(1);
							if (macdDataDraw[2][i] < 0) {
								mPaint.setColor(Color.GREEN);
							} else {
								mPaint.setColor(Color.RED);
							}
							canvas.drawLine((float) (interval * i + interval / 2 + leftWhite), maxH - tradeHeight / 2,
									(float) (interval * i + interval / 2 + leftWhite), maxH - tradeHeight / 2 - 2
											* (macdDataDraw[2][i]) * zbrul, mPaint);
							mPaint.setStrokeWidth(interval - 2);
							mPaint.setColor(Color.WHITE);
							canvas.drawLine((float) (interval * i + interval / 2 + leftWhite),
									(float) ((maxH - tradeHeight / 2) - 0.5), (float) (interval * i + interval / 2 + leftWhite),
									(float) ((maxH - tradeHeight / 2) + 0.5), mPaint);

							mPaint.setStrokeWidth(1);
							if (i > 0) {
								mPaint.setColor(Color.YELLOW);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
										(float) (maxH - (macdDataDraw[0][i - 1] - lowMacd) * zbrul), (float) (interval * i
												+ interval / 2 + leftWhite), (float) (maxH - (macdDataDraw[0][i] - lowMacd)
												* zbrul), mPaint);
								mPaint.setColor(Const.MA_ZISE);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
										(float) (maxH - (macdDataDraw[1][i - 1] - lowMacd) * zbrul), (float) (interval * i
												+ interval / 2 + leftWhite), (float) (maxH - (macdDataDraw[1][i] - lowMacd)
												* zbrul), mPaint);

							}

						}
						// 画VOL指标
						if (Const.INDEX_VOL.equals(index)) {
							// 画vol
							if (open - close < 0) {
								mPaint.setColor(Color.RED);
							} else {
								mPaint.setColor(Const.K_DOWN);
							}
							mPaint.setStrokeWidth(interval - 2);
							canvas.drawLine((float) (interval * i + interval / 2 + leftWhite), maxH, (float) (interval * i
									+ interval / 2 + leftWhite), maxH - volDataDraw[3][i] * zbrul, mPaint);
							if (open - close < 0) {
								mPaint.setStrokeWidth(interval - 4);
								mPaint.setColor(Color.BLACK);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite), (float) maxH - 1,
										(float) (interval * i + interval / 2 + leftWhite), (float) maxH - volDataDraw[3][i]
												* zbrul + 1, mPaint);
							}
							mPaint.setStrokeWidth(1);
							if (i > 0) {
								mPaint.setColor(Color.WHITE);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
										(float) (maxH - (volDataDraw[0][i - 1]) * zbrul),
										(float) (interval * i + interval / 2 + leftWhite), (float) (maxH - (volDataDraw[0][i])
												* zbrul), mPaint);
								mPaint.setColor(Color.YELLOW);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
										(float) (maxH - (volDataDraw[1][i - 1]) * zbrul),
										(float) (interval * i + interval / 2 + leftWhite), (float) (maxH - (volDataDraw[1][i])
												* zbrul), mPaint);
								mPaint.setColor(Const.MA_ZISE);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
										(float) (maxH - (volDataDraw[2][i - 1]) * zbrul),
										(float) (interval * i + interval / 2 + leftWhite), (float) (maxH - (volDataDraw[2][i])
												* zbrul), mPaint);
							}

						}
						// 画KDJ指标
						if (Const.INDEX_KDJ.equals(index)) {
							mPaint.setStrokeWidth(1);
							if (i > 0) {
								mPaint.setColor(Color.WHITE);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
										(float) (maxH - (kdjDataDraw[0][i - 1] - minZB) * zbrul), (float) (interval * i
												+ interval / 2 + leftWhite),
										(float) (maxH - (kdjDataDraw[0][i] - minZB) * zbrul), mPaint);
								mPaint.setColor(Color.YELLOW);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
										(float) (maxH - (kdjDataDraw[1][i - 1] - minZB) * zbrul), (float) (interval * i
												+ interval / 2 + leftWhite),
										(float) (maxH - (kdjDataDraw[1][i] - minZB) * zbrul), mPaint);
								mPaint.setColor(Const.MA_ZISE);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
										(float) (maxH - (kdjDataDraw[2][i - 1] - minZB) * zbrul), (float) (interval * i
												+ interval / 2 + leftWhite),
										(float) (maxH - (kdjDataDraw[2][i] - minZB) * zbrul), mPaint);
							}
						}
						// 画RSI指标
						if (Const.INDEX_RSI.equals(index)) {
							mPaint.setStrokeWidth(1);
							if (i > 0) {
								mPaint.setColor(Color.WHITE);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
										(float) (maxH - (rsiDataDraw[0][i - 1] - minZB) * zbrul), (float) (interval * i
												+ interval / 2 + leftWhite),
										(float) (maxH - (rsiDataDraw[0][i] - minZB) * zbrul), mPaint);
								mPaint.setColor(Color.YELLOW);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
										(float) (maxH - (rsiDataDraw[1][i - 1] - minZB) * zbrul), (float) (interval * i
												+ interval / 2 + leftWhite),
										(float) (maxH - (rsiDataDraw[1][i] - minZB) * zbrul), mPaint);
								mPaint.setColor(Const.MA_ZISE);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
										(float) (maxH - (rsiDataDraw[2][i - 1] - minZB) * zbrul), (float) (interval * i
												+ interval / 2 + leftWhite),
										(float) (maxH - (rsiDataDraw[2][i] - minZB) * zbrul), mPaint);
							}
						}
						if (Const.INDEX_OBV.equals(index)) {
							mPaint.setStrokeWidth(1);
							if (i > 0) {
								mPaint.setColor(Color.YELLOW);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
										(float) (maxH - (obvDataDraw[0][i - 1] - minZB) * zbrul), (float) (interval * i
												+ interval / 2 + leftWhite),
										(float) (maxH - (obvDataDraw[0][i] - minZB) * zbrul), mPaint);

							}
						}
						if (Const.INDEX_CCI.equals(index)) {
							mPaint.setStrokeWidth(1);
							if (i > 0) {
								mPaint.setColor(Color.YELLOW);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
										(float) (maxH - (cciDataDraw[0][i - 1] - minZB) * zbrul), (float) (interval * i
												+ interval / 2 + leftWhite),
										(float) (maxH - (cciDataDraw[0][i] - minZB) * zbrul), mPaint);

							}
						}
						if (Const.INDEX_PSY.equals(index)) {
							mPaint.setStrokeWidth(1);
							if (i > 0) {
								mPaint.setColor(Color.YELLOW);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
										(float) (maxH - (psyDataDraw[0][i - 1] - minZB) * zbrul), (float) (interval * i
												+ interval / 2 + leftWhite),
										(float) (maxH - (psyDataDraw[0][i] - minZB) * zbrul), mPaint);

							}
						}
						if (open - close < 0) {
							mPaint.setStrokeWidth(interval - 4);
							mPaint.setColor(Color.BLACK);
							canvas.drawLine((float) (interval * i + interval / 2 + leftWhite),
									(float) (kHeight + topWhite - (util.getFloat(s.getK_open()) - minPrice) * rul) - 1,
									(float) (interval * i + interval / 2 + leftWhite),
									(float) (kHeight + topWhite - (util.getFloat(s.getK_close()) - minPrice) * rul) + 1, mPaint);

							// 暂时不美化 // 交易量
							// canvas.drawLine(
							// (float) (interval * i + interval / 2 +
							// leftWhite),
							// maxH - 1,
							// (float) (interval * i + interval / 2 +
							// leftWhite),
							// maxH - util.getFloat(lsDraw.get(i).getK_total())
							// * trul
							// + 1, mPaint);
						}

						if (i > 0) {
							if (indexMA) {
								if (maSet1list.size() > 20 && maSet2list.size() > 20 && maSet3list.size() > 20) {
									mPaint.setStrokeWidth(1);
									// ma5
									mPaint.setStyle(Paint.Style.FILL);
									mPaint.setColor(Color.WHITE);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
											(float) (kHeight + topWhite - (maSet1listDraw.get(i - 1) - minPrice) * rul),
											(float) (interval * i + interval / 2 + leftWhite),
											(float) (kHeight + topWhite - (maSet1listDraw.get(i) - minPrice) * rul), mPaint);
									mPaint.setColor(Color.YELLOW);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
											(float) (kHeight + topWhite - (maSet2listDraw.get(i - 1) - minPrice) * rul),
											(float) (interval * i + interval / 2 + leftWhite),
											(float) (kHeight + topWhite - (maSet2listDraw.get(i) - minPrice) * rul), mPaint);
									mPaint.setColor(Const.MA_ZISE);
									canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
											(float) (kHeight + topWhite - (maSet3listDraw.get(i - 1) - minPrice) * rul),
											(float) (interval * i + interval / 2 + leftWhite),
											(float) (kHeight + topWhite - (maSet3listDraw.get(i) - minPrice) * rul), mPaint);
								}
							} else {

								mPaint.setStrokeWidth(1);
								// ma5
								mPaint.setStyle(Paint.Style.FILL);
								mPaint.setColor(Color.WHITE);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (kHeight
										+ topWhite - (bollDataDraw[0][i - 1] - minPrice) * rul), (float) (interval * i + interval
										/ 2 + leftWhite), (float) (kHeight + topWhite - (bollDataDraw[0][i] - minPrice) * rul),
										mPaint);
								mPaint.setColor(Color.YELLOW);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (kHeight
										+ topWhite - (bollDataDraw[1][i - 1] - minPrice) * rul), (float) (interval * i + interval
										/ 2 + leftWhite), (float) (kHeight + topWhite - (bollDataDraw[1][i] - minPrice) * rul),
										mPaint);
								mPaint.setColor(Const.MA_ZISE);
								canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval, (float) (kHeight
										+ topWhite - (bollDataDraw[2][i - 1] - minPrice) * rul), (float) (interval * i + interval
										/ 2 + leftWhite), (float) (kHeight + topWhite - (bollDataDraw[2][i] - minPrice) * rul),
										mPaint);

							}

						}

					}
					mPaint.setStyle(Paint.Style.FILL);
					// drawTitle(canvas);
					moveLine(canvas, lsDraw);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				mPaint.setStyle(Style.FILL);

				// 画实线坐标
				mPaint.setColor(nextRed1);
				mPaint.setStrokeWidth(1.0f);
				// 横坐标
				canvas.drawLine(1.1f, maxH, width - rightWhite, maxH, mPaint);
				canvas.drawLine(1.1f, 1.1f, width - rightWhite, 1.1f, mPaint);
				canvas.drawLine(1.1f, topWhite, width - rightWhite, topWhite, mPaint);
				// mid
				canvas.drawLine(1.1f, topWhite + kHeight, width - rightWhite, topWhite + kHeight, mPaint);
				canvas.drawLine(1.1f, topWhite + kHeight + midHeight, width - rightWhite, topWhite + kHeight + midHeight, mPaint);
				// 纵坐标
				canvas.drawLine(1.1f, maxH, 1.1f, 1.1f, mPaint);
				canvas.drawLine(width - rightWhite, maxH, width - rightWhite, 1.1f, mPaint);
				canvas.drawLine(leftWhite, maxH, leftWhite, topWhite + kHeight + midHeight, mPaint);
				canvas.drawLine(leftWhite, topWhite + kHeight, leftWhite, topWhite, mPaint);

				// K线 中值水平线
				mPaint.setStyle(Style.STROKE);
				mPaint.setColor(nextRed3);
				PathEffect effects = new DashPathEffect(new float[] { 6, 8, 6, 8 }, 1);
				mPaint.setPathEffect(effects);
				mPaint.setStrokeWidth(2.0f);
				Path path;
				for (int i = 1; i < 4; i++) {
					path = new Path();
					path.moveTo(leftWhite, topWhite + kHeight * i / 4);
					path.lineTo(width - rightWhite, topWhite + kHeight * i / 4);
					canvas.drawPath(path, mPaint);
				}
				mPaint.setStyle(Style.FILL);
				mPaint.setPathEffect(null);
				mPaint.setStrokeWidth(1.0f);
				mPaint.setTextSize(18);
				mPaint.setTextAlign(Paint.Align.RIGHT);
				// 标记K线 图左侧 数值刻度
				mPaint.setColor(Color.RED);
				canvas.drawText("0.00", leftWhite, topWhite + 15, mPaint);
				canvas.drawText("0.00", leftWhite, topWhite + 8 + kHeight * 1 / 4, mPaint);
				mPaint.setColor(Color.WHITE);
				canvas.drawText("0.00", leftWhite, topWhite + 6 + kHeight * 1 / 2, mPaint);
				mPaint.setColor(Color.GREEN);
				canvas.drawText("0.00", leftWhite, topWhite + 6 + kHeight * 3 / 4, mPaint);
				canvas.drawText("0.00", leftWhite, topWhite - 5 + kHeight, mPaint);
			}
			mPaint.setAntiAlias(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 移动标记线
	public void moveLine(Canvas canvas, List<KData> ls) {
		// Log.i("temp", "x->ls.size()->" + ls.size());
		if (n <= ls.size() - 1 && drawLineCount > 0) {
			drawLineCount--;
			mPaint.setColor(Color.WHITE);
			canvas.drawLine(x, this.topWhite, x, kHeight + topWhite, mPaint);
			// canvas.drawLine(x, this.maxH - this.tradeHeight, x, this.maxH,
			// mPaint);
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(Color.BLACK);
			mPaint.setStrokeWidth(1.0f);
			mPaint.setTextSize(22);
			mPaint.setTextAlign(Paint.Align.LEFT);
			float textX = 0f;
			if (n < ls.size() / 2) {
				canvas.drawRect(new RectF(width - 150, topWhite, width, topWhite + 150), mPaint);
				mPaint.setStyle(Style.STROKE);
				mPaint.setColor(Color.RED);
				canvas.drawRect(new RectF(width - 150, topWhite, width, topWhite + 150), mPaint);
				textX = width - 146;
			} else {
				canvas.drawRect(new RectF(leftWhite, topWhite, leftWhite + 150, topWhite + 150), mPaint);
				mPaint.setStyle(Style.STROKE);
				mPaint.setColor(Color.RED);
				canvas.drawRect(new RectF(leftWhite, topWhite, leftWhite + 150, topWhite + 150), mPaint);
				textX = leftWhite + 4;
			}
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(Color.WHITE);
			mPaint.setTextSize(22);
			String[] time = ls.get(n).getK_date().split(" ");
			String[] date = time[0].split("/");
			// String[] t = time[1].split(":");
			//Log.i("temp", "ls.get(n).getK_date()" + ls.get(n).getK_date());
			//Log.i("temp", time.length + "");
			//Log.i("temp", time[0]);
			//Log.i("temp", time[1]);

			if ("00:00:00".equals(time[1])) {
				canvas.drawText("时:" + date[2] + date[0] + date[1], textX, topWhite * 2 + 10, mPaint);
			} else {
				canvas.drawText("时:" + time[1], textX, topWhite * 2 + 10, mPaint);
			}

			canvas.drawText("开:" + String.valueOf(df_base.format(Float.parseFloat(ls.get(n).getK_open()))), textX,
					topWhite * 2 + 35, mPaint);
			canvas.drawText("高:" + String.valueOf(df_base.format(Float.parseFloat(ls.get(n).getK_high()))), textX,
					topWhite * 2 + 60, mPaint);
			canvas.drawText("低:" + String.valueOf(df_base.format(Float.parseFloat(ls.get(n).getK_low()))), textX,
					topWhite * 2 + 85, mPaint);
			canvas.drawText("收:" + String.valueOf(df_base.format(Float.parseFloat(ls.get(n).getK_close()))), textX,
					topWhite * 2 + 110, mPaint);

		}

	}

	// // 移动显示日期
	// public void moveDate(Canvas canvas, List<KData> ls) {
	// if (x != -1) {
	// float dx = leftWhite;
	// if (x >= tableWidth / 2 + leftWhite)
	// dx = x - this.interval / 2 - 55;
	// else
	// dx = x - this.interval / 2;
	// if (n < ls.size() - 1) {
	// String date = ls.get(n).getK_date();
	// // String time = date.substring(date.length() - 6);
	// String show = date + "   开：" + ls.get(n).getK_open() + "   收："
	// + ls.get(n).getK_close() + "   高："
	// + ls.get(n).getK_high() + "   低："
	// + ls.get(n).getK_low();
	// canvas.drawText(show, leftWhite + 1, maxH - tradeHeight - 3,
	// mPaint);
	// }
	// }
	// }

	public void setX(float x) {
		this.x = x;

	}

	public float getX() {
		return this.x;
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			postInvalidate();
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/* 取得手指触控屏幕的位置 */
		float x = event.getX();
		//Log.i("temp", "x->" + x);
		try {
			/* 触控事件的处理 */
			switch (event.getAction()) {
			/* 移动位置 */
			case MotionEvent.ACTION_MOVE:
				drawLineCount = 30;
				if (x - this.leftWhite - interval / 2 <= 0) {
					x = this.leftWhite + interval / 2;
				} else if (x > this.width - this.rightWhite - interval / 2) {
					x = this.width - this.rightWhite - interval / 2;
				} else {

				}

				n = (int) ((x - leftWhite) / interval);
				x = this.leftWhite + n * interval + interval / 2;
				setN(n);

				setX(x);
				break;
			// case MotionEvent.ACTION_DOWN:
			// Log.i("temp", "x->ACTION_DOWN");
			// drawLineCount = 30;
			// if (x - this.leftWhite - interval / 2 <= 0) {
			// x = this.leftWhite + interval / 2;
			// Log.i("temp", "x-><=0" + x);
			// } else if (x > this.width - this.rightWhite - interval / 2) {
			// x = this.width - this.rightWhite - interval / 2;
			// Log.i("temp", "x->>" + x);
			// } else {
			// n = (int) ((x - leftWhite) / interval);
			// x = this.leftWhite + n * interval + interval / 2;
			// setN(n);
			// Log.i("temp", "x->else->" + x);
			// }
			// Log.i("temp", "x->setX->" + x);
			// setX(x);
			// break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;

	}

	public void run() {

		while (!Thread.currentThread().isInterrupted()) {

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Thread.currentThread().interrupt();
			}

			this.postInvalidate();

		}

	}
}
