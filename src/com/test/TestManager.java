package com.test;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.sfdc.Org;
import com.sfdc.User;
import com.utils.Utils;
import com.utils.XLSHandler;

public class TestManager {

	private static Logger LOGGER = Logger.getLogger(TestManager.class);
	
	
	
	OrgManager orgManager;
	HashMap<String, Org> orgMap; 
	public TestManager(OrgManager orgManager)
	{
		this.orgManager = orgManager;
		orgManager.loadReplicas();
		orgMap = orgManager.orgMap;
	}	
	//Test Feed Items Normal TEst Cases
	public void testNormalFeedItem()
	{
		HashMap<String, TestCase> cases = loadFeedItemNormal();
		for(TestCase tc: cases.values())
		{
			createFeedItem(tc);
		}
				
		//check in all the Orgs
		for(TestCase tc: cases.values())
		{
			for(Org org: orgMap.values())
			{
				checkFeedItemInOrg(org, tc);				
			}
		}
		
		writeFeedItemNormalResults(cases);
	}
	private void writeFeedItemNormalResults(HashMap<String, TestCase> cases)
	{
		XLSHandler xls = new XLSHandler();	
		ArrayList<HashMap<String, String>> rows = new ArrayList<HashMap<String, String>>();
		LOGGER.info("Writting Test Case Results :  ");
		for(TestCase tc: cases.values())
		{
			HashMap<String, String> row = new HashMap<String, String>();
			row.put("Action", tc.getAction());
			row.put("TestCase", tc.getTestCaseNumber());
			row.put("Type", tc.getType());
			row.put("SourceOrg", tc.getSourceOrg());
			row.put("CreatedBy", tc.getCreatedBy());
			row.put("Parent", tc.getParent());
			row.put("Message", tc.getMessage());
			for(String userCode: tc.result.keySet())
			{
				row.put(userCode, tc.result.get(userCode));
			}
			rows.add(row);
		}
		xls.writeExcelFileData("C:\\temp\\FeedItem_Normal.xlsx", rows);
	}
	private void checkFeedItemInOrg(Org org, TestCase testCase)
	{
		for(String userCode: org.replicaUsers.keySet())
		{
			boolean isPass = false;
			int maxIterations = Master.config.getNumberOfIterations();
			int numberOfIterations = 0;
			long waitSeconds = Master.config.getCheckForSecondsPerIteration() * 1000;
			HashMap<String, User> replicas = org.replicaUsers.get(userCode);
			if(org.getOrgName()!= testCase.getSourceOrg() && replicas.containsKey(testCase.getCreatedBy()) && replicas.containsKey(testCase.getParent()))
			{				
				String createdById = replicas.get(testCase.getCreatedBy()).getUserId();
				String parentId = replicas.get(testCase.getParent()).getUserId();
				
				ArrayList<String> queryFieldList = new ArrayList<String>();
		        queryFieldList.add("Id");
		        queryFieldList.add("Body");
		        queryFieldList.add("SystemModstamp");
		        String query = " FROM FEEDITEM WHERE CreatedById = '" + createdById + "' AND ParentId = '" + parentId + "'";
		        while(numberOfIterations < maxIterations && !isPass)
		        {
			       try{
			        	LOGGER.info("Querying FeedItem "+ org.getOrgName() + " Iteration: " + numberOfIterations);
			    	   ArrayList<HashMap<String, String>> feeditems = org.sfdc.executeQuery(queryFieldList, query);
				        for(HashMap<String, String> fi: feeditems)
				        {
				        	if(fi.get("BODY")!=null && testCase.getMessage()!=null && fi.get("BODY").equals(testCase.getMessage()))
				        	{
				        		isPass = true;
				        		String timeTaken = Utils.getTimeDifference(testCase.getCreatedDate(), fi.get("SYSTEMMODSTAMP"));
				        		testCase.result.put(userCode, timeTaken);
				        		testCase.resultIds.put(org.getOrgName(), fi.get("ID"));
				        	}
				        }
				        if(!isPass)
				        	{
				        		LOGGER.info("Sleeping for " + waitSeconds/1000 + " seconds");
				        		Thread.sleep(waitSeconds);
				        	}
			       }catch(Exception e)
			       {
			    	   LOGGER.error("Error fetching Feeditem from Org (" + org.getOrgName() + "): " + e.getMessage());
			       }
			        numberOfIterations++;
		        }
							
			}
		}
	}
	//Load Test Cases from FeedItem_Normal
	private HashMap<String,TestCase> loadFeedItemNormal()
	{
		XLSHandler orgXLS = new XLSHandler();
		ArrayList<HashMap<String, String>> testData = orgXLS.getExcelRows("C:\\temp\\FeedItem_Normal.xlsx");
		LOGGER.info("Test Case Loaded: " + testData.size());
		HashMap<String, TestCase> cases = new HashMap<String, TestCase>();
		for(HashMap<String, String> testRow: testData)
			{
				TestCase t = Utils.getTestCase(testRow); 
				cases.put(t.getTestCaseNumber(), t);
			}
		return cases;
	} 	
	//Create FeedItem from a test case
	private void createFeedItem(TestCase t)
	{
		LOGGER.info("Test Case : " + t.getTestCaseNumber());
		ArrayList<HashMap<String, Object>> records = new ArrayList<HashMap<String, Object>>();
		Org org = orgMap.get(t.getSourceOrg());
		User liveUser = org.users.get(t.getCreatedBy());
		User replicaUser = org.replicaUsers.get(liveUser.getUserCode()).get(t.getParent()); 
		HashMap<String, Object> feedItem = new HashMap<String, Object>();					
		if(t.getMessage()!=null)
			feedItem.put("Body", t.getMessage());
		feedItem.put("SOBJECTTYPE", "FeedItem");
		feedItem.put("ParentId", replicaUser.getUserId());
		feedItem.put("CreatedById", liveUser.getUserId());
		records.add(feedItem);
		
		LOGGER.info("Posting a FeedItem in " + org.getOrgName());
		ArrayList<String> ids = org.sfdc.createRecords(records);
		if(ids.size()>0)
			t.setRecordId(ids.get(0));
		else
			t.setStatus("FAIL");	
		LOGGER.info("FeedItem Posted:  " + ids.get(0));
		
		ArrayList<String> queryFieldList = new ArrayList<String>();
        queryFieldList.add("Id");
        queryFieldList.add("SystemModstamp");
        String query = " FROM FEEDITEM where Id='" + ids.get(0) + "'";
        ArrayList<HashMap<String, String>> feeditems = org.sfdc.executeQuery(queryFieldList, query);        	        		
		if(feeditems.size()>0)
		{
			t.setCreatedDate(feeditems.get(0).get("SYSTEMMODSTAMP"));
		}        		
	}
	//Create FeedItem from a test case
		private void createFeedComment(TestCase t)
		{
			LOGGER.info("Test Case : " + t.getTestCaseNumber());
			ArrayList<HashMap<String, Object>> records = new ArrayList<HashMap<String, Object>>();
			Org org = orgMap.get(t.getSourceOrg());
			User liveUser = org.users.get(t.getCreatedBy());
			User replicaUser = org.replicaUsers.get(liveUser.getUserCode()).get(t.getParent()); 
			HashMap<String, Object> feedItem = new HashMap<String, Object>();					
			if(t.getMessage()!=null)
				feedItem.put("Body", t.getMessage());
			feedItem.put("SOBJECTTYPE", "FeedItem");
			feedItem.put("ParentId", replicaUser.getUserId());
			feedItem.put("CreatedById", liveUser.getUserId());
			records.add(feedItem);
			
			LOGGER.info("Posting a FeedItem in " + org.getOrgName());
			ArrayList<String> ids = org.sfdc.createRecords(records);
			if(ids.size()>0)
				t.setRecordId(ids.get(0));
			else
				t.setStatus("FAIL");	
			LOGGER.info("FeedItem Posted:  " + ids.get(0));
			
			ArrayList<String> queryFieldList = new ArrayList<String>();
	        queryFieldList.add("Id");
	        queryFieldList.add("SystemModstamp");
	        String query = " FROM FEEDITEM where Id='" + ids.get(0) + "'";
	        ArrayList<HashMap<String, String>> feeditems = org.sfdc.executeQuery(queryFieldList, query);        	        		
			if(feeditems.size()>0)
			{
				t.setCreatedDate(feeditems.get(0).get("SYSTEMMODSTAMP"));
			}        		
		}
}
