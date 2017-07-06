package main.listener.change;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JComboBox;

import adpater.file.TextFileAdapter;

public class SelectTrainingModelFolderChangeListener implements ItemListener {

	private static final String outputModelRootDirPath = System.getProperty("user.dir") + "/data/svm/model";
	private TextFileAdapter textFileAdapter = new TextFileAdapter();
	private JComboBox<String> cbExpectedTopicLabelClassifying;

	public SelectTrainingModelFolderChangeListener(JComboBox<String> cbExpectedTopicLabelClassifying) {
		this.cbExpectedTopicLabelClassifying = cbExpectedTopicLabelClassifying;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		if (e.getStateChange() == ItemEvent.SELECTED) {
			Object item = e.getItem();
			this.loadTheAvailableTopicInModelFolder(item.toString());
		}
	}

	//loadTheAvailableTopicInModelFolder
	private void loadTheAvailableTopicInModelFolder(String selectedFolderName) {
		this.cbExpectedTopicLabelClassifying.removeAllItems();
		List<String> topics = this.textFileAdapter.parseSingleFileToListString(
				this.outputModelRootDirPath + "/" + selectedFolderName + "/topic_list.txt");
		if (topics != null && topics.size() > 0) {
			for (String topic : topics) {
				this.cbExpectedTopicLabelClassifying.addItem(topic);
			}
		}
	}

}
