package com.idbi.intech.aml.databeans;

import java.util.ArrayList;

public class STRWalkInControlDataBean {

	 private ArrayList<STRWalkInTransactionDataBean> transactionDataBeans = new ArrayList<STRWalkInTransactionDataBean>();
	 private ArrayList<STRWalkInBranchDataBean> branchDataBeans = new ArrayList<STRWalkInBranchDataBean>();
	 private ArrayList<STRWalkInCustomerDataBean> customerDataBeans = new ArrayList<STRWalkInCustomerDataBean>();
	public ArrayList<STRWalkInTransactionDataBean> getTransactionDataBeans() {
		return transactionDataBeans;
	}
	public void setTransactionDataBeans(
			ArrayList<STRWalkInTransactionDataBean> transactionDataBeans) {
		this.transactionDataBeans = transactionDataBeans;
	}
	public ArrayList<STRWalkInBranchDataBean> getBranchDataBeans() {
		return branchDataBeans;
	}
	public void setBranchDataBeans(
			ArrayList<STRWalkInBranchDataBean> branchDataBeans) {
		this.branchDataBeans = branchDataBeans;
	}
	public ArrayList<STRWalkInCustomerDataBean> getCustomerDataBeans() {
		return customerDataBeans;
	}
	public void setCustomerDataBeans(
			ArrayList<STRWalkInCustomerDataBean> customerDataBeans) {
		this.customerDataBeans = customerDataBeans;
	}
	
}
