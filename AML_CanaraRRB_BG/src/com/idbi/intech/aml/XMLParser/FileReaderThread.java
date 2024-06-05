package com.idbi.intech.aml.XMLParser;

import java.io.File;
import java.util.ResourceBundle;

public class FileReaderThread implements Runnable {

	@Override
	public void run() {
		while (true) {
			ResourceBundle rb = ResourceBundle
					.getBundle("com.idbi.intech.aml.XMLParser.XMLFile");
			String source = rb.getString("xmlFileSource");
			String dest = rb.getString("xmlFileDest");
			String delimiter=null;
			ReadXMLFile readxmlFile = new ReadXMLFile();
			
			File xmlFile = new File(source);
			File fileList[] = xmlFile.listFiles();
			//if (fileList.length == 4) {
				for (File file : fileList) {
					if (file.getName().contains(".xml")) {
						readxmlFile.readFile(file,
								source + "\\" + file.getName(), dest + "\\"
										+ file.getName());
					} 
					else if(file.getName().contains(".csv")){
						delimiter=rb.getString("delimiter");
						 ReadCSVFile.parseCSV(file, source + "\\" + file.getName(), dest + "\\"
									+ file.getName(),delimiter);
					}
					else {
						System.out.println("File format of " + file.getName()
								+ " file is not excepted");
					}
				}
//			} else {
//				System.out.println("Files missing");
//			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {

		Thread thread = new Thread(new FileReaderThread());
		thread.start();
	}
}
