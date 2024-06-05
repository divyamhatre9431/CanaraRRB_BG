package com.idbi.intech.aml.databeans;

import java.util.ArrayList;

public class STRConfigFileBean {

	private String createdDate;

	private String report_id;
	private String report_type;
	private String report_name;
	private String report_status;
	private String comments;

	private ArrayList<Account> accounts = new ArrayList<Account>();

	public String getReport_id() {
		return report_id;
	}

	public void setReport_id(String report_id) {
		this.report_id = report_id;
	}

	public String getReport_type() {
		return report_type;
	}

	public void setReport_type(String report_type) {
		this.report_type = report_type;
	}

	public String getReport_name() {
		return report_name;
	}

	public void setReport_name(String report_name) {
		this.report_name = report_name;
	}

	public String getReport_status() {
		return report_status;
	}

	public void setReport_status(String report_status) {
		this.report_status = report_status;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setAccounts(ArrayList<Account> accounts) {
		this.accounts = accounts;
	}

	public ArrayList<Account> getAccounts() {
		return accounts;
	}

}
