package edu.thu.keg.mr;

import java.io.File;
import java.net.MalformedURLException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

public interface XMLFile {
	public Document read(File fileName) throws MalformedURLException,
			DocumentException;

	public Element getRootElement(Document doc);
}
