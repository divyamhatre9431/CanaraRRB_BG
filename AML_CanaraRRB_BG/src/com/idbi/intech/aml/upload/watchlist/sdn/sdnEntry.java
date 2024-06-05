package com.idbi.intech.aml.upload.watchlist.sdn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class sdnEntry {
	
	@XmlElement
	public String uid;
	@XmlElement
	public String firstName;
	@XmlElement
	public String lastName;
	@XmlElement
	public String sdnType;
	@XmlElement
	public String remarks;
	@XmlElement
	public programList programList;
	@XmlElement
	public akaList akaList;
	@XmlElement
	public addressList addressList;
	@XmlElement
	public citizenshipList citizenshipList;
	@XmlElement
	public dateOfBirthList dateOfBirthList;
	@XmlElement
	public nationalityList nationalityList;
	@XmlElement
	public placeOfBirthList placeOfBirthList;
	@XmlElement
	public idList idList;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getSdnType() {
		return sdnType;
	}
	public void setSdnType(String sdnType) {
		this.sdnType = sdnType;
	}
	public programList getProgramList() {
		return programList;
	}
	public void setProgramList(programList programList) {
		this.programList = programList;
	}
	public akaList getAkaList() {
		return akaList;
	}
	public void setAkaList(akaList akaList) {
		this.akaList = akaList;
	}
	public addressList getAddressList() {
		return addressList;
	}
	public void setAddressList(addressList addressList) {
		this.addressList = addressList;
	}
	public citizenshipList getCitizenshipList() {
		return citizenshipList;
	}
	public void setCitizenshipList(citizenshipList citizenshipList) {
		this.citizenshipList = citizenshipList;
	}
	public dateOfBirthList getDateOfBirthList() {
		return dateOfBirthList;
	}
	public void setDateOfBirthList(dateOfBirthList dateOfBirthList) {
		this.dateOfBirthList = dateOfBirthList;
	}
	public nationalityList getNationalityList() {
		return nationalityList;
	}
	public void setNationalityList(nationalityList nationalityList) {
		this.nationalityList = nationalityList;
	}
	public placeOfBirthList getPlaceOfBirthList() {
		return placeOfBirthList;
	}
	public void setPlaceOfBirthList(placeOfBirthList placeOfBirthList) {
		this.placeOfBirthList = placeOfBirthList;
	}
	public idList getIdList() {
		return idList;
	}
	public void setIdList(idList idList) {
		this.idList = idList;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	

}
