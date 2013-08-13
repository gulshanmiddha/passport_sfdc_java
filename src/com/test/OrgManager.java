package com.test;

import java.util.ArrayList;
import java.util.HashMap;

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
		
		loadReplicas();
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
			LOGGER.info("Setting Replicas : ");
			for(HashMap<String, String> liveUserRow: replicaData)
			{
				Org liveOrg = orgMap.get(liveUserRow.get("ORGNAME"));
				User liveUser = new User();
				liveUser.setUserName(liveUserRow.get("USERNAME"));
				liveUser.setUserCode(liveUserRow.get("USERCODE"));
				liveUser.setUserId(liveUserRow.get(liveOrg.getOrgName()));
				liveOrg.addLiveUser(liveUser);
				
				for(Org replicaOrg: orgMap.values())
				{
					if(liveOrg.getOrgName() != replicaOrg.getOrgName())
					{
						liveOrg.addReplicaUser(liveUser.getUserCode(), replicaOrg.getOrgName(), liveUserRow.get(replicaOrg.getOrgName()));
					}
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
				String liveUserName = liveUserRow.get("USERNAME");				
				Org liveOrg = orgMap.get(liveUserRow.get("ORGNAME"));
				User liveUser = liveOrg.users.get(liveUserName);
				liveUser.setUserCode(liveUserRow.get("USERCODE"));
				LOGGER.info("Replicas for : " + liveUserName + " in " + liveOrg.getOrgName());
				for(Org replicaOrg: orgMap.values())
				{
					if(liveOrg.getOrgName() != replicaOrg.getOrgName())
					{
						String replicaUserId = liveUser.getUserId().toLowerCase() + "." + liveOrg.getOrgId().toLowerCase() + "@" + replicaOrg.getOrgId().toLowerCase() + ".dup"; 
						if(replicaOrg.users.containsKey(replicaUserId))
						{
							HashMap<String, User> replicaUsers = replicaOrg.replicaUsers.containsKey(liveUser.getUserName())?liveOrg.replicaUsers.get(liveUser.getUserName()): new HashMap<String, User>(); 
							replicaUsers.put(liveOrg.getOrgName(), replicaOrg.users.get(replicaUserId));							
							replicaOrg.replicaUsers.put(liveUser.getUserName(), replicaUsers);
						}
					}
					else
					{
						HashMap<String, User> replicaUsers = liveOrg.replicaUsers.containsKey(liveUser.getUserName())?liveOrg.replicaUsers.get(liveUser.getUserName()): new HashMap<String, User>(); 
						replicaUsers.put(liveOrg.getOrgName(), liveUser);							
						liveOrg.replicaUsers.put(liveUser.getUserName(), replicaUsers);
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
			ArrayList<HashMap<String, String>> xlRows = new ArrayList<HashMap<String, String>>();
			
			ArrayList<HashMap<String, String>> rows = new ArrayList<HashMap<String, String>>();
			for(Org o: orgMap.values())
			{
				HashMap<String, HashMap<String, User>>  replicaUsers = o.replicaUsers;
				for(String liveUserName: replicaUsers.keySet())
				{
					HashMap<String, User> replicaUserMap = replicaUsers.get(liveUserName);
					
					HashMap<String, String> row = new HashMap<String, String>();
					row.put("OrgName", o.getOrgName());
					//row.put("UserCode", );								
					row.put("UserName", liveUserName);
					row.put("UserCode", replicaUserMap.get(o.getOrgName()).getUserCode());
					
					for(String orgName: replicaUserMap.keySet())
					{
						row.put(orgName, replicaUserMap.get(orgName).getUserId());
					}
					xlRows.add(row);
				}
			}
			if(xlRows.size()>0)
			{
				XLSHandler xls = new XLSHandler();
				xls.writeExcelFileData("C:\\temp\\Replicas.xlsx", xlRows);
			}
		}
	}
}
