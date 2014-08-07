package edu.thu.keg.mrdap.task.impl;

import java.util.Date;
import java.util.List;

import edu.thu.keg.mrdap.dataset.Dataset;
import edu.thu.keg.mrdap.task.Task;

public class TaskImpl implements Task {
	private String id = null;
	private Date date = null;
	private String name = null;
	private String owner = null;
	private List<Dataset> datasets = null;
	TaskStatus status = null;
	TaskType type = null;

	public TaskImpl(String id, Date date, String name, String owner,
			TaskStatus status, TaskType type, List<Dataset> datasets) {
		this.id = id;
		this.date = date;
		this.name = name;
		this.owner = owner;
		this.status = status;
		this.type = type;
		this.datasets = datasets;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.task.Task#getDatasets()
	 */
	@Override
	public List<Dataset> getDatasets() {
		// TODO Auto-generated method stub
		return this.datasets;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.task.Task#getDate()
	 */
	@Override
	public Date getDate() {
		// TODO Auto-generated method stub
		return this.date;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.task.Task#getStute()
	 */
	@Override
	public TaskStatus getStute() {
		// TODO Auto-generated method stub
		return this.status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.task.Task#getType()
	 */
	@Override
	public TaskType getType() {
		// TODO Auto-generated method stub
		return this.type;
	}

}
