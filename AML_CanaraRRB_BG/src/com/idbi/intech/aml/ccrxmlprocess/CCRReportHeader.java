package com.idbi.intech.aml.ccrxmlprocess;


public class CCRReportHeader {	
	
	String reportType;
	String reportFormatType;
	String dataStructureVersion;
	String generationUtilityVersion;
	String dataSource;
	String reportingEntityName;
	String reportingEntityCategory;
	String reRegistrationNumber;
	String fiureid;
	String poName;
	String poDesignation;
	String address;
	String city;
	String stateCode;
	String pinCode;
	String countryCode;
	String telephone;
	String mobile;
	String fax;
	String poEmail;
	String batchNumber;
	String batchDate;
	String monthOfReport;
	String yearOfReport;
	String operationalMode;
	String batchType;
	String originalBatchID;
	String reasonOfRevision;
	String pkiCertificateNum;	
	
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getReportFormatType() {
		return reportFormatType;
	}
	public void setReportFormatType(String reportFormatType) {
		this.reportFormatType = reportFormatType;
	}
	public String getDataStructureVersion() {
		return dataStructureVersion;
	}
	public void setDataStructureVersion(String dataStructureVersion) {
		this.dataStructureVersion = dataStructureVersion;
	}
	public String getGenerationUtilityVersion() {
		return generationUtilityVersion;
	}
	public void setGenerationUtilityVersion(String generationUtilityVersion) {
		this.generationUtilityVersion = generationUtilityVersion;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public String getReportingEntityName() {
		return reportingEntityName;
	}
	public void setReportingEntityName(String reportingEntityName) {
		this.reportingEntityName = reportingEntityName;
	}
	public String getReportingEntityCategory() {
		return reportingEntityCategory;
	}
	public void setReportingEntityCategory(String reportingEntityCategory) {
		this.reportingEntityCategory = reportingEntityCategory;
	}
	public String getReRegistrationNumber() {
		return reRegistrationNumber;
	}
	public void setReRegistrationNumber(String reRegistrationNumber) {
		this.reRegistrationNumber = reRegistrationNumber;
	}
	public String getFiureid() {
		return fiureid;
	}
	public void setFiureid(String fiureid) {
		this.fiureid = fiureid;
	}
	public String getPoName() {
		return poName;
	}
	public void setPoName(String poName) {
		this.poName = poName;
	}
	public String getPoDesignation() {
		return poDesignation;
	}
	public void setPoDesignation(String poDesignation) {
		this.poDesignation = poDesignation;
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
	public String getPoEmail() {
		return poEmail;
	}
	public void setPoEmail(String poEmail) {
		this.poEmail = poEmail;
	}
	public String getBatchNumber() {
		return batchNumber;
	}
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	public String getBatchDate() {
		return batchDate;
	}
	public void setBatchDate(String batchDate) {
		this.batchDate = batchDate;
	}
	public String getMonthOfReport() {
		return monthOfReport;
	}
	public void setMonthOfReport(String monthOfReport) {
		this.monthOfReport = monthOfReport;
	}
	public String getYearOfReport() {
		return yearOfReport;
	}
	public void setYearOfReport(String yearOfReport) {
		this.yearOfReport = yearOfReport;
	}
	public String getOperationalMode() {
		return operationalMode;
	}
	public void setOperationalMode(String operationalMode) {
		this.operationalMode = operationalMode;
	}
	public String getBatchType() {
		return batchType;
	}
	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}
	public String getOriginalBatchID() {
		return originalBatchID;
	}
	public void setOriginalBatchID(String originalBatchID) {
		this.originalBatchID = originalBatchID;
	}
	public String getReasonOfRevision() {
		return reasonOfRevision;
	}
	public void setReasonOfRevision(String reasonOfRevision) {
		this.reasonOfRevision = reasonOfRevision;
	}
	public String getPkiCertificateNum() {
		return pkiCertificateNum;
	}
	public void setPkiCertificateNum(String pkiCertificateNum) {
		this.pkiCertificateNum = pkiCertificateNum;
	}	
	
	@Override
	public String toString() {
		return "CCRReportHeader [reportType=" + reportType
				+ ", reportFormatType=" + reportFormatType
				+ ", dataStructureVersion=" + dataStructureVersion
				+ ", generationUtilityVersion=" + generationUtilityVersion
				+ ", dataSource=" + dataSource + ", reportingEntityName="
				+ reportingEntityName + ", reportingEntityCategory="
				+ reportingEntityCategory + ", reRegistrationNumber="
				+ reRegistrationNumber + ", fiureid=" + fiureid + ", poName="
				+ poName + ", poDesignation=" + poDesignation + ", address="
				+ address + ", city=" + city + ", stateCode=" + stateCode
				+ ", pinCode=" + pinCode + ", countryCode=" + countryCode
				+ ", telephone=" + telephone + ", mobile=" + mobile + ", fax="
				+ fax + ", poEmail=" + poEmail + ", batchNumber=" + batchNumber
				+ ", batchDate=" + batchDate + ", monthOfReport="
				+ monthOfReport + ", yearOfReport=" + yearOfReport
				+ ", operationalMode=" + operationalMode + ", batchType="
				+ batchType + ", originalBatchID=" + originalBatchID
				+ ", reasonOfRevision=" + reasonOfRevision
				+ ", pkiCertificateNum=" + pkiCertificateNum + "]";
	}
}