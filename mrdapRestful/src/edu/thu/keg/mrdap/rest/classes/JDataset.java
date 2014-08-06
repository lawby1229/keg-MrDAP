package edu.thu.keg.mrdap.rest.classes;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import edu.thu.keg.mrdap.dataset.Dataset;

@XmlRootElement
public class JDataset {
	private String id;
	private String type;
	private String serial;
	private String name;
	private Date date;

	public JDataset() {

	}

	public JDataset(Dataset dataset) {
		this.id = dataset.getId();
		this.type = dataset.getType();
		this.serial = dataset.getSerial();
		this.name = dataset.getName();
		this.date = dataset.getDate();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the serial
	 */
	public String getSerial() {
		return serial;
	}

	/**
	 * @param serial
	 *            the serial to set
	 */
	public void setSerial(String serial) {
		this.serial = serial;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
