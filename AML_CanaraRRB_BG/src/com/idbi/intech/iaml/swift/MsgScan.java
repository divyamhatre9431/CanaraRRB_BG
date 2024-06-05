package com.idbi.intech.iaml.swift;

public class MsgScan implements Runnable {
	String msgInfo = null;
	String url = null;
	String user = null;
	String password = "";

	public MsgScan(String msgInfo, String url, String user, String password) {
		this.msgInfo = msgInfo;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	@Override
	public void run() {
		MsgScanDAO daoObj = new MsgScanDAO();
		daoObj.LoadDatabaseAml(url,user,password);
		daoObj.getNoiseWord();
		daoObj.getMessageScanned(msgInfo);
	}
}

