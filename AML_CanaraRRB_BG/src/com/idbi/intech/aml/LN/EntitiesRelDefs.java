package com.idbi.intech.aml.LN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class EntitiesRelDefs {
	@XmlElement
	private String RelationID;
	@XmlElement
	private String RelationDef;
	
	public String getRelationID() {
		return RelationID;
	}
	public void setRelationID(String relationID) {
		RelationID = relationID;
	}
	public String getRelationDef() {
		return RelationDef;
	}
	public void setRelationDef(String relationDef) {
		RelationDef = relationDef;
	}
}
