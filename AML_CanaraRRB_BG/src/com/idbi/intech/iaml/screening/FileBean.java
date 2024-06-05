package com.idbi.intech.iaml.screening;

import java.sql.Timestamp;

public class FileBean 
{
	private String fileName;
	private String fileType;
	private String filePriority;
	private String fileReqData;
	private String fileResData;
	private Timestamp fileReqTime;
	private Timestamp fileResTime;
	private String fileProcessFlg;
	private String fileReqPath;
	private String fileResPath;
	private String fileProcPath;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFilePriority() {
		return filePriority;
	}
	public void setFilePriority(String filePriority) {
		this.filePriority = filePriority;
	}
	public String getFileReqData() {
		return fileReqData;
	}
	public void setFileReqData(String fileReqData) {
		this.fileReqData = fileReqData;
	}
	public String getFileResData() {
		return fileResData;
	}
	public void setFileResData(String fileResData) {
		this.fileResData = fileResData;
	}
	public Timestamp getFileReqTime() {
		return fileReqTime;
	}
	public void setFileReqTime(Timestamp fileReqTime) {
		this.fileReqTime = fileReqTime;
	}
	public Timestamp getFileResTime() {
		return fileResTime;
	}
	public void setFileResTime(Timestamp fileResTime) {
		this.fileResTime = fileResTime;
	}
	public String getFileProcessFlg() {
		return fileProcessFlg;
	}
	public void setFileProcessFlg(String fileProcessFlg) {
		this.fileProcessFlg = fileProcessFlg;
	}
	public String getFileReqPath() {
		return fileReqPath;
	}
	public void setFileReqPath(String fileReqPath) {
		this.fileReqPath = fileReqPath;
	}
	public String getFileResPath() {
		return fileResPath;
	}
	public void setFileResPath(String fileResPath) {
		this.fileResPath = fileResPath;
	}
	public String getFileProcPath() {
		return fileProcPath;
	}
	public void setFileProcPath(String fileProcPath) {
		this.fileProcPath = fileProcPath;
	}
	
	@Override
	public String toString() {
		return "FileBean [fileName=" + fileName + ", fileType=" + fileType + ", filePriority=" + filePriority
				+ ", fileReqData=" + fileReqData + ", fileResData=" + fileResData + ", fileReqTime=" + fileReqTime
				+ ", fileResTime=" + fileResTime + ", fileProcessFlg=" + fileProcessFlg + ", fileReqPath=" + fileReqPath
				+ ", fileResPath=" + fileResPath + ", fileProcPath=" + fileProcPath + "]";
	}
	
}
