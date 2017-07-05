package main.listener.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;

import main.MainGUI;

public class ModelTrainingFolderSelectActionListener implements ActionListener {

	private MainGUI mainGUI;
	private JFileChooser fcFolderForModelTraining;
	private JLabel labelFolderPathInput;

	public ModelTrainingFolderSelectActionListener(MainGUI mainGUI, JLabel labelFolderPathInput, 
			JFileChooser fcFolderForModelTraining) {

		this.mainGUI = mainGUI;
		this.labelFolderPathInput = labelFolderPathInput;
		this.fcFolderForModelTraining = fcFolderForModelTraining;
		this.fcFolderForModelTraining.setCurrentDirectory(new File(System.getProperty("user.dir") + "/data"));
		this.fcFolderForModelTraining.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (this.fcFolderForModelTraining.showOpenDialog(this.mainGUI) == JFileChooser.APPROVE_OPTION) {
			// load from file
			File selectedFolder = fcFolderForModelTraining.getSelectedFile();
			this.labelFolderPathInput.setText(selectedFolder.getPath());
		}

	}

}
