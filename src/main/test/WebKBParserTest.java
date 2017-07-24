package main.test;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import adpater.file.TextFileAdapter;
import tool.entity.Document;
import tool.parser.CorpusDocumentParser;

public class WebKBParserTest {

	private static TextFileAdapter textFileAdapter = new TextFileAdapter();
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	public static void main(String[] args) {

		// TODO Auto-generated method stub

		// 20ng-train-no-stop.txt
		String reutersDataInputFilePath = System.getProperty("user.dir")
				+ "/data/dataset/WebKB/webkb-train-stemmed.txt";

		String corpusRootDirPath = System.getProperty("user.dir") + "/data/dataset/training/WebKB";

		CorpusDocumentParser reutersParser = new CorpusDocumentParser();
		List<String> topics = reutersParser.parseAllTopicFromFile(reutersDataInputFilePath);

		System.out.println("Total topic -> [" + topics.size() + "]");

		for (String topic : topics) {

			List<Document> belongTopicDocs = reutersParser.parseDocByTopicFromFile(topic, reutersDataInputFilePath);

			String topicFolderNamePath = corpusRootDirPath + "/" + topic;
			if (!new File(topicFolderNamePath).exists()) {
				new File(topicFolderNamePath).mkdirs();
			}

			for (Document doc : belongTopicDocs) {
				String docFileNamePath = topicFolderNamePath + "/" + dateFormat.format(new Date()) + ".txt";
				textFileAdapter.writeToDataStringFile(doc.getContent(), docFileNamePath);
			}

			System.out.println(topic + "-> [" + belongTopicDocs.size() + "]");
		}

	}

}
