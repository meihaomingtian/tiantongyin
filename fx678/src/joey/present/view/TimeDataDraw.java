package joey.present.view;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import joey.present.data.KData;
import joey.present.data.PriceData;
import joey.present.util.Const;
import joey.present.util.DensityUtil;
import joey.present.util.Util;
import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TimeDataDraw extends View {

	private Util util = new Util();
	private String ex = Const.SHGOLD1;
	private String code;
	private boolean initFlag = false;

	public boolean isInitFlag() {
		return initFlag;
	}

	public void setInitFlag(boolean initFlag) {
		this.initFlag = initFlag;
	}

	private String timePeriod = "";
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
	private int points = 38;// 点数
	private float maxPrice = 0; // 最高价
	private float minPrice = 1000000;// 最低价
	private float lastClose = 0;// 最后收盘价
	private KData lastUpdateData = null;
	private String stockName;// 股票名称 K线title
	private float tradeHeight;// 交易量高度
	private float maxTradeVolume = 0;// 最大交易量
	private String beginDay;// 起始日期
	private String endDay;// 结束日期
	private DecimalFormat df2 = new DecimalFormat("0.00");
	private DecimalFormat df4 = new DecimalFormat("0.0000");
	private DecimalFormat df0 = new DecimalFormat("");
	public float x = -1;// 指示线的横坐标位置 初始化时不显示
	public List<KData> ls = null;
	public List<Float> shareAverage = null;
	private int n = 1;// 第N点的数据
	private float kButton;// k线下边线坐标
	private float qujian;

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	// private float kRight;// k线右边线坐标
	Context context;

	public TimeDataDraw(Context context, float width, float height, String ex) {
		super(context);
		this.context = context;
		mPaint = new Paint();
		this.width = width + 0.1f;
		this.height = height + 0.1f;
		maxH = height - 20.1f;
		leftWhite = 80.1f;
		rightWhite = 1.1f;
		topWhite = 15.1f;
		kHeight = (maxH - topWhite) * 0.7f;
		tradeHeight = maxH - kHeight - topWhite;
		tableWidth = width - rightWhite - leftWhite;
		this.ex = ex;
		// this.setBackgroundColor(Color.BLACK);
		// new Thread(this).start();
	}

	public void setLastClose(float lastclose) {
		lastClose = lastclose;
	}

	public void setCode(String code) {
		this.code = code;
		if (Const.SHGOLD1.equals(ex)) {
			points = 600;
			timePeriod = Const.SHGOLD_PERIOD;
		} else if (Const.HJXH2.equals(ex)) {
			points = 288;
			timePeriod = Const.HJXH_PERIOD;
		} else if (Const.STOCKINDEX3.equals(ex)) {
			points = 550;
			timePeriod = Const.STOCKINDEX_PERIOD;
		} else if (Const.WH4.equals(ex)) {
			points = 288;
			timePeriod = Const.WH_PERIOD;

		} else if (Const.NYMEX5.equals(ex)) {
			points = 276;
			timePeriod = Const.NYMEX_PERIOD;
		} else if (Const.COMEX6.equals(ex)) {
			points = 286;
			timePeriod = Const.COMEX_PERIOD;
		} else if (Const.IPE7.equals(ex)) {
			points = 144;
			timePeriod = Const.IPE_PERIOD;
		} else if (Const.TTJ8.equals(ex)) {
			points = 264;
			timePeriod = Const.TTJ_PERIOD;
		} else if (Const.SHQH9.equals(ex)) {
			points = 225;
			timePeriod = Const.SHQH_PERIOD;
		} else if (Const.YGY10.equals(ex)) {
			points = 264;
			timePeriod = Const.TTJ_PERIOD;
		} else if (Const.BOCE17.equals(ex)) {
			points = 156;
			timePeriod = Const.BOCE_PERIOD;
		} else if (Const.HXCE13.equals(ex)) {
			points = 288;
			timePeriod = Const.HXCE_PERIOD;
		} else if (Const.HF16.equals(ex)) {
			points = 264;
			timePeriod = Const.HF_PERIOD;
		} else if (Const.QDCE15.equals(ex)) {
			points = 264;
			timePeriod = Const.QDCE_PERIOD;
		}else if (Const.BS19.equals(ex)) {
			points = 264;
			timePeriod = Const.BS_PERIOD;
		}

		if (Const.A0001.equals(code) || Const.A01.equals(code)) {
			points = 240;
			timePeriod = Const.CHINA_PERIOD;
		}
		if (Const.AOI.equals(code)) {
			points = 420;
			timePeriod = Const.AOI_PERIOD;
		}
		if (Const.FTSE.equals(code) || Const.DAX.equals(code) || Const.CAC40.equals(code)) {
			points = 600;
			timePeriod = Const.FTSEDAXCAC40_PERIOD;
		}
		if (Const.NASDAQ.equals(code) || Const.DJIA.equals(code) || Const.SP500.equals(code) || Const.CRB.equals(code)) {
			points = 450;
			timePeriod = Const.NASDAQDJIASP500CRB_PERIOD;
		}
		if (Const.NIKKI.equals(code) || Const.KSE.equals(code)) {
			points = 360;
			timePeriod = Const.NIKKIKSE_PERIOD;
		}
		if (Const.STI.equals(code) || Const.KLSE.equals(code)) {
			points = 480;
			timePeriod = Const.STIKLSE_PERIOD;
		}
		if (Const.PCOMP.equals(code)) {
			points = 330;
			timePeriod = Const.PCOMP_PERIOD;
		}
		if (Const.TWI.equals(code)) {
			points = 270;
			timePeriod = Const.TWI_PERIOD;
		}
		if (Const.SET.equals(code)) {
			points = 390;
			timePeriod = Const.SET_PERIOD;
		}
		if (Const.SENSEX.equals(code)) {
			points = 390;
			timePeriod = Const.SENSEX_PERIOD;
		}
	}

	public void updateNew(KData data) {
		if (lastUpdateData != null) {
			String timeLong = lastUpdateData.getK_timeLong();
			String min = util.formatTimeMin(timeLong);
			String newTimeLong = data.getK_timeLong();
			String newMin = util.formatTimeMin(newTimeLong);
			if (newMin.equals(min) || (Long.valueOf(newTimeLong) - Long.valueOf(timeLong)) <= 180) {
				KData dataTmp = ls.get(ls.size() - 1);
				dataTmp.setK_average(data.getK_average());
				dataTmp.setK_close(data.getK_close());
				dataTmp.setK_date(data.getK_date());
				dataTmp.setK_timeLong(data.getK_timeLong());
				dataTmp.setK_high(data.getK_high());
				dataTmp.setK_low(data.getK_low());
				dataTmp.setK_open(data.getK_open());
				dataTmp.setK_volume(data.getK_volume());
			} else {
				ls.add(data);
			}
		} else {
			// ls.add(data);
			// shareAverage.add(util.getFloat(data.getK_average()));
		}
		float totalValue = 0f;
		float totalTrade = 0f;
		shareAverage = new ArrayList<Float>();
		for (int i = 0; i < ls.size(); i++) {
			totalValue = totalValue + util.getFloat(ls.get(i).getK_close()) * util.getFloat(ls.get(i).getK_volume());
			totalTrade = totalTrade + util.getFloat(ls.get(i).getK_volume());
			shareAverage.add(totalValue / totalTrade);
			float tmpMinprice = util.getFloat(ls.get(i).getK_low());
			float tmpMaxprice = util.getFloat(ls.get(i).getK_high());
			float tmpMaxTradeVol = util.getFloat(ls.get(i).getK_volume());
			if (maxPrice < tmpMaxprice) {
				maxPrice = tmpMaxprice;
			}
			if (minPrice > tmpMinprice) {
				minPrice = tmpMinprice;
			}
			if (maxTradeVolume < tmpMaxTradeVol) {
				maxTradeVolume = tmpMaxTradeVol;
			}

		}
		qujian = 0l;
		if ((maxPrice - lastClose) > (lastClose - minPrice)) {
			qujian = (float) ((maxPrice - lastClose));
		} else {
			qujian = (float) ((lastClose - minPrice));
		}
		if (qujian < 0) {
			qujian = 0 - qujian;
		}
		lastUpdateData = data;
		Message msg = handler.obtainMessage();
		handler.dispatchMessage(msg);
	}

	public void updateData(List<KData> data) {
		ls = data;

		shareAverage = new ArrayList<Float>();
		interval = tableWidth / points;
		float totalValue = 0f;
		float totalTrade = 0f;
		for (int i = 0; i < ls.size(); i++) {
			totalValue = totalValue + util.getFloat(ls.get(i).getK_close()) * util.getFloat(ls.get(i).getK_volume());
			totalTrade = totalTrade + util.getFloat(ls.get(i).getK_volume());
			shareAverage.add(totalValue / totalTrade);
			float tmpMinprice = util.getFloat(ls.get(i).getK_low());
			float tmpMaxprice = util.getFloat(ls.get(i).getK_high());
			float tmpMaxTradeVol = util.getFloat(ls.get(i).getK_volume());
			if (maxPrice < tmpMaxprice) {
				maxPrice = tmpMaxprice;
			}
			if (minPrice > tmpMinprice) {
				minPrice = tmpMinprice;
			}
			if (maxTradeVolume < tmpMaxTradeVol) {
				maxTradeVolume = tmpMaxTradeVol;
			}

		}

	}

	// public TimeDataDraw(Context context, float width, float height,
	// List<PriceData> demodata, List<Float> average) {
	// super(context);
	// mPaint = new Paint();
	// shareAverage = average;
	// // ls = demodata;
	// days = ls.size();
	// this.width = width;
	// this.height = height;
	// maxH = height - 10;
	// leftWhite = 10;
	// rightWhite = 40;
	// topWhite = 15;
	// midHeight = 15;
	// kHeight = (maxH - topWhite) * 0.7f;
	// tradeHeight = maxH - kHeight - topWhite - midHeight;
	// tableWidth = width - rightWhite - leftWhite;
	// interval = tableWidth / days;
	// for (int i = 0; i < days; i++) {
	// float tmpMinprice = util.getFloat(ls.get(i).getPrice_low());
	// float tmpMaxprice = util.getFloat(ls.get(i).getPrice_high());
	// float tmpMaxTradeVol = util.getFloat(ls.get(i).getPrice_volume());
	// if (maxPrice < tmpMaxprice) {
	// maxPrice = tmpMaxprice;
	// }
	// if (minPrice > tmpMinprice) {
	// minPrice = tmpMinprice;
	// }
	// if (maxTradeVolume < tmpMaxTradeVol) {
	// maxTradeVolume = tmpMaxTradeVol;
	// }
	//
	// }
	//
	// beginDay = "2010-04-08";
	// endDay = "2010-06-07";
	// stockName = "分时数据";
	//
	// }

	int nextRed1 = Color.argb(255, 100, 0, 0);
	int nextRed2 = Color.argb(255, 100, 0, 0);
	int nextRed3 = Color.argb(255, 120, 0, 0);

	public void draw(Canvas canvas) {
		super.draw(canvas);
		canvas.drawColor(Color.BLACK);

		mPaint.setStyle(Style.FILL);
		mPaint.setStrokeWidth(1.0f);
		mPaint.setColor(nextRed1);
		// 价量分界线
		canvas.drawLine(1.1f, topWhite + kHeight, width - rightWhite, topWhite + kHeight, mPaint);
		// 底边
		canvas.drawLine(1.1f, maxH, width - rightWhite, maxH, mPaint);
		// 上边
		canvas.drawLine(1.1f, topWhite, width - rightWhite, topWhite, mPaint);

		// 纵坐标: 红色纵实线 表格中间竖线（暂时取消）
		canvas.drawLine((width - rightWhite + leftWhite) / 2, maxH, (width - rightWhite + leftWhite) / 2, topWhite, mPaint);
		// 表格左边实线（暂时取消 ）
		canvas.drawLine(1.1f, maxH, 1.1f, topWhite, mPaint);
		// 对应数字右边红色实线（暂时取消）
		canvas.drawLine(leftWhite, maxH, leftWhite, topWhite, mPaint);
		// 表格右边实线（暂时取消 ）
		canvas.drawLine(width - rightWhite, maxH, width - rightWhite, topWhite, mPaint);

		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(2.0f);
		mPaint.setColor(nextRed3);
		PathEffect effects2 = new DashPathEffect(new float[] { 6, 8, 6, 8 }, 1);
		mPaint.setPathEffect(null);
		mPaint.setPathEffect(effects2);
		Path path;
		// 分时线 中值水平线
		for (int i = 1; i < 4; i++) {

			path = new Path();
			path.moveTo(leftWhite, topWhite + kHeight * i / 4);
			path.lineTo(width - rightWhite, topWhite + kHeight * i / 4);
			canvas.drawPath(path, mPaint);
		}

		if (initFlag) {
			// if (false) {

			double rul = kHeight / (qujian * 2);
			float trul = 0;
			if (maxTradeVolume > 0) {
				trul = tradeHeight / maxTradeVolume;
			}

			mPaint.setStyle(Style.FILL);
			// 画实线坐标

			mPaint.setStrokeWidth(0.5f);
			mPaint.setColor(Color.RED);
			// 价量分界线 - 分时表 绿色数字和黄色数字中间那条红色横线(暂时取消)
			// canvas.drawLine(1.1f, topWhite + kHeight, width - rightWhite,
			// topWhite + kHeight, mPaint);
			// 横坐标： 红色横实线 上面那条竖横线（暂时取消）
			// canvas.drawLine(1.1f, maxH, width - rightWhite, maxH, mPaint);
			// canvas.drawLine(1.1f, topWhite, width - rightWhite, topWhite,
			// mPaint);

			// 纵坐标: 红色纵实线 表格中间竖线（暂时取消）
			// canvas.drawLine((width - rightWhite + leftWhite) / 2, maxH,
			// (width
			// - rightWhite + leftWhite) / 2, topWhite, mPaint);
			// 表格左边实线（暂时取消 ）
			// canvas.drawLine(1.1f, maxH, 1.1f, topWhite, mPaint);
			// 对应数字右边红色实线（暂时取消）
			// canvas.drawLine(leftWhite, maxH, leftWhite, topWhite, mPaint);
			// 表格右边实线（暂时取消 ）
			// canvas.drawLine(width - rightWhite, maxH, width - rightWhite,
			// topWhite, mPaint);

			PathEffect effects = new DashPathEffect(new float[] { 6, 8, 6, 8 }, 1);
			mPaint.setPathEffect(effects);
			// 分时线 中值水平线
			for (int i = 1; i < 4; i++) {
				// canvas.drawLine(leftWhite, topWhite + kHeight * i / 4, width
				// - rightWhite, topWhite + kHeight * i / 4, mPaint);
			}
			// 对应高： 额： 开：下面的虚线（暂时取消）
			// canvas.drawLine((width - rightWhite - leftWhite) / 4 + leftWhite,
			// maxH, (width - rightWhite - leftWhite) / 4 + leftWhite,
			// topWhite, mPaint);
			// 对应收： 低： 时： 下面的虚线（暂时取消）
			// canvas.drawLine((width - rightWhite - leftWhite) * 3 / 4
			// + leftWhite, maxH, (width - rightWhite - leftWhite) * 3 / 4
			// + leftWhite, topWhite, mPaint);
			// 交易量中值水平线

			// canvas.drawLine(leftWhite,
			// topWhite + 2 + kHeight + tradeHeight / 2, width
			// - rightWhite, topWhite + 2 + kHeight + tradeHeight
			// / 2, mPaint);
			mPaint.setStyle(Style.FILL);
			mPaint.setPathEffect(null);

			// 分时线和交易量中间的空距
			// mPaint.setColor(Color.DKGRAY);
			// mPaint.setStrokeWidth(13);
			// canvas.drawLine(leftWhite, topWhite + kHeight,
			// width - rightWhite, topWhite + kHeight,
			// mPaint);
			// 标记分时线 图左侧 数值刻度
			mPaint.setAntiAlias(true);
			mPaint.setStrokeWidth(1.0f);
			mPaint.setTextSize(DensityUtil.dip2px(context, 8));
			mPaint.setTextAlign(Paint.Align.RIGHT);
			DecimalFormat dfforview;
			if (Const.WH4.equals(ex) && !Const.USD.equals(code) && !Const.USDJPY.equals(code)) {
				dfforview = df4;
			} else {
				dfforview = df2;
			}
			canvas.drawText(String.valueOf(dfforview.format(lastClose + qujian)), leftWhite, topWhite + 15, mPaint);
			canvas.drawText(String.valueOf(dfforview.format(lastClose + qujian / 2)), leftWhite, topWhite + 8 + kHeight * 1 / 4,
					mPaint);
			mPaint.setColor(Color.WHITE);
			canvas.drawText(String.valueOf(dfforview.format((lastClose))), leftWhite, topWhite + 6 + kHeight * 1 / 2, mPaint);
			mPaint.setColor(Color.GREEN);
			canvas.drawText(String.valueOf(dfforview.format(lastClose - qujian / 2)), leftWhite, topWhite + 6 + kHeight * 3 / 4,
					mPaint);
			canvas.drawText(String.valueOf(dfforview.format(lastClose - qujian)), leftWhite, topWhite - 5 + kHeight, mPaint);

			// 交易量刻度
			mPaint.setColor(Color.YELLOW);
			canvas.drawText(String.valueOf((int) (maxTradeVolume)), leftWhite, topWhite + 2 + kHeight + 13, mPaint);
			canvas.drawText(String.valueOf((int) (maxTradeVolume / 2)), leftWhite, topWhite + 9 + kHeight + tradeHeight / 2,
					mPaint);
			String[] tmp = timePeriod.split("-");
			mPaint.setTextAlign(Paint.Align.LEFT);
			if (tmp.length >= 2) {
				canvas.drawText(tmp[0], leftWhite, maxH + 14, mPaint);
				canvas.drawText(tmp[1], width - rightWhite - 44, maxH + 14, mPaint);
			}
			mPaint.setTextAlign(Paint.Align.RIGHT);
			if (tmp.length >= 4) {
				canvas.drawText(tmp[2], (width - rightWhite + leftWhite) / 2, maxH + 14, mPaint);
				mPaint.setTextAlign(Paint.Align.LEFT);
				canvas.drawText(tmp[3], (width - rightWhite + leftWhite) / 2, maxH + 14, mPaint);
			}
			if (tmp.length >= 6) {
				mPaint.setTextAlign(Paint.Align.CENTER);
				canvas.drawText(tmp[4], (width - rightWhite - leftWhite) / 4 + leftWhite, maxH + 14, mPaint);
				canvas.drawText(tmp[5], (width - rightWhite - leftWhite) * 3 / 4 + leftWhite, maxH + 14, mPaint);
			}

			mPaint.setStyle(Style.STROKE);
			// moveDate(canvas);
			// canvas.drawText(stockName, leftWhite, topWhite - 3, mPaint);
			// canvas.drawText(beginDay, leftWhite+1, maxH-tradeHeight-2,
			// mPaint);
			// canvas.drawText(endDay, width-rightWhite-64, maxH-tradeHeight-2,
			// mPaint);y
			// mPaint.setStrokeWidth(2);
			int l = ls.size();
			for (int i = 0; i < l; i++) {
				if (i > 0) {
					mPaint.setColor(Color.WHITE);
					// mPaint.setStrokeWidth(1);
					canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
							(float) (kHeight + topWhite - (util.getFloat(ls.get(i - 1).getK_close()) - (lastClose - qujian))
									* rul), (float) (interval * i + interval / 2 + leftWhite),
							(float) (kHeight + topWhite - (util.getFloat(ls.get(i).getK_close()) - (lastClose - qujian)) * rul),
							mPaint);
					mPaint.setColor(Color.YELLOW);
					// mPaint.setStrokeWidth(1);
					canvas.drawLine((float) (interval * i + interval / 2 + leftWhite) - interval,
							(float) (kHeight + topWhite - (shareAverage.get(i - 1) - (lastClose - qujian)) * rul),
							(float) (interval * i + interval / 2 + leftWhite),
							(float) (kHeight + topWhite - (shareAverage.get(i) - (lastClose - qujian)) * rul), mPaint);
				}

				mPaint.setColor(Color.RED);
				if (i > 0 && util.getFloat(ls.get(i - 1).getK_close()) > util.getFloat(ls.get(i).getK_close())) {
					mPaint.setColor(Color.GREEN);
				}
				// 交易量
				canvas.drawLine((float) (interval * i + interval / 2 + leftWhite), maxH,
						(float) (interval * i + interval / 2 + leftWhite), maxH - util.getFloat(ls.get(i).getK_volume()) * trul,
						mPaint);

			}
			// drawTitle(canvas);
			// moveLine(canvas);
		} else {
			// 数据取得中的表格虚实线（暂时取消）
			// mPaint.setStyle(Style.FILL);
			// 画实线坐标

			// mPaint.setStrokeWidth(0.5f);
			mPaint.setColor(Color.RED);
			// 价量分界线
			// canvas.drawLine(1, topWhite + kHeight, width - rightWhite,
			// topWhite
			// + kHeight, mPaint);
			// 横坐标： 红色横实线
			// canvas.drawLine(1, maxH, width - rightWhite, maxH, mPaint);
			// canvas.drawLine(1, topWhite, width - rightWhite, topWhite,
			// mPaint);

			// 纵坐标: 红色纵实线
			// canvas.drawLine((width - rightWhite + leftWhite) / 2, maxH,
			// (width
			// - rightWhite + leftWhite) / 2, topWhite, mPaint);
			// canvas.drawLine(1, maxH, 1, topWhite, mPaint);
			// canvas.drawLine(leftWhite, maxH, leftWhite, topWhite, mPaint);
			// canvas.drawLine(width - rightWhite, maxH, width - rightWhite,
			// topWhite, mPaint);

			// PathEffect effects = new DashPathEffect(new float[] { 4, 6, 4, 6
			// }, 1);
			// mPaint.setPathEffect(effects);
			// 分时线 中值水平线
			// for (int i = 1; i < 4; i++) {
			// canvas.drawLine(leftWhite, topWhite + kHeight * i / 4, width
			// - rightWhite, topWhite + kHeight * i / 4, mPaint);
			// }
			// canvas.drawLine((width - rightWhite - leftWhite) / 4 + leftWhite,
			// maxH, (width - rightWhite - leftWhite) / 4 + leftWhite,
			// topWhite, mPaint);
			// canvas.drawLine((width - rightWhite - leftWhite) * 3 / 4
			// + leftWhite, maxH, (width - rightWhite - leftWhite) * 3 / 4
			// + leftWhite, topWhite, mPaint);
			// 交易量中值水平线
			// canvas.drawLine(leftWhite,
			// topWhite + 2 + kHeight + tradeHeight / 2, width
			// - rightWhite, topWhite + 2 + kHeight + tradeHeight
			// / 2, mPaint);
			mPaint.setStyle(Style.FILL);
			mPaint.setPathEffect(null);
			// 分时线和交易量中间的空距
			// mPaint.setColor(Color.DKGRAY);
			// mPaint.setStrokeWidth(13);
			// canvas.drawLine(leftWhite, topWhite + kHeight,
			// width - rightWhite, topWhite + kHeight,
			// mPaint);
			// 标记分时线 图左侧 数值刻度
			mPaint.setStrokeWidth(1f);
			mPaint.setTextSize(18);
			mPaint.setTextAlign(Paint.Align.RIGHT);
			canvas.drawText("0.00", leftWhite, topWhite + 15, mPaint);
			canvas.drawText("0.00", leftWhite, topWhite + 8 + kHeight * 1 / 4, mPaint);
			mPaint.setColor(Color.WHITE);
			canvas.drawText("0.00", leftWhite, topWhite + 6 + kHeight * 1 / 2, mPaint);
			mPaint.setColor(Color.GREEN);
			canvas.drawText("0.00", leftWhite, topWhite + 6 + kHeight * 3 / 4, mPaint);
			canvas.drawText("0.00", leftWhite, topWhite - 5 + kHeight, mPaint);

			// 交易量刻度
			mPaint.setColor(Color.YELLOW);
			canvas.drawText(String.valueOf((int) (maxTradeVolume)), leftWhite, topWhite + 2 + kHeight + 13, mPaint);
			canvas.drawText(String.valueOf((int) (maxTradeVolume / 2)), leftWhite, topWhite + 9 + kHeight + tradeHeight / 2,
					mPaint);
			String[] tmp = timePeriod.split("-");
			mPaint.setTextAlign(Paint.Align.LEFT);
			if (tmp.length >= 2) {
				canvas.drawText(tmp[0], leftWhite, maxH + 14, mPaint);
				canvas.drawText(tmp[1], width - rightWhite - 44, maxH + 14, mPaint);
			}
			mPaint.setTextAlign(Paint.Align.RIGHT);
			if (tmp.length >= 4) {
				canvas.drawText(tmp[2], (width - rightWhite + leftWhite) / 2, maxH + 14, mPaint);
				mPaint.setTextAlign(Paint.Align.LEFT);
				canvas.drawText(tmp[3], (width - rightWhite + leftWhite) / 2, maxH + 14, mPaint);
			}
			if (tmp.length >= 6) {
				mPaint.setTextAlign(Paint.Align.CENTER);
				canvas.drawText(tmp[4], (width - rightWhite - leftWhite) / 4 + leftWhite, maxH + 14, mPaint);
				canvas.drawText(tmp[5], (width - rightWhite - leftWhite) * 3 / 4 + leftWhite, maxH + 14, mPaint);
			}
		}

		mPaint.setAntiAlias(false);
	}

	// 移动标记线
	public void moveLine(Canvas canvas) {
		if (n < ls.size() - 1) {
			canvas.drawLine(x, this.topWhite, x, kHeight + topWhite, mPaint);

			canvas.drawLine(x, this.maxH - this.tradeHeight, x, this.maxH, mPaint);
			if (n < ls.size() - 1) {
				float y = (kHeight + topWhite - (util.getFloat(ls.get(n).getK_close()) - (lastClose - qujian))
						* (kHeight / (qujian * 2)));
				canvas.drawLine(leftWhite, y, width - rightWhite, y, mPaint);

			}
		}

	}

	// 移动显示日期
	public void moveDate(Canvas canvas) {
		if (x != -1) {
			float dx = leftWhite;
			if (x >= tableWidth / 2 + leftWhite)
				dx = x - this.interval / 2 - 55;
			else
				dx = x - this.interval / 2;
			if (n < ls.size() - 1) {
				String date = ls.get(n).getK_date();
				String show = " 时间：" + date + "   价：" + ls.get(n).getK_close() + "   量：" + ls.get(n).getK_volume();
				canvas.drawText(show, leftWhite + 1, maxH - tradeHeight - 3, mPaint);
			}
		}
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getX() {
		return this.x;
	}

	// public void run() {
	//
	// while (!Thread.currentThread().isInterrupted()) {
	//
	// try {
	// Thread.sleep(100);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// Thread.currentThread().interrupt();
	// }
	//
	// this.postInvalidate();
	//
	// }
	//
	// }
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			postInvalidate();
		}
	};

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// /* 取得手指触控屏幕的位置 */
	// float x = event.getX();
	// try {
	// /* 触控事件的处理 */
	// switch (event.getAction()) {
	// /* 移动位置 */
	// case MotionEvent.ACTION_MOVE:
	// if (x - this.leftWhite - interval / 2 <= 0) {
	// x = this.leftWhite + interval / 2;
	// } else if (x > this.width - this.rightWhite - interval / 2) {
	// x = this.width - this.rightWhite - interval / 2;
	//
	// } else {
	// n = (int) ((x - leftWhite) / interval);
	// x = this.leftWhite + n * interval + interval / 2;
	// setN(n);
	// }
	// setX(x);
	// break;
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return true;
	//
	// }
}