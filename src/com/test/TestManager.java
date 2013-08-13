package com.test;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.sfdc.Org;
import com.utils.Utils;
import com.utils.XLSHandler;

public class TestManager {

	private static Logger LOGGER = Logger.getLogger(TestManager.class);
	
	OrgManager orgManager;
	HashMap<String, Org> orgMap; 
	public TestManager(OrgManager orgManager)
	{
		this.orgManager = orgManager;
		orgMap = orgManager.orgMap;
	}	
	
	public void loadFeedItemNormal()
	{
		XLSHandler orgXLS = new XLSHandler();
		ArrayList<HashMap<String, String>> testData = orgXLS.getExcelRows("C:\\temp\\FeedItem_Normal.xlsx");
		LOGGER.info("Test Case Loaded: " + testData.size());
		ArrayList<TestCase> cases = new ArrayList<TestCase>();
		for(HashMap<String, String> testRow: testData)
			cases.add(Utils.getTestCase(testRow));
	} 	
	
	private TestCase createFeedItem(TestCase t)
	{
		ArrayList<HashMap<String, Object>> records = new ArrayList<HashMap<String, Object>>();
		Org org = orgMap.get(t.getSourceOrg());
		
		HashMap<String, Object> feedItem = new HashMap<String, Object>();
		feedItem.put("SOBJECTTYPE", "FeedItem");		
		feedItem.put("CreatedById", org.users.get(t.getCreatedBy()).getUserId());
		
		return t;
	}
}
