package main.listener.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import adpater.file.TextFileAdapter;
import main.MainGUI;
import main.handler.OutputLogHandler;
import main.processor.ModelTrainingProcessor;

public class ModelTrainActionListener implements ActionListener {

	private static final int MAX_WAIT_TIME = 0;

	private TextFileAdapter textFileAdapter = new TextFileAdapter();
	private OutputLogHandler outputLogHandler = new OutputLogHandler();

	// params
	private MainGUI mainGUI;
	private JButton btnStart;
	private JButton btnStop;
	private JButton btnSelectedFolder;
	private JTextField txtNumberOfTakenOutWord;
	private JFileChooser fcSelectedFolder;
	private JTextArea txtOverallLog;
	private JTable tableExtractedTopic;

	// threads
	private ModelTrainingProcessor modelTrainingProcessor;

	// timers
	private Timer overallLogTimer;

	public ModelTrainActionListener(MainGUI mainGUI, JButton btnStart, JButton btnStop, JButton btnSelectedFolder,
			JTextField txtNumberOfTakenOutWord, JFileChooser fcSelectedFolder, JTextArea txtOverallLog,
			JTable tableExtractedTopic) {

		super();
		this.mainGUI = mainGUI;
		this.btnStart = btnStart;
		this.btnStop = btnStop;
		this.btnSelectedFolder = btnSelectedFolder;
		this.txtNumberOfTakenOutWord = txtNumberOfTakenOutWord;
		this.fcSelectedFolder = fcSelectedFolder;
		this.txtOverallLog = txtOverallLog;
		this.tableExtractedTopic = tableExtractedTopic;

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		// TODO Auto-generated method stub
		this.clearDataForm();

		if (this.fcSelectedFolder.getSelectedFile() != null) {

			if (this.isNumberic(this.txtNumberOfTakenOutWord.getText())) {

				String selectedFolderPath = this.fcSelectedFolder.getSelectedFile().getAbsolutePath();
				String jobLogFolderPath = this.outputLogHandler.generateLogFolder();

				if (jobLogFolderPath != null) {

					// init overall log file
					String jobOverAllLogFilePath = jobLogFolderPath + "/overall_logs.txt";
					this.textFileAdapter.writeToDataStringFile(">>START<<", jobOverAllLogFilePath);

					this.disableAllFormElement();

					try {

						// init timer
						this.initializeLogTimer(jobOverAllLogFilePath);

						// start timer
						this.overallLogTimer.start();

						// start threads
						int numberOfTakenOutWord = Integer.parseInt(this.txtNumberOfTakenOutWord.getText());
						this.modelTrainingProcessor = new ModelTrainingProcessor(selectedFolderPath, jobLogFolderPath,
								jobOverAllLogFilePath, numberOfTakenOutWord, this.tableExtractedTopic);
						this.modelTrainingProcessor.start();

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			} else {
				JOptionPane.showMessageDialog(this.mainGUI,
						"The input number of taken word for each topic is invalid !", "Error",
						JOptionPane.ERROR_MESSAGE);
			}

		} else {
			JOptionPane.showMessageDialog(this.mainGUI, "Please select the document's folder for training !", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void initializeLogTimer(String overallLogFilePath) throws FileNotFoundException {

		// overall comments log
		BufferedReader overallLogReader = new BufferedReader(new FileReader(overallLogFilePath));
		this.overallLogTimer = new Timer(MAX_WAIT_TIME, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String line;
				try {
					if ((line = overallLogReader.readLine()) != null) {
						txtOverallLog.append(line + "\n");
						if (line.equals(">>END<<")) {
							enableAllFormElement();
							return;
						}
					}
				} catch (IOException ex) {
					ex.printStackTrace();
					return;
				}
			}
		});

	}

	private void disableAllFormElement() {

		this.btnStart.setEnabled(false);
		this.btnSelectedFolder.setEnabled(false);
		this.txtNumberOfTakenOutWord.setEnabled(false);
		this.btnStop.setEnabled(true);
		this.btnStop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				overallLogTimer.stop();
				clearDataForm();
				enableAllFormElement();
				if (modelTrainingProcessor != null) {
					modelTrainingProcessor.stop();
				}
			}
		});
	}

	private void enableAllFormElement() {
		this.btnStart.setEnabled(true);
		this.btnSelectedFolder.setEnabled(true);
		this.txtNumberOfTakenOutWord.setEnabled(true);
		this.btnStop.setEnabled(false);
	}

	private void clearDataForm() {

		DefaultTableModel defaultTableModel = (DefaultTableModel) this.tableExtractedTopic.getModel();
		int rowCount = defaultTableModel.getRowCount();
		// Remove rows one by one from the end of the table
		for (int i = rowCount - 1; i >= 0; i--) {
			defaultTableModel.removeRow(i);
		}

		this.txtOverallLog.setText(null);

	}

	private boolean isNumberic(String inputValue) {
		try {
			int parsedNumber = Integer.parseInt(inputValue);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}
