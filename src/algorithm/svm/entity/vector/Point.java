package algorithm.svm.entity.vector;

public class Point {
	
	private int position;
	private int value;
	
	
	public Point(int position, int value) {
		super();
		this.position = position;
		this.value = value;
	}


	public int getPosition() {
		return position;
	}


	public void setPosition(int position) {
		this.position = position;
	}


	public int getValue() {
		return value;
	}


	public void setValue(int value) {
		this.value = value;
	}
	
	
	
	
	
}
