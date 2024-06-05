package com.idbi.intech.aml.util;

import java.util.ResourceBundle;

public class User {

	private InfoLogger logger =null;
	
	public void setInfoLogger(InfoLogger loger)
	{
		logger = loger;
	}
	
	public InfoLogger getInfoLogger()
	{
		return this.logger;
	}
	
	public int getLogLevel()
	{
		ResourceBundle amlProp = ResourceBundle.getBundle("AMLProp");
		return Integer.parseInt(amlProp.getString("LOGLEVEL"));
	}
	
}
