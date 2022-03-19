package com.paocos.sminotaspese.model.entities.json;

public class RspBase {
	
	private long executionTime;
	private String ipAddress;
	private boolean status;
	private String message;
	 
	/**
	 * @return the executionTime
	 */
	public long getExecutionTime() {
		return executionTime;
	}
	/**
	 * @param startTime the executionTime to set
	 */
	public void setExecutionTime(long startTime) {
		this.executionTime = startTime;
	}
	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Status: " + isStatus() + " Execution Time: " + getExecutionTime();
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
