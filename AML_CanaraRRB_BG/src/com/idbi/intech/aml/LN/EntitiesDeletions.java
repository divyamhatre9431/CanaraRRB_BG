package com.idbi.intech.aml.LN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class EntitiesDeletions {
	@XmlElement
	private String Ent_ID;
	@XmlElement
	private String Name;
	@XmlElement
	private String DateDeleted;
	
	public String getEnt_ID() {
		return Ent_ID;
	}
	public void setEnt_ID(String ent_ID) {
		Ent_ID = ent_ID;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getDateDeleted() {
		return DateDeleted;
	}
	public void setDateDeleted(String dateDeleted) {
		DateDeleted = dateDeleted;
	}
}
