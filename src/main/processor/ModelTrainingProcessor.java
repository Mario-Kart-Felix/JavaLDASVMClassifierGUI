package main.processor;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import adpater.file.TextFileAdapter;
import algorithm.lda.Corpus;
import algorithm.lda.LdaGibbsSampler;
import algorithm.lda.LdaUtil;
import algorithm.svm.entity.SVMVector;
import algorithm.svm.handler.Doc2VectorHandler;
import algorithm.svm.trainer.SVMTrainProcessor;
import tool.textanalyzer.WordTokenizer;

public class ModelTrainingProcessor implements Runnable {

	private static int allowedWordMinLength = 3;
	private volatile boolean stopped = false;

	private Doc2VectorHandler doc2VectorHandler = new Doc2VectorHandler();
	private TextFileAdapter textFileAdapter = new TextFileAdapter();
	private WordTokenizer wordTokenizer;

	// params
	private Thread t;
	private String inputFolderPath;
	private String jobLogFolderPath;
	private String jobOverAllLogFilePath;
	private int numberOfTakenOutWord;
	private JTable extractedTopicTable;

	// static
	private static final int numberOfTopic = 1;
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private static final String outputModelRootDirPath = System.getProperty("user.dir") + "/data/svm/model";

	public ModelTrainingProcessor(String inputFolderPath, String jobLogFolderPath, String jobOverAllLogFilePath,
			int numberOfTakenOutWord, JTable extractedTopicTable) {

		this.inputFolderPath = inputFolderPath;
		this.jobLogFolderPath = jobLogFolderPath;
		this.jobOverAllLogFilePath = jobOverAllLogFilePath;
		this.numberOfTakenOutWord = numberOfTakenOutWord;
		this.extractedTopicTable = extractedTopicTable;

		this.wordTokenizer = new WordTokenizer(true, true, true, allowedWordMinLength);

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

	public void stop() {
		if (this.t != null) {
			this.t.interrupt();
		}
	}

	private void startJob() {

		if (this.jobLogFolderPath != null) {

			// create folder for model storing
			File storedModelDir = new File(this.outputModelRootDirPath + "/model_" + dateFormat.format(new Date()));
			if (!storedModelDir.exists()) {
				storedModelDir.mkdirs();
			}

			String topicListFilePath = storedModelDir.getAbsolutePath() + "/" + "topic_list.txt";
			String extractedTopicKeywordFolderPath = storedModelDir.getPath() + "/keywords";
			if (!(new File(extractedTopicKeywordFolderPath).exists())) {
				new File(extractedTopicKeywordFolderPath).mkdirs();
			}

			String outputTrainingVectorFolderPath = storedModelDir.getPath() + "/vectors";
			if (!(new File(outputTrainingVectorFolderPath).exists())) {
				new File(outputTrainingVectorFolderPath).mkdirs();
			}

			String outputVocabularyFilePath = storedModelDir.getAbsolutePath() + "/" + "vocabularies.txt";
			String outputAllTrainingVectorFilePath = storedModelDir.getAbsolutePath() + "/" + "training_vectors.txt";
			String outputModelFilePath = storedModelDir.getAbsolutePath() + "/" + "model.txt";

			Long startTime = new Date().getTime();

			this.textFileAdapter.writeAppendToFile("**Step 1: Extracting keywords from each topic by LDA\n",
					this.jobOverAllLogFilePath);

			File[] topicFolders = new File(inputFolderPath).listFiles();

			if (topicFolders != null && topicFolders.length > 0) {

				for (File topicFolder : topicFolders) {

					if (topicFolder.isDirectory()) {

						try {

							String topicName = topicFolder.getName();

							this.textFileAdapter.writeAppendToFile("Processing topic: -> [" + topicName + "]",
									this.jobOverAllLogFilePath);
							this.textFileAdapter.writeAppendToFile(topicName, topicListFilePath);

							String outputKeywordForTopicFilePath = extractedTopicKeywordFolderPath + "/" + topicName
									+ ".txt";

							this.extractWordFromDocFolder(topicFolder.getAbsolutePath(), outputKeywordForTopicFilePath);
							
							// tables
							Object[] row = {topicName, topicFolder.getAbsolutePath() };
							DefaultTableModel tableModel = (DefaultTableModel) this.extractedTopicTable.getModel();
							tableModel.addRow(row);
							
							this.textFileAdapter.writeAppendToFile("-------------------------\n",
									this.jobOverAllLogFilePath);

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}

				this.textFileAdapter.writeAppendToFile("--> Done \n", this.jobOverAllLogFilePath);

				// step 2: merging the keyword and store to vocabulary file

				this.textFileAdapter.writeAppendToFile("**Step 2: Merging the keyword and store to vocabulary file\n",
						this.jobOverAllLogFilePath);
				this.mergeVocabularyList(extractedTopicKeywordFolderPath, outputVocabularyFilePath);
				this.textFileAdapter.writeAppendToFile("--> Done \n", this.jobOverAllLogFilePath);

				// step 3: generate training vector for each topic from
				// document's corpus
				this.textFileAdapter.writeAppendToFile(
						"**Step 3: Generate training vector for each topic from provided document's corpus\n",
						this.jobOverAllLogFilePath);
				int assignedTopicLabel = 1;
				List<String> vocabularyList = this.textFileAdapter
						.parseSingleFileToListString(outputVocabularyFilePath);

				for (File topicFolder : topicFolders) {

					String topicName = topicFolder.getName();
					this.textFileAdapter.writeAppendToFile("Processing topic: -> [" + topicName + "]",
							this.jobOverAllLogFilePath);
					String topicOutputVectorFilePath = outputTrainingVectorFolderPath + "/" + topicName + ".txt";
					this.generateTrainingVectorForEachTopicFromCorpus(topicFolder.getAbsolutePath(), assignedTopicLabel,
							vocabularyList, topicOutputVectorFilePath);

					assignedTopicLabel++;
					this.textFileAdapter.writeAppendToFile("-------------------------\n", this.jobOverAllLogFilePath);
				}

				List<String> allVectors = this.textFileAdapter
						.parseMulipleFileToListString(outputTrainingVectorFolderPath);
				if (allVectors != null & allVectors.size() > 0) {
					this.textFileAdapter.writeToDataFile(allVectors, outputAllTrainingVectorFilePath);
				}

				this.textFileAdapter.writeAppendToFile("--> Done \n", this.jobOverAllLogFilePath);

				// step 4: training the svm's model based on provided training
				// vectors
				this.textFileAdapter.writeAppendToFile(
						"**Step 4: Training the svm's model based on provided training vectors\n",
						this.jobOverAllLogFilePath);
				SVMTrainProcessor svmTrainProcessor = new SVMTrainProcessor(outputAllTrainingVectorFilePath,
						outputModelFilePath);
				try {
					svmTrainProcessor.run();
					this.textFileAdapter.writeAppendToFile("--> Done \n", this.jobOverAllLogFilePath);
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

	}

	// extractWordFromDocFolder
	private void extractWordFromDocFolder(String corpusFolderPath, String outputFilePath) throws IOException {

		// 1. Load corpus from disk
		Corpus corpus = Corpus.load(corpusFolderPath, this.wordTokenizer);

		// 2. Create a LDA sampler
		LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());

		// 3. Train it
		ldaGibbsSampler.gibbs(this.numberOfTopic);

		// 4. The phi matrix is a LDA model, you can use LdaUtil to
		// explain it.
		double[][] phi = ldaGibbsSampler.getPhi();

		Map<String, Double>[] topicMaps = LdaUtil.translate(phi, corpus.getVocabulary(), this.numberOfTakenOutWord);

		List<String> entries = new ArrayList<>();

		for (Map<String, Double> topicMap : topicMaps) {
			// System.out.printf("topic %d :\n", i++);
			for (Map.Entry<String, Double> entry : topicMap.entrySet()) {
				entries.add(entry.toString());
				this.textFileAdapter.writeAppendToFile(entry.toString(), this.jobOverAllLogFilePath);
			}

		}

		textFileAdapter.writeToDataFile(entries, outputFilePath);
		LdaUtil.explain(topicMaps);

	}

	// mergeVocabularyList
	private void mergeVocabularyList(String storedTopicKeywordFolderPath, String outputVocabularyFilePath) {

		File storedTopicKeywordFolder = new File(storedTopicKeywordFolderPath);

		File[] keywordFiles = storedTopicKeywordFolder.listFiles();

		if (keywordFiles != null && keywordFiles.length > 0) {

			List<String> mergedKeywordList = new ArrayList<>();

			for (File keywordFile : storedTopicKeywordFolder.listFiles()) {
				if (keywordFile.isFile()) {
					List<String> keywordPairs = textFileAdapter
							.parseSingleFileToListString(keywordFile.getAbsolutePath());
					for (String keywordPair : keywordPairs) {
						String[] splits = keywordPair.split("=");
						String keyword = splits[0].trim();
						if (!mergedKeywordList.contains(keyword)) {
							mergedKeywordList.add(keyword);
						}
					}
				}
			}

			// sorting
			Collections.sort(mergedKeywordList, String.CASE_INSENSITIVE_ORDER);

			// write data to file
			this.textFileAdapter.writeToDataFile(mergedKeywordList, outputVocabularyFilePath);

		}

	}

	// generateTrainingVectorForEachTopicFromCorpus
	private void generateTrainingVectorForEachTopicFromCorpus(String topicCorpusFolderPath, int assignedTopicLabel,
			List<String> vocabularyList, String outputVectorFilePath) {

		if (new File(topicCorpusFolderPath).isDirectory()) {
			List<String> topicVectorStrings = new ArrayList<>();
			try {
				List<SVMVector> trainDocVectors = this.doc2VectorHandler.proceed(vocabularyList, assignedTopicLabel,
						topicCorpusFolderPath, this.jobOverAllLogFilePath);
				for (SVMVector vector : trainDocVectors) {
					if (vector != null) {

						if (vector.toString() != null) {

							if (vector.getPoints().size() > 0) {
								topicVectorStrings.add(vector.toString());
							}

						}

					}
				}

				this.textFileAdapter.writeToDataFile(topicVectorStrings, outputVectorFilePath);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
