package com.config;

public final class AppConfig {
	
    // SFDC
    private String sfdcEndpoint;
    private String sfdcUsername;
    private String sfdcPassword;
    private String orgUsersFile;
    private Boolean clearDataByDefault;

    public Boolean getClearDataByDefault() {
        return clearDataByDefault;
    }

    public void setClearDataByDefault(Boolean clearDataByDefault) {
        this.clearDataByDefault = clearDataByDefault;
    }

    public String getOrgUsersFile() {
        return orgUsersFile;
    }

    public void setOrgUsersFile(String orgUsersFile) {
        this.orgUsersFile = orgUsersFile;
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
   
    
}