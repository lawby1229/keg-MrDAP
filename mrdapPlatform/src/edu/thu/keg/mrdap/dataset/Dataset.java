package edu.thu.keg.mrdap.dataset;

import java.util.Date;

public interface Dataset {
	public String getId();

	public Date getDate();
	
	public String getSerial();
	
	public String getName();

	public String getType();

	public String getPath();

	public int getSizeMb();
	
	
}
