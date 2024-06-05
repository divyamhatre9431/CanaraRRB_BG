package com.idbi.intech.aml.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CommonMethod {

	public static boolean makeZipFile(String zipDirectory) {

		File inFolder = null;
		File outFolder = null;

		ZipOutputStream outzip = null;
		BufferedInputStream in = null;

		try {
			inFolder = new File(zipDirectory);
			outFolder = new File(zipDirectory + ".zip");

			outzip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFolder)));

			byte[] data = new byte[1000];
			String files[] = inFolder.list();

			for (String file : files) {

				FileInputStream fileInput = new FileInputStream(inFolder.getPath() + "/" + file);
				in = new BufferedInputStream(fileInput, 1000);

				outzip.putNextEntry(new ZipEntry(file));
				int count;
				while ((count = in.read(data, 0, 1000)) != -1) {
					outzip.write(data, 0, count);
				}
				outzip.closeEntry();

				fileInput.close();
			}

			outzip.flush();
			outzip.close();

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				outzip.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String element : children) {
				boolean success = deleteDir(new File(dir, element));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

}
