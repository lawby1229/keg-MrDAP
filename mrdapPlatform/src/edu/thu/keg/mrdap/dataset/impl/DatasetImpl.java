package edu.thu.keg.mrdap.dataset.impl;

import edu.thu.keg.mrdap.dataset.Dataset;

public class DatasetImpl implements Dataset {
	private String id = null;
	private String type = null;
	private String name = null;
	private String owner = null;
	private String path = null;
	private int sizeMb = 0;

	public DatasetImpl(String id, String type, String name, String owner,
			String path, int sizeMb) {
		this.id = id;
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
}
