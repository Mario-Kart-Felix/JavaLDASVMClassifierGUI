package main.test;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import adpater.file.TextFileAdapter;
import tool.parser.reuters.ReutersParser;
import tool.parser.reuters.entity.ReutersDocument;

public class ReutersParserTest {
	
	private static TextFileAdapter textFileAdapter = new TextFileAdapter();
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		
		//R52
		/*String reutersDataInputFilePath = System.getProperty("user.dir")
				+ "/data/dataset/Reuters-21578-R52/training/r52-train-all-terms.txt";*/
		
		//R8
		String reutersDataInputFilePath = System.getProperty("user.dir")
				+ "/data/dataset/Reuters-21578-R8/training/no-stop/r8-train-no-stop.txt";
		
		String corpusRootDirPath = System.getProperty("user.dir") + "/data/dataset/training/Reuters-21578-R8"; 
		
		ReutersParser reutersParser = new ReutersParser();
		List<String> topics = reutersParser.parseAllTopicFromFile(reutersDataInputFilePath);

		System.out.println("Total topic -> [" + topics.size() + "]");
		
		for (String topic : topics) {
			
			List<ReutersDocument> belongTopicDocs = reutersParser.parseDocByTopicFromFile(topic,
					reutersDataInputFilePath);
			
			
			String topicFolderNamePath = corpusRootDirPath + "/" + topic;
			if(!new File(topicFolderNamePath).exists()) {
				//new File(topicFolderNamePath).mkdirs();
			}
			
			for(ReutersDocument doc : belongTopicDocs) {
				//String docFileNamePath = topicFolderNamePath + "/" + dateFormat.format(new Date()) + ".txt";
				//textFileAdapter.writeToDataStringFile(doc.getContent(), docFileNamePath);
			}
			
			System.out.println(topic + "-> [" + belongTopicDocs.size() + "]");
		}

	}

}
