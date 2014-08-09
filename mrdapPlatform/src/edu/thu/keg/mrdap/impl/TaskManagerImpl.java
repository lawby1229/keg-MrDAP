package edu.thu.keg.mrdap.impl;

import hdfs.MFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jettison.json.JSONException;

import task.FinalStatus;
import task.State;
import task.TaskExecutor;
import task.TaskQuery;
import task.TaskQuery.TaskInfo;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import edu.thu.keg.mrdap.DatasetManager;
import edu.thu.keg.mrdap.TaskManager;
import edu.thu.keg.mrdap.dataset.Dataset;
import edu.thu.keg.mrdap.dataset.impl.DatasetImpl;
import edu.thu.keg.mrdap.impl.DatasetManagerImpl.Storage;
import edu.thu.keg.mrdap.task.Task;
import edu.thu.keg.mrdap.task.impl.TaskImpl;
import edu.thu.keg.mrdap.task.impl.TaskStatus;
import edu.thu.keg.mrdap.task.impl.TaskType;

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
	public void saveXMLChanges() throws IOException {
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
	public Task setTask(TaskType type, String name, String owner,
			List<String> filePaths) {
		// if (tasks.containsKey(id)) {
		// System.out.println(id + " task is already egxisted!");
		// return null;
		// }
		List<String> datasets = new ArrayList<String>();
		try {
			for (String path : filePaths) {
				MFile mf = new MFile(path);

				if (mf.isDirectory()) {
					datasets.addAll(DatasetManagerImpl.getInstance()
							.getAllFilesPath(mf.getPath()));
				} else
					datasets.add(mf.getPath());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date now = new Date();
		Task ts = new TaskImpl(now, name, owner, TaskStatus.READY, type,
				datasets, Config.getHadoopRoot() + "\\result\\"
						+ now.toString());
		return ts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.TaskManager#runTask(edu.thu.keg.mrdap.task.Task)
	 */
	@Override
	public String runTask(Task task) {
		// try {
		String pathAll = "";
		for (String path : task.getDatasets()) {
			pathAll = pathAll + "," + path;
		}
		String appId = "WCLOVELQ";
		// TaskExecutor.submit("pagerank.PageRank", 6, 4, 8, 8,
		// "./spark-kmeans-10.jar", pathAll, task.getOutputPath());
		task.run(appId);
		addTask(task);
		try {
			saveXMLChanges();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.TaskManager#killTask(java.lang.String)
	 */
	@Override
	public void killTask(String id) {
		tasks.get(id).kill();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.TaskManager#getTaskInfo(java.lang.String)
	 */
	@Override
	public TaskStatus getTaskInfo(String id) {
		// TODO Auto-generated method stub
		Task task = tasks.get(id);
		try {
			TaskInfo ti = TaskQuery.getTaskInfo(id);
			if (ti.getState().equals(State.RUNNING.name()))
				task.run(id);
			else if (ti.getState().equals(State.KILLED.name()))
				task.kill();
			else if (ti.getState().equals(State.FINISHED)) {
				if (ti.getFinalStatus().equals(FinalStatus.SUCCEEDED.name())) {
					task.success();
				} else if (ti.getFinalStatus()
						.equals(FinalStatus.FAILED.name())) {
					task.fail();
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return task.getStute();
	}
}
