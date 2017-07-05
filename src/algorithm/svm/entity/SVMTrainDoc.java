package algorithm.svm.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import algorithm.svm.entity.vector.Point;

public class SVMTrainDoc {
	
	private int topicLabel;
	private List<String> docContent;
	
	
	public SVMTrainDoc(int topicLabel, List<String> docContent) {
		super();
		this.topicLabel = topicLabel;
		this.docContent = docContent;
	}
	
	
	//toVector
	public SVMVector toVector(List<String> vocabularyList) {
		
		if(this.docContent!=null) {
			
			List<Point> vectorPoints = new ArrayList<>();
			int position = 0;
			
			for(String word : vocabularyList) {
				
				Point vectorPoint;
				
				if(this.docContent.contains(word)) {
					vectorPoint = new Point(position, 1);
				} else {
					vectorPoint = new Point(position, 0);
				}
				
				vectorPoints.add(vectorPoint);
				position++;
				
			}
			
			if(vectorPoints.size()>0) {
				return new SVMVector(this.topicLabel, vectorPoints);
			}
			
		}
		
		
		return null;
	}

	public int getTopicLabel() {
		return topicLabel;
	}
	
	public void setTopicLabel(int topicLabel) {
		this.topicLabel = topicLabel;
	}
	
	public List<String> getDocContent() {
		return docContent;
	}
	
	public void setDocContent(List<String> docContent) {
		this.docContent = docContent;
	}
	
	
	
}
