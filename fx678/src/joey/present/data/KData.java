package joey.present.data;

public class KData {

	private String k_timeLong;
	private String k_date;
	private String k_close;
	private String k_open;
	private String k_high;
	private String k_low;
	private String k_money;
	private String k_total;
	private String k_volume;
	private String k_average;

	public String getK_timeLong() {
		return k_timeLong;
	}

	public void setK_timeLong(String k_timeLong) {
		this.k_timeLong = k_timeLong;
	}

	public String getK_volume() {
		return k_volume;
	}

	public void setK_volume(String k_volume) {
		this.k_volume = k_volume;
	}

	public String getK_average() {
		return k_average;
	}

	public void setK_average(String k_average) {
		this.k_average = k_average;
	}

	public String getK_date() {
		return k_date;
	}

	public void setK_date(String k_date) {
		this.k_date = k_date;
	}

	public String getK_close() {
		return k_close;
	}

	public void setK_close(String k_close) {
		this.k_close = k_close;
	}

	public String getK_open() {
		return k_open;
	}

	public void setK_open(String k_open) {
		this.k_open = k_open;
	}

	public String getK_high() {
		return k_high;
	}

	public void setK_high(String k_high) {
		this.k_high = k_high;
	}

	public String getK_low() {
		return k_low;
	}

	public void setK_low(String k_low) {
		this.k_low = k_low;
	}

	public String getK_money() {
		return k_money;
	}

	public void setK_money(String k_money) {
		this.k_money = k_money;
	}

	public String getK_total() {
		return k_total;
	}

	public void setK_total(String k_total) {
		this.k_total = k_total;
	}

	@Override
	public String toString() {
		return "KData/timeLong=" + k_timeLong + ",date=" + k_date + ",close=" + k_close + ",open="
				+ k_open + "high=" + k_high + "low=" + k_low + "]";
	}

}
