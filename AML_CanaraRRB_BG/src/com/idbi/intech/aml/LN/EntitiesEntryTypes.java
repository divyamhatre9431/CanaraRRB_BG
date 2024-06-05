package com.idbi.intech.aml.LN;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;



@XmlAccessorType(XmlAccessType.FIELD) 
public class EntitiesEntryTypes {
	@XmlElement
	private String ID;
	@XmlElement
	private String EntryDesc;
	

	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getEntryDesc() {
		return EntryDesc;
	}
	public void setEntryDesc(String entryDesc) {
		EntryDesc = entryDesc;
	}
	
	

}
