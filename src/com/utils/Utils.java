package com.utils;

import java.util.HashMap;

import com.sfdc.Org;
import com.test.TestCase;

public class Utils {
	
	public static Org getAppConfig(HashMap<String, String> row)
	{
		Org org = new Org();
		if(row!=null && row.size() > 0)
		{
			if(row.containsKey("USERNAME"))
				org.setSfdcUsername(row.get("USERNAME"));
			if(row.containsKey("PASSWORD"))
				org.setSfdcPassword(row.get("PASSWORD"));
			if(row.containsKey("ORGNAME"))
				org.setOrgName(row.get("ORGNAME"));
			if(row.containsKey("SECURITYTOKEN"))
				org.setSecurityToken(row.get("SECURITYTOKEN"));
			if(row.containsKey("ORGTYPE"))
				org.setOrgType(row.get("ORGTYPE"));
		}
		return org;
	}
	
	
	public static TestCase getTestCase(HashMap<String, String> row)
	{
		TestCase tc = new TestCase();
		if(row!=null && row.size() > 0)
		{
			if(row.containsKey("TESTCASE"))
				tc.setTestCaseNumber(row.get("TESTCASE"));
			if(row.containsKey("TYPE"))
				tc.setType(row.get("TYPE"));
			if(row.containsKey("SOURCEORG"))
				tc.setSourceOrg(row.get("SOURCEORG"));
			if(row.containsKey("CREATEDBY"))
				tc.setCreatedBy(row.get("CREATEDBY"));
			if(row.containsKey("PARENTID"))
				tc.setParent(row.get("PARENTID"));
			if(row.containsKey("MESSAGE"))
				tc.setMessage(row.get("MESSAGE"));
			if(row.containsKey("ACTION"))
				tc.setAction(row.get("ACTION"));
		}
		return tc;
	}
	
	
}


