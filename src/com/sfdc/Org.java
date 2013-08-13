package com.sfdc;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class Org {
	
	private static Logger LOGGER = Logger.getLogger(Org.class);
	private String sfdcEndpoint;
    private String sfdcUsername;
    private String sfdcPassword;
    private String orgName;
    private String securityToken;
    private String orgType;    
    private String orgId;
    public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	private SFDC sfdc;
    public HashMap<String, User>  users;
    public HashMap<String, HashMap<String, User>>  replicaUsers; // liveusername and related replica users        
    
    public Org()
	{
    	replicaUsers = new HashMap<String, HashMap<String, User>>();
    	users = new HashMap<String, User>();
	}
    
    public String getSfdcEndpoint() {
		return sfdcEndpoint;
	}
	public void setSfdcEndpoint(String sfdcEndpoint) {
		this.sfdcEndpoint = sfdcEndpoint;
	}
	public String getSfdcUsername() {
		return sfdcUsername;
	}
	public void setSfdcUsername(String sfdcUsername) {
		this.sfdcUsername = sfdcUsername;
	}
	public String getSfdcPassword() {
		return sfdcPassword;
	}
	public void setSfdcPassword(String sfdcPassword) {
		this.sfdcPassword = sfdcPassword;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getSecurityToken() {
		return securityToken;
	}
	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}
	public String getOrgType() {
		return orgType;
	}
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}
	
	public void init()
	{
		LOGGER.info("Initializing Org: " + orgName + "   " + sfdcUsername);
		if(sfdcUsername!=null && sfdcPassword!=null)
		{
			sfdc = new SFDC(sfdcUsername, sfdcPassword);
			orgId = sfdc.getOrgId();
		}
	}
	public void addLiveUser(User u)
	{
		if(users == null)
			users = new HashMap<String, User>();
		users.put(u.getUserCode(), u);
	}
	public void addReplicaUser(String liveUserCode, String replicaOrg, String replicaUserId)
	{
		if(replicaUsers == null)
			replicaUsers = new HashMap<String, HashMap<String, User>>();
		
		HashMap<String, User> replicas = replicaUsers.get(liveUserCode);
		if(replicas == null)
				replicas = new HashMap<String, User>();
		
		User u = new User();
		u.setUserId(replicaUserId);
		replicas.put(replicaOrg, u);		
		
		replicaUsers.put(liveUserCode, replicas);
	}
	public void loadUsersFromSalesforce()
	{
		users = new HashMap<String, User>();
		ArrayList<String> queryFieldList = new ArrayList<String>();
        queryFieldList.add("Id");
        queryFieldList.add("Username");
        queryFieldList.add("IsActive");
        String query = " FROM USER ";
        try{
        	LOGGER.info("Querying Users from : " + orgName);
        	ArrayList<HashMap<String, String>> sfdcusers = sfdc.executeQuery(queryFieldList, query);
        	for(HashMap<String, String> hm : sfdcusers){
                User u = new User();
                u.setUserId(hm.get("ID"));
                u.setUserName(hm.get("USERNAME"));
                u.setActive(hm.get("ISACTIVE")=="true"?true:false);
                users.put(u.getUserName(), u);
            }
        	LOGGER.info(users.size() + " Users in " + orgName);
        }catch(Exception e){
            e.printStackTrace();
        }
	}
	public void cleanOrg()
	{
		LOGGER.info(" Cleaning ===================================================> " + orgName);
		
		sfdc.deleteAllRecords("FeedItem", false);
				
		sfdc.deleteAllRecords("PassportDev2__Mapping__c", false);
				
		sfdc.deleteAllRecords("PassportDev2__Log__c", false);
				
		sfdc.deleteAllRecords("CollaborationGroup", false);		
		
		sfdc.deleteAllRecords("PassportDev2__Queue__c", false);
		
	}
	public void updateCustomSettings()
	{
		HashMap<String, String> customSettings = new HashMap<String, String>();
		customSettings.put("General.CLEANUP_LOGS_SINGLE_BULK_SIZE", "300");
		customSettings.put("General.CLEANUP_LOGS_THRESHOLD", "1000");
		customSettings.put("General.LoggingThreshold", "0");
		
		ArrayList<String> queryFieldList = new ArrayList<String>();
        queryFieldList.add("Id");
        queryFieldList.add("Name");
        queryFieldList.add("PassportDev2__Value__c");
        String query = " FROM PassportDev2__PassportScheduledConfiguration__c ";
        
        try{
        	LOGGER.info("Querying Custom Settings from : " + orgName);
        	ArrayList<HashMap<String, String>> records = sfdc.executeQuery(queryFieldList, query);
        	
        	ArrayList<HashMap<String, Object>> updatedrecords = new ArrayList<HashMap<String, Object>>();
        	
        	for(HashMap<String, String> record: records)
        	{
        		if(customSettings.containsKey(record.get("NAME")))
        		{
        			HashMap<String, Object> updatedmap = new HashMap<String, Object>();        			
        			updatedmap.put("SOBJECTTYPE", "PassportDev2__PassportScheduledConfiguration__c");
        			updatedmap.put("PassportDev2__Value__c", customSettings.get(record.get("NAME")));
        			updatedmap.put("ID", record.get("ID"));
        			updatedrecords.add(updatedmap);
        		}
        	}
        	
        	sfdc.updateRecords(updatedrecords);
        	
        	LOGGER.info(users.size() + " Users in " + orgName);
        }catch(Exception e){
            e.printStackTrace();
        }
        
	}
}
