package com.idbi.intech.aml.bg_process;

public class CTRTransactionDatabean {

	private String tranId;
	private String PartTranSrlNo;
	private String tranDate;
	private String Remark;
	private String branchId;
	private String custId;
	private String tranAcNo;
	private String tranType;
	private String creDebFlg;
	private String tranAmt;
	private String tranCurrency;
	
	
	private String recordType;
	private String lineNumber;
	private String branchRefNo;
	private String disPositionOfFunds;
	
	
	
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public String getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}
	public String getBranchRefNo() {
		return branchRefNo;
	}
	public void setBranchRefNo(String branchRefNo) {
		this.branchRefNo = branchRefNo;
	}
	public String getDisPositionOfFunds() {
		return disPositionOfFunds;
	}
	public void setDisPositionOfFunds(String disPositionOfFunds) {
		this.disPositionOfFunds = disPositionOfFunds;
	}
	public String getTranId() {
		return tranId;
	}
	public void setTranId(String tranId) {
		this.tranId = tranId;
	}
	public String getPartTranSrlNo() {
		return PartTranSrlNo;
	}
	public void setPartTranSrlNo(String partTranSrlNo) {
		PartTranSrlNo = partTranSrlNo;
	}
	public String getTranDate() {
		return tranDate;
	}
	public void setTranDate(String tranDate) {
		this.tranDate = tranDate;
	}
	public String getRemark() {
		return Remark;
	}
	public void setRemark(String remark) {
		Remark = remark;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getTranAcNo() {
		return tranAcNo;
	}
	public void setTranAcNo(String tranAcNo) {
		this.tranAcNo = tranAcNo;
	}
	public String getTranType() {
		return tranType;
	}
	public void setTranType(String tranType) {
		this.tranType = tranType;
	}
	public String getCreDebFlg() {
		return creDebFlg;
	}
	public void setCreDebFlg(String creDebFlg) {
		this.creDebFlg = creDebFlg;
	}
	public String getTranAmt() {
		return tranAmt;
	}
	public void setTranAmt(String tranAmt) {
		this.tranAmt = tranAmt;
	}
	public String getTranCurrency() {
		return tranCurrency;
	}
	public void setTranCurrency(String tranCurrency) {
		this.tranCurrency = tranCurrency;
	}
	
	
	
}
