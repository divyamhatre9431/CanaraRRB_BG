package com.idbi.intech.iaml.finnet;

public class StrReqDataBean 
{
	private String strNo;
	private String batchNo;
	private String fromDate;
	private String endDate;
	private String reportType;
	private String txnChannel;
	
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
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getStrNo() {
		return strNo;
	}
	public void setStrNo(String strNo) {
		this.strNo = strNo;
	}
	public String getTxnChannel() {
		return txnChannel;
	}
	public void setTxnChannel(String txnChannel) {
		this.txnChannel = txnChannel;
	}
	
	

}
