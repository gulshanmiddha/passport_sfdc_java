package com.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.sfdc.Org;
import com.test.OrgManager;
import com.test.TestCase;

public class Utils {
	private static Logger LOGGER = Logger.getLogger(Utils.class);	
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
			if(row.containsKey("PARENT"))
				tc.setParent(row.get("PARENT"));
			if(row.containsKey("MESSAGE"))
				tc.setMessage(row.get("MESSAGE"));
			if(row.containsKey("ACTION"))
				tc.setAction(row.get("ACTION"));
		}
		return tc;
	}
	
	public static String getTimeDifference(String startTime, String endTime)
	{
		try
		{
			if(startTime.indexOf(".")>-1)
				startTime = startTime.substring(0, startTime.indexOf("."));
			if(endTime.indexOf(".")>-1)
				endTime = endTime.substring(0, endTime.indexOf("."));
			
			Calendar startCal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			startCal.setTime(sdf.parse(startTime));// all done
			
			Calendar endCal = Calendar.getInstance();
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			endCal.setTime(sdf2.parse(endTime));// all done								
			
			double d = ((double) (endCal.getTimeInMillis() - startCal.getTimeInMillis())) / (1000);							    
		    int seconds = (int)d;
		    //--- counting
		    int minutes = seconds/60;
		    seconds=seconds-minutes*60;
		    String s = Integer.toString(seconds);
		    String m = Integer.toString(minutes);
		    return m + "." + s;
		}
		catch(Exception e)
		{
			LOGGER.error(" Error calculating Time Differnce:  " + e.getMessage());
		}
		return null;
	}
}


