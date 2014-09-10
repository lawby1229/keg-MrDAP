package edu.thu.keg.mr;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class MroFile implements XMLFile {
	String inputFolder = null;
	String outputFolder = null;

	FileWriter fw1 = null;
	FileWriter fw2 = null;
	FileWriter fw3 = null;
	String version = "new";
	LinkedHashMap<String, Integer> Attribute2Num = new LinkedHashMap<String, Integer>();
	LinkedHashMap<String, Integer> Attribute2index = new LinkedHashMap<String, Integer>();

	public MroFile(String inputFolder, String outputFolder, int serial,
			String version) {
		this.inputFolder = inputFolder;
		this.outputFolder = outputFolder + "/" + serial + "/";
		loadFormat("MRO1", 1);
		loadFormat("MRO2", 2);
		loadFormat("MRO3", 3);
		File folder = new File(inputFolder);
		// extractFile(folder);
		this.version = version;
		List<File> files = new ArrayList<File>();
		getFiles(folder, files);

		for (int i = 0; i < files.size(); i++) {
			if (!files.get(i).isDirectory()) {
				System.out.println("Analyzing: " + files.get(i).getPath());
				Element root = null;
				try {
					root = getRootElement(read(files.get(i)));
					readFromXML(root);
					if (version.toLowerCase().equals("old"))
						writeToFile(root);
					else
						writeToFileNew(root);
				} catch (MalformedURLException | DocumentException e) {
					System.err.println("xml文件有错！");
					// e.printStackTrace();
				}

			}
		}

		// printAttribute();

	}

	void extractFile(File dir) {
		for (File files : dir.listFiles()) {
			if (!files.isDirectory()) {
				if (files.getName().endsWith(".zip"))
					try {
						ZipTools.unzip(files.getPath(), null);
					} catch (IOException e) {
						// System.out.println(files.getName());
						e.printStackTrace();
					}
			} else
				extractFile(files);
		}
	}

	void getFiles(File dir, List<File> re) {
		for (File file : dir.listFiles()) {
			if (!file.isDirectory()) {
				if (file.getName().endsWith(".xml"))
					re.add(file);
			} else
				getFiles(file, re);
		}
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
					fw.write(s.trim() + " ");
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
				Attribute2Num.put(line.trim().toLowerCase(), serail);
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

	void readFromXML(Element root) throws DocumentException {

		List<Element> smr = root.selectNodes("//smr");
		if (smr == null)
			throw new DocumentException("xml文件没有smr");
		String[] attbutes = smr.get(0).getText().trim().toLowerCase()
				.split(" +");
		// System.out.println("一共有属性" + attbutes.length + "个");
		for (int i = 0; i < attbutes.length; i++) {
			if (Attribute2Num.get(attbutes[i]) == null)
				System.out.println(attbutes[i]);
			Attribute2index.put(attbutes[i].toLowerCase(), i);
		}

	}

	private void makeHaFile(Element root) {
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
			fileName = year + "" + String.format("%02d", month)
					+ String.format("%02d", day) + ".ha";
			String frontAtrri = "";
			if (version.toLowerCase().equals("old"))
				frontAtrri = "eNBId startTime endTime cellId MmeUeS1apId MmeGroupId MmeCode TimeStamp";
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
	}

	public void writeToFile(Element root) {
		makeHaFile(root);
		Node fileHeader = root.selectSingleNode("//fileHeader");
		String startTime = fileHeader.valueOf("@startTime");
		String endTime = fileHeader.valueOf("@endTime");
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
						line1 = line1.trim() + "#";
						line2 = line2.trim() + "#";
						line3 = line3.trim() + "#";
					}

					while (itAttr.hasNext()) {
						s = itAttr.next();
						// System.out.println(s);
						int fileNum = Attribute2Num.get(s);
						// if(valueLine[fileNum])
						String word = "NIL";
						if (Attribute2index.containsKey(s))
							word = valueLine[Attribute2index.get(s)];
						if (fileNum == 1) {
							line1 += word + " ";
						} else if (Attribute2Num.get(s) == 2) {
							line2 += word + " ";
						} else if (Attribute2Num.get(s) == 3) {
							line3 += word + " ";
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

	/**
	 * 
	 CellID#TimeStamp#MmeCode#MmeGroupId#MmeUeS1apId#次数# 子帧号 ENBId starttime
	 * endtime 载波号
	 * 
	 * @param root
	 */
	public void writeToFileNew(Element root) {
		makeHaFile(root);
		Node fileHeader = root.selectSingleNode("//fileHeader");
		String startTime = fileHeader.valueOf("@startTime");
		String endTime = fileHeader.valueOf("@endTime");
		Node eNB = root.selectSingleNode("//eNB");
		String enbId = eNB.valueOf("@id");
		// System.out.println("基站号:" + towerId);
		List<Element> objs = root.selectNodes("//object");
		try {
			// 每一个obj

			for (Element obj : objs) {

				String lineMain = "";
				String id[] = obj.valueOf("@id").trim().split(":");
				lineMain = id[0];
				if (id.length < 3)
					continue;
				lineMain += "#"
						+ getTimeAllString(obj.valueOf("@TimeStamp").trim());
				lineMain += "#" + obj.valueOf("@MmeCode").trim();
				lineMain += "#" + obj.valueOf("@MmeGroupId").trim();
				lineMain += "#" + obj.valueOf("@MmeUeS1apId").trim();

				String line1 = "";
				String line2 = "";
				String line3 = "";

				int times = 1;
				// 每一个v
				// System.out.println(obj.valueOf("@TimeStamp").trim() + " "
				// + obj.valueOf("@id").trim());
				for (Iterator<Element> v = obj.elementIterator(); v.hasNext();) {
					String[] valueLine = v.next().getText().trim().split(" +");
					Iterator<String> itAttr = Attribute2Num.keySet().iterator();
					String s = "";
					line1 = lineMain.trim();
					line2 = lineMain.trim();
					line3 = lineMain.trim();

					String content = enbId + " " + getTimeMinuString(startTime)
							+ " " + getTimeMinuString(endTime) + " " + id[1];

					line1 += "#" + times + "#" + id[2] + " " + content;
					line2 += "#" + times + "#" + id[2] + " " + content;
					line3 += "#" + times + "#" + id[2] + " " + content;

					while (itAttr.hasNext()) {
						s = itAttr.next();
						// System.out.println(s);
						int fileNum = Attribute2Num.get(s);
						// if(valueLine[fileNum])
						String word = "NIL";
						if (Attribute2index.containsKey(s))
							word = valueLine[Attribute2index.get(s)];
						if (fileNum == 1) {
							line1 += " " + word;
						} else if (Attribute2Num.get(s) == 2) {
							line2 += " " + word;
						} else if (Attribute2Num.get(s) == 3) {
							line3 += " " + word;
						} else
							throw new IllegalArgumentException("解析xml分配行数有错误！ "
									+ " " + s);
					}
					fw1.write(line1.trim() + "\n");
					fw2.write(line2.trim() + "\n");
					fw3.write(line3.trim() + "\n");
					times++;
				}

			}
		} catch (IOException e) {

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

	private String getTimeAllString(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date reportTime;
		String timeNew = "";
		try {
			reportTime = sdf.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(reportTime);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			int second = cal.get(Calendar.SECOND);

			timeNew = year + String.format("%02d", month)
					+ String.format("%02d", day) + String.format("%02d", hour)
					+ String.format("%02d", minute)
					+ String.format("%02d", second);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return timeNew;
	}

	private String getTimeMinuString(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date reportTime;
		String timeNew = "";
		try {
			reportTime = sdf.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(reportTime);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			int second = cal.get(Calendar.SECOND);

			timeNew = String.format("%02d", hour)
					+ String.format("%02d", minute);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return timeNew;
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
		MroFile mf = new MroFile("in/mro10", "out/mro10", 1, "new");
		// int re = GETLSC("wwwaww", "wwwww");
		// System.out.println(re);
	}

	// public static int GETLSC(String a, String b) {
	// int map[][] = new int[a.length() + 1][b.length() + 1];
	// for (int i = 0; i <= a.length(); i++) {
	// map[i][0] = 0;
	// }
	// for (int i = 0; i <= b.length(); i++) {
	// map[0][i] = 0;
	// }
	// int max = 0;
	// for (int i = 1; i <= a.length(); i++) {
	// for (int j = 1; j <= b.length(); j++) {
	// if (a.charAt(i - 1) == b.charAt(j - 1)) {
	// map[i][j] = map[i - 1][j - 1] + 1;
	// if (max < map[i][j])
	// max = map[i][j];
	// } else
	// map[i][j] = 0;
	// }
	// }
	// return max;
	// }
}
