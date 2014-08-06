package edu.thu.keg.mrdap.task;

import java.util.Date;
import java.util.List;

import edu.thu.keg.mrdap.dataset.Dataset;

public interface Task {
	public String getId();

	public List<Dataset> getDatasets();

	public Date getDate();
}
