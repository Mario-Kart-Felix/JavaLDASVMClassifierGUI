// package com.hankcs.lda;
package algorithm.lda;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import tool.textanalyzer.StopwordFilter;
import tool.textanalyzer.WordTokenizer;

/**
 *
 * @author hankcs
 */
public class Corpus {

	static final int allowedMinWordLength = 5;
	static StopwordFilter stopwordFilter = new StopwordFilter();

	List<int[]> documentList;
	Vocabulary vocabulary;

	public Corpus() {
		documentList = new LinkedList<int[]>();
		vocabulary = new Vocabulary();
	}

	public int[] addDocument(List<String> document) {
		int[] doc = new int[document.size()];
		int i = 0;
		for (String word : document) {
			doc[i++] = vocabulary.getId(word, true);
		}
		documentList.add(doc);
		return doc;
	}

	public int[][] toArray() {
		return documentList.toArray(new int[0][]);
	}

	public int getVocabularySize() {
		return vocabulary.size();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (int[] doc : documentList) {
			sb.append(Arrays.toString(doc)).append("\n");
		}
		sb.append(vocabulary);
		return sb.toString();
	}

	/**
	 * Load documents from disk
	 *
	 * @param folderPath
	 *            is a folder, which contains text documents.
	 * @return a corpus
	 * @throws IOException
	 */

	// loadSingleFile
	public static Corpus loadSingleFile(String filePath, WordTokenizer wordTokenizer) throws IOException {

		Corpus corpus = new Corpus();

		File file = new File(filePath);

		if (file.isFile()) {

			// truong hop file
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String line;
			List<String> wordList = new LinkedList<String>();

			while ((line = br.readLine()) != null) {
				// String[] words = line.split(" ");
				List<String> words = wordTokenizer.proceedText(line);
				for (String word : words) {

					if (word.trim().length() < allowedMinWordLength)
						continue;
					wordList.add(word);

				}
			}

			br.close();

			corpus.addDocument(wordList);

			if (corpus.getVocabularySize() == 0) {
				return null;
			}

		}

		return corpus;

	}

	// load
	public static Corpus load(String folderPath, WordTokenizer wordTokenizer) throws IOException {

		Corpus corpus = new Corpus();

		File folder = new File(folderPath);

		if (folder.isDirectory()) {

			for (File file : folder.listFiles()) {

				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				String line;
				List<String> wordList = new LinkedList<String>();

				while ((line = br.readLine()) != null) {

					// String[] words = line.split(" ");

					List<String> words = wordTokenizer.proceedText(line);

					for (String word : words) {

						if (word.trim().length() < allowedMinWordLength)
							continue;
						wordList.add(word);

					}
				}

				br.close();

				corpus.addDocument(wordList);

			}

			if (corpus.getVocabularySize() == 0) {
				return null;
			}

		}

		return corpus;

	}

	public Vocabulary getVocabulary() {
		return vocabulary;
	}

	public int[][] getDocument() {
		return toArray();
	}

	public static int[] loadDocument(String path, Vocabulary vocabulary, WordTokenizer wordTokenizer)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
		List<Integer> wordList = new LinkedList<Integer>();
		while ((line = br.readLine()) != null) {
			// String[] words = line.split(" ");
			List<String> words = wordTokenizer.proceedText(line);
			for (String word : words) {

				if (word.trim().length() < allowedMinWordLength)
					continue;
				Integer id = vocabulary.getId(word);
				if (id != null)
					wordList.add(id);

			}
		}
		br.close();
		int[] result = new int[wordList.size()];
		int i = 0;
		for (Integer integer : wordList) {
			result[i++] = integer;
		}
		return result;
	}
}