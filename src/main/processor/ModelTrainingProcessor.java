package main.processor;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;

import adpater.file.TextFileAdapter;
import algorithm.lda.Corpus;
import algorithm.lda.LdaGibbsSampler;
import algorithm.lda.LdaUtil;
import algorithm.svm.handler.Doc2VectorHandler;
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
		if(this.t!=null) {
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

			Long startTime = new Date().getTime();

			File[] topicFolders = new File(inputFolderPath).listFiles();

			if (topicFolders != null && topicFolders.length > 0) {

				for (File topicFolder : topicFolders) {
					
					if (topicFolder.isDirectory()) {

						String topicName = topicFolder.getName();

						String outputKeywordForTopicFilePath = storedModelDir.getAbsolutePath() + "/" + topicName
								+ "_keywords.txt";
						this.textFileAdapter.writeAppendToFile(topicFolder.getName(), topicListFilePath);
						try {
							this.extractWordFromDocFolder(topicFolder.getAbsolutePath(), outputKeywordForTopicFilePath);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}

		}

	}

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
			}

		}

		textFileAdapter.writeToDataFile(entries, outputFilePath);
		LdaUtil.explain(topicMaps);

	}

}
