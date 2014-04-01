package joey.present.util;

import java.text.DecimalFormat;

import android.graphics.Color;

public class Const {

	public static final DecimalFormat df4 = new DecimalFormat("0.0000");
	public static final DecimalFormat df3 = new DecimalFormat("0.000");
	public static final DecimalFormat df2 = new DecimalFormat("0.00");
	public static final DecimalFormat df1 = new DecimalFormat("0.0");
	public static final DecimalFormat df0 = new DecimalFormat("0");

	public static final String PREFERENCES_NAME = "optionaldata";
	public static final String PREF_OPTIONAL = "prefoptional";
	public static final String PREF_OPTIONAL_NAME = "prefoptionalname";
	public static final String PREF_EX = "prefex";

	public static final String TIME_MIN = "MM/dd/yyyy HH:mm";
	public static final String TIME_SEC = "MM/dd/yyyy HH:mm:ss";
	public static final String TIME_YMD = "yyyyMMdd";
	public static final String TIME_HMS = "HH:mm:ss";
	public static final String TIME_YD = "MM/dd/yyyy";

	// 列表数据
	// public static final String URL_MARKET =
	// "http://mhth.fx678.com/quotelist.aspx?key=PM_key";
	public static final String URL_MARKET = "http://mhth.fx678.com/quotelist.aspx?key=PM_key";
	// 单条数据
	public static final String URL_NOW = "http://mhth.fx678.com/SingleNewQuote.aspx?ex=PM_ex&code=PM_code&date=PM_date&count=PM_count";
	// 分时数据
	public static final String URL_TIMEDATA = "http://mhth.fx678.com/TickChart.aspx?ex=PM_ex&code=PM_code";
	// K线数据
	public static final String URL_KDATA = "http://mhth.fx678.com/CandleChart.aspx?ex=PM_ex&code=PM_code&type=PM_type&count=PM_count";
	public static final String URL_DIY = "http://mhth.fx678.com/diy.aspx?code=PM_code";
	public static final String URL_KEY = "PM_key";
	public static final String URL_EX = "PM_ex";
	public static final String URL_CODE = "PM_code";
	public static final String URL_DATE = "PM_date";
	public static final String URL_COUNT = "PM_count";
	public static final String URL_TYPE = "PM_type";

	public static final String UDP_IP = "m1.fx678.com";
	// public static final String UDP_IP = "mht.fx678.com";
	public static final int UDP_PORT = 21134;
	public static final String UDP_PARA = "login,";
	public static final String UDP_DIY = "diy,";

	public static final int K_DOWN = Color.rgb(84, 252, 252);
	public static final int MA_ZISE = Color.rgb(255, 0, 255);
	public static final String YGY = "ygy";
	public static final String SHGOLD = "shgold";
	public static final String HJXH = "hjxh";
	public static final String STOCKINDEX = "stockindex";
	public static final String WH = "wh";
	public static final String NYMEX = "nymex";
	public static final String COMEX = "comex";
	public static final String IPE = "ipe";
	public static final String TTJ = "ttj";
	public static final String SHQH = "shqh";
	public static final String BOCE = "bo";
	public static final String HXCE = "hxce";
	public static final String HF = "hf";
	public static final String QDCE = "qdce";
	public static final String BS = "bs";

	public static final String SHGOLD_NAME = "上金所";
	public static final String HJXH_NAME = "国际黄金";
	public static final String YGY_NAME = "广贵所";
	public static final String STOCKINDEX_NAME = "全球股指";
	public static final String WH_NAME = "外汇";
	public static final String NYMEX_NAME = "NYME原油";
	public static final String COMEX_NAME = "COMEX期金";
	public static final String IPE_NAME = "IPE原油";
	public static final String TTJ_NAME = "津贵所";
	public static final String SHQH_NAME = "上海期货";
	public static final String BOCE_NAME = "渤商所";
	public static final String HXCE_NAME = "津贵所";
	public static final String HF_NAME = "外汇市场";
	public static final String QDCE_NAME = "全球股指";
	public static final String BS_NAME = "国际黄金";

