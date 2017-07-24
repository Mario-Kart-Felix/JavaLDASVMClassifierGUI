package tool.entity;

public class Document {
	
	private String topicLabelString;
	private String content;
	
	public Document() {
		super();
	}
	
	public Document(String topicLabelString, String content) {
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
