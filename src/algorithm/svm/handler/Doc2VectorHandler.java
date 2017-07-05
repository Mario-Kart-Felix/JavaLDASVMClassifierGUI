package algorithm.svm.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adpater.file.TextFileAdapter;
import algorithm.svm.entity.SVMTrainDoc;
import algorithm.svm.entity.SVMVector;
import tool.textanalyzer.WordTokenizer;

public class Doc2VectorHandler {

	private TextFileAdapter textFileAdapter = new TextFileAdapter();
	private static int allowedWordMinLength = 3;

	private WordTokenizer wordTokenizer;

	public Doc2VectorHandler() {
		super();
		this.wordTokenizer = new WordTokenizer(true, true, true, allowedWordMinLength);
	}

	public List<SVMVector> proceed(List<String> vocabularyList, int assignedLabel, String docCorpusDirPath,
			String outputLogFile) throws IOException {

		List<SVMVector> svmVectors = new ArrayList<>();

		File[] fileList = new File(docCorpusDirPath).listFiles();

		for (File file : fileList) {

			if (file.isFile()) {

				this.textFileAdapter.writeAppendToFile("Processing file: [" + file.getName() + "]", outputLogFile);

				try {

					List<String> docTerms = this.wordTokenizer.proceedFile(file.getPath());

					if (docTerms != null) {

						SVMTrainDoc svmTrainDoc = new SVMTrainDoc(assignedLabel, docTerms);
						SVMVector vector = svmTrainDoc.toVector(vocabularyList);

						if (vector != null) {
							if (vector.toString() != null) {
								this.textFileAdapter.writeAppendToFile("doc2vector: [" + vector.toString() + "]",
										outputLogFile);
								svmVectors.add(vector);
							}
							
						}

					} else {
						continue;
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
					continue;
				}

			}

		}

		return svmVectors;

	}

}
