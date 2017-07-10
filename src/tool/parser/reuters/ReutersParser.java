package tool.parser.reuters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adpater.file.TextFileAdapter;
import tool.parser.reuters.entity.ReutersDocument;

public class ReutersParser {

	private TextFileAdapter textFileAdapter;

	public ReutersParser() {
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

	public List<ReutersDocument> parseFullDocFromFile(String inputFilePath) {

		List<String> lines = this.textFileAdapter.parseSingleFileToListString(inputFilePath);

		if (lines != null && lines.size() > 0) {

			List<ReutersDocument> docs = new ArrayList<>();

			for (String line : lines) {

				if (line.trim().length() > 0) {

					String[] splits = line.trim().split("\t");
					//System.out.println(splits[0] + " - " + splits[1]);
					docs.add(new ReutersDocument(splits[0], splits[1]));
				}

			}

			return docs;

		}

		return null;
	}
	
	public List<ReutersDocument> parseDocByTopicFromFile(String topicLabelString, String inputFilePath) {

		List<String> lines = this.textFileAdapter.parseSingleFileToListString(inputFilePath);

		if (lines != null && lines.size() > 0) {

			List<ReutersDocument> docs = new ArrayList<>();

			for (String line : lines) {

				if (line.trim().length() > 0) {

					String[] splits = line.trim().split("\t");
					if(splits[0].trim().equalsIgnoreCase(topicLabelString)) {
						//System.out.println(splits[0] + " - " + splits[1]);
						docs.add(new ReutersDocument(splits[0], splits[1]));
					}
					
				}

			}

			return docs;

		}

		return null;
	}

}
