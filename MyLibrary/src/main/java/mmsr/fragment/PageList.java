package mmsr.fragment;

import java.util.ArrayList;

/** Contains getter and setter method for varialbles  */
public class PageList {

	/** Variables */
	private ArrayList<String> en = new ArrayList<String>();
	private ArrayList<String> bm = new ArrayList<String>();
	private ArrayList<String> cn = new ArrayList<String>();
	private ArrayList<String> py = new ArrayList<String>();
	private ArrayList<String> tm = new ArrayList<String>();
	private ArrayList<String> picture = new ArrayList<String>();
	
	/** In Setter method default it will return arraylist 
	 *  change that to add  */
	
	public ArrayList<String> getEN() {
		return en;
	}

	public void setEN(String script) {
		this.en.add(script);
	}

	public ArrayList<String> getBM() {
		return bm;
	}

	public void setBM(String script) {
		this.bm.add(script);
	}
	
	public ArrayList<String> getCN() {
		return cn;
	}

	public void setCN(String script) {
		this.cn.add(script);
	}
	
	public ArrayList<String> getPY() {
		return py;
	}

	public void setPY(String script) {
		this.py.add(script);
	}
	
	public ArrayList<String> getTM() {
		return tm;
	}

	public void setTM(String script) {
		this.tm.add(script);
	}
	
	public ArrayList<String> getPicture() {
		return picture;
	}

	public void setPicture(String file) {
		this.picture.add(file);
	}
}
