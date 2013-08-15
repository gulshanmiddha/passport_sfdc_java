package com.config;

public final class AppConfig {
	 
    private Boolean clearDataByDefault;
    private int checkForSecondsPerIteration;
    private int numberOfIterations;

    public int getCheckForSecondsPerIteration() {
		return checkForSecondsPerIteration;
	}

	public void setCheckForSecondsPerIteration(int checkForSecondsPerIteration) {
		this.checkForSecondsPerIteration = checkForSecondsPerIteration;
	}

	public int getNumberOfIterations() {
		return numberOfIterations;
	}

	public void setNumberOfIterations(int numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
	}

	public Boolean getClearDataByDefault() {
        return clearDataByDefault;
    }

    public void setClearDataByDefault(Boolean clearDataByDefault) {
        this.clearDataByDefault = clearDataByDefault;
    }

       
    
}