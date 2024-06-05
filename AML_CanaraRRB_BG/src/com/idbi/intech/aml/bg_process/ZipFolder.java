package com.idbi.intech.aml.bg_process;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFolder {
	
	private static String logFile = BgProcessLogger.generateLogFileName("ZipFolder");
	public static void zipFolder(String sourceFolderName) {
		BgProcessLogger.writeToFile(logFile, "ZipFolder", "zipFolder","Start");
		try {

			File inFolder = new File(sourceFolderName);
			File outFolder = new File(sourceFolderName+".zip");
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(outFolder)));
			BufferedInputStream in = null;
			byte[] data = new byte[1000];
			String files[] = inFolder.list();
			for (int i = 0; i < files.length; i++) {
				in = new BufferedInputStream(new FileInputStream(
						inFolder.getPath() + "/" + files[i]), 1000);
				out.putNextEntry(new ZipEntry(files[i]));
				int count;
				while ((count = in.read(data, 0, 1000)) != -1) {
					out.write(data, 0, count);
				}
				out.closeEntry();
			}
			out.flush();
			out.close();
			BgProcessLogger.writeToFile(logFile, "ZipFolder", "zipFolder","End");
		} catch (Exception e) {
			//e.printStackTrace();
			BgProcessLogger.writeToFileException(logFile,"ZipFolder", "zipFolder", e);
		}
		BgProcessLogger.writeToFile(logFile, "ZipFolder", "zipFolder","End");
	}

	public static void main(String []args)
	{
		BgProcessLogger.writeToFile(logFile, "ZipFolder", "main","Start");
		zipFolder("D:\\161");
		zipFolder("D:\\162");
		
		BgProcessLogger.writeToFile(logFile, "ZipFolder", "main","End");
	}
}
