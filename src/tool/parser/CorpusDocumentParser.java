package tool.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adpater.file.TextFileAdapter;
import tool.entity.Document;

public class CorpusDocumentParser {

	private TextFileAdapter textFileAdapter;

	public CorpusDocumentParser() {
		this.textFileAdapter = new TextFileAdapter();
	}

	public List<String> parseAllTopicFromFile(String inputFilePath) {

		List<String> lines = this.textFileAdapter.parseSingleFileToListString(inputFilePath);

		if (lines != null && lines.size() > 0) {

			List<String> topics = new ArrayList<>();

			for (String line : lines) {

				if (line.trim().length() > 0) {

					String[] splits = line.trim().split("\t");
					if (!topics.contains(splits[0].trim())) {
						topics.add(splits[0].trim());
					}
				}

			}

			// sorting A->Z
			Collections.sort(topics, String.CASE_INSENSITIVE_ORDER);

			return topics;

		}

		return null;
	}

	public List<Document> parseFullDocFromFile(String inputFilePath) {

		List<String> lines = this.textFileAdapter.parseSingleFileToListString(inputFilePath);

		if (lines != null && lines.size() > 0) {

			List<Document> docs = new ArrayList<>();

			for (String line : lines) {

				if (line.trim().length() > 0) {

					String[] splits = line.trim().split("\t");
					//System.out.println(splits[0] + " - " + splits[1]);
					docs.add(new Document(splits[0], splits[1]));
				}

			}

			return docs;

		}

		return null;
	}
	
	public List<Document> parseDocByTopicFromFile(String topicLabelString, String inputFilePath) {

		List<String> lines = this.textFileAdapter.parseSingleFileToListString(inputFilePath);

		if (lines != null && lines.size() > 0) {

			List<Document> docs = new ArrayList<>();

			for (String line : lines) {

				if (line.trim().length() > 0) {

					String[] splits = line.trim().split("\t");
					
					if(splits!=null && splits.length==2) {
						
						if(splits[0].trim().length()>0 && splits[1].trim().length()>0) {
							
							if(splits[0].trim().equalsIgnoreCase(topicLabelString)) {
								//System.out.println(splits[0] + " - " + splits[1]);
								docs.add(new Document(splits[0], splits[1]));
							}
							
						}
						
					}
					
				
					
				}

			}

			return docs;

		}

		return null;
	}

}
