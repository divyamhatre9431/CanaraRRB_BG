package com.idbi.intech.aml.databeans;

import java.util.ArrayList;

public class Account {

	private STRAccountFileBean accountFileBean = new STRAccountFileBean();
	private STRBranchFileBean branchFileBean = new STRBranchFileBean();

	private ArrayList<STRIndividualFileBean> individualFileBeans = new ArrayList<STRIndividualFileBean>();
	private ArrayList<STRLPEFIleBean> strlpefIleBeans = new ArrayList<STRLPEFIleBean>();

	private ArrayList<STRTransactionFileBean> transactionFileBeans = new ArrayList<STRTransactionFileBean>();

	public STRAccountFileBean getAccountFileBean() {
		return accountFileBean;
	}

	public void setAccountFileBean(STRAccountFileBean accountFileBean) {
		this.accountFileBean = accountFileBean;
	}

	public STRBranchFileBean getBranchFileBean() {
		return branchFileBean;
	}

	public void setBranchFileBean(STRBranchFileBean branchFileBean) {
		this.branchFileBean = branchFileBean;
	}

	public ArrayList<STRIndividualFileBean> getIndividualFileBeans() {
		return individualFileBeans;
	}

	public void setIndividualFileBeans(ArrayList<STRIndividualFileBean> individualFileBeans) {
		this.individualFileBeans = individualFileBeans;
	}

	public ArrayList<STRLPEFIleBean> getStrlpefIleBeans() {
		return strlpefIleBeans;
	}

	public void setStrlpefIleBeans(ArrayList<STRLPEFIleBean> strlpefIleBeans) {
		this.strlpefIleBeans = strlpefIleBeans;
	}

	public ArrayList<STRTransactionFileBean> getTransactionFileBeans() {
		return transactionFileBeans;
	}

	public void setTransactionFileBeans(ArrayList<STRTransactionFileBean> transactionFileBeans) {
		this.transactionFileBeans = transactionFileBeans;
	}
}
