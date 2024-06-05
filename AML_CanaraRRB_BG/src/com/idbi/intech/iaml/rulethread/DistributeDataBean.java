package com.idbi.intech.iaml.rulethread;

public class DistributeDataBean {
	private final String custId;
	private final String custVertical;
	
	public String getCustId() {
		return custId;
	}
	public String getCustVertical() {
		return custVertical;
	}
	
	public DistributeDataBean(String custId, String custVertical) {
		this.custId = custId;
		this.custVertical = custVertical;
	}
	
	

}
