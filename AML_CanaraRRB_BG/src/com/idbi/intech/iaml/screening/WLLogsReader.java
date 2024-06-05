package com.idbi.intech.iaml.screening;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

public class WLLogsReader {
	
	private static final Logger logger = Logger.getLogger(WLLogsReader.class);

	public static void generateLogs() {
		
		try {
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			InputStream is;
			is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			
			String requestPath = amlProp.getProperty("IN1_REQUEST");
			String responsePath = amlProp.getProperty("IN1_RESPONSE");
			String logsPath = amlProp.getProperty("IN1_LOGS");
			
			File reqFiles = new File(requestPath);
			String fname[] = reqFiles.list();
			
			for (String reqFile : fname)
			{
				try
				{
					String fileNameWithOutExt = FilenameUtils.removeExtension(reqFile);
					List<String> requestJsonList = fileReaderList(requestPath + reqFile);
					
					boolean isFileExist = isFileExist(fileNameWithOutExt, logsPath);
					
					if(!isFileExist)
					{
						for(String requestData : requestJsonList)
						{
							List<String> responseJsonList = fileReaderList(responsePath + fileNameWithOutExt+".res");
							
							for(String responseData : responseJsonList)
							{
								
								String noSpaceLineText = responseData.trim();
								String[] data = noSpaceLineText.split("\\|", -1);
								String suspFlg = data[10].toString();
								
								if(suspFlg.equalsIgnoreCase("E"))
								{
									//System.out.println("requestData : "+fileNameWithOutExt+"----------"+requestData+"----------"+responseData);
									logger.info(fileNameWithOutExt+"----------"+requestData+"----------"+responseData);
								}
								
							}
						}
					}
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				
			}
			
		} catch (IOException e) {
			logger.info("Exception occured");
			e.printStackTrace();
		}

	}

	
	public static List<String> fileReaderList(String filePath) throws IOException {
		String lineText = null;
		BufferedReader lineReader = null;
		File file = null;
		List<String> requestList = new ArrayList<String>();
		try {
			file = new File(filePath);
			lineReader = new BufferedReader(new FileReader(file));
			
			while ((lineText = lineReader.readLine()) != null) {
				try {
					String noSpaceLineText = lineText.trim();
					requestList.add(noSpaceLineText);
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (lineReader != null) {
				lineReader.close();
			}
		}
		return requestList;
	}
	
	
	public static boolean isFileExist(String fileName, String filePath) {

		boolean isExist = false;

		try {

			File logFiles = new File(filePath);
			String logFilesName[] = logFiles.list();

			for (String logFile : logFilesName) {
				
				File file = new File(filePath+""+logFile);
				Scanner scanner = new Scanner(file);

				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if (line.contains(fileName)) {
						isExist = true;
					}
					
					if(isExist)
					{
						break;
					}
				}

				scanner.close();
			}

		} catch (Exception e) {
			isExist = false;
			e.printStackTrace();
		}

		return isExist;
	}
	
	
	public static void main(String args[]) {
		try {
			generateLogs();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
