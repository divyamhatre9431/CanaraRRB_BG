package com.idbi.intech.aml.LN;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD) 
public class EntitiesSanctionDob {
	@XmlElement
	private String SanctionsDobId;
	@XmlElement
	private String Ent_Id;
	@XmlElement
	private String DOB;
	
	public String getSanctionsDobId() {
		return SanctionsDobId;
	}
	public void setSanctionsDobId(String sanctionsDobId) {
		SanctionsDobId = sanctionsDobId;
	}
	public String getEnt_Id() {
		return Ent_Id;
	}
	public void setEnt_Id(String ent_Id) {
		Ent_Id = ent_Id;
	}
	public String getDOB() {
		return DOB;
	}
	public void setDOB(String dOB) {
		DOB = dOB;
	}
	
	

}
