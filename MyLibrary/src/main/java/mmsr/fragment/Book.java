package mmsr.fragment;

import java.util.List;
import java.util.Map;

public class Book {
	private int currentPage = 0;
	private int totalPage;
	private String title;
	private List<String> picture;
	private Map<String, List<String>> script;
	
	public Book(){};
	
	public Book(int totalPage, String title) {
		super();
		this.currentPage = 0;
		this.totalPage = totalPage;
		this.title = title;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public List<String> getPicture() {
		return picture;
	}

	public void setPicture(List<String> picture) {
		this.picture = picture;
		
		//Assign size of picture list to total page
		setTotalPage(this.picture.size());
	}

	public Map<String, List<String>> getScript() {
		return script;
	}

	public void setScript(Map<String, List<String>> script) {
		this.script = script;
	}
	
	public String getScriptByPage(String language, int i){
		return script.get(language).get(i).toString();
	}
	
	public void gotoNext(){
		if(currentPage < totalPage -1){
			this.currentPage++;
			setCurrentPage(this.currentPage);
		}
	}
	
	public void gotoPrevious(){
		if(currentPage > 0){
			this.currentPage--;
			setCurrentPage(this.currentPage);
		}
	}
	
	public void gotoFirst(){
		setCurrentPage(0);
	}
	
	public void gotoLast(){
		setCurrentPage(this.totalPage);
	}
	
	public String getCurrentPicture(){
		return picture.get(currentPage);
	}
}
