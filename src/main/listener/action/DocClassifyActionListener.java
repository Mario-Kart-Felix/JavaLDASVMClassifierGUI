package main.listener.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import adpater.file.TextFileAdapter;
import main.MainGUI;
import main.handler.OutputLogHandler;
import main.processor.DocClassifyProcessor;

public class DocClassifyActionListener implements ActionListener {

	private static final int MAX_WAIT_TIME = 0;

	private TextFileAdapter textFileAdapter = new TextFileAdapter();
	private DocClassifyProcessor docClassifyProcessor;
	private OutputLogHandler outputLogHandler = new OutputLogHandler();

	private MainGUI mainGUI;
	private JButton btnStart;
	private JButton btnStop;
	private JButton btnSelectedFolder;
	private JFileChooser fcSelectedFolder;
	private JTextArea txtOverallLog;
	private JTable tableOutput;

	// timers
	private Timer overallLogTimer;

	public DocClassifyActionListener(MainGUI mainGUI, JButton btnStart, JButton btnStop, JButton btnSelectedFolder,
			JFileChooser fcSelectedFolder, JTextArea txtOverallLog, JTable tableOutput) {

		// frame
		this.mainGUI = mainGUI;

		// buttons
		this.btnStart = btnStart;
		this.btnStop = btnStop;
		this.btnSelectedFolder = btnSelectedFolder;

		// fileChooser
		this.fcSelectedFolder = fcSelectedFolder;

		// textarea
		this.txtOverallLog = txtOverallLog;

		// table
		this.tableOutput = tableOutput;

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		// TODO Auto-generated method stub
		this.clearDataForm();
		
		if (this.fcSelectedFolder.getSelectedFile() != null) {

			String selectedFolderPath = this.fcSelectedFolder.getSelectedFile().getAbsolutePath();
			String jobLogFolderPath = this.outputLogHandler.generateLogFolder();

			if (jobLogFolderPath != null) {

				// init overall log file
				String jobOverAllLogFilePath = jobLogFolderPath + "/overall_logs.txt";
				this.textFileAdapter.writeToDataStringFile(">>START<<", jobOverAllLogFilePath);

				this.docClassifyProcessor = new DocClassifyProcessor(selectedFolderPath, jobLogFolderPath,
						jobOverAllLogFilePath, this.tableOutput);
				docClassifyProcessor.run();

				this.disableAllFormElement();
				
				try {

					// init timer
					this.initializeLogTimer(jobOverAllLogFilePath);

					// start timer
					this.overallLogTimer.start();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		} else {
			JOptionPane.showMessageDialog(this.mainGUI, "Please select the document's folder for classifying !",
					"Error", JOptionPane.ERROR_MESSAGE);
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
					// xảy ra lỗi trong quá trình đọc file
					ex.printStackTrace();
					return;
				}
			}
		});

	}

	private void disableAllFormElement() {
		this.btnStart.setEnabled(false);
		this.btnSelectedFolder.setEnabled(false);
		this.btnStop.setEnabled(true);
	}

	private void enableAllFormElement() {
		this.btnStart.setEnabled(true);
		this.btnSelectedFolder.setEnabled(true);
		this.btnStop.setEnabled(false);
	}

	private void clearDataForm() {

		DefaultTableModel defaultTableModel = (DefaultTableModel) this.tableOutput.getModel();
		int rowCount = defaultTableModel.getRowCount();
		// Remove rows one by one from the end of the table
		for (int i = rowCount - 1; i >= 0; i--) {
			defaultTableModel.removeRow(i);
		}
		
		this.txtOverallLog.setText(null);

	}

}
