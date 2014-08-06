package edu.thu.keg.mrdap.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import edu.thu.keg.mrdap.DatasetManager;
import edu.thu.keg.mrdap.Platform;
import edu.thu.keg.mrdap.TaskManager;
import edu.thu.keg.mrdap.dataset.Dataset;

public class PlatformImpl implements Platform {
	public PlatformImpl(String file) {
		try {
			Config.init(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public DatasetManager getDatasetManager() {
		// TODO Auto-generated method stub
		return DatasetManagerImpl.getInstance();
	}

	@Override
	public TaskManager getTaskManager() {
		// TODO Auto-generated method stub
		return TaskManagerImpl.getInstance();
	}

	public static void main(String arg[]) {
		PlatformImpl p = new PlatformImpl("config.xml");
		System.out.println("Loading...");
		p.crud();
	}

	public void crud() {

		getDatasetManager().createDataset("1", "1st", new Date(), "mro", "one",
				"ybz", "a1/sd", 10);
		getDatasetManager().createDataset("2", "2ed", new Date(), "mro", "two",
				"ybz", "a2/sd", 20);
		getDatasetManager().createDataset("3", "3rd", new Date(), "mro",
				"three", "ybz", "a4/sd", 30);

		getTaskManager().createTask(
				"0",
				"one-two",
				"ybz",
				Arrays.asList(getDatasetManager().getDatasetList().toArray(
						new Dataset[0])));

		try {
			getDatasetManager().saveChanges();
			getTaskManager().saveChanges();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
