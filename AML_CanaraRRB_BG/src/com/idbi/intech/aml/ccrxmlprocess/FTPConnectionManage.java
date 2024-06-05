package com.idbi.intech.aml.ccrxmlprocess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.idbi.intech.aml.util.InfoLogger;

public class FTPConnectionManage {

	
	//logger.logVerboseText(FTPConnectionManage.class.get, "ctrXMLDetails", "Start");
	
	public static FTPClient getFTPConnection(String serverIP, String userId, 
				String password, String directoryLocation,InfoLogger log){
		
		FTPClient client = null;
		
		try{
		
			if(client == null){
				client = new FTPClient();
			}
			
			client.connect(serverIP);
			client.login(userId,password);
			client.changeWorkingDirectory(directoryLocation);		
		
		}catch(Exception e){
			log.logVerboseText(FTPConnectionManage.class.getSimpleName(), "getFTPConnection", e.getMessage());
		}
		
		return client;		
	}	
	
	public static boolean transferFTPFilesToServerLocation(
			FTPClient client, String serverLocationToTransferFilesFromFTP, InfoLogger log){
		
		FTPFile[] files = null;
		FileOutputStream fos = null;
		
		if(client == null){
			return false;
		}else{			
			
			try{
				files = client.listFiles();			
				
				int j = files.length > 30 ? 30 : files.length; 
				
				for (int i = 0; i < j; i++) {
					
					if (files[i].isFile()) {
						
						int dotPos = files[i].getName().lastIndexOf(".");
						String extension = files[i].getName().substring(dotPos);
						
						if (extension.equalsIgnoreCase(".xml")){
							
							//System.out.println("FTPFile: " + files[i].getName() + "; " + files[i].getSize());
							File file = new File(serverLocationToTransferFilesFromFTP + files[i].getName());
							fos = new FileOutputStream(file);
							client.retrieveFile(files[i].getName(), fos);
							fos.flush();
							fos.close();
							// On Success Delete the file
							//client.deleteFile( files[i].getName());
						}
					}
				}
			}catch(Exception e){
				log.logVerboseText(FTPConnectionManage.class.getSimpleName(), "transferFTPFilesToServerLocation", e.getMessage());
				return false;
			}finally{
				
				if(fos != null){					
					try {
						fos.close();
					} catch (IOException e) {						
						log.logVerboseText(FTPConnectionManage.class.getSimpleName(), "transferFTPFilesToServerLocation", e.getMessage());
					}
				}
			}
			
			return true;
		}		
	}
	
	public static void disconnectFTP(FTPClient client, InfoLogger log) {
		try {
			client.logout();
			client.disconnect();
			//System.out.println("Succesfully Logged Out.");
		} catch (Exception ex) {			
			log.logVerboseText(FTPConnectionManage.class.getSimpleName(), "disconnectFTP", ex.getMessage());
		}
	}
}