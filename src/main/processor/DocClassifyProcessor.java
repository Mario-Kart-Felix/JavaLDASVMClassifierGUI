package main.processor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import adpater.file.TextFileAdapter;
import algorithm.svm.entity.SVMVector;
import algorithm.svm.handler.Doc2VectorHandler;
import algorithm.svm.predictor.SVMPredictProcessor;

public class DocClassifyProcessor implements Runnable {

	private Doc2VectorHandler doc2VectorHandler = new Doc2VectorHandler();
	private TextFileAdapter textFileAdapter = new TextFileAdapter();

	// params
	private Thread t;
	private String inputFolderPath;
	private String jobLogFolderPath;
	private String jobOverAllLogFilePath;
	private JTable outputTable;

	// static
	private static final String modelFilePath = System.getProperty("user.dir") + "/data/svm/model/model.txt";
	private static final String topicLabelListFilePath = System.getProperty("user.dir") + "/data/corpus/topic_list.txt";
	private static final String vocabularyListFilePath = System.getProperty("user.dir")
			+ "/data/corpus/vocabularies.txt";

	public DocClassifyProcessor(String inputFolderPath, String jobLogFolderPath, 
			String jobOverAllLogFilePath, JTable outputTable) {
		this.inputFolderPath = inputFolderPath;
		this.jobLogFolderPath = jobLogFolderPath;
		this.jobOverAllLogFilePath = jobOverAllLogFilePath;
		this.outputTable = outputTable;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.startJob();

	}

	public void start() {
		Thread t = new Thread(this);
		t.start();
	}

	private void startJob() {

		if (this.jobLogFolderPath != null) {

			Long startTime = new Date().getTime();

			int assignedTopicLabel = -1;
			List<String> topics = this.textFileAdapter.parseSingleFileToListString(this.topicLabelListFilePath);
			String doc2VectorOutputFilePath = this.jobLogFolderPath + "/doc2vector.txt";
			String predictOutputFilePath = this.jobLogFolderPath + "/predict.txt";

			try {

				File[] inputFiles = new File(this.inputFolderPath).listFiles();

				this.convertDoc2VectorFromFolder(assignedTopicLabel, this.inputFolderPath, doc2VectorOutputFilePath);

				SVMPredictProcessor svmPredictProcessor = new SVMPredictProcessor(this.modelFilePath,
						doc2VectorOutputFilePath, predictOutputFilePath);
				svmPredictProcessor.run();
				List<String> predictOutputs = textFileAdapter.parseSingleFileToListString(predictOutputFilePath);

				int docCount = 0;
				predictOutputs.remove(0);

				for (String predictOutput : predictOutputs) {

					// System.out.println(predictOutput);

					String[] splitOutputs = predictOutput.split(" ");

					// String dataCol1 = testTextDocuments.get(count).fileName;

					int docLabelIndex = (int) Double.parseDouble(splitOutputs[0]);

					String documentName = inputFiles[docCount].getName();
					String predictedTopic = topics.get(docLabelIndex - 1);
					String accuracyValue = String.valueOf(Double.parseDouble(splitOutputs[docLabelIndex]) * 100);

					Object[] row = { documentName, predictedTopic, accuracyValue };
					DefaultTableModel tableModel = (DefaultTableModel) this.outputTable.getModel();

					tableModel.addRow(row);
					docCount++;

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Long endTime = new Date().getTime();
			this.textFileAdapter.writeAppendToFile("Finished in: -> [" + (endTime - startTime) + "] (ms)",
					this.jobOverAllLogFilePath);
			this.textFileAdapter.writeAppendToFile(">>END<<", this.jobOverAllLogFilePath);

		}

	}

	private void convertDoc2VectorFromFolder(int assignedTopicLabel, String folderPath, String doc2VectorOutputFilePath)
			throws IOException {

		List<String> vocabularyList = this.textFileAdapter.parseSingleFileToListString(this.vocabularyListFilePath);

		List<String> allVectorString = new ArrayList<>();
		List<SVMVector> svmVectors = this.doc2VectorHandler.proceed(vocabularyList, assignedTopicLabel, folderPath,
				this.jobOverAllLogFilePath);

		for (SVMVector vector : svmVectors) {

			if (vector != null) {

				if (vector.toString() != null) {
					allVectorString.add(vector.toString());
					// this.textFileAdapter.writeAppendToFile(vector.toString(),
					// this.jobOverAllLogFilePath);
					// out.println(vector.toString());
				}

			} else {
				continue;
			}

		}

		// write data to file
		textFileAdapter.writeToDataFile(allVectorString, doc2VectorOutputFilePath);

	}

}
