package edu.thu.keg.mrdap.task;

import java.util.Date;
import java.util.List;

import edu.thu.keg.mrdap.dataset.Dataset;
import edu.thu.keg.mrdap.task.impl.TaskStatus;

public interface Task {
	public String getId();

	public List<String> getDatasets();

	public Date getDate();

	public TaskStatus getStute();

	public String getType();

	public String getOutputPath();

	public void run(String id);

	public void kill();

	public void fail();

	public void success();
}
