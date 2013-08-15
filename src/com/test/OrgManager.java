package com.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.apache.log4j.Logger;

import com.sfdc.Org;
import com.sfdc.User;
import com.utils.Utils;
import com.utils.XLSHandler;

public class OrgManager 
{
	HashMap<String, Org> orgMap = new HashMap<String, Org>();
	public Integer noOfOrgs = 0;
	private static Logger LOGGER = Logger.getLogger(OrgManager.class);
	
	public OrgManager()
	{
		loadAllOrgs();
		
		//loadReplicas();
	}
	
	public void loadAllOrgs()
	{
		XLSHandler orgXLS = new XLSHandler();
		ArrayList<HashMap<String, String>> orgData = orgXLS.getExcelRows("C:\\temp\\Orgs.xlsx");
		for(HashMap<String, String> rowMap: orgData)
		{
			if(rowMap.size()>0)
			{
				Org org = Utils.getAppConfig(rowMap);
				
				org.init();	
				
				if(org.getOrgName()!=null)
					orgMap.put(org.getOrgName(), org);
			}
		}
		noOfOrgs = orgMap.size();		
	}
	public void cleanAllOrgs()
	{
		if(orgMap!=null && orgMap.size()>0)
		{
			//load users in Org from Salesforce.
			for(Org o: orgMap.values())
			{
				o.cleanOrg();
			}
		}
	}
	public void updateCustomSettings()
	{
		if(orgMap!=null && orgMap.size()>0)
		{
			//load users in Org from Salesforce.
			for(Org o: orgMap.values())
			{
				o.updateCustomSettings();
			}
		}
	}
	public void loadReplicas()
	{
		LOGGER.info("Loading Replicas for : " + orgMap.size() + " Orgs");
		if(orgMap!=null && orgMap.size()>0)
		{
			XLSHandler orgXLS = new XLSHandler();
			ArrayList<HashMap<String, String>> replicaData = orgXLS.getExcelRows("C:\\temp\\Replicas.xlsx");
			LOGGER.info("Loading Replicas from sheet: ");
			Set<String> userCodes = new HashSet<String>();
			//fetch all available user codes
			for(HashMap<String, String> row: replicaData)
			{
				userCodes.add(row.get("USERCODE"));
			}
			for(HashMap<String, String> row: replicaData)
			{
				Org org = orgMap.get(row.get("ORGNAME"));
				User user = new User();
				user.setUserName(row.get("USERNAME"));
				user.setUserCode(row.get("USERCODE"));
				user.setUserId(row.get(user.getUserCode()));
				org.addLiveUser(user);
								
				for(String uCode: userCodes)
				{
					org.addReplicaUser(user.getUserCode(), uCode, row.get(uCode));
				}				
			}
		}
	}
	public void fetchReplicaUsers()
	{
		LOGGER.info("Fetching Replicas for : " + orgMap.size() + " Orgs");
		if(orgMap!=null && orgMap.size()>0)
		{
			XLSHandler orgXLS = new XLSHandler();
			ArrayList<HashMap<String, String>> replicaData = orgXLS.getExcelRows("C:\\temp\\Replicas.xlsx");
			
			//load users in Org from Salesforce.
			for(Org o: orgMap.values())
			{
				o.loadUsersFromSalesforce();
			}
			
			LOGGER.info("Setting Replicas :  ");
			for(HashMap<String, String> liveUserRow: replicaData)
			{
				String rowUserName = liveUserRow.get("USERNAME");												
				String rowUserCode = liveUserRow.get("USERCODE");				
				Org rowOrg = orgMap.get(liveUserRow.get("ORGNAME"));
				User rowUser = rowOrg.users.get(liveUserRow.get("USERNAME"));
				rowUser.setUserCode(rowUserCode);
				LOGGER.info("Replicas for : " + rowUserName + " for " + rowUserCode);								
				
				for(HashMap<String, String> row2: replicaData)
				{
					String rowUserName2 = row2.get("USERNAME");												
					String rowUserCode2 = row2.get("USERCODE");
					Org rowOrg2 = orgMap.get(row2.get("ORGNAME"));
					User rowUser2 = rowOrg2.users.get(row2.get("USERNAME"));
					String username = "";
					if(rowOrg.getOrgName() == rowOrg2.getOrgName())					
						username = rowUserName2;					
					else 
					{
						username = rowUser2.getUserId().toLowerCase() + "." + rowOrg2.getOrgId().toLowerCase() + "@" + rowOrg.getOrgId().toLowerCase() + ".dup"; 
					}
					if(rowOrg.users.containsKey(username))
						{
							
							HashMap<String, User> replicaUsers = rowOrg.replicaUsers.containsKey(rowUser.getUserCode())?rowOrg.replicaUsers.get(rowUser.getUserCode()): new HashMap<String, User>(); 
							replicaUsers.put(rowUserCode2, rowOrg.users.get(username));							
							rowOrg.replicaUsers.put(rowUser.getUserCode(), replicaUsers);
						}										
				}								
			}
			LOGGER.info("Writting Replicas to Sheet: ");
			writeReplicas();
			LOGGER.info("Fetching Replicas Complete");
		}
	}
	private void writeReplicas()
	{
		LOGGER.info("Start Writting Replicas to Sheet: ");
		if(orgMap!=null && orgMap.size()>0)
		{
			XLSHandler xls = new XLSHandler();
			ArrayList<HashMap<String, String>> replicaData = xls.getExcelRows("C:\\temp\\Replicas.xlsx");
			
			LOGGER.info("Writting Replicas :  ");
			for(HashMap<String, String> userRow: replicaData)
			{
				Org org = orgMap.get(userRow.get("ORGNAME"));
				String userCode = userRow.get("USERCODE");
				HashMap<String, User> replicas = org.replicaUsers.get(userCode);
				for(String repCode: replicas.keySet())
				{
					userRow.put(repCode, replicas.get(repCode).getUserId());
				}								
			}
			xls.writeExcelFileData("C:\\temp\\Replicas.xlsx", replicaData);
		}
	}
}
