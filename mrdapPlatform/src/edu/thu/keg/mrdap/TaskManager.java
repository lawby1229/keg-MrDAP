package edu.thu.keg.mrdap;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.thu.keg.mrdap.dataset.Dataset;
import edu.thu.keg.mrdap.task.Task;

public interface TaskManager {

	public Collection<Task> getTaskList();

	public Task getTask(String id);

	public void saveChanges() throws IOException;

	public void createTask(String id, Date date, String name, String owner,
			List<Dataset> datasets);
}
