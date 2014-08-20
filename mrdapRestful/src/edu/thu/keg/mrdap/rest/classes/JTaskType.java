package edu.thu.keg.mrdap.rest.classes;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jettison.json.JSONException;

import edu.thu.keg.link.taskTypeQuery.TaskTypeField;
import edu.thu.keg.link.taskTypeQuery.TaskTypeQuery.TaskType;

@XmlRootElement
public class JTaskType {
	private String id = "";
	private String name = "";
	private String inputMeta = "";
	private String outputMeta = "";
	private String description = "";

	public JTaskType() {

	}

	public JTaskType(TaskType taskType) {
		try {
			this.id = taskType.getValue(TaskTypeField.ID);
			this.name = taskType.getValue(TaskTypeField.NAME);
			this.inputMeta = taskType.getValue(TaskTypeField.INPUTMETA);
			this.outputMeta = taskType.getValue(TaskTypeField.OUTPUT_META);
			this.description = taskType.getValue(TaskTypeField.DESCRIPTION);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
	 * @return the inputMeta
	 */
	public String getInputMeta() {
		return inputMeta;
	}

	/**
	 * @param inputMeta
	 *            the inputMeta to set
	 */
	public void setInputMeta(String inputMeta) {
		this.inputMeta = inputMeta;
	}

	/**
	 * @return the outputMeta
	 */
	public String getOutputMeta() {
		return outputMeta;
	}

	/**
	 * @param outputMeta
	 *            the outputMeta to set
	 */
	public void setOutputMeta(String outputMeta) {
		this.outputMeta = outputMeta;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
