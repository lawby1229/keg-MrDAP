package edu.thu.keg.mrdap;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.thu.keg.mrdap.task.Task;
import edu.thu.keg.mrdap.task.impl.TaskStatus;

public interface TaskManager {

	public Collection<Task> getTaskList();

	public Task getTask(String id);

	public void saveXMLChanges() throws IOException;

	public Task setTask(String type, String name, String owner,
			List<String> filePaths);

	//
	// public void createTask(String id, Date date, TaskType type, String name,
	// String owner, List<String> directorys, List<String> filePaths);

	public String runTask(Task task);

	public void killTask(String id);

	public void removeTask(String id);

	public TaskStatus getTaskInfo(String id);
}
