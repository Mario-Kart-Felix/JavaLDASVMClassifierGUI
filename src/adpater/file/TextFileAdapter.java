package adpater.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TextFileAdapter {

	private static final boolean DISPLAYING_LOG = true;

	public TextFileAdapter() {
	}
	
	//parseMulipleFileToStringForeachFile
	public List<String> parseMulipleFileToStringForeachFile(String dataDirPath) {
		File dataDir = new File(dataDirPath);
		File[] files = dataDir.listFiles();
		if (files.length > 0) {
			List<String> results = new ArrayList<String>();
			for (File file : files) {
				if (file.isFile()) {
					if (DISPLAYING_LOG) {
						System.out.println("**INFO: reading file [" + file.getName() + "]");
					}
					String fileData = this.parseSingleFileToString(file.getPath());
					results.add(fileData);
				}

			}
			return results;
		}
		return null;
	}

	//parseMulipleFileToListString
	public List<String> parseMulipleFileToListString(String dataDirPath) {
		File dataDir = new File(dataDirPath);
		File[] files = dataDir.listFiles();
		if (files.length > 0) {
			List<String> results = new ArrayList<String>();
			for (File file : files) {
				if (file.isFile()) {
					if (DISPLAYING_LOG) {
						System.out.println("**INFO: reading file [" + file.getName() + "]");
					}
					List<String> fileData = this.parseSingleFileToListString(file.getPath());
					results.addAll(fileData);
				}

			}
			return results;
		}
		return null;
	}

	//parseSingleFileToListString
	public List<String> parseSingleFileToListString(String dataFilePath) {
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(dataFilePath)))) {
			List<String> results = new ArrayList<>();
			String line;
			while ((line = reader.readLine()) != null) {
				// System.out.println(line.trim());
				line = line.trim();
				if (line.length() > 0) {
					results.add(line);
				}
			}
			return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//parseSingleFileToString
	public String parseSingleFileToString(String dataFilePath) {
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(dataFilePath)))) {
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				// System.out.println(line.trim());
				line = line.trim();
				if (line.length() > 0) {
					stringBuilder.append(line).append(" ");
				}
			}
			return stringBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void writeToDataFile(List<String> inputData, String outputDataFilePath) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(new File(outputDataFilePath)))) {
			for (String input : inputData) {
				writer.println(input);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeToDataStringFile(String inputData, String outputDataFilePath) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(new File(outputDataFilePath)))) {
			writer.println(inputData);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeAppendToFile(String text, String inputDataFilePath) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(inputDataFilePath, true)));
			out.println(text);
			out.close();
		} catch (IOException e) {
			// exception handling left as an exercise for the reader
			return;
		}
	}

	public void clearTheFileContent(String dataFilePath) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(new File(dataFilePath)))) {
			writer.print("");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
