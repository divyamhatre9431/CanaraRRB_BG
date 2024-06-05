package com.idbi.intech.iaml.finnet;

public class RequestDataBean 
{
	private String requestId;
	private String batchNo;
	private String fromDate;
	private String endDate;
	private String fileType;
	private String strNo;
	private String reportType;
	
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getStrNo() {
		return strNo;
	}
	public void setStrNo(String strNo) {
		this.strNo = strNo;
	}
	
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	
	@Override
	public String toString() {
		return "RequestDataBean [requestId=" + requestId + ", batchNo=" + batchNo + ", fromDate=" + fromDate
				+ ", endDate=" + endDate + ", fileType=" + fileType + ", strNo=" + strNo + ", reportType=" + reportType
				+ "]";
	}
	
	

}
