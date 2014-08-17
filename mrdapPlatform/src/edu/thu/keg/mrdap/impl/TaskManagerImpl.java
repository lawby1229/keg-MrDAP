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

import taskClient.TaskClient;
import taskQuery.FinalStatus;
import taskQuery.State;
import taskQuery.TaskQuery;
import taskQuery.TaskQuery.TaskInfo;

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
				datasets, Config.getHadoopRoot() + "mobileRES/" + type.name()
						+ now.getTime());
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
		List<String> paths = task.getDatasets();
		String pathAll = paths.get(0);
		for (int i = 1; i < paths.size(); i++) {
			pathAll = pathAll + "," + paths.get(i);
		}
		System.out.println(pathAll);
		String appId = "WCLOVELQ" + System.currentTimeMillis();
		String pack = "";
		String jar = "";

		switch (task.getType().name()) {
		case "Task1":
			pack = "mobile.Task1";
			jar = "Task1.jar";
			break;
		case "Task2":
			pack = "";
			jar = "";
			break;
		case "Task3":
			pack = "";
			jar = "";
			break;
		}
		try {
			appId = TaskClient.submit(pack, 5, 4, 2, 2, "/home/hadoop/slib/"
					+ jar, pathAll, task.getOutputPath(), "");
			System.out.println("输出路径：" + task.getOutputPath());
			// TaskExecutor.submit("pagerank.PageRank", 6, 4, 8, 8,
			// "./spark-kmeans-10.jar", pathAll, task.getOutputPath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 运行该任务，更新task id
		task.run(appId);
		// 从hadoop得到任务运行状态，并更新task的状态
		getTaskInfo(appId);
		// 将hadoop的任务添加到tasks前台虚拟任务集中
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
		TaskClient.kill(id);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.TaskManager#removeTask(java.lang.String)
	 */
	@Override
	public void removeTask(String id) {
		// TODO Auto-generated method stub
		if (tasks.containsKey(id)) {
			tasks.remove(id);
			try {
				saveXMLChanges();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.TaskManager#getTaskInfo(java.lang.String)
	 */
	@Override
	public TaskStatus getTaskInfo(String id) {
		// TODO Auto-generated method stub
		if (!tasks.containsKey(id))
			return null;
		Task task = tasks.get(id);

		try {
			System.out.println("获取任务状态id： " + id);
			TaskInfo ti = TaskQuery.getTaskInfo(id);
			if (ti.getState().equals(State.RUNNING.name()))
				task.run(id);
			else if (ti.getState().equals(State.KILLED.name()))
				task.kill();
			else if (ti.getState().equals(State.FINISHED.name())) {
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
