package com.idbi.intech.aml.upload.watchlist.sdn;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class publshInformation {
	
	@XmlElement
	public String Publish_Date;
	@XmlElement
	public int Record_Count;
	
	public String getPublish_Date() {
		return Publish_Date;
	}
	public void setPublish_Date(String publish_Date) {
		Publish_Date = publish_Date;
	}
	public int getRecord_Count() {
		return Record_Count;
	}
	public void setRecord_Count(int record_Count) {
		Record_Count = record_Count;
	}
	
	
}