	public static final String SHGOLD_PERIOD = "21:00-15:30-2:30-/9:00";
	public static final String HJXH_PERIOD = "06:00-06:00";
	public static final String STOCKINDEX_PERIOD = "09:00-15:30";
	public static final String WH_PERIOD = "06:00-06:00";
	public static final String NYMEX_PERIOD = "06:00-05:00";
	public static final String COMEX_PERIOD = "21:10-21:00";
	public static final String IPE_PERIOD = "18:00-06:00";
	public static final String TTJ_PERIOD = "06:00-04:00";
	public static final String YGY_PERIOD = "06:00-04:00";
	public static final String BOCE_PERIOD = "19:00-16:00";
	public static final String HXCE_PERIOD = "06:00-06:00-18:00";
	public static final String HF_PERIOD = "06:00-04:00-17:00";
	public static final String QDCE_PERIOD = "06:00-04:00-17:00";
	public static final String BS_PERIOD = "06:00-04:00-17:00";

	public static final String AOI_PERIOD = "07:00-14:00";
	public static final String FTSEDAXCAC40_PERIOD = "15:00-00:30";
	public static final String NASDAQDJIASP500CRB_PERIOD = "21:30-05:00";
	public static final String NIKKIKSE_PERIOD = "08:00-14:00";
	public static final String STIKLSE_PERIOD = "09:00-17:00";
	public static final String PCOMP_PERIOD = "09:30-15:00";
	public static final String TWI_PERIOD = "09:00-13:30";
	public static final String SET_PERIOD = "11:00-17:30";
	public static final String SENSEX_PERIOD = "10:30-17:00";
	public static final String CHINA_PERIOD = "09:30-15:00-11:30-13:00";
	public static final String SHQH_PERIOD = "09:00-15:00-11:30-13:30";

	public static final String SHGOLD1 = "1";
	public static final String HJXH2 = "2";
	public static final String STOCKINDEX3 = "3";
	public static final String WH4 = "4";
	public static final String NYMEX5 = "5";
	public static final String COMEX6 = "6";
	public static final String IPE7 = "7";
	public static final String TTJ8 = "8";
	public static final String SHQH9 = "9";
	public static final String YGY10 = "10";
	public static final String BOCE17 = "17";
	public static final String HXCE13 = "13";
	public static final String HF16 = "16";
	public static final String QDCE15 = "15";
	public static final String BS19 = "19";

	public static final String INDEX_MACD = "MACD";
	public static final String INDEX_VOL = "VOL";
	public static final String INDEX_RSI = "RSI";
	public static final String INDEX_BOLL = "BOLL";
	public static final String INDEX_KDJ = "KDJ";
	public static final String INDEX_OBV = "OBV";
	public static final String INDEX_CCI = "CCI";
	public static final String INDEX_PSY = "PSY";

	public static final String CAL_TIME = "Time";
	public static final String CAL_COUNTRY = "Country";
	public static final String CAL_ITEM = "Item";
	public static final String CAL_IMPORTANCE = "Importance";
	public static final String CAL_LASTVALUE = "LastValue";
	public static final String CAL_PREDICTION = "Prediction";
	public static final String CAL_ACTUAL = "Actual";

	public static final String K_TIME = "time";
	public static final String K_NEW = "new";
	public static final String K_DATE = "date";
	public static final String K_CLOSE = "close";
	public static final String K_OPEN = "open";
	public static final String K_HIGH = "high";
	public static final String K_LOW = "low";
	public static final String K_AVERAGE = "average";
	public static final String K_LASTCLOSE = "lastclose";
	public static final String K_MONEY = "money";
	public static final String K_VOLUME = "volume";
	public static final String K_TOTAL = "total";

	public static final String ABNAME = "Abname";
	public static final String AB = "Ab";
	public static final String ABLOT = "Ablot";

	public static final String FENBI_TIME = "time";
	public static final String FENBI_PRICE = "fprice";
	public static final String FENBI_VOLUME = "fvolume";

