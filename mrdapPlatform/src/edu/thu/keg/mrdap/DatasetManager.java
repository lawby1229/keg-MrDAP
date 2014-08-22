package edu.thu.keg.mrdap;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.thu.keg.mrdap.dataset.Dataset;

public interface DatasetManager {
	
	public void refreshDatasetInHadoop();

	public Collection<Dataset> getDatasetList();

	public Dataset getDataset(String id);

	public boolean containsDataset(String id);

	public void saveXMLChanges() throws IOException;

	public void createDataset(String id, String serial, Date date, String type,
			String name, String owner, String path, long sizeMb, boolean isDic);

	public List<String> getAllFilesPath(String rootPath);
}
