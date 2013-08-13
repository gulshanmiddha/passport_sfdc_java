package com.sfdc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.DeleteResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class SFDC {
	
	private PartnerConnection connection;
	private String orgId;
	private static Logger LOGGER = Logger.getLogger(SFDC.class);
	public SFDC (String username, String password)
	{
		ConnectorConfig config = new ConnectorConfig();
        config.setUsername(username);
        config.setPassword(password);
		try{
			connection = Connector.newConnection(config);
			orgId = connection.getUserInfo().getOrganizationId();
		}catch(Exception e)
		{
			LOGGER.error(" Error Connecting to Salesforce :: " + username + ". Cause :  " + e.getMessage());
		}
		
	}
	public  ArrayList<HashMap<String, String>> executeQuery(ArrayList<String> fields,String clause)
	{
		return executeQuery(fields,clause, false);
	}
	public  ArrayList<HashMap<String, String>> executeQuery(ArrayList<String> fields,String clause, boolean queryAll) {
        
        ArrayList<HashMap<String, String>> retMap = new ArrayList<HashMap<String, String>>();                
        
        try {
            String query = "SELECT ";
            Integer counter = 1;
            
            for(String field : fields){
                if(counter < fields.size())
                    query += field + " , ";
                else
                    query += field + " ";
                counter++;
            }
            
            query += clause;
            
          boolean done = false;  
          // query for the 5 newest contacts      
          
          QueryResult queryResults;;
          if(queryAll)
        	  queryResults = connection.queryAll(query);
          else
        	  queryResults = connection.query(query);	
          LOGGER.info(" Querying  " + queryResults.getSize());
          while (!done && queryResults.getSize() > 0) {
              
            if (queryResults.getSize() > 0) {
              for (SObject s: queryResults.getRecords()) {
                  

                  HashMap<String,String> tempMap = new HashMap<String,String>();
                          
                  for(String field : fields){
                      if(field.toUpperCase().equals("ID")){
                          tempMap.put(field.toUpperCase(), s.getId());
                      }else{ 
                          tempMap.put(field.toUpperCase(), s.getField(field)!=null?s.getField(field).toString():null);
                      }
                      
                  }  
                  
                  retMap.add(tempMap);
              }
              
              if (queryResults.isDone()) {
                    done = true;
                } else {
                    queryResults = connection.queryMore(queryResults.getQueryLocator());
                }
            }
              
          }
         

        } catch (Exception e) {
          e.printStackTrace();
        }    
        
        return retMap;
  }
	public void deleteSFDCRecords(String[] ids) 
	{
		LOGGER.info(" Deleting " + ids.length);
		HashMap [] records = null;
		if(ids!=null && ids.length>0)
		{
			records = new HashMap[ids.length]; 
		}
		if(ids.length>200)
		{
			int counter = 0;
			int supercounter = 0;
		    ArrayList tempIdList = new ArrayList();
		    for (String element : ids) {
			    if(element!=null)
			    {
					tempIdList.add(element);
			        counter++;
			        supercounter++;
			        if (counter == 200)
			            {
			        		String idArray[] = (String[])tempIdList.toArray(new String[0]);
			        		//String[] idArray = (String[])tempIdList.toArray();
			        		deleteBatch(idArray);
			        		LOGGER.info("Deleted: " + supercounter + " out of " + ids.length);
			                counter = 0;
			                tempIdList.clear();
			            }
			    }
			}
	
		    if (tempIdList.size() > 0)
		    	{
		    		deleteBatch((String[])tempIdList.toArray(new String[0]));		    		
		    	}
		}
		else
		{
			deleteBatch(ids);
		}
	}
	public ArrayList<String> createRecords(ArrayList<HashMap<String, Object>> records)
	{
		ArrayList<String> result = new ArrayList<String>();
		SObject[] sobjects = new SObject[records.size()];
		int scounter = 0;
		for(HashMap<String, Object> record: records)
		{
			SObject sobject = new SObject();
			sobject.setType((String)record.get("SOBJECTTYPE"));
			ArrayList<String> nullFields = new ArrayList();
			for(String key: record.keySet())
			{
				if(key!="SOBJECTTYPE") 
				{
					Object obj = record.get(key);
					if(obj instanceof Date) 
						sobject.setField(key, (Date)obj);						
					else if(obj instanceof Boolean)
						sobject.setField(key, (Boolean)obj);
					else if(obj instanceof String)
						sobject.setField(key, (String)obj);
					else if(obj instanceof Double)
						sobject.setField(key, (Double)obj);
					if(obj==null)
						nullFields.add(key);
				}				
			}
			sobject.setFieldsToNull((String[])nullFields.toArray(new String[0]));
			sobjects[scounter++] = sobject;
		}
		
		
		LOGGER.info(" Inserting " + sobjects.length);		
		if(sobjects.length>200)
		{
			int counter = 0;
			int supercounter = 0;
		    ArrayList<SObject> tempIdList = new ArrayList<SObject>();
		    for (SObject element : sobjects) {
			    if(element!=null)
			    {
					tempIdList.add(element);
			        counter++;
			        supercounter++;
			        if (counter == 200)
			            {
			        		SObject sobjectArray[] = (SObject[])tempIdList.toArray(new SObject[0]);
			        		//String[] idArray = (String[])tempIdList.toArray();
			        		ArrayList<String> tresult = insertBatch(sobjectArray);
			        		result.addAll(tresult);
			        		LOGGER.info("Deleted: " + supercounter + " out of " + sobjects.length);
			                counter = 0;
			                tempIdList.clear();
			            }
			    }
			}
	
		    if (tempIdList.size() > 0)
		    	{
		    		ArrayList<String> tresult = insertBatch((SObject[])tempIdList.toArray(new SObject[0]));
		    		result.addAll(tresult);
		    	}
		}		
		else
		{
			result = insertBatch(sobjects);
		}
		return result;
	}
	private ArrayList<String> insertBatch(SObject[] sobjects) {	            
		ArrayList<String> result = new ArrayList<String>();
		try {	      
	      // delete the records in Salesforce.com by passing an array of Ids
	      SaveResult[] createResults = connection.create(sobjects);
	      
	      // check the results for any errors
	      for (int i=0; i< createResults.length; i++) {
	        if (createResults[i].isSuccess()) {
	          //System.out.println(i+". Successfully deleted record - Id: " + deleteResults[i].getId());
	        	result.add(createResults[i].getId());
	        } else {
	            com.sforce.soap.partner.Error[] errors = createResults[i].getErrors();
	          for (int j=0; j< errors.length; j++) {
	            System.out.println("ERROR Inserting record: " + errors[j].getMessage());
	            result.add(null);
	          }
	        }    
	      }
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
		return result;
	  }
	public void updateRecords(ArrayList<HashMap<String, Object>> records)
	{
		SObject[] sobjects = new SObject[records.size()];
		int scounter = 0;
		for(HashMap<String, Object> record: records)
		{
			SObject sobject = new SObject();
			sobject.setType((String)record.get("SOBJECTTYPE"));
			sobject.setId((String)record.get("ID"));
			ArrayList<String> nullFields = new ArrayList();
			for(String key: record.keySet())
			{
				if(key!="SOBJECTTYPE" && key!="ID") 
				{
					Object obj = record.get(key);
					if(obj instanceof Date) 
						sobject.setField(key, (Date)obj);						
					else if(obj instanceof Boolean)
						sobject.setField(key, (Boolean)obj);
					else if(obj instanceof String)
						sobject.setField(key, (String)obj);
					else if(obj instanceof Double)
						sobject.setField(key, (Double)obj);
					if(obj==null)
						nullFields.add(key);
				}				
			}
			sobject.setFieldsToNull((String[])nullFields.toArray(new String[0]));
			sobjects[scounter++] = sobject;
		}
		
		
		LOGGER.info(" Updating " + sobjects.length);		
		if(sobjects.length>200)
		{
			int counter = 0;
			int supercounter = 0;
		    ArrayList<SObject> tempIdList = new ArrayList<SObject>();
		    for (SObject element : sobjects) {
			    if(element!=null)
			    {
					tempIdList.add(element);
			        counter++;
			        supercounter++;
			        if (counter == 200)
			            {
			        		SObject sobjectArray[] = (SObject[])tempIdList.toArray(new SObject[0]);
			        		//String[] idArray = (String[])tempIdList.toArray();
			        		updateBatch(sobjectArray);
			        		LOGGER.info("Deleted: " + supercounter + " out of " + sobjects.length);
			                counter = 0;
			                tempIdList.clear();
			            }
			    }
			}
	
		    if (tempIdList.size() > 0)
		    	{
		    		updateBatch((SObject[])tempIdList.toArray(new SObject[0]));		    		
		    	}
		}		
		else
		{
			updateBatch(sobjects);
		}
	}
	private void updateBatch(SObject[] sobjects) {	            
	    try {
	       
	      // delete the records in Salesforce.com by passing an array of Ids
	      SaveResult[] updateResults = connection.update(sobjects);
	      LOGGER.info("Records Updated");
	      // check the results for any errors
	      for (int i=0; i< updateResults.length; i++) {
	        if (updateResults[i].isSuccess()) {
	          //System.out.println(i+". Successfully deleted record - Id: " + deleteResults[i].getId());
	        } else {
	            com.sforce.soap.partner.Error[] errors = updateResults[i].getErrors();
	          for (int j=0; j< errors.length; j++) {
	            System.out.println("ERROR Updating record: " + errors[j].getMessage());
	          }
	        }    
	      }
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    } 	   	    
	  }
	private void deleteBatch(String[] ids) {	            
	    try {
	       
	      // delete the records in Salesforce.com by passing an array of Ids
	      DeleteResult[] deleteResults = connection.delete(ids);
	      
	      // check the results for any errors
	      for (int i=0; i< deleteResults.length; i++) {
	        if (deleteResults[i].isSuccess()) {
	          //System.out.println(i+". Successfully deleted record - Id: " + deleteResults[i].getId());
	        } else {
	            com.sforce.soap.partner.Error[] errors = deleteResults[i].getErrors();
	          for (int j=0; j< errors.length; j++) {
	            System.out.println("ERROR deleting record: " + errors[j].getMessage());
	          }
	        }    
	      }
	      
	    } catch (Exception e) {
	      e.printStackTrace();
	    } 	   	    
	  }
	
	public void deleteAllRecords(String objectName, boolean emptyRecycleBin)
	{
		ArrayList<String> fieldList = new ArrayList<String>();
		fieldList.add("Id");
		String query = " from " + objectName + "";
		if(objectName=="PassportDev2__Mapping__c")
				query = query  + " WHERE PassportDev2__Type__c!='User' ";
		ArrayList<HashMap<String, String>>  res = executeQuery(fieldList, query);
		ArrayList<String> delIdList = new ArrayList<String>();
		for(HashMap<String, String> hm : res){
            delIdList.add(hm.get("ID"));
        }
		LOGGER.info(" Deleting " + objectName + " ==> " + delIdList.size());
		if(delIdList.size() > 0)
            deleteSFDCRecords(delIdList.toArray(new String[delIdList.size()]));		
		
		if(emptyRecycleBin)
			emptyRecycleBin(objectName);
	}
	public void emptyRecycleBin(String objectName)
	{
		ArrayList<String> fieldList = new ArrayList<String>();
		fieldList.add("Id");
		String query = " from " + objectName + " where isDeleted=true ";
		ArrayList<HashMap<String, String>>  res = executeQuery(fieldList, query, true);
		ArrayList<String> delIdList = new ArrayList<String>();
		for(HashMap<String, String> hm : res){
            delIdList.add(hm.get("ID"));
        }
		LOGGER.info(" Empty Recycle Bin for " + objectName + " ==> " + delIdList.size());
		
		HashMap [] records = null;
		if(delIdList!=null && delIdList.size()>0)
		{
			records = new HashMap[delIdList.size()]; 
		}
		if(delIdList.size()>200)
		{
			int counter = 0;
			int supercounter = 0;
		    ArrayList tempIdList = new ArrayList();
		    for (String element : delIdList) {
			    if(element!=null)
			    {
					tempIdList.add(element);
			        counter++;
			        supercounter++;
			        if (counter == 200)
			            {
			        		String idArray[] = (String[])tempIdList.toArray(new String[0]);
			        		//String[] idArray = (String[])tempIdList.toArray();			        		
			        		emptyRecycleBin(idArray);
			        		LOGGER.info("Erased: " + supercounter + " out of " + delIdList.size());
			                counter = 0;
			                tempIdList.clear();
			            }
			    }
			}
	
		    if (tempIdList.size() > 0)
		    	{
		    		emptyRecycleBin((String[])tempIdList.toArray(new String[0]));		    		
		    	}
		}	
		
	} 
	private void emptyRecycleBin(String[] ids){
	        try {
	            connection.emptyRecycleBin(ids);
	        } catch (ConnectionException ex) {
	            Logger.getLogger(SFDC.class.getName()).log(Level.FATAL, null, ex);
	        }
	    }		
	public String getOrgId()
	{
		return this.orgId;
	}
}
