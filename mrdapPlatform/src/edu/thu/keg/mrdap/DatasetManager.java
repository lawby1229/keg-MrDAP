package edu.thu.keg.mrdap;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import edu.thu.keg.mrdap.dataset.Dataset;

public interface DatasetManager {

	public Collection<Dataset> getDatasetList();

	public Dataset getDataset(String id);

	public void saveXMLChanges() throws IOException;

	public void createDataset(String id, String serial, Date date, String type,
			String name, String owner, String path, long sizeMb);
}
