package joey.present.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import joey.present.data.CalData;
import joey.present.data.InforPojo;
import joey.present.data.KData;
import joey.present.data.PriceData;
import joey.present.data.TypePojo;
import joey.present.view.PriceView;

public class Util {
	private static final Logger logger = LoggerFactory.getLogger(Util.class);
	private DecimalFormat df4 = new DecimalFormat("0.0000");
	private DecimalFormat df2 = new DecimalFormat("0.00");
	private DecimalFormat df = new DecimalFormat("");
	private HttpClient httpClient = new DefaultHttpClient();
	private HttpResponse httpResponse;

	public String formatTimeMin(String time) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(Const.TIME_MIN);
			long timeTemp = Long.valueOf(time + "000");
			Date dt = new Date(timeTemp);
			res = sdf.format(dt);
		} catch (Exception e) {
		}

		return res;
	}

	public String formatFloat00(float orig) {
		String res = "";
		res = String.valueOf((float) Math.round(orig * 100) / 100);
		return res;
	}

	public String formatTimeHms(String time) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(Const.TIME_HMS);
			long timeTemp = Long.valueOf(time + "000");
			Date dt = new Date(timeTemp);
			res = sdf.format(dt);
		} catch (Exception e) {
		}

		return res;
	}

	public String formatTimeYD(String time) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(Const.TIME_HMS);
			long timeTemp = Long.valueOf(time + "000");
			Date dt = new Date(timeTemp);
			res = sdf.format(dt);
		} catch (Exception e) {
		}

		return res;
	}

	public String formatTimeSec(String time) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(Const.TIME_SEC);
			long timeTemp = Long.valueOf(time + "000");
			Date dt = new Date(timeTemp);
			res = sdf.format(dt);
		} catch (Exception e) {
		}

		return res;
	}

	public String formatTimeYMD(String time) {
		String res = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(Const.TIME_YMD);
			long timeTemp = Long.valueOf(time + "000");
			Date dt = new Date(timeTemp);
			res = sdf.format(dt);
		} catch (Exception e) {
		}

		return res;
	}

	// 不用了
	public PriceData resTimeNow(String last) {
		PriceData priceData = null;
		if (last.indexOf(",") >= 0) {
			priceData = new PriceData();
			String[] tmp = last.split(",");
			for (int i = 0; i < tmp.length; i++) {

				String[] tmpvalue = tmp[i].split(":");
				if (Const.K_TIME.equals(tmpvalue[0])) {
					priceData.setPrice_quotetime(tmpvalue[1]);
				}
				if (Const.K_NEW.equals(tmpvalue[0])) {
					priceData.setPrice_last(tmpvalue[1]);
				}
				if (Const.K_OPEN.equals(tmpvalue[0])) {
					priceData.setPrice_open(tmpvalue[1]);
				}
				if (Const.K_HIGH.equals(tmpvalue[0])) {
					priceData.setPrice_high(tmpvalue[1]);
				}
				if (Const.K_LOW.equals(tmpvalue[0])) {
					priceData.setPrice_low(tmpvalue[1]);
				}
				if (Const.K_VOLUME.equals(tmpvalue[0])) {
					priceData.setPrice_volume(tmpvalue[1]);
				}
				if (Const.K_TOTAL.equals(tmpvalue[0])) {
					priceData.setPrice_total(tmpvalue[1]);
				}
				if (Const.K_LASTCLOSE.equals(tmpvalue[0])) {
					priceData.setPrice_lastclose(tmpvalue[1]);
				}
				if (Const.K_AVERAGE.equals(tmpvalue[0])) {
					priceData.setPrice_average(tmpvalue[1]);
				}
			}
			float updown = getFloat(priceData.getPrice_last()) - getFloat(priceData.getPrice_lastclose());
			float updownrate = updown / getFloat(priceData.getPrice_lastclose());
			updownrate = (float) Math.round(updownrate * 10000) / 100;
			priceData.setPrice_updown(String.valueOf(updown));
			priceData.setPrice_updownrate(String.valueOf(updownrate));

		}
		return priceData;
	}

	public float getFloat(String temp) {

		float res = 0;
		try {
			res = Float.valueOf(temp);
			// BigDecimal b = new BigDecimal(res);
			// res = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		} catch (Exception e) {
		}
		return res;
	}

	// UDP数据
	public PriceData getTimeNowUDPStringTTJ(String udp, PriceData timeNow) {
		PriceData res = new PriceData();
		try {
			JSONObject objectson = new JSONObject(udp);
			res.setPrice_quotetime(getJsonValue(objectson, Const.PRICE_QUOTETIME));
			res.setPrice_last(getJsonValue(objectson, Const.PRICE_LAST));
			res.setPrice_open(getJsonValue(objectson, Const.PRICE_OPEN));
			res.setPrice_high(getJsonValue(objectson, Const.PRICE_HIGH));
			res.setPrice_low(getJsonValue(objectson, Const.PRICE_LOW));
			res.setPrice_lastclose(getJsonValue(objectson, Const.PRICE_LASTCLOSE));
			res.setPrice_lastsettle(getJsonValue(objectson, Const.PRICE_LASTSETTLE));
			res.setPrice_code(getJsonValue(objectson, Const.PRICE_CODE));
			res.setPrice_name(getJsonValue(objectson, Const.PRICE_NAME));
			float lastClose = getFloat(res.getPrice_lastclose());
			float updown = getFloat(res.getPrice_last()) - lastClose;
			float updownrate = updown / lastClose * 100;
			res.setPrice_updown(String.valueOf(df2.format(updown)));
			res.setPrice_updownrate(String.valueOf(df2.format(updownrate)));
			res.setPriceTTJbuy(getJsonValue(objectson, Const.PRICE_TTJBUY));
			res.setPriceTTJsell(getJsonValue(objectson, Const.PRICE_TTJSELL));
			// //log.i("temp"," ampl -> " + getJsonValue(objectson,
			// Const.PRICE_TTJAMLITUDE));
			res.setPriceTTJAmplitude(getJsonValue(objectson, Const.PRICE_TTJAMLITUDE));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return res;
		}
		return res;
	}

	// UDP数据
	public synchronized PriceData getTimeNowUDPString(String udp, PriceData timeNow) {
		PriceData res = new PriceData();
		try {
			JSONObject objectson = new JSONObject(udp);
			String code = getJsonValue(objectson, Const.PRICE_CODE);
			if (code == null || "".equals(code)) {
				return null;
			}
			res.setPrice_quotetime(getJsonValue(objectson, Const.PRICE_QUOTETIME));
			res.setPrice_last(getJsonValue(objectson, Const.PRICE_LAST));
			res.setPrice_turnover(getJsonValue(objectson, Const.PRICE_TURNOVER));
			res.setPrice_open(getJsonValue(objectson, Const.PRICE_OPEN));
			res.setPrice_high(getJsonValue(objectson, Const.PRICE_HIGH));
			res.setPrice_low(getJsonValue(objectson, Const.PRICE_LOW));
			res.setPrice_volume(getJsonValue(objectson, Const.PRICE_VOLUME));
			res.setPrice_total(getJsonValue(objectson, Const.PRICE_TOTAL));
			res.setPrice_lastclose(getJsonValue(objectson, Const.PRICE_LASTCLOSE));
			res.setPrice_lastsettle(getJsonValue(objectson, Const.PRICE_LASTSETTLE));
			res.setPrice_code(getJsonValue(objectson, Const.PRICE_CODE));
			res.setPrice_name(getJsonValue(objectson, Const.PRICE_NAME));
			res.setPrice_average(getJsonValue(objectson, Const.PRICE_AVERAGE));
			res.setPrice_bid1(getJsonValue(objectson, Const.PRICE_BID1));
			res.setPrice_bid2(getJsonValue(objectson, Const.PRICE_BID2));
			res.setPrice_bid3(getJsonValue(objectson, Const.PRICE_BID3));
			res.setPrice_bid4(getJsonValue(objectson, Const.PRICE_BID4));
			res.setPrice_bid5(getJsonValue(objectson, Const.PRICE_BID5));
			res.setPrice_bidlot1(getJsonValue(objectson, Const.PRICE_BIDLOT1));
			res.setPrice_bidlot2(getJsonValue(objectson, Const.PRICE_BIDLOT2));
			res.setPrice_bidlot3(getJsonValue(objectson, Const.PRICE_BIDLOT3));
			res.setPrice_bidlot4(getJsonValue(objectson, Const.PRICE_BIDLOT4));
			res.setPrice_bidlot5(getJsonValue(objectson, Const.PRICE_BIDLOT5));
			res.setPrice_ask1(getJsonValue(objectson, Const.PRICE_ASK1));
			res.setPrice_ask2(getJsonValue(objectson, Const.PRICE_ASK2));
			res.setPrice_ask3(getJsonValue(objectson, Const.PRICE_ASK3));
			res.setPrice_ask4(getJsonValue(objectson, Const.PRICE_ASK4));
			res.setPrice_ask5(getJsonValue(objectson, Const.PRICE_ASK5));
			res.setPrice_asklot1(getJsonValue(objectson, Const.PRICE_ASKLOT1));
			res.setPrice_asklot2(getJsonValue(objectson, Const.PRICE_ASKLOT2));
			res.setPrice_asklot3(getJsonValue(objectson, Const.PRICE_ASKLOT3));
			res.setPrice_asklot4(getJsonValue(objectson, Const.PRICE_ASKLOT4));
			res.setPrice_asklot5(getJsonValue(objectson, Const.PRICE_ASKLOT5));
			// ttj
			res.setPriceTTJbuy(getJsonValue(objectson, Const.PRICE_TTJBUY));
			res.setPriceTTJsell(getJsonValue(objectson, Const.PRICE_TTJSELL));

			if ("".equals(res.getPrice_lastclose()) && timeNow != null) {
				res.setPrice_lastclose(timeNow.getPrice_lastclose());
				res.setPrice_lastsettle(timeNow.getPrice_lastsettle());
				res.setPrice_average(timeNow.getPrice_average());
			}
			float lastClose = getFloat(res.getPrice_lastclose());
			if (Const.AUT_D.equals(res.getPrice_code()) || Const.AGT_D.equals(res.getPrice_code())) {
				if (res.getPrice_lastsettle() != null && !"".equals(res.getPrice_lastsettle())) {
					lastClose = getFloat(res.getPrice_lastsettle());
				}
			}
			float updown = getFloat(res.getPrice_last()) - lastClose;
			float updownrate = updown / lastClose * 100;
			res.setPrice_updown(String.valueOf(df2.format(updown)));
			res.setPrice_updownrate(String.valueOf(df2.format(updownrate)));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return null;
		}
		return res;
	}

	// 分笔
	public List<PriceData> getFenbi(String url, String ex) {
		List<PriceData> result = new ArrayList<PriceData>();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					PriceData pData = new PriceData();
					JSONObject temp = (JSONObject) arrayJson.get(i);
					pData.setPrice_code(getJsonValue(temp, Const.PRICE_CODE));
					pData.setPrice_quotetime(getJsonValue(temp, Const.PRICE_QUOTETIME));
					pData.setPrice_open(getJsonValue(temp, Const.PRICE_OPEN));
					pData.setPrice_high(getJsonValue(temp, Const.PRICE_HIGH));
					pData.setPrice_last(getJsonValue(temp, Const.PRICE_LAST));
					pData.setPrice_low(getJsonValue(temp, Const.PRICE_LOW));
					pData.setPrice_lastclose(getJsonValue(temp, Const.PRICE_LASTCLOSE));
					pData.setPrice_lastsettle(getJsonValue(temp, Const.PRICE_LASTSETTLE));
					pData.setPrice_volume(getJsonValue(temp, Const.PRICE_VOLUME));
					float lastClose = 0;
					if (Const.AUT_D.equals(pData.getPrice_code()) || Const.AGT_D.equals(pData.getPrice_code())) {
						if (pData.getPrice_lastsettle() != null && !"".equals(pData.getPrice_lastsettle())) {
							lastClose = getFloat(pData.getPrice_lastsettle());
						}
					}
					float updown = getFloat(pData.getPrice_last()) - lastClose;
					float updownrate = updown / lastClose * 100;
					if (Const.WH4.equals(ex) && !Const.USD.equals(pData.getPrice_code())
							&& !Const.USDJPY.equals(pData.getPrice_code())) {
						pData.setPrice_updown(String.valueOf(df4.format(updown)));
					} else {
						pData.setPrice_updown(String.valueOf(df2.format(updown)));
					}
					pData.setPrice_updownrate(String.valueOf(df2.format(updownrate)));
					if (i < arrayJson.length() - 1) {
						float now = getFloat(getJsonValue(temp, Const.PRICE_VOLUME));
						float last = getFloat(getJsonValue((JSONObject) arrayJson.get(i + 1), Const.PRICE_VOLUME));
						float volume = now - last;
						pData.setPrice_volume(String.valueOf(df.format(volume)));

					}
					result.add(pData);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	// 画面启动初期使用
	public PriceData getTimeNowTTJ(String url, String ex) {
		List<PriceData> list = new ArrayList<PriceData>();
		PriceData priceData = new PriceData();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					PriceData priceDataTemp = new PriceData();
					JSONObject objectson = (JSONObject) arrayJson.get(i);
					priceDataTemp.setPrice_quotetime(getJsonValue(objectson, Const.PRICE_QUOTETIME));
					priceDataTemp.setPrice_last(getJsonValue(objectson, Const.PRICE_LAST));
					priceDataTemp.setPrice_open(getJsonValue(objectson, Const.PRICE_OPEN));
					priceDataTemp.setPrice_high(getJsonValue(objectson, Const.PRICE_HIGH));
					priceDataTemp.setPrice_low(getJsonValue(objectson, Const.PRICE_LOW));
					priceDataTemp.setPriceTTJsell(getJsonValue(objectson, Const.PRICE_TTJSELL));
					priceDataTemp.setPriceTTJbuy(getJsonValue(objectson, Const.PRICE_TTJBUY));
					priceDataTemp.setPriceTTJAmplitude(getJsonValue(objectson, Const.PRICE_TTJAMLITUDE));
					// priceDataTemp.setPrice_total(getJsonValue(objectson,
					// Const.PRICE_TOTAL));
					priceDataTemp.setPrice_lastclose(getJsonValue(objectson, Const.PRICE_LASTCLOSE));
					priceDataTemp.setPrice_code(getJsonValue(objectson, Const.PRICE_CODE));

					float lastClose = getFloat(priceDataTemp.getPrice_lastclose());
					float updown = getFloat(priceDataTemp.getPrice_last()) - lastClose;
					float updownrate = updown / lastClose * 100;
					if (Const.WH4.equals(ex) && !Const.USD.equals(priceDataTemp.getPrice_code())
							&& !Const.USDJPY.equals(priceDataTemp.getPrice_code())) {
						priceDataTemp.setPrice_updown(String.valueOf(df4.format(updown)));
					} else {
						priceDataTemp.setPrice_updown(String.valueOf(df2.format(updown)));
					}
					priceDataTemp.setPrice_updownrate(String.valueOf(df2.format(updownrate)));
					if (i < arrayJson.length() - 1) {
						float now = getFloat(getJsonValue(objectson, Const.PRICE_VOLUME));
						float last = getFloat(getJsonValue((JSONObject) arrayJson.get(i + 1), Const.PRICE_VOLUME));
						String volume = String.valueOf(now - last);
						priceDataTemp.setPrice_volume(volume);
					}
					list.add(priceDataTemp);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0) {
			priceData = list.get(0);
		}
		return priceData;
	}

	// 画面启动初期使用
	public PriceData getTimeNow(String url, String ex) {
		List<PriceData> list = new ArrayList<PriceData>();
		PriceData priceData = new PriceData();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					PriceData priceDataTemp = new PriceData();
					JSONObject objectson = (JSONObject) arrayJson.get(i);
					priceDataTemp.setPrice_quotetime(getJsonValue(objectson, Const.PRICE_QUOTETIME));
					priceDataTemp.setPrice_last(getJsonValue(objectson, Const.PRICE_LAST));
					priceDataTemp.setPrice_turnover(getJsonValue(objectson, Const.PRICE_TURNOVER));
					priceDataTemp.setPrice_open(getJsonValue(objectson, Const.PRICE_OPEN));
					priceDataTemp.setPrice_high(getJsonValue(objectson, Const.PRICE_HIGH));
					priceDataTemp.setPrice_low(getJsonValue(objectson, Const.PRICE_LOW));
					priceDataTemp.setPrice_volume(getJsonValue(objectson, Const.PRICE_VOLUME));
					// priceDataTemp.setPrice_total(getJsonValue(objectson,
					// Const.PRICE_TOTAL));
					priceDataTemp.setPrice_lastclose(getJsonValue(objectson, Const.PRICE_LASTCLOSE));
					priceDataTemp.setPrice_lastsettle(getJsonValue(objectson, Const.PRICE_LASTSETTLE));
					priceDataTemp.setPrice_code(getJsonValue(objectson, Const.PRICE_CODE));
					priceDataTemp.setPrice_average(getJsonValue(objectson, Const.PRICE_AVERAGE));
					priceDataTemp.setPrice_bid1(getJsonValue(objectson, Const.PRICE_BID1));
					priceDataTemp.setPrice_bid2(getJsonValue(objectson, Const.PRICE_BID2));
					priceDataTemp.setPrice_bid3(getJsonValue(objectson, Const.PRICE_BID3));
					priceDataTemp.setPrice_bid4(getJsonValue(objectson, Const.PRICE_BID4));
					priceDataTemp.setPrice_bid5(getJsonValue(objectson, Const.PRICE_BID5));
					priceDataTemp.setPrice_bidlot1(getJsonValue(objectson, Const.PRICE_BIDLOT1));
					priceDataTemp.setPrice_bidlot2(getJsonValue(objectson, Const.PRICE_BIDLOT2));
					priceDataTemp.setPrice_bidlot3(getJsonValue(objectson, Const.PRICE_BIDLOT3));
					priceDataTemp.setPrice_bidlot4(getJsonValue(objectson, Const.PRICE_BIDLOT4));
					priceDataTemp.setPrice_bidlot5(getJsonValue(objectson, Const.PRICE_BIDLOT5));
					priceDataTemp.setPrice_ask1(getJsonValue(objectson, Const.PRICE_ASK1));
					priceDataTemp.setPrice_ask2(getJsonValue(objectson, Const.PRICE_ASK2));
					priceDataTemp.setPrice_ask3(getJsonValue(objectson, Const.PRICE_ASK3));
					priceDataTemp.setPrice_ask4(getJsonValue(objectson, Const.PRICE_ASK4));
					priceDataTemp.setPrice_ask5(getJsonValue(objectson, Const.PRICE_ASK5));
					priceDataTemp.setPrice_asklot1(getJsonValue(objectson, Const.PRICE_ASKLOT1));
					priceDataTemp.setPrice_asklot2(getJsonValue(objectson, Const.PRICE_ASKLOT2));
					priceDataTemp.setPrice_asklot3(getJsonValue(objectson, Const.PRICE_ASKLOT3));
					priceDataTemp.setPrice_asklot4(getJsonValue(objectson, Const.PRICE_ASKLOT4));
					priceDataTemp.setPrice_asklot5(getJsonValue(objectson, Const.PRICE_ASKLOT5));
					float lastClose = getFloat(priceDataTemp.getPrice_lastclose());
					if (Const.AUT_D.equals(priceDataTemp.getPrice_code()) || Const.AGT_D.equals(priceDataTemp.getPrice_code())) {
						lastClose = getFloat(priceDataTemp.getPrice_lastsettle());
					}
					if (Const.TTJ8.equals(ex)) {
						priceDataTemp.setPriceTTJbuy(getJsonValue(objectson, Const.PRICE_TTJBUY));
						priceDataTemp.setPriceTTJsell(getJsonValue(objectson, Const.PRICE_TTJSELL));
					}
					float updown = getFloat(priceDataTemp.getPrice_last()) - lastClose;
					float updownrate = updown / lastClose * 100;
					if (Const.WH4.equals(ex) && !Const.USD.equals(priceDataTemp.getPrice_code())
							&& !Const.USDJPY.equals(priceDataTemp.getPrice_code())) {
						priceDataTemp.setPrice_updown(String.valueOf(df4.format(updown)));
					} else {
						priceDataTemp.setPrice_updown(String.valueOf(df2.format(updown)));
					}
					priceDataTemp.setPrice_updownrate(String.valueOf(df2.format(updownrate)));
					if (i < arrayJson.length() - 1) {
						float now = getFloat(getJsonValue(objectson, Const.PRICE_VOLUME));
						float last = getFloat(getJsonValue((JSONObject) arrayJson.get(i + 1), Const.PRICE_VOLUME));
						String volume = String.valueOf(now - last);
						priceDataTemp.setPrice_volume(volume);
					}
					list.add(priceDataTemp);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() > 0) {
			priceData = list.get(0);
		}
		return priceData;
	}

	public List<KData> getTimeData(String history) {
		List<KData> result = new ArrayList<KData>();
		HttpGet httpRequest = new HttpGet(history);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					KData kData = new KData();
					JSONObject temp = (JSONObject) arrayJson.get(i);

					kData.setK_timeLong(getJsonValue(temp, Const.K_DATE));
					kData.setK_date(formatTimeSec(kData.getK_timeLong()));
					kData.setK_close(getJsonValue(temp, Const.K_CLOSE));
					kData.setK_open(getJsonValue(temp, Const.K_OPEN));
					kData.setK_high(getJsonValue(temp, Const.K_HIGH));
					kData.setK_low(getJsonValue(temp, Const.K_LOW));
					// kData.setK_total(getJsonValue(temp, Const.K_TOTAL));
					kData.setK_volume(getJsonValue(temp, Const.K_VOLUME));
					result.add(kData);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public List<KData> getKData(String url) {
		List<KData> result = new ArrayList<KData>();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					KData kData = new KData();
					JSONObject temp = (JSONObject) arrayJson.get(i);
					kData.setK_timeLong(getJsonValue(temp, Const.K_DATE));
					kData.setK_date(formatTimeSec(kData.getK_timeLong()));
					kData.setK_close(getJsonValue(temp, Const.K_CLOSE));
					kData.setK_open(getJsonValue(temp, Const.K_OPEN));
					kData.setK_high(getJsonValue(temp, Const.K_HIGH));
					kData.setK_low(getJsonValue(temp, Const.K_LOW));
					kData.setK_volume(getJsonValue(temp, Const.K_VOLUME));
					result.add(kData);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public List<PriceData> getPriceData(String url, String type) {
		logger.debug("取数据" + new Date().toLocaleString());
		List<PriceData> result = new ArrayList<PriceData>();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				logger.debug("取到数据" + new Date().toLocaleString());
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					PriceData pData = new PriceData();
					JSONObject temp = (JSONObject) arrayJson.get(i);
					pData.setPrice_quotetime(getJsonValue(temp, Const.PRICE_QUOTETIME));
					pData.setPrice_open(getJsonValue(temp, Const.PRICE_OPEN));
					pData.setPrice_high(getJsonValue(temp, Const.PRICE_HIGH));
					pData.setPrice_last(getJsonValue(temp, Const.PRICE_LAST));
					if (Const.SHGOLD.equals(type)) {
						pData.setPrice_turnover(getJsonValue(temp, Const.PRICE_TURNOVER));
					}
					pData.setPrice_low(getJsonValue(temp, Const.PRICE_LOW));
					pData.setPrice_name(getJsonValue(temp, Const.PRICE_NAME));
					pData.setPrice_code(getJsonValue(temp, Const.PRICE_CODE));
					pData.setPrice_lastclose(getJsonValue(temp, Const.PRICE_LASTCLOSE));
					pData.setPrice_lastsettle(getJsonValue(temp, Const.PRICE_LASTSETTLE));
					// pData.setPrice_updown(getJsonValue(temp,
					// Const.PRICE_UPDOWN));
					// pData.setPrice_updownrate(getJsonValue(temp,
					// Const.PRICE_UPDOWNRATE));
					pData.setPrice_average(getJsonValue(temp, Const.PRICE_AVERAGE));
					pData.setPrice_volume(getJsonValue(temp, Const.PRICE_VOLUME));
					pData.setPrice_Decimal(getJsonValue(temp, Const.PRICE_DECIMAL));
					float lastClose = 0;
					lastClose = getFloat(pData.getPrice_lastclose());
					if (Const.AUT_D.equals(pData.getPrice_code()) || Const.AGT_D.equals(pData.getPrice_code())) {
						if (pData.getPrice_lastsettle() != null && !"".equals(pData.getPrice_lastsettle())) {
							lastClose = getFloat(pData.getPrice_lastsettle());
						}
					}
					float updown = getFloat(pData.getPrice_last()) - lastClose;
					float updownrate = updown / lastClose * 100;
					pData.setPrice_updown(String.valueOf(df4.format(updown)));
					pData.setPrice_updownrate(String.valueOf(df2.format(updownrate)));
					result.add(pData);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("返回数据到前台" + new Date().toLocaleString());
		return result;

	}

	public List<PriceData> getPriceDataTTJ(String url, String type) {
		List<PriceData> result = new ArrayList<PriceData>();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					PriceData pData = new PriceData();
					JSONObject temp = (JSONObject) arrayJson.get(i);
					pData.setPrice_quotetime(getJsonValue(temp, Const.PRICE_QUOTETIME));
					pData.setPrice_open(getJsonValue(temp, Const.PRICE_OPEN));
					pData.setPrice_high(getJsonValue(temp, Const.PRICE_HIGH));
					pData.setPrice_last(getJsonValue(temp, Const.PRICE_LAST));
					pData.setPrice_low(getJsonValue(temp, Const.PRICE_LOW));
					pData.setPrice_name(getJsonValue(temp, Const.PRICE_NAME));
					pData.setPrice_code(getJsonValue(temp, Const.PRICE_CODE));
					pData.setPrice_lastclose(getJsonValue(temp, Const.PRICE_LASTCLOSE));
					pData.setPriceTTJbuy(getJsonValue(temp, Const.PRICE_TTJBUY));
					pData.setPriceTTJsell(getJsonValue(temp, Const.PRICE_TTJSELL));
					pData.setPriceTTJAmplitude(getJsonValue(temp, Const.PRICE_TTJAMLITUDE));
					pData.setPrice_Decimal(getJsonValue(temp, Const.PRICE_DECIMAL));
					// //log.i("temp","ampl -> " + getJsonValue(temp,
					// Const.PRICE_TTJAMLITUDE));
					float lastClose = 0;
					lastClose = getFloat(pData.getPrice_lastclose());
					float updown = getFloat(pData.getPrice_last()) - lastClose;
					float updownrate = updown / lastClose * 100;
					pData.setPrice_updown(String.valueOf(df4.format(updown)));
					pData.setPrice_updownrate(String.valueOf(df2.format(updownrate)));
					result.add(pData);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	public List<PriceData> getPriceDataOP(String url, String diycode, String diyex) {
		List<PriceData> result = new ArrayList<PriceData>();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					PriceData pData = new PriceData();
					JSONObject temp = (JSONObject) arrayJson.get(i);
					pData.setPrice_quotetime(getJsonValue(temp, Const.PRICE_QUOTETIME));
					pData.setPrice_open(getJsonValue(temp, Const.PRICE_OPEN));
					pData.setPrice_high(getJsonValue(temp, Const.PRICE_HIGH));
					pData.setPrice_last(getJsonValue(temp, Const.PRICE_LAST));
					pData.setPrice_turnover(getJsonValue(temp, Const.PRICE_TURNOVER));
					pData.setPrice_low(getJsonValue(temp, Const.PRICE_LOW));
					pData.setPrice_name(getJsonValue(temp, Const.PRICE_NAME));
					pData.setPrice_code(getJsonValue(temp, Const.PRICE_CODE));
					pData.setPrice_lastclose(getJsonValue(temp, Const.PRICE_LASTCLOSE));
					pData.setPrice_lastsettle(getJsonValue(temp, Const.PRICE_LASTSETTLE));
					pData.setPrice_Decimal(getJsonValue(temp, Const.PRICE_DECIMAL));
					// pData.setPrice_updown(getJsonValue(temp,
					// Const.PRICE_UPDOWN));
					// pData.setPrice_updownrate(getJsonValue(temp,
					// Const.PRICE_UPDOWNRATE));
					pData.setPrice_volume(getJsonValue(temp, Const.PRICE_VOLUME));
					float lastClose = 0;
					lastClose = getFloat(pData.getPrice_lastclose());
					if (Const.AUT_D.equals(pData.getPrice_code()) || Const.AGT_D.equals(pData.getPrice_code())) {
						if (pData.getPrice_lastsettle() != null && !"".equals(pData.getPrice_lastsettle())) {
							lastClose = getFloat(pData.getPrice_lastsettle());
						}
					}
					float updown = getFloat(pData.getPrice_last()) - lastClose;
					float updownrate = updown / lastClose * 100;
					String codeTmp = pData.getPrice_code();
					int t = 0;
					String[] codearray = diycode.split(",");
					for (int j = 0; j < codearray.length; j++) {
						if (codeTmp.equals(codearray[j])) {
							t = j;
						}
					}
					String[] codeEXarray = diyex.split(",");
					if (Const.WH4.equals(codeEXarray[t]) && !Const.USD.equals(pData.getPrice_code())
							&& !Const.USDJPY.equals(pData.getPrice_code())) {
						pData.setPrice_updown(String.valueOf(df4.format(updown)));
					} else {
						pData.setPrice_updown(String.valueOf(df2.format(updown)));
					}
					pData.setPrice_updownrate(String.valueOf(df2.format(updownrate)));
					result.add(pData);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	public List<CalData> getCalData(String url) {
		List<CalData> result = new ArrayList<CalData>();
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				JSONArray arrayJson = new JSONArray(builder.toString());
				for (int i = 0; i < arrayJson.length(); i++) {
					CalData calData = new CalData();
					JSONObject temp = (JSONObject) arrayJson.get(i);
					calData.setCalTime(getJsonValue(temp, Const.CAL_TIME));
					calData.setCalCountry(getJsonValue(temp, Const.CAL_COUNTRY));
					calData.setCalItem(getJsonValue(temp, Const.CAL_ITEM));
					calData.setCalImportance(getJsonValue(temp, Const.CAL_IMPORTANCE));
					calData.setCalLastValue(getJsonValue(temp, Const.CAL_LASTVALUE));
					calData.setCalPrediction(getJsonValue(temp, Const.CAL_PREDICTION));
					calData.setCalActual(getJsonValue(temp, Const.CAL_ACTUAL));
					result.add(calData);
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private String getJsonValue(JSONObject temp, String key) {
		String res = "";
		try {
			res = temp.getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return res;
	}

	private String getJsonValueUDP(JSONObject temp, String key, String UDP) {
		String res = "";
		if (UDP.indexOf(key) < 0) {
			return res;
		}
		try {
			res = temp.getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return res;
	}

	// news
	public List<InforPojo> getInforByXML(String url) throws Exception {
		List<InforPojo> result = new ArrayList<InforPojo>();

		URL inforUrl = new URL(url);
		URLConnection ucon = inforUrl.openConnection();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		InputStream input = ucon.getInputStream();
		// InputStreamReader streamReader = new InputStreamReader(input,
		// "UTF-8");
		// InputSource inputSource = new InputSource(streamReader);
		Document doc = builder.parse(input);
		NodeList items = doc.getElementsByTagName("item");
		for (int i = 0; i < items.getLength(); i++) {
			InforPojo inforPojo = new InforPojo();
			Element item = (Element) items.item(i);
			inforPojo.setTitle_(getData(item, "title"));
			// inforPojo.setContent_(getData(item, "description"));
			// 只取时间，不取日期
			// inforPojo.setTime_(formatTime(getData(item, "pubDate")));
			inforPojo.setTime_(getData(item, "pubDate"));
			// inforPojo.setTime_(formatTime2(getData(item, "pubDate")));
			inforPojo.setLink_(getData(item, "ID"));
			// inforPojo.setAuthor_(getData(item, "author"));
			// Log.d("+++++++++++inforPojo: ", inforPojo.toString());
			inforPojo.setImg(getData(item, "Img"));
			result.add(inforPojo);
		}

		return result;
	}

	public String getData(Element item, String key) {

		String ret = "";
		if (item.getElementsByTagName(key) != null) {
			if (item.getElementsByTagName(key).item(0) != null) {
				if (item.getElementsByTagName(key).item(0).getFirstChild() != null) {
					ret = item.getElementsByTagName(key).item(0).getFirstChild().getNodeValue();
				}
			}
		}

		return ret;
	}

	public String getChildData(Element item, String tagName, String name, String key) {

		String ret = "";
		NodeList itemList = item.getElementsByTagName(tagName);
		for (int i = 0; i < itemList.getLength(); i++) {
			Element itemTemp = (Element) itemList.item(i);
			if (name.equals(itemTemp.getAttribute("name"))) {
				ret = getData(itemTemp, key);
			}
		}

		return ret;
	}

	public List<TypePojo> getTypeByXML(String url) throws Exception {
		List<TypePojo> result = new ArrayList<TypePojo>();

		URL inforUrl = new URL(url);
		URLConnection ucon = inforUrl.openConnection();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		Document doc = builder.parse(ucon.getInputStream());
		NodeList items = doc.getElementsByTagName("item");
		for (int i = 0; i < items.getLength(); i++) {
			TypePojo typePojo = new TypePojo();
			Element item = (Element) items.item(i);
			typePojo.setTypeno_(getData(item, "typeno"));
			typePojo.setTypename_(getData(item, "typename"));
			typePojo.setTypeupdateurl_(getData(item, "typeupdateurl"));
			typePojo.setAutoupdate_(getData(item, "autoupdate"));
			result.add(typePojo);
		}

		return result;
	}

	private String formatTime(String time) {
		if (time != null) {
			if (time.length() > 0) {
				String[] timeValue = time.split(" ");
				time = timeValue[4];
			}
		}
		return time;
	}

	public String checkVersion(String url) {
		String res = "0";
		HttpGet httpRequest = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				StringBuilder builder = new StringBuilder();
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
				for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
					builder.append(s);
				}
				// Toast.makeText(getApplicationContext(), builder.toString(),
				// 6000).show();
				res = builder.toString();

			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	public List<InforPojo> getInforByXML1(String url) throws Exception {
		List<InforPojo> result = new ArrayList<InforPojo>();

		URL inforUrl = new URL(url);
		URLConnection ucon = inforUrl.openConnection();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		InputStream input = ucon.getInputStream();
		// InputStreamReader streamReader = new InputStreamReader(input,
		// "UTF-8");
		// InputSource inputSource = new InputSource(streamReader);
		Document doc = builder.parse(input);
		NodeList items = doc.getElementsByTagName("item");
		for (int i = 0; i < items.getLength(); i++) {
			InforPojo inforPojo = new InforPojo();
			Element item = (Element) items.item(i);
			inforPojo.setTitle_(getData(item, "title"));
			// inforPojo.setContent_(getData(item, "description"));
			// 只取时间，不取日期
			// inforPojo.setTime_(formatTime(getData(item, "pubDate")));
			inforPojo.setTime_(getData(item, "distime"));
			// inforPojo.setTime_(formatTime2(getData(item, "pubDate")));
			inforPojo.setLink_(getData(item, "ID"));
			// inforPojo.setAuthor_(getData(item, "author"));
			// Log.d("+++++++++++inforPojo: ", inforPojo.toString());
			//inforPojo.setImg(getData(item, "Img"));
			result.add(inforPojo);
		}

		return result;
	}

}
