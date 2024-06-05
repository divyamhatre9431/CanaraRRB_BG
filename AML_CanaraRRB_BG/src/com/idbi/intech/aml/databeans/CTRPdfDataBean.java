/**
 * © Copyright IDBI Stringech Limited
 * 
 * File Name  : STRDataBean.java
 * Created By : Pradip Garala
 * 
 * Modification History
 * 
 * 12-09-2011	Pradip Garala		Initial version
 */

package com.idbi.intech.aml.databeans;

import java.io.Serializable;

public class CTRPdfDataBean implements Serializable {

	private static final long serialVersionUID = 1L;

	// part 1 details of report

	private String month1 = "";
	private String month2 = "";
	private String year1 = "";
	private String year2 = "";

	private String supplementaryYN = "No"; // "Yes/No/Off";

	private String originalReportDate1 = "";
	private String originalReportDate2 = "";
	private String originalReportMonth1 = "";
	private String originalReportMonth2 = "";
	private String originalReportYear1 = "";
	private String originalReportYear2 = "";

	// part 2 details of principal officer

	private String nameOfBank = "";
	private String bsrCode = "";
	private String fiuId = "";

	private String categoryCode = "";
	private String nameOfPrincipalOfficer = "";
	private String designationOfPrincipalOfficer = "";

	private String address = "";
	private String street = "";
	private String locality = "";
	private String city = "";
	private String state = "";

	private String pincode = "";
	private String telephone = "";
	private String fax = "";
	private String email = "";

	// part 3

	private String branchesTotal = "";
	private String branchesReported = "";
	private String branchesCTR = "";
	private String ctrOriginal = "";
	private String ctrReplacement = "";
	private String ctrForMonth = "";

	public String getBranchesTotal() {
		return branchesTotal;
	}

	public void setBranchesTotal(String branchesTotal) {
		this.branchesTotal = branchesTotal;
	}

	public String getBranchesReported() {
		return branchesReported;
	}

	public void setBranchesReported(String branchesReported) {
		this.branchesReported = branchesReported;
	}

	public String getBranchesCTR() {
		return branchesCTR;
	}

	public void setBranchesCTR(String branchesCTR) {
		this.branchesCTR = branchesCTR;
	}

	public String getCtrOriginal() {
		return ctrOriginal;
	}

	public void setCtrOriginal(String ctrOriginal) {
		this.ctrOriginal = ctrOriginal;
	}

	public String getCtrReplacement() {
		return ctrReplacement;
	}

	public void setCtrReplacement(String ctrReplacement) {
		this.ctrReplacement = ctrReplacement;
	}

	public String getCtrForMonth() {
		return ctrForMonth;
	}

	public void setCtrForMonth(String ctrForMonth) {
		this.ctrForMonth = ctrForMonth;
	}

	public String getMonth1() {
		return month1;
	}

	public void setMonth1(String month1) {
		this.month1 = month1;
	}

	public String getMonth2() {
		return month2;
	}

	public void setMonth2(String month2) {
		this.month2 = month2;
	}

	public String getYear1() {
		return year1;
	}

	public void setYear1(String year1) {
		this.year1 = year1;
	}

	public String getYear2() {
		return year2;
	}

	public void setYear2(String year2) {
		this.year2 = year2;
	}

	public String getSupplementaryYN() {
		return supplementaryYN;
	}

	public void setSupplementaryYN(String supplementaryYN) {
		this.supplementaryYN = supplementaryYN;
	}

	public String getOriginalReportDate1() {
		return originalReportDate1;
	}

	public void setOriginalReportDate1(String originalReportDate1) {
		this.originalReportDate1 = originalReportDate1;
	}

	public String getOriginalReportDate2() {
		return originalReportDate2;
	}

	public void setOriginalReportDate2(String originalReportDate2) {
		this.originalReportDate2 = originalReportDate2;
	}

	public String getOriginalReportMonth1() {
		return originalReportMonth1;
	}

	public void setOriginalReportMonth1(String originalReportMonth1) {
		this.originalReportMonth1 = originalReportMonth1;
	}

	public String getOriginalReportMonth2() {
		return originalReportMonth2;
	}

	public void setOriginalReportMonth2(String originalReportMonth2) {
		this.originalReportMonth2 = originalReportMonth2;
	}

	public String getOriginalReportYear1() {
		return originalReportYear1;
	}

	public void setOriginalReportYear1(String originalReportYear1) {
		this.originalReportYear1 = originalReportYear1;
	}

	public String getOriginalReportYear2() {
		return originalReportYear2;
	}

	public void setOriginalReportYear2(String originalReportYear2) {
		this.originalReportYear2 = originalReportYear2;
	}

	public String getNameOfBank() {
		return nameOfBank;
	}

	public void setNameOfBank(String nameOfBank) {
		this.nameOfBank = nameOfBank;
	}

	public String getBsrCode() {
		return bsrCode;
	}

	public void setBsrCode(String bsrCode) {
		this.bsrCode = bsrCode;
	}

	public String getFiuId() {
		return fiuId;
	}

	public void setFiuId(String fiuId) {
		this.fiuId = fiuId;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getNameOfPrincipalOfficer() {
		return nameOfPrincipalOfficer;
	}

	public void setNameOfPrincipalOfficer(String nameOfPrincipalOfficer) {
		this.nameOfPrincipalOfficer = nameOfPrincipalOfficer;
	}

	public String getDesignationOfPrincipalOfficer() {
		return designationOfPrincipalOfficer;
	}

	public void setDesignationOfPrincipalOfficer(String designationOfPrincipalOfficer) {
		this.designationOfPrincipalOfficer = designationOfPrincipalOfficer;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
} // end of class
