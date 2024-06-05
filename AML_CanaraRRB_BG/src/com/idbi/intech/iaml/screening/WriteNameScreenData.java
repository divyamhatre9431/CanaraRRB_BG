package com.idbi.intech.iaml.screening;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import com.idbi.intech.iaml.factory.ConnectionFactory;

public class WriteNameScreenData implements Runnable {
	
	public void writeNameScreenData()
	{
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			connection = ConnectionFactory.makeConnectionAMLLive();
			
			Properties amlProp = new Properties();
			String dir = System.getProperty("user.dir");
			InputStream is;
			is = new FileInputStream(dir + "/aml-config.properties");
			amlProp.load(is);
			int maxThreads = Integer.valueOf(amlProp.getProperty("maxResponseThreads"));
			int cnt = 0;
			
			String checkSQL = "select * from name_screen_file_log where file_process_flg='S' order by file_priority";
			//System.out.println("checkSQL : "+checkSQL);
			stmt = connection.prepareStatement(checkSQL);
			rs = stmt.executeQuery();
			
			while(rs.next())
			{
				cnt++;
				String fileType = rs.getString("FILE_TYPE");
				String fileName = rs.getString("FILE_NAME");
				String responseData = rs.getString("FILE_RES_DATA");
				//System.out.println("fileType : "+fileType+"-"+fileName+"-"+responseData);
				
				String fileNameWithoutExt = fileName.substring(0, fileName.length()-4);
				String responsePath = amlProp.getProperty(fileType+"_response");
				
				Writer w = new FileWriter(responsePath + fileNameWithoutExt + ".RES");
				w.write(responseData);
				w.close();
				
				PreparedStatement updateStmt = null;
				String updateSQL = "update name_screen_file_log set file_process_flg='Y' where file_name=?";
				updateStmt = connection.prepareStatement(updateSQL);
				updateStmt.setString(1, fileName);
				updateStmt.executeUpdate();
				connection.commit();
				
				if(maxThreads == cnt)
				{
					break;
				}
			}
			
			stmt.close();
			connection.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		while(true)
		{
			try
			{
				Properties amlProp = new Properties();
				String dir = System.getProperty("user.dir");
				InputStream is;
				is = new FileInputStream(dir + "/aml-config.properties");
				amlProp.load(is);
				int sleepTime = Integer.valueOf(amlProp.getProperty("sleepTime"));
				
				writeNameScreenData();
				
				Thread.sleep(sleepTime);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args) {
		try {
			Thread threadObj = new Thread(new WriteNameScreenData());
			threadObj.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
