package com.idbi.intech.aml.upload.watchlist;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import com.idbi.intech.aml.upload.watchlist.sdn.sdnList;


public class DatafeedUploadNMSCR implements Runnable {
	@Override
	public void run() {
		while (true) {
			
			String filePath = "";

			try {
				Properties reportProp = new Properties();
				String dir = System.getProperty("user.dir");
				InputStream is = new FileInputStream(dir + "/aml-config.properties");
				//InputStream is = new FileInputStream(dir + "/Nmscr-aml-config.properties");
				reportProp.load(is);
				is.close();

				filePath = reportProp.getProperty("WATCHLISTUPLOAD");
			} catch (Exception e) {
				e.printStackTrace();
			}

			DatafeedUploadDaoNMSCR feedDao = new DatafeedUploadDaoNMSCR();
			List<String> reqIdList = feedDao.getUnprocessedRequest();

			for (String reqData : reqIdList) {
				System.out.println("reqData : " + reqData);
				String[] reqDataArr = reqData.split("~");

				try {
					String xmlFilePath = filePath + "" + reqDataArr[0] + ".xml";

					if (reqDataArr[1].equals("SDN")) {
						
						System.out.println("In SDN : " + xmlFilePath);

						JAXBContext context = JAXBContext.newInstance(sdnList.class);
						InputStream inputStream = new FileInputStream(new File(xmlFilePath));
						XMLStreamReader xsr = XMLInputFactory.newFactory().createXMLStreamReader(inputStream);
						XMLReaderWithoutNamespace xr = new XMLReaderWithoutNamespace(xsr);
						Unmarshaller um = context.createUnmarshaller();
						sdnList sdn = (sdnList) um.unmarshal(xr);

						feedDao.uploadSdnData(sdn,reqDataArr[0]);
						
					} else {
						
						System.out.println("In Individual : " + xmlFilePath);

						JAXBContext context = JAXBContext.newInstance(CONSOLIDATED_LIST.class);
						InputStream inputStream = new FileInputStream(new File(xmlFilePath));
						XMLStreamReader xsr = XMLInputFactory.newFactory().createXMLStreamReader(inputStream);
						XMLReaderWithoutNamespace xr = new XMLReaderWithoutNamespace(xsr);
						Unmarshaller um = context.createUnmarshaller();
						CONSOLIDATED_LIST ind = (CONSOLIDATED_LIST) um.unmarshal(xr);

						feedDao.uploadIndividualData(ind,reqDataArr[0]);
					}

					feedDao.updateRequestStatus(reqDataArr[0], "Y");
				} catch (Exception e) {
					feedDao.updateRequestStatus(reqDataArr[0], "N");
					e.printStackTrace();
				}

			}

			try { 
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args)
	{
		Thread t = new Thread(new DatafeedUploadNMSCR());
		t.start();
	}
	
}
