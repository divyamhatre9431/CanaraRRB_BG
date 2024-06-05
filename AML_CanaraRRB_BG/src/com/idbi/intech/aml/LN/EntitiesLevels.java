package com.idbi.intech.aml.LN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD) 
public class EntitiesLevels {
	@XmlElement
	private String ID;
	@XmlElement
	private String LevelDesc;
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getLevelDesc() {
		return LevelDesc;
	}
	public void setLevelDesc(String levelDesc) {
		LevelDesc = levelDesc;
	}
	
	

}
