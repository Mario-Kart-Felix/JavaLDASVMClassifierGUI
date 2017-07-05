package tool.textanalyzer;

import java.util.ArrayList;
import java.util.List;

import adpater.file.TextFileAdapter;

public class WordTokenizer {

	// TODO Auto-generated method stub
	private static final String TEXT_PATTERN_REGEX = "[^A-Za-z0-9_ ]";

	private StopwordFilter stopwordFilter;

	private boolean toLowerCase;
	private boolean removeSpecialChar;
	private boolean removeStopword;
	private int allowedWordMinLength;

	public WordTokenizer(boolean toLowerCase, boolean removeSpecialChar, boolean removeStopword,
			int allowedWordMinLength) {

		super();
		// TODO Auto-generated constructor stub
		this.stopwordFilter = new StopwordFilter();

		this.toLowerCase = toLowerCase;
		this.removeSpecialChar = removeSpecialChar;
		this.removeStopword = removeStopword;
		this.allowedWordMinLength = allowedWordMinLength;

	}

	// proceedFile
	public List<String> proceedFile(String inputFile) {

		TextFileAdapter textFileAdapter = new TextFileAdapter();
		return this.proceedText(textFileAdapter.parseSingleFileToString(inputFile));

	}

	// proceedText
	public List<String> proceedText(String text) {

		List<String> analyzedTokens = new ArrayList<>();

		String[] tokens = text.split(" ");

		for (String token : tokens) {

			String returnToken = token.trim();

			// allowedWordMinLength
			if (returnToken.length() < this.allowedWordMinLength) {
				continue;
			}

			// removeSpecialChar
			if (this.removeSpecialChar) {
				returnToken = returnToken.replaceAll(TEXT_PATTERN_REGEX, "");
			}

			// toLowerCase
			if (this.toLowerCase) {
				returnToken = returnToken.toLowerCase();
			}

			// remove stopword
			if (this.removeStopword) {
				if (this.stopwordFilter.isStopword(returnToken)) {
					continue;
				}
			}

			analyzedTokens.add(returnToken);
			// out.println(returnToken);

		}

		return analyzedTokens;

		// return null;
	}

}
