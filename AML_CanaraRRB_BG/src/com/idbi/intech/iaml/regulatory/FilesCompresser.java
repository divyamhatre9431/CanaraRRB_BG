package com.idbi.intech.iaml.regulatory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FilesCompresser {

	public static void compress(String zipFile, String[] srcFiles) {
		try {
			byte[] buffer = new byte[1024];

			FileOutputStream fos = new FileOutputStream(zipFile+".zip");

			ZipOutputStream zos = new ZipOutputStream(fos);

			for (int i = 0; i < srcFiles.length; i++) 
			{
				File srcFile = new File(srcFiles[i]);
				FileInputStream fis = new FileInputStream(srcFile);
				zos.putNextEntry(new ZipEntry(srcFile.getName()));
				int length;

				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
				zos.closeEntry();
				fis.close();
			}
			zos.close();
		} catch (IOException ioe) {
			System.out.println("Error creating zip file: " + ioe);
		}
	}

}
