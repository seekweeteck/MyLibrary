package mmsr.stat;

public class ReadRecord {
	private int r_id;
	private String r_date;
	private String r_time;
	private int r_en;
	private int r_bm;
	private int r_cn;
	private int r_tm;

	public ReadRecord() {
	}

	public ReadRecord(int id, String date, String time) {
		setR_id(r_id);
		setR_date(r_date);
		setR_time(r_time);
		setR_en(0);
		setR_bm(0);
		setR_cn(0);
		setR_tm(0);
	}

	public void addEN() {
		r_en++;
	}

	public void addBM() {
		r_bm++;
	}

	public void addCN() {
		r_cn++;
	}

	public void addTM() {
		r_tm++;
	}

	public String getR_date() {
		return r_date;
	}

	public void setR_date(String r_date) {
		this.r_date = r_date;
	}

	public String getR_time() {
		return r_time;
	}

	public void setR_time(String r_time) {
		this.r_time = r_time;
	}

	public int getR_id() {
		return r_id;
	}

	public void setR_id(int r_id) {
		this.r_id = r_id;
	}

	public int getR_en() {
		return r_en;
	}

	public void setR_en(int r_en) {
		this.r_en = r_en;
	}

	public int getR_bm() {
		return r_bm;
	}

	public void setR_bm(int r_bm) {
		this.r_bm = r_bm;
	}

	public int getR_cn() {
		return r_cn;
	}

	public void setR_cn(int r_cn) {
		this.r_cn = r_cn;
	}

	public int getR_tm() {
		return r_tm;
	}

	public void setR_tm(int r_tm) {
		this.r_tm = r_tm;
	}

}
