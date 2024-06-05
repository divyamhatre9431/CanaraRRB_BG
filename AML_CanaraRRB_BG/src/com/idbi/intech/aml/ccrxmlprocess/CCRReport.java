package com.idbi.intech.aml.ccrxmlprocess;

import java.math.BigInteger;
import java.util.List;

public class CCRReport {
	
	BigInteger reportSerialNo;
	BigInteger originalReportSerialNum;	
	String branchRefNumType;	
	String branchRefNum;
	String branchName;
	String address;
	String city;
	String stateCode;
	String pinCode;
	String countryCode;
	String telephone;
	String mobile;
	String fax;
	String branchEmail;	
	BigInteger iNR1000NoteCount;
	BigInteger iNR500NoteCount;
	BigInteger iNR100NoteCount;
	BigInteger iNR50NoteCount;
	BigInteger iNR20NoteCount;
	BigInteger iNR10NoteCount;
	BigInteger iNR5NoteCount;
	BigInteger fICNValue;
	String dateOfTendering;
	BigInteger cashTendered;
	String dateOfDetection;
	String detectedAt;	
	String policeInformed;
	String policeReportDetail;
	String tenderingPerson;
	String accountHolder;	
	String accountNumber;	
	String priorityRating;
	String incidentRemarks;	
	List<CCRReportTransaction> ccrReportTransactions;
	
