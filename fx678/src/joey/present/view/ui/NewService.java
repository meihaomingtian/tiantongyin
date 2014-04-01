package joey.present.view.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import joey.present.data.InforPojo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class NewService {
	// private NewItem nt = null;
	// private List<NewItem> list = null;
	// private String tag;

	private InforPojo nt = null;
	private List<InforPojo> list = null;
	private String tag;

	/*
	 * 用 DOM4J 解析xml文件
	 * 
	 * @param strUrl 网络xml地址
	 * 
	 * @param javaBean 实体类对象
	 * 
	 * @return List 返回实体类的一个list集合
	 * 
	 * @throws Exception
	 */

	public List<InforPojo> getNews(InputStream is) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();

		SAXParser parser = factory.newSAXParser();

		NewItemHandler handler = new NewItemHandler();

		parser.parse(is, handler);

		return handler.getList();

	}

	private final class NewItemHandler extends DefaultHandler {

		public List<InforPojo> getList() {

			return list;
		}

		public void startDocument() throws SAXException {

			list = new ArrayList<InforPojo>();

		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {

			if ("item".equals(localName)) {

				nt = new InforPojo();

			}

			tag = localName;

		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {

			if (tag != null) {

				String textdata = new String(ch, start, length);

				if ("title".equals(tag)) {

					nt.setTitle_(textdata);

				} else if ("Img".equals(tag)) {

					nt.setLink_(textdata);

				} else if ("ID".equals(tag)) {
					nt.setImg(textdata);
				}

			}

		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			tag = null;

			if ("item".equals(localName)) {

				list.add(nt);

				nt = null;

			}

		}

	}

}

/**
 * XmlPull解析方法 4.0中可以解析成功 2.3解析不到数据。 换上面的SAX解析
 * 
 */

// public List<NewItem> getNews(InputStream is) {
//
// // 创建XmlPullParseFactory工厂实例
// XmlPullParserFactory factory;
//
// try {
// factory = XmlPullParserFactory.newInstance();
// XmlPullParser prase;
// prase = factory.newPullParser();
// prase.setInput(is, "UTF-8");
//
// // 产生第一个事件
// int evenType = prase.getEventType();
// while (evenType != XmlPullParser.END_DOCUMENT) { // 不是文档的结尾
// String name = prase.getName(); // 获取解析器当前指向的元素名称
//
// switch (evenType) {
//
// case XmlPullParser.START_DOCUMENT:
// list = new ArrayList<NewItem>();
// break;
// case XmlPullParser.START_TAG:
// if ("item".equals(name)) {
// nt = new NewItem();
// }
//
// if ("title".equals(name)) {
// nt.setTitle(prase.nextText());
// }
//
// if ("Img".equals(name)) {
// nt.setImg(prase.nextText());
//
// }
//
// break;
//
// case XmlPullParser.END_TAG:
// if ("item".equals(name)) {
// list.add(nt);
// nt = null;
// break;
// }
// }
// evenType = prase.next(); // 进入下一个元素进行解析
// }
// } catch (XmlPullParserException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// } catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
//
// return list;
// }

