package com.idbi.intech.aml.LN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class EntitiesRelationships {
	@XmlElement
	private String RID;
	@XmlElement
	private String Ent_IDParent;
	@XmlElement
	private String Ent_IDChild;
	@XmlElement
	private String RelationID;
	
	public String getRID() {
		return RID;
	}
	public void setRID(String rID) {
		RID = rID;
	}
	public String getEnt_IDParent() {
		return Ent_IDParent;
	}
	public void setEnt_IDParent(String ent_IDParent) {
		Ent_IDParent = ent_IDParent;
	}
	public String getEnt_IDChild() {
		return Ent_IDChild;
	}
	public void setEnt_IDChild(String ent_IDChild) {
		Ent_IDChild = ent_IDChild;
	}
	public String getRelationID() {
		return RelationID;
	}
	public void setRelationID(String relationID) {
		RelationID = relationID;
	}
}
