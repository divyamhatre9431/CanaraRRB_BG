package com.idbi.intech.iaml.screening;

public class Modal {

	private String keyName="";
	private int oldCount=0;
	private String oldKeyName="";
	private boolean flg=false;
	
	
	public boolean isFlg() {
		return flg;
	}
	public void setFlg(boolean flg) {
		this.flg = flg;
	}
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public int getOldCount() {
		return oldCount;
	}
	public void setOldCount(int oldCount) {
		this.oldCount = oldCount;
	}
	public String getOldKeyName() {
		return oldKeyName;
	}
	public void setOldKeyName(String oldKeyName) {
		this.oldKeyName = oldKeyName;
	}
}
