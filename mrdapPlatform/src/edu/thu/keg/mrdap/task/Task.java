package edu.thu.keg.mrdap.task;

import java.util.Date;
import java.util.List;

import edu.thu.keg.mrdap.dataset.Dataset;
import edu.thu.keg.mrdap.task.impl.TaskStatus;
import edu.thu.keg.mrdap.task.impl.TaskType;

public interface Task {
	public String getId();

	public List<Dataset> getDatasets();

	public Date getDate();

	public TaskStatus getStute();

	public TaskType getType();
}
