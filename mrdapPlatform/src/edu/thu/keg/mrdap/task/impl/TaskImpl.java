package edu.thu.keg.mrdap.task.impl;

import java.util.List;

import edu.thu.keg.mrdap.dataset.Dataset;
import edu.thu.keg.mrdap.task.Task;

public class TaskImpl implements Task {
	private String id = null;
	private String name = null;
	private String owner = null;
	private List<Dataset> datasets = null;
	TaskStatus status = null;

	public TaskImpl(String id, String name, String owner, TaskStatus status,
			List<Dataset> datasets) {
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.status = status;
		this.datasets = datasets;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.id;
	}
}
