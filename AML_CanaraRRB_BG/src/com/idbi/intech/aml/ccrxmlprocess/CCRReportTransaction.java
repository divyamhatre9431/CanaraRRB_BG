package com.idbi.intech.aml.ccrxmlprocess;


public class CCRReportTransaction {	
	
	String denomination;
	String currencySerialNo;
	String currencyRemarks;	
	
	public String getDenomination() {
		return denomination;
	}
	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}
	public String getCurrencySerialNo() {
		return currencySerialNo;
	}
	public void setCurrencySerialNo(String currencySerialNo) {
		this.currencySerialNo = currencySerialNo;
	}
	public String getCurrencyRemarks() {
		return currencyRemarks;
	}
	public void setCurrencyRemarks(String currencyRemarks) {
		this.currencyRemarks = currencyRemarks;
	}
	
	@Override
	public String toString() {
		return "CCRReportTransaction [denomination="
				+ denomination + ", currencySerialNo=" + currencySerialNo
				+ ", currencyRemarks=" + currencyRemarks + "]";
	}
}