package edu.thu.keg.mrdap.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import edu.thu.keg.mrdap.TaskManager;
import edu.thu.keg.mrdap.dataset.Dataset;
import edu.thu.keg.mrdap.dataset.impl.DatasetImpl;
import edu.thu.keg.mrdap.impl.DatasetManagerImpl.Storage;
import edu.thu.keg.mrdap.task.Task;
import edu.thu.keg.mrdap.task.impl.TaskImpl;
import edu.thu.keg.mrdap.task.impl.TaskStatus;

public class TaskManagerImpl implements TaskManager {

	public static class Storage {
		Task[] tasks;

		/**
		 * @param datasets
		 * @param views
		 */
		private Storage(Task[] tasks) {
			super();
			this.tasks = tasks;
		}

	}

	private static TaskManagerImpl instance;
	private HashMap<String, Task> tasks = null;
	private XStream xstream;

	private TaskManagerImpl() {
		tasks = new HashMap<String, Task>();
		try {
			System.out.println("(TaskManagerImpl) Loading Task File Name: "
					+ Config.getTaskFile());
			loadTasks(Config.getTaskFile());
		} catch (Exception ex) {
			// log.warn(ex.getMessage());

		}
	}

	private XStream getXstream() {
		if (xstream == null) {
			xstream = new XStream(new StaxDriver());
		}
		return xstream;
	}

	private void addTask(Task ts) {
		tasks.put(ts.getId(), ts);
	}

	private void loadTasks(String fileName) {
		String f = fileName;
		File Fi = new File(f);
		Storage sto = (Storage) getXstream().fromXML(Fi);
		for (Task ts : sto.tasks) {
			addTask(ts);
		}
	}

	@Override
	public void saveChanges() throws IOException {
		Writer fw;
		Storage sto = new Storage(tasks.values().toArray(new Task[0]));
		fw = new OutputStreamWriter(new FileOutputStream(Config.getTaskFile()),
				"UTF-8");
		getXstream().marshal(sto, new PrettyPrintWriter(fw));
		fw.close();
	}

	public synchronized static TaskManager getInstance() {
		// TODO multi-thread
		if (instance == null)
			instance = new TaskManagerImpl();
		return instance;
	}

	@Override
	public Collection<Task> getTaskList() {
		// TODO Auto-generated method stub
		return tasks.values();
	}

	@Override
	public Task getTask(String id) {
		// TODO Auto-generated method stub
		return tasks.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.TaskManager#createTask(java.lang.String,
	 * java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public void createTask(String id, Date date, String name, String owner,
			List<Dataset> datasets) {
		if (tasks.containsKey(id)) {
			System.out.println(id + " task is already egxisted!");
			return;
		}
		Task ts = new TaskImpl(id, date, name, owner, TaskStatus.READY,
				datasets);
		addTask(ts);
	}

}
