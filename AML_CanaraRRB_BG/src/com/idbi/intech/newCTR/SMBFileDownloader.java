/*
 * package com.idbi.intech.newCTR;
 * 
 * import java.io.File; import java.io.FileInputStream;
 * 
 * import org.apache.commons.io.IOUtils;
 * 
 * import jcifs.smb.NtlmPasswordAuthentication; import jcifs.smb.SmbFile; import
 * jcifs.smb.SmbFileOutputStream;
 * 
 * public class SMBFileDownloader {
 * 
 * public static void main(String[] args) { try { String user = "Administrator";
 * String pass ="Pnb@1234";
 * 
 * String srcPath="D:/test1.txt"; String
 * destPath="smb://10.192.26.220/aml/test1.txt";
 * 
 * //File localFile = new File(srcPath); NtlmPasswordAuthentication auth = new
 * NtlmPasswordAuthentication("",user, pass); SmbFile smbFileDest = new
 * SmbFile(destPath,auth);
 * 
 * SmbFileOutputStream out = new SmbFileOutputStream(smbFileDest);
 * FileInputStream fis = new FileInputStream(localFile);
 * out.write(IOUtils.toByteArray(fis)); out.close();
 * 
 * SmbFileOutputStream smbfos = new SmbFileOutputStream(smbFileDest);
 * smbfos.write("testing....and writing to a file".getBytes());
 * System.out.println("completed ...nice !"); } catch(Exception e) {
 * e.printStackTrace(); }
 * 
 * }
 * 
 * }
 */