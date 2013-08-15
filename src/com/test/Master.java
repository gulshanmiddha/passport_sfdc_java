package com.test;

import java.util.Scanner;

import org.apache.log4j.Logger;

import com.config.AppConfig;
import com.config.Configurator;

public class Master {

	private static Logger LOGGER = Logger.getLogger(Master.class);
	public static AppConfig config;
	public OrgManager orgManager; 
	public TestManager testManager;
	public Master()
	{
		Master.config = Configurator.getAppConfig();
	}
	public void init()
	{
		Master.config = Configurator.getAppConfig();
		orgManager = new OrgManager();
		testManager = new TestManager(orgManager);
	}
	public void Menu()
	{
		System.out.println("Enter the number and press Enter \n");	    
		System.out.println("1 : Reload SFDC Sessions \n");
		System.out.println("2 : Fetch Replica Users \n");
		System.out.println("3 : Clean all Orgs \n");
		System.out.println("4 : Update Custom Settings \n");
		System.out.println("5 : FeedItems Normal \n");
		System.out.println("0 : Exit \n");
	}
	public void testFeedItemNormal()
	{
		testManager = new TestManager(orgManager);
	}
	public static void main(String[] args) {
			
		LOGGER.info("Loading Sessions for all the Orgs ....");  
			Master manager = new Master();
		LOGGER.info("Sessions Loaded into the memory ...");	
		LOGGER.info("CLEAR ALL BY DEFUALT: " + Master.config.getClearDataByDefault());	
		LOGGER.info("WAIT SECONDS: " + config.getCheckForSecondsPerIteration());
		LOGGER.info("ITERATIONS: " + config.getNumberOfIterations());
		
			manager.Menu();
		
	      Scanner in = new Scanner(System.in);
	      int choice = -1;
	      while(choice != 0)
	      {
	          choice = in.nextInt();
		      
		      switch(choice){
		      case 1: LOGGER.info("Reloading SFDC Sessions...");
	          	try{
	          		manager.init();
	          	}catch(Exception e){System.out.println("Error Loading SFDC Sessions");}
	          	manager.Menu();
	          break;  
		      	case 2: LOGGER.info("Starting Fetch Replica users process ...");
		          	try{
		          		manager.orgManager.fetchReplicaUsers();  
		          	}catch(Exception e){System.out.println("Error Fetching Replica Users");}
		          	manager.Menu();
		          break;
		          case 3: LOGGER.info("Cleaning Orgs in process ...");
		          try{		
		        	  	manager.orgManager.cleanAllOrgs();
		          }catch(Exception e){System.out.println("Error Cleaning Orgs");}
		          manager.Menu();
		          		break;
		          case 4: LOGGER.info("Updating Custom Settings ...");
		    		try{
		    			manager.orgManager.updateCustomSettings();
		    		}catch(Exception e){System.out.println("Error Updating Custom Settings");}
		    		manager.Menu();
		    		break;
		          case 5: LOGGER.info("Executing Feed Items Normal ...");
		    		try{
		    			manager.testManager.testNormalFeedItem();
		    		}catch(Exception e)
		    		{
		    			LOGGER.error("Error Testing Normal FeedItem: " + e.getMessage());
		    		}
		    		manager.Menu();
		    		break;
		          case 0: LOGGER.info("B..Bye....");
		          		System.exit(1);
		          default: LOGGER.info("Invalid input ...");
		          manager.Menu();
		          break;  
		      }      
	      }

	}
		
	
}
