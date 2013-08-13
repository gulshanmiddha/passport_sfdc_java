package com.test;

import java.util.ArrayList;
import java.util.HashMap;

public class TestCase {
	
	private String TestCaseNumber;
	private String Type;
	private String SourceOrg;
	private String CreatedBy;
	private String Parent;
	private String Message;
	private String Action;
	private String Status;
	private String recordId;
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getTestCaseNumber() {
		return TestCaseNumber;
	}
	public void setTestCaseNumber(String testCaseNumber) {
		TestCaseNumber = testCaseNumber;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getSourceOrg() {
		return SourceOrg;
	}
	public void setSourceOrg(String sourceOrg) {
		SourceOrg = sourceOrg;
	}
	public String getCreatedBy() {
		return CreatedBy;
	}
	public void setCreatedBy(String createdBy) {
		CreatedBy = createdBy;
	}
	public String getParent() {
		return Parent;
	}
	public void setParent(String parent) {
		Parent = parent;
	}
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	public String getAction() {
		return Action;
	}
	public void setAction(String action) {
		Action = action;
	}	
}
