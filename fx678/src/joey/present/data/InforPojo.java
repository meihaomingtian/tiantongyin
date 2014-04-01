package joey.present.data;

public class InforPojo {
	// 标题
	private String title_;
	// 内容
	private String content_;
	// 发布时间
	private String time_;
	// 链接
	private String link_;
	// 来源
	private String author_;
	
	//图片资源
	private String img;

	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getAuthor_() {
		return author_;
	}
	public void setAuthor_(String author) {
		author_ = author;
	}
	public String getTitle_() {
		return title_;
	}
	public void setTitle_(String title) {
		title_ = title;
	}
	public String getContent_() {
		return content_;
	}
	public void setContent_(String content) {
		content_ = content;
	}
	public String getTime_() {
		return time_;
	}
	public void setTime_(String time) {
		time_ = time;
	}
	public String getLink_() {
		return link_;
	}
	public void setLink_(String link) {
		link_ = link;
	}

	public String toString() {
		String result = "";
		result = "Title: " + title_ + "time: " + time_ + "Description: "
				+ content_ + "Link: " + link_ + "Author" + author_ + "Img" + img;
		return result;
	}
}
