package main.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adpater.file.TextFileAdapter;
import algorithm.lda.Corpus;
import algorithm.lda.LdaGibbsSampler;
import algorithm.lda.LdaUtil;
import tool.textanalyzer.WordTokenizer;

public class LDATest {

	private static WordTokenizer wordTokenizer;
	private static int allowedWordMinLength = 3;

	private static int numberOfTopic = 5;
	private static int numberOfTakenOutWord = 20;
	private static String currentUserDirPath = System.getProperty("user.dir");

	public static void main(String[] args) throws IOException {
		
		// TODO Auto-generated method stub
		
		//String corpusFolderPath = currentUserDirPath + "/" + "/data/dataset/training/topics_dir_10/data_mining";
		String topicName = "information_retrieval";
		TextFileAdapter textFileAdapter = new TextFileAdapter();
		String corpusFolderPath = "C:\\Workspace\\DocTopicLabel_LDA"
				+ "\\data\\output\\svm\\predict\\test_set\\" + topicName;
		String outputTestSetFilePath = "C:\\Workspace\\DocTopicLabel_LDA\\data\\output\\"
				+ "svm\\predict\\test_set\\"+topicName+"_KL.txt";
		wordTokenizer = new WordTokenizer(true, true, true, allowedWordMinLength);
		
		File[] testDocFiles = new File(corpusFolderPath).listFiles();
		
		for(File testDocFile : testDocFiles) {
			
			if(testDocFile.isFile()) {
				
				try {
					
					// 1. Load corpus from disk
					//Corpus corpus = Corpus.load(corpusFolderPath, wordTokenizer);
					Corpus corpus = Corpus.loadSingleFile(testDocFile.getAbsolutePath(), wordTokenizer);

					// 2. Create a LDA sampler
					LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());

					// 3. Train it
					ldaGibbsSampler.gibbs(numberOfTopic);

					// 4. The phi matrix is a LDA model, you can use LdaUtil to
					// explain it.
					double[][] phi = ldaGibbsSampler.getPhi();
					Map<String, Double>[] topicMaps = LdaUtil.translate(phi, corpus.getVocabulary(), numberOfTakenOutWord);

					double[][] theta = ldaGibbsSampler.getTheta();

					LdaUtil.explain(topicMaps);
					LdaUtil.dispTheta(theta);
					LdaUtil.displayProbTopic(theta);
					LdaUtil.dispThetaInNum(theta);

					int maxProbDistributedTopicPos = getMaxProbDistributedTopicOverCorpus(theta);
					
					//System.out.println("Max distributed topic probability over corpus -> [" + maxProbDistributedTopicPos + "]");
					
					Map<String, Double> toMap = topicMaps[maxProbDistributedTopicPos];
					List<String> output = new ArrayList<>();
					for (Map.Entry<String, Double> entry : toMap.entrySet()) {
						System.out.println(entry.toString());
						textFileAdapter.writeAppendToFile(entry.toString(), outputTestSetFilePath);
						output.add(entry.toString());
					}
					
					textFileAdapter.writeAppendToFile("---", outputTestSetFilePath);
					
				} catch(Exception e) {
					e.printStackTrace();
					continue;
				}
				
				
				
			}
		}
		
		

	}

	public static int getMaxProbDistributedTopicOverCorpus(double[][] theta) {

		double[] totalProbistributedTopicMaps = new double[theta[0].length];
		int maxProbDistributedTopicPos = 0;

		for (int k = 0; k < theta[0].length; k++) {

			double totalProb = 0;

			for (int m = 0; m < theta.length; m++) {
				totalProb += theta[m][k];
			}

			totalProbistributedTopicMaps[k] = totalProb;

			if (totalProb > totalProbistributedTopicMaps[maxProbDistributedTopicPos]) {
				maxProbDistributedTopicPos = k;
			}

		}

		return maxProbDistributedTopicPos;

	}

}
