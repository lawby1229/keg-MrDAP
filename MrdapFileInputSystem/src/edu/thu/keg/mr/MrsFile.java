package edu.thu.keg.mr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class MrsFile implements XMLFile {
	Element root;
	String outputFolder;

	public MrsFile(String inFileFolder, String outputFolder, int serial) {
		try {
			this.outputFolder = outputFolder + "/" + serial + "/";
			File folder = new File(inFileFolder);
			File[] files = folder.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (!files[i].isDirectory()) {
					System.out.println("Analyzing: " + files[i].getPath());
					root = getRootElement(read(files[i]));
					loadMrs();
				}
			}
		} catch (MalformedURLException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void loadMrs() {
		String fileName = "";
		Node fileHeader = root.selectSingleNode("//fileHeader");
		String startTime = fileHeader.valueOf("@startTime");
		String endTime = fileHeader.valueOf("@startTime");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try {
			Date reportTime = sdf.parse(startTime);
			Calendar cal = Calendar.getInstance();
			cal.setTime(reportTime);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			fileName = year + "" + String.format("%02d", month) + day + ".ha";
			Node eNB = root.selectSingleNode("//eNB");
			String towerId = eNB.valueOf("@id");
			String lineMain = towerId;
			List<Element> measurements = root.selectNodes("//measurement");
			for (int i = 0; i < measurements.size(); i++) {
				Element measure = measurements.get(i);
				String measureName = measure.valueOf("@mrName");
				File f = new File(outputFolder + "/" + measureName + "/");
				if (!f.isDirectory())
					f.mkdirs();
				File desc = new File(f, measureName + ".desc");
				if (!desc.exists()) {
					FileWriter fw = new FileWriter(desc, true);
					Node smr = measure.selectSingleNode("smr");
					String attri = "towerId" + " " + "startTime" + " " + "id";
					fw.write(attri + " " + smr.getText().trim());
					fw.close();
				}

				FileWriter fw = new FileWriter(new File(f, fileName), true);
				List<Node> objs = measure.selectNodes("object");
				for (int j = 0; j < objs.size(); j++) {
					Node obj = objs.get(j);
					String id = obj.valueOf("@id");
					List<Node> vs = obj.selectNodes("v");
					for (Node v : vs) {
						fw.write(lineMain + " " + startTime + " " + endTime
								+ " " + id + " " + v.getText().trim() + "\n");
					}
				}
				fw.close();

			}

			// File f2 = new File(outputFolder + "2");
			// if (!f2.isDirectory())
			// f2.mkdirs();
			// File f3 = new File(outputFolder + "3");
			// if (!f3.isDirectory())
			// f3.mkdirs();
			//
			// fw1 = new FileWriter(new File(f1, fileName), true);
			// fw2 = new FileWriter(new File(f2, fileName), true);
			// fw3 = new FileWriter(new File(f3, fileName), true);

		} catch (ParseException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@Override
	public Document read(File fileName) throws MalformedURLException,
			DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(fileName);
		return document;
	}

	@Override
	public Element getRootElement(Document doc) {
		// TODO Auto-generated method stub
		return doc.getRootElement();

	}

	public static void main(String arg[]) {
		MrsFile mrs = new MrsFile("in/mrs", "out/mrs", 1);
		// mrs.loadMrs();
	}
}
