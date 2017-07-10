package tool.parser.reuters.entity;

public class ReutersDocument {
	
	private String topicLabelString;
	private String content;
	
	public ReutersDocument() {
		super();
	}
	
	public ReutersDocument(String topicLabelString, String content) {
		super();
		this.topicLabelString = topicLabelString;
		this.content = content;
	}

	public String getTopicLabelString() {
		return topicLabelString;
	}
	
	public void setTopicLabelString(String topicLabelString) {
		this.topicLabelString = topicLabelString;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	
	
}
