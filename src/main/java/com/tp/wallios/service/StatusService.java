/*
 * Copyright (C) 2013-2022 TP Entertainment.
 */
package com.tp.wallios.service;

/**
 * Created by The TP Entertainment
 * Author : Phung Quang Nam
 *          nam.phung@tp.com.vn
 * Fri 01 07 2022
 */
public interface StatusService {

	/**
	 * Set/change status of current server
	 * @param serverName
	 * @param status
	 */
	public void setServerStatus(Integer serverName, String status);
	
	/**
	 * Get status of current server
	 * @param server
	 * @return
	 */
	public String getServerStatus(Integer server);
	
	/**
	 * Call this when start service
	 */
	public void start();
}
