package com.test;

import java.util.Scanner;

import org.apache.log4j.Logger;

public class Master {

	private static Logger LOGGER = Logger.getLogger(Master.class);
	public OrgManager orgManager; 
	
	public Master()
	{
		orgManager = new OrgManager();
	}
	
	
	
	public static void main(String[] args) {
			
		LOGGER.info("Loading Sessions for all the Orgs ....");  
			Master manager = new Master();
		LOGGER.info("Sessions Loaded into the memory ...");	
		
		System.out.println("Enter the number and press Enter \n");	    
		System.out.println("1 : Fetch Replica Users \n");
		System.out.println("2 : Clean all Orgs \n");
		System.out.println("3 : Update Custom Settings \n");
		
	      Scanner in = new Scanner(System.in);
	      int choice = -1;
	      
	      try{
	          choice = in.nextInt();
	      }catch(Exception e){
	          LOGGER.info("Invalid Input ...");
	          System.exit(1);
	      }
	      
	      switch(choice){
          case 1: LOGGER.info("Starting Fetch Replica users process ...");
          		manager.orgManager.fetchReplicaUsers();  
                  break;
          case 2: LOGGER.info("Cleaning Orgs in process ...");
          		manager.orgManager.cleanAllOrgs();  
          		break;
          case 3: LOGGER.info("Updating Custom Settings ...");
    		manager.orgManager.updateCustomSettings();  
    		break;		
          default: LOGGER.info("Invalid input ...");
                  break;  
      }      

	}
		
	
}
