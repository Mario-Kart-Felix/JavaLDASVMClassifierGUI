package main.handler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OutputLogHandler {

	private static final String overallLogFilePath = System.getProperty("user.dir") + "/data/logs";
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	public OutputLogHandler() {
	}

	public String generateLogFolder() {

		String logFolderName = dateFormat.format(new Date());
		Path logFolderPath = Paths.get(this.overallLogFilePath + "/" + logFolderName);

		if (!Files.exists(logFolderPath)) {
			try {
				Files.createDirectories(logFolderPath);
				return logFolderPath.toString();
			} catch (IOException e) {
				// fail to create directory
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}

}
