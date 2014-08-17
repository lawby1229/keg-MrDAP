package edu.thu.keg.mr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;

public class MroFile implements XMLFile {
	String inputFolder = null;
	String outputFolder = null;

	FileWriter fw1 = null;
	FileWriter fw2 = null;
	FileWriter fw3 = null;

	LinkedHashMap<String, Integer> Attribute2Num = new LinkedHashMap<String, Integer>();
	LinkedHashMap<String, Integer> Attribute2index = new LinkedHashMap<String, Integer>();

	public MroFile(String inputFolder, String outputFolder, int serial) {
		this.inputFolder = inputFolder;
		this.outputFolder = outputFolder + "/" + serial + "/";
		loadFormat("MRO1", 1);
		loadFormat("MRO2", 2);
		loadFormat("MRO3", 3);
		File folder = new File(inputFolder);
		File[] files = folder.listFiles();
		try {
			for (int i = 0; i < files.length; i++) {
				if (!files[i].isDirectory()) {
					System.out.println("Analyzing: " + files[i].getPath());
					Element root = getRootElement(read(files[i]));
					readFromXML(root);
					writeToFile(root);
				}
			}
		} catch (MalformedURLException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// printAttribute();

	}

	void writeAttribute(File file, int type, String frontAttri) {
		Iterator<String> it = Attribute2Num.keySet().iterator();
		try {
			FileWriter fw = new FileWriter(file);
			if (!frontAttri.equals(""))
				fw.write(frontAttri + " ");
			String s = "";
			while (it.hasNext()) {
				s = it.next();
				if (Attribute2Num.get(s) == type)
					fw.write(s + " ");
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void printAttribute() {
		Iterator<String> it = Attribute2Num.keySet().iterator();
		try {
			FileWriter fw = new FileWriter(new File("Attrubutes2Num"));
			String s = "";
			while (it.hasNext()) {
				s = it.next();
				fw.write(s + "-" + Attribute2Num.get(s) + "\n");
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void loadFormat(String fileName, int serail) {
		LineNumberReader ln = null;
		try {
			FileReader fr = new FileReader(new File(fileName));
			ln = new LineNumberReader(fr);
			String line = ln.readLine();
			while (line != null && !line.equals("")) {
				Attribute2Num.put(line.trim(), serail);
				line = ln.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				ln.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	void readFromXML(Element root) {

		List<Element> smr = root.selectNodes("//smr");
		String[] attbutes = smr.get(0).getText().trim().split(" +");
		// System.out.println("一共有属性" + attbutes.length + "个");
		for (int i = 0; i < attbutes.length; i++) {
			if (Attribute2Num.get(attbutes[i]) == null)
				System.out.println(attbutes[i]);
			Attribute2index.put(attbutes[i], i);
		}

	}

	public void writeToFile(Element root) {
		String fileName = "";
		Node fileHeader = root.selectSingleNode("//fileHeader");
		String startTime = fileHeader.valueOf("@startTime");
		String endTime = fileHeader.valueOf("@endTime");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		try {
			Date reportTime = sdf.parse(startTime);
			Calendar cal = Calendar.getInstance();
			cal.setTime(reportTime);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			fileName = year + "" + String.format("%02d", month) + day + ".ha";
			String frontAtrri = "eNBId startTime endTime cellId MmeUeS1apId MmeGroupId MmeCode TimeStamp ";
			File f1 = new File(outputFolder + "/" + "mro1");
			if (!f1.isDirectory()) {
				f1.mkdirs();
				writeAttribute(new File(f1, "mro1.desc"), 1, frontAtrri);
			}
			File f2 = new File(outputFolder + "/" + "mro2");
			if (!f2.isDirectory()) {
				f2.mkdirs();
				writeAttribute(new File(f2, "mro2.desc"), 2, frontAtrri);
			}
			File f3 = new File(outputFolder + "/" + "mro3");
			if (!f3.isDirectory()) {
				f3.mkdirs();
				writeAttribute(new File(f3, "mro3.desc"), 3, frontAtrri);
			}
			fw1 = new FileWriter(new File(f1, fileName), true);
			fw2 = new FileWriter(new File(f2, fileName), true);
			fw3 = new FileWriter(new File(f3, fileName), true);

		} catch (ParseException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Node eNB = root.selectSingleNode("//eNB");
		String towerId = eNB.valueOf("@id");
		// System.out.println("基站号:" + towerId);
		List<Element> objs = root.selectNodes("//object");
		try {
			for (Element obj : objs) {
				String lineMain = towerId;
				lineMain += " " + startTime;
				lineMain += " " + endTime;
				String line1 = "";
				String line2 = "";
				String line3 = "";
				lineMain += " " + obj.valueOf("@id").trim();
				lineMain += " " + obj.valueOf("@MmeUeS1apId").trim();
				lineMain += " " + obj.valueOf("@MmeGroupId").trim();
				lineMain += " " + obj.valueOf("@MmeCode").trim();
				lineMain += " " + obj.valueOf("@TimeStamp").trim();
				line1 = "";
				line2 = "";
				line3 = "";
				line1 += lineMain.trim();
				line2 += lineMain.trim();
				line3 += lineMain.trim();
				for (Iterator<Element> v = obj.elementIterator(); v.hasNext();) {
					String[] valueLine = v.next().getText().trim().split(" +");
					Iterator<String> itAttr = Attribute2Num.keySet().iterator();
					String s = "";
					if (!line1.equals("")) {
						line1 += "#";
						line2 += "#";
						line3 += "#";
					}

					while (itAttr.hasNext()) {
						s = itAttr.next();
						if (Attribute2Num.get(s) == 1) {
							line1 += " " + valueLine[Attribute2index.get(s)];
						} else if (Attribute2Num.get(s) == 2) {
							line2 += " " + valueLine[Attribute2index.get(s)];
						} else if (Attribute2Num.get(s) == 3) {
							line3 += " " + valueLine[Attribute2index.get(s)];
						} else
							throw new IllegalArgumentException("解析xml分配行数有错误！ "
									+ " " + s);
					}

				}
				fw1.write(line1.trim() + "\n");
				fw2.write(line2.trim() + "\n");
				fw3.write(line3.trim() + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				fw1.close();
				fw2.close();
				fw3.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public Document read(File fileName) throws MalformedURLException,
			DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(fileName);
		return document;
	}

	public Element getRootElement(Document doc) {
		return doc.getRootElement();
	}

	public static void main(String arg[]) {
		MroFile mf = new MroFile("in/mro", "out/mro", 1);

	}
}
