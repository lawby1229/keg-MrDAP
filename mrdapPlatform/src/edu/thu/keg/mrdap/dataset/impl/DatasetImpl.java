package edu.thu.keg.mrdap.dataset.impl;

import java.util.Date;

import edu.thu.keg.mrdap.dataset.Dataset;

public class DatasetImpl implements Dataset {
	private String id = null;
	private Date date = null;
	private String serial = null;
	private String type = null;
	private String name = null;
	private String owner = null;
	private String path = null;
	private long sizeMb = 0;

	public DatasetImpl(String id, String serial, Date date, String type,
			String name, String owner, String path, long sizeMb) {
		this.id = id;
		this.serial = serial;
		this.date = date;
		this.type = type;
		this.name = name;
		this.owner = owner;
		this.path = path;
		this.sizeMb = sizeMb;

	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.dataset.Dataset#getDate()
	 */
	@Override
	public Date getDate() {
		// TODO Auto-generated method stub
		return this.date;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.dataset.Dataset#getSerial()
	 */
	@Override
	public String getSerial() {
		// TODO Auto-generated method stub
		return this.serial;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.dataset.Dataset#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.dataset.Dataset#getType()
	 */
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return this.type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.dataset.Dataset#getPath()
	 */
	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return this.path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.dataset.Dataset#getSizeMb()
	 */
	@Override
	public long getSizeMb() {
		// TODO Auto-generated method stub
		return this.sizeMb;
	}

}