	public static final String PRICE_CODE = "Code";
	public static final String PRICE_NAME = "Name";
	public static final String PRICE_QUOTETIME = "QuoteTime";
	public static final String PRICE_LOW = "Low";
	public static final String PRICE_LAST = "Last";
	public static final String PRICE_CLOSE = "Close";
	public static final String PRICE_SETTLE = "Settle";
	public static final String PRICE_POSI = "Posi";
	public static final String PRICE_UPDOWN = "UpDown";
	public static final String PRICE_UPDOWNRATE = "UpDownRate";
	public static final String PRICE_AVERAGE = "Average";
	public static final String PRICE_SEQUENCENO = "SequenceNo";
	public static final String PRICE_HIGH = "High";
	public static final String PRICE_HIGHLIMIT = "HighLimit";
	public static final String PRICE_LASTCLOSE = "LastClose";
	public static final String PRICE_LASTSETTLE = "LastSettle";
	public static final String PRICE_LOWLIMIT = "LowLimit";
	public static final String PRICE_OPEN = "Open";
	public static final String PRICE_TURNOVER = "TurnOver";
	public static final String PRICE_VOLUME = "Volume";
	public static final String PRICE_TOTAL = "Total";
	public static final String PRICE_WEIGHT = "Weight";
	public static final String PRICE_ASK1 = "Ask1";
	public static final String PRICE_ASK2 = "Ask2";
	public static final String PRICE_ASK3 = "Ask3";
	public static final String PRICE_ASK4 = "Ask4";
	public static final String PRICE_ASK5 = "Ask5";
	public static final String PRICE_ASKLOT1 = "AskLot1";
	public static final String PRICE_ASKLOT2 = "AskLot2";
	public static final String PRICE_ASKLOT3 = "AskLot3";
	public static final String PRICE_ASKLOT4 = "AskLot4";
	public static final String PRICE_ASKLOT5 = "AskLot5";
	public static final String PRICE_BID1 = "Bid1";
	public static final String PRICE_BID2 = "Bid2";
	public static final String PRICE_BID3 = "Bid3";
	public static final String PRICE_BID4 = "Bid4";
	public static final String PRICE_BID5 = "Bid5";
	public static final String PRICE_BIDLOT1 = "BidLot1";
	public static final String PRICE_BIDLOT2 = "BidLot2";
	public static final String PRICE_BIDLOT3 = "BidLot3";
	public static final String PRICE_BIDLOT4 = "BidLot4";
	public static final String PRICE_BIDLOT5 = "BidLot5";
	public static final String PRICE_TTJBUY = "Buy";
	public static final String PRICE_TTJSELL = "Sell";
	public static final String PRICE_TTJAMLITUDE = "Amplitude";
	public static final String PRICE_DECIMAL = "Decimal";

	public static final String AUT_D = "AuT+D";
	public static final String AGT_D = "AgT+D";
	public static final String USD = "USD";
	public static final String USDJPY = "USDJPY";
	public static final String A0001 = "1A0001";
	public static final String A01 = "2A01";
	public static final String AOI = "AOI";
	public static final String FTSE = "FTSE";
	public static final String DAX = "DAX";
	public static final String CAC40 = "CAC40";
	public static final String NASDAQ = "NASDAQ";
	public static final String DJIA = "DJIA";
	public static final String SP500 = "SP500";
	public static final String CRB = "CRB";
	public static final String SET = "SET";
	public static final String SENSEX = "SENSEX";
	public static final String NIKKI = "NIKKI";
	public static final String KSE = "KSE";
	public static final String STI = "STI";
	public static final String TWI = "TWI";
	public static final String KLSE = "KLSE";
	public static final String PCOMP = "PCOMP";

	public final static String PREFERENCESNEWS_NAME = "newsTypePref0821";
	public final static String UPDATE = "updateTime";
	public final static String VERSION = "version";
	public final static String MARKET_UPDATE = "marketUpdateTime";
	public final static String SOFT_UPDATE = "softUpdateTime";
	public final static String UPDATE_URL = "updateUrl";
	public final static String SOFT_UPDATE_URL = "softUpdateUrl";
	public final static String NEWS_NUM = "news_";
	public final static String NEWS_COUNT = "newsCount";
	public final static String MARKET_NUM = "market_";
	public final static String MARKET_COUNT = "marketCount";
	public final static String ABOUT = "汇通财经 \n汇通财经 www.fx678.com\n版本号：V";
	public final static String VERSIONCODE = "v1.1.3";

	public final static String APP_NAME = "汇通财经";
	public final static String APP_Version = "V1.1.3";
	public final static String APP_VersionCheckURL = "http://m.fx678.com/Upgrade.aspx?ver=NEWFX678_ANDROID_" + APP_Version;

}