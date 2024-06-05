package com.idbi.intech.aml.upload.watchlist.sdn;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class programList {
	
	@XmlElement
	public List<String> program;

	public List<String> getProgram() {
		return program;
	}

	public void setProgram(List<String> program) {
		this.program = program;
	}

	
	
}
