package main.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithm.lda.Corpus;
import algorithm.lda.LdaGibbsSampler;
import algorithm.lda.LdaUtil;
import tool.textanalyzer.WordTokenizer;

public class LDAKeywordExtraction {

	private static WordTokenizer wordTokenizer;
	private static int allowedWordMinLength = 3;

	public static void main(String[] args) throws IOException {

		// TODO Auto-generated method stub
		int numberOfTopic = 3;
		int numberOfTakenOutWord = 10;
		wordTokenizer = new WordTokenizer(true, true, true, allowedWordMinLength);

		// 1. Load corpus from disk
		String topicCorpusFolder = System.getProperty("user.dir") + "/data/test/training/artificial_intelligence";
		Corpus corpus = Corpus.load(topicCorpusFolder, wordTokenizer);

		// 2. Create a LDA sampler
		LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());

		// 3. Train it
		ldaGibbsSampler.gibbs(numberOfTopic);

		// 4. The phi matrix is a LDA model, you can use LdaUtil to
		// explain it.
		double[][] phi = ldaGibbsSampler.getPhi();

		Map<String, Double>[] topicMaps = LdaUtil.translate(phi, corpus.getVocabulary(), numberOfTakenOutWord);

		LdaUtil.explain(topicMaps);
		
		double[][] theta = ldaGibbsSampler.getTheta();

		LdaUtil.dispTheta(theta);
		LdaUtil.dispThetaInNum(theta);
		LdaUtil.displayProbTopic(theta);
		
		
		// LdaUtil.explain(topicMaps);

	}

}
