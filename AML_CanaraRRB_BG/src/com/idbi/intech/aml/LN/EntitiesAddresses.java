package com.idbi.intech.aml.LN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class EntitiesAddresses {
	@XmlElement
	private String Address_ID;
	@XmlElement
	private String Ent_ID;
	@XmlElement
	private String Address;
	@XmlElement
	private String City;
	@XmlElement
	private String StateProvince;
	@XmlElement
	private String Country;
	@XmlElement
	private String PostalCode;
	@XmlElement
	private String Remarks;
	@XmlElement
	private String NameSources;
	
	
	public String getAddress_ID() {
		return Address_ID;
	}
	public void setAddress_ID(String address_ID) {
		Address_ID = address_ID;
	}
	public String getEnt_ID() {
		return Ent_ID;
	}
	public void setEnt_ID(String ent_ID) {
		Ent_ID = ent_ID;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getCity() {
		return City;
	}
	public void setCity(String city) {
		City = city;
	}
	public String getStateProvince() {
		return StateProvince;
	}
	public void setStateProvince(String stateProvince) {
		StateProvince = stateProvince;
	}
	public String getCountry() {
		return Country;
	}
	public void setCountry(String country) {
		Country = country;
	}
	public String getPostalCode() {
		return PostalCode;
	}
	public void setPostalCode(String postalCode) {
		PostalCode = postalCode;
	}
	public String getRemarks() {
		return Remarks;
	}
	public void setRemarks(String remarks) {
		Remarks = remarks;
	}
	public String getNameSources() {
		return NameSources;
	}
	public void setNameSources(String nameSources) {
		NameSources = nameSources;
	}
	
	

}
