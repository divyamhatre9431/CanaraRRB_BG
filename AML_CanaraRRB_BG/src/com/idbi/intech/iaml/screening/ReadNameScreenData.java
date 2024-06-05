package com.idbi.intech.iaml.screening;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Properties;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class ReadNameScreenData implements Runnable {
	
	
	public void readNameScreenData(String inputFilePath,String priority,String fileType,String fileProcessPath) {
		try {
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			InputStream is;
			is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			//String inputFilePath = amlProp.getProperty("filePath");
			//String processedPath = amlProp.getProperty("custcoreProcessed");
			//String responsePath = amlProp.getProperty("custcoreResponsePath");
			int maxThreads = Integer.valueOf(amlProp.getProperty("maxThreads"));
			int cnt = 0;
			Connection connection = null;
			
			File reqFiles = new File(inputFilePath);
			String fname[] = reqFiles.list();
			FileBean fileObj = new FileBean();
			
			for (String reqFile : fname)
			{
				fileObj.setFileReqPath(inputFilePath + reqFile);
				fileObj.setFileResPath(fileProcessPath + reqFile);
				fileObj.setFilePriority(priority);
				fileObj.setFileType(fileType);
				
				try
				{
					connection = ConnectionFactory.makeConnectionAMLLive();
					cnt++;
					int count = requestReaderList(fileObj,connection);
					
					if(count > 0)
					{
						File srcFile = new File(fileObj.getFileReqPath());
						File destDir = new File(fileObj.getFileResPath());
						Path src = srcFile.toPath();
						Files.move(src, destDir.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}
					
					if(cnt == maxThreads)
					{
						break;
					}
					
					if (connection != null) {
						connection.close();
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			//logger.info("Exception occured");
			e.printStackTrace();
		}
	}
	
	
	public int requestReaderList(FileBean fileData,Connection connection) throws IOException {
		String lineText = null;
		BufferedReader lineReader = null;
		File file = null;
		//Connection connection = null;
		PreparedStatement pstmtLog = null;
		int count = 0;
		
		try {
			//connection = ConnectionFactory.makeConnectionAMLLive();
			file = new File(fileData.getFileReqPath());
			fileData.setFileName(file.getName());
			fileData.setFileReqTime(new Timestamp(file.lastModified()));
			lineReader = new BufferedReader(new FileReader(file));
			
			while ((lineText = lineReader.readLine()) != null) {
				try {
					String noSpaceLineText = lineText.trim();
					fileData.setFileReqData(noSpaceLineText);
					//System.out.println("noSpaceLineText : "+noSpaceLineText+" - file : "+file.getName()+" - file : "+new Date(file.lastModified()));
					
					pstmtLog = connection.prepareStatement("insert into name_screen_file_log(FILE_NAME,FILE_TYPE,FILE_PRIORITY,FILE_REQ_DATA,FILE_REQ_TIME,FILE_PROCESS_FLG) values (?,?,?,?,?,?) ");
					
					pstmtLog.setString(1, fileData.getFileName());
					pstmtLog.setString(2, fileData.getFileType());
					pstmtLog.setString(3, fileData.getFilePriority());
					pstmtLog.setString(4, fileData.getFileReqData());
					pstmtLog.setTimestamp(5, fileData.getFileReqTime());
					pstmtLog.setString(6, "N");
					
					count = pstmtLog.executeUpdate();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			file = null;
			
			connection.commit();
			connection.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (lineReader != null) {
				lineReader.close();
			}
		}
		
		return count;
	}
	
	
	@Override
	public void run() {
		while (true) {
			try {
				Properties amlProp = new Properties();
				String dir = System.getProperty("user.dir");
				InputStream is;
				is = new FileInputStream(dir + "/aml-config.properties");
				amlProp.load(is);
				int sleepTime = Integer.valueOf(amlProp.getProperty("sleepTime"));
				String fileProp = amlProp.getProperty("fileProp");
				String[] filePropArr = fileProp.split(",");
				
				for(String fileData : filePropArr)
				{
					String[] fileDataArr = fileData.split("-");
					
					String reqFilePath = amlProp.getProperty(fileDataArr[0]);
					String filePrior = fileDataArr[1];
					String fileType = fileDataArr[0];
					String procFilePath = amlProp.getProperty(fileDataArr[0]+"_process");
					
					readNameScreenData(reqFilePath,filePrior,fileType,procFilePath);
				}
				
				Thread.sleep(sleepTime);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

	public static void main(String[] args) {
		try {
			Thread threadObj = new Thread(new ReadNameScreenData());
			threadObj.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
