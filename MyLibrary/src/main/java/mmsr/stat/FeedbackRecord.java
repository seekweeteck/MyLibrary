package mmsr.stat;

public class FeedbackRecord {
	private int id;
	private Float rating;
	private int parent;
	private int sibling;
	private int others;
	
	public FeedbackRecord(){
		setId(0);
		setRating((float) 0.0);
		setParent(0);
		setSibling(0);
		setOthers(0);
	}
	
	public Float getRating() {
		return rating;
	}
	public void setRating(Float rating) {
		this.rating = rating;
	}
	public int getParent() {
		return parent;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}
	public int getSibling() {
		return sibling;
	}
	public void setSibling(int sibling) {
		this.sibling = sibling;
	}
	public int getOthers() {
		return others;
	}
	public void setOthers(int others) {
		this.others = others;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