	public BigInteger getReportSerialNo() {
		return reportSerialNo;
	}
	public void setReportSerialNo(BigInteger reportSerialNo) {
		this.reportSerialNo = reportSerialNo;
	}
	public BigInteger getOriginalReportSerialNum() {
		return originalReportSerialNum;
	}
	public void setOriginalReportSerialNum(BigInteger originalReportSerialNum) {
		this.originalReportSerialNum = originalReportSerialNum;
	}
	public String getBranchRefNumType() {
		return branchRefNumType;
	}
	public void setBranchRefNumType(String branchRefNumType) {
		this.branchRefNumType = branchRefNumType;
	}
	public String getBranchRefNum() {
		return branchRefNum;
	}
	public void setBranchRefNum(String branchRefNum) {
		this.branchRefNum = branchRefNum;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStateCode() {
		return stateCode;
	}
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getBranchEmail() {
		return branchEmail;
	}
	public void setBranchEmail(String branchEmail) {
		this.branchEmail = branchEmail;
	}
	public BigInteger getiNR1000NoteCount() {
		return iNR1000NoteCount;
	}
	public void setiNR1000NoteCount(BigInteger iNR1000NoteCount) {
		this.iNR1000NoteCount = iNR1000NoteCount;
	}
	public BigInteger getiNR500NoteCount() {
		return iNR500NoteCount;
	}
	public void setiNR500NoteCount(BigInteger iNR500NoteCount) {
		this.iNR500NoteCount = iNR500NoteCount;
	}
	public BigInteger getiNR100NoteCount() {
		return iNR100NoteCount;
	}
	public void setiNR100NoteCount(BigInteger iNR100NoteCount) {
		this.iNR100NoteCount = iNR100NoteCount;
	}
	public BigInteger getiNR50NoteCount() {
		return iNR50NoteCount;
	}
	public void setiNR50NoteCount(BigInteger iNR50NoteCount) {
		this.iNR50NoteCount = iNR50NoteCount;
	}
	public BigInteger getiNR20NoteCount() {
		return iNR20NoteCount;
	}
	public void setiNR20NoteCount(BigInteger iNR20NoteCount) {
		this.iNR20NoteCount = iNR20NoteCount;
	}
	public BigInteger getiNR10NoteCount() {
		return iNR10NoteCount;
	}
	public void setiNR10NoteCount(BigInteger iNR10NoteCount) {
		this.iNR10NoteCount = iNR10NoteCount;
	}
	public BigInteger getiNR5NoteCount() {
		return iNR5NoteCount;
	}
	public void setiNR5NoteCount(BigInteger iNR5NoteCount) {
		this.iNR5NoteCount = iNR5NoteCount;
	}
	public BigInteger getfICNValue() {
		return fICNValue;
	}
	public void setfICNValue(BigInteger fICNValue) {
		this.fICNValue = fICNValue;
	}
	public String getDateOfTendering() {
		return dateOfTendering;
	}
	public void setDateOfTendering(String dateOfTendering) {
		this.dateOfTendering = dateOfTendering;
	}
	public BigInteger getCashTendered() {
		return cashTendered;
	}
	public void setCashTendered(BigInteger cashTendered) {
		this.cashTendered = cashTendered;
	}
	public String getDateOfDetection() {
		return dateOfDetection;
	}
	public void setDateOfDetection(String dateOfDetection) {
		this.dateOfDetection = dateOfDetection;
	}
	public String getDetectedAt() {
		return detectedAt;
	}
	public void setDetectedAt(String detectedAt) {
		this.detectedAt = detectedAt;
	}
	public String getPoliceInformed() {
		return policeInformed;
	}
	public void setPoliceInformed(String policeInformed) {
		this.policeInformed = policeInformed;
	}
	public String getPoliceReportDetail() {
		return policeReportDetail;
	}
	public void setPoliceReportDetail(String policeReportDetail) {
		this.policeReportDetail = policeReportDetail;
	}
	public String getTenderingPerson() {
		return tenderingPerson;
	}
	public void setTenderingPerson(String tenderingPerson) {
		this.tenderingPerson = tenderingPerson;
	}
	public String getAccountHolder() {
		return accountHolder;
	}
	public void setAccountHolder(String accountHolder) {
		this.accountHolder = accountHolder;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getPriorityRating() {
		return priorityRating;
	}
	public void setPriorityRating(String priorityRating) {
		this.priorityRating = priorityRating;
	}
	
	public void setCcrReportTransactions(List<CCRReportTransaction> ccrReportTransactions) {
		this.ccrReportTransactions = ccrReportTransactions;
	}
	public List<CCRReportTransaction> getCcrReportTransactions() {
		return ccrReportTransactions;
	}
	public void setIncidentRemarks(String incidentRemarks) {
		this.incidentRemarks = incidentRemarks;
	}
	public String getIncidentRemarks() {
		return incidentRemarks;
	}
	
	@Override
	public String toString() {
		return "CCRReport [reportSerialNo=" + reportSerialNo
				+ ", originalReportSerialNum=" + originalReportSerialNum
				+ ", branchRefNumType=" + branchRefNumType + ", branchRefNum="
				+ branchRefNum + ", branchName=" + branchName + ", address="
				+ address + ", city=" + city + ", stateCode=" + stateCode
				+ ", pinCode=" + pinCode + ", countryCode=" + countryCode
				+ ", telephone=" + telephone + ", mobile=" + mobile + ", fax="
				+ fax + ", branchEmail=" + branchEmail + ", iNR1000NoteCount="
				+ iNR1000NoteCount + ", iNR500NoteCount=" + iNR500NoteCount
				+ ", iNR100NoteCount=" + iNR100NoteCount + ", iNR50NoteCount="
				+ iNR50NoteCount + ", iNR20NoteCount=" + iNR20NoteCount
				+ ", iNR10NoteCount=" + iNR10NoteCount + ", iNR5NoteCount="
				+ iNR5NoteCount + ", fICNValue=" + fICNValue
				+ ", dateOfTendering=" + dateOfTendering + ", cashTendered="
				+ cashTendered + ", dateOfDetection=" + dateOfDetection
				+ ", detectedAt=" + detectedAt + ", policeInformed="
				+ policeInformed + ", policeReportDetail=" + policeReportDetail
				+ ", tenderingPerson=" + tenderingPerson + ", accountHolder="
				+ accountHolder + ", accountNumber=" + accountNumber
				+ ", priorityRating=" + priorityRating + ", incidentRemarks="
				+ incidentRemarks + ", ccrReportTransactions="
				+ ccrReportTransactions + "]";
	}	
}