package algorithm.svm.entity;

import java.util.List;

import algorithm.svm.entity.vector.Point;

public class SVMVector {

	private int topicLabel;
	private List<Point> points;

	public SVMVector(int topicLabel, List<Point> points) {
		super();
		this.topicLabel = topicLabel;
		this.points = points;
	}

	@Override
	public String toString() {
		int positivePoint = 0;
		if (this.points.size() > 0) {

			String vectorString = this.topicLabel + " ";
			for (Point point : this.points) {
				if (point.getValue() == 1) {
					vectorString += point.getPosition() + ":" + point.getValue() + " ";
					positivePoint++;
				}
			}

			if (positivePoint > 0) {
				return vectorString.trim();
			}
			return null;

		}
		return null;
	}

	public int getTopicLabel() {
		return topicLabel;
	}

	public void setTopicLabel(int topicLabel) {
		this.topicLabel = topicLabel;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

}
