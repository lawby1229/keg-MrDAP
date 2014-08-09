package hdfs;

import java.io.File;
import java.io.IOException;

public class Default {
	public static final String MASTER_ADDR = "10.1.1.121";
	public static final String DEFALUT_FS_URL = "hdfs://" + MASTER_ADDR
			+ ":9900";
	public static final String ROOT = File.separator + "Mobile";
	public static final String APPLICATION_INFO_URL = "http://" + MASTER_ADDR
			+ ":8088";
	public static final String HADOOP_HOME = "/usr/local/hadoop-2.2.0";
	public static final String SPARK_HOME = "/usr/local/spark-1.0.1";

	public static void initHadoop() {

		File workaround = new File(".");
		System.getProperties().put("hadoop.home.dir",
				workaround.getAbsolutePath());
		new File("./bin").mkdirs();
		try {
			new File("./bin/winutils.exe").createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
