package com.config;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;


/* 
########################################################################### 
# File..................: Configurator.java
# Version...............: 1.0
# Created by............: Vikram Middha
# Created Date..........: 27-Jul-2012
# Last Modified by......: 
# Last Modified Date....: 
# Description...........: This class reads the Resilient.properties file and 
*                         populates AppConfig object.
# Change Request History: 				   							 
########################################################################### 
*/
public class Configurator {

    private static Logger LOGGER = Logger.getLogger(Configurator.class);

    /**
     * Private constructor.
     */
    private Configurator() {
            throw new UnsupportedOperationException("Class is not instantiable.");
    }

    /**
     * initialize and get the Configuration
     * 
     * @return
     */
    public static AppConfig getAppConfig()  {

        LOGGER.info("Loading the configurations from properties file .........................");

        Properties props = new Properties();
        AppConfig appConfig = new AppConfig();

        try {
                File directory = new File (".");
                LOGGER.info("Canonical path ==== "+ directory.getCanonicalPath());
                
                FileInputStream fis ;
                    fis = new FileInputStream("C:/Temp/Passport.properties");
                    props.load(fis);
                    appConfig.setClearDataByDefault(props.getProperty("clearAllDataByDefault").toUpperCase().equals("YES") ? Boolean.TRUE : Boolean.FALSE);
                    appConfig.setCheckForSecondsPerIteration(Integer.parseInt(props.getProperty("checkForSecondsPerIteration")));
                    appConfig.setNumberOfIterations(Integer.parseInt(props.getProperty("numberOfIterations")));
                    LOGGER.info(" Configuration Properties loaded successfully ");                                        
                    

        } catch (Exception e) {
        	LOGGER.error("CANNOT SET PASSPORT PROPERTIES ..." + e.getMessage());
        } 
        return appConfig;
    }

}
