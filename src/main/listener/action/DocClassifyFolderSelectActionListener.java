package main.listener.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;

import main.MainGUI;

public class DocClassifyFolderSelectActionListener implements ActionListener {

	private MainGUI mainGUI;
	private JFileChooser fcFolderForDocClassifying;
	private JLabel labelFolderPathInput;

	public DocClassifyFolderSelectActionListener(MainGUI mainGUI, JLabel labelFolderPathInput, JFileChooser fcFolderForDocClassifying) {

		this.mainGUI = mainGUI;
		this.labelFolderPathInput = labelFolderPathInput;
		this.fcFolderForDocClassifying = fcFolderForDocClassifying;
		this.fcFolderForDocClassifying.setCurrentDirectory(new File(System.getProperty("user.dir") + "/data"));
		this.fcFolderForDocClassifying.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (this.fcFolderForDocClassifying.showOpenDialog(this.mainGUI) == JFileChooser.APPROVE_OPTION) {
			// load from file
			File selectedFolder = fcFolderForDocClassifying.getSelectedFile();
			this.labelFolderPathInput.setText(selectedFolder.getPath());
		}

	}

}
