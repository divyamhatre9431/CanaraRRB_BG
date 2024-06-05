package com.idbi.intech.aml.upload.watchlist.sdn;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class citizenshipList { 
	
	@XmlElement
	public List<citizenship> citizenship;

	public List<citizenship> getCitizenship() {
		return citizenship;
	}

	public void setCitizenship(List<citizenship> citizenship) {
		this.citizenship = citizenship;
	}
	
	
}