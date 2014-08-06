package edu.thu.keg.mrdap.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import edu.thu.keg.mrdap.DatasetManager;
import edu.thu.keg.mrdap.dataset.Dataset;
import edu.thu.keg.mrdap.dataset.impl.DatasetImpl;
import edu.thu.keg.mrdap.task.Task;

public class DatasetManagerImpl implements DatasetManager {

	public static class Storage {

		Dataset[] datasets;

		/**
		 * @param datasets
		 * @param views
		 */
		private Storage(Dataset[] datasets) {
			super();
			this.datasets = datasets;

		}

	}

	private HashMap<String, Dataset> datasets = null;

	private XStream xstream;
	private static DatasetManagerImpl instance;

	private DatasetManagerImpl() {
		datasets = new HashMap<String, Dataset>();

		try {
			System.out
					.println("(DatasetManagerImpl) Loading Dataset File Name: "
							+ Config.getDataSetFile());
			loadDataSets(Config.getDataSetFile());
		} catch (Exception ex) {
			// log.warn(ex.getMessage());

		}
	}

	private XStream getXstream() {
		if (xstream == null) {
			xstream = new XStream(new StaxDriver());
			// xstream.alias("Jdbc", JdbcProvider.class);
			// xstream.registerConverter(new AbstractSingleValueConverter() {

			// @Override
			// public boolean canConvert(
			// @SuppressWarnings("rawtypes") Class arg0) {
			// // return arg0.equals(JdbcProvider.class);
			// }

			// @Override
			// public Object fromString(String str) {
			// return new JdbcProvider(str);
			// }

			// });
		}
		return xstream;
	}

	private void addDataset(Dataset ds) {
		datasets.put(ds.getId(), ds);
	}

	private void loadDataSets(String fileName) {
		String f = fileName;
		File Fi = new File(f);
		Storage sto = (Storage) getXstream().fromXML(Fi);
		for (Dataset ds : sto.datasets) {
			addDataset(ds);
		}

	}

	@Override
	public void saveChanges() throws IOException {
		Writer fw;
		Storage sto = new Storage(datasets.values().toArray(new Dataset[0]));
		fw = new OutputStreamWriter(new FileOutputStream(
				Config.getDataSetFile()), "UTF-8");
		getXstream().marshal(sto, new PrettyPrintWriter(fw));
		fw.close();
	}

	public synchronized static DatasetManager getInstance() {
		// TODO multi-thread
		if (instance == null)
			instance = new DatasetManagerImpl();
		return instance;
	}

	@Override
	public Collection<Dataset> getDatasetList() {
		// TODO Auto-generated method stub
		return datasets.values();
	}

	@Override
	public Dataset getDataset(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.thu.keg.mrdap.DatasetManager#createDataset(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public void createDataset(String id, String type, String name,
			String owner, String path, int sizeMb) {
		if (datasets.containsKey(id)) {
			System.out.println(id + " dataset is already egxisted!");
			return;
		}
		Dataset ds = new DatasetImpl(id, type, name, owner, path, sizeMb);
		addDataset(ds);
	}

}
