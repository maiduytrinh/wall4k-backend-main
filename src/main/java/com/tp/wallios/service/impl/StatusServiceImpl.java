/*
 * Copyright (C) 2013-2022 TP Entertainment.
 */
package com.tp.wallios.service.impl;

import com.tp.wallios.Server;
import com.tp.wallios.service.StatusService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by The TP Entertainment
 * Author : Phung Quang Nam
 *          nam.phung@tp.com.vn
 * Fri 01 07 2022
 */
public class StatusServiceImpl implements StatusService {
	
	private static final Logger LOG = LoggerFactory.getLogger(StatusServiceImpl.class);

	private static Map<Integer, String> serverStt = new ConcurrentHashMap<Integer, String>();

	@Override
	public void setServerStatus(Integer serverName, String status) {
		if (serverName == 9999) {
      System.setProperty("debug", String.valueOf("cao".equalsIgnoreCase(status)));
      LOG.info("Status debug mode: " + System.getProperty("debug"));
      return;
    }
    if (serverName == 9998) {
      if ("cao".equalsIgnoreCase(status)) {
        System.setProperty("reportServer", "false");
      } else {
        System.setProperty("reportServer", System.getProperty("reportServerOrg"));
      }
      //
      LOG.info("Status reportServer: " + System.getProperty("reportServer"));
      return;
    }
    if (serverName == 9997) {
      System.setProperty("oldversion", String.valueOf(!"cao".equalsIgnoreCase(status)));
      LOG.info("Status oldversion mode: " + System.getProperty("oldversion"));
      return;
    }
    String port = System.getProperty("port", "8282");
    int subPort = Integer.valueOf(port.substring(2));
    if (subPort == 30) {
      subPort = 80;
    }
    LOG.info("setServerStatus server: " + serverName + " status: " + (status == "" ? "OK" : status));
    if ("cao".equalsIgnoreCase(status)) {
      serverStt.put(serverName, status);
    } else {
      serverStt.remove(serverName);
    }

	}
	
	public String getServerStatus(Integer server) {
    if(server == 0 || server == 1) {
      StringBuilder builder = new StringBuilder("[");
      for (Integer key : serverStt.keySet()) {
        if (builder.length() > 2) {
          builder.append(",");
        }
        builder.append("{\"name\":\"")
               .append(key).append("\",\"status\":\"")
               .append(serverStt.get(key)).append("\"}");
      }
      return builder.append("]").toString();
    }
    if (server == 503) {
      server = 50300 + Integer.valueOf(System.getProperty("port", "8181").substring(2));
      if (serverStt.containsKey(server)) {
        return serverStt.get(server);
      }
      server = Integer.valueOf(System.getProperty("port", "8181"));
      if (serverStt.containsKey(server)) {
        return serverStt.get(server);
      }
    }
    if (serverStt.containsKey(server)) {
      return serverStt.get(server);
    }
    return "";
  }
	
	private void loadServerStatus() {
    try {
      String watchDir = System.getProperty("watched.dir", Server.WATCHED_DIR);
      File file [] = new File(watchDir).listFiles();
      for (File f : file) {
        if(StringUtils.isNumeric(f.getName())) {
          serverStt.put(Integer.valueOf(f.getName()), "cao");
          LOG.warn("Warning[ Server: " + f.getName() + " cao ]");
        }
      }
    } catch (Exception e) {
      LOG.error("loadServerStatus error", e);
    }
  }

	@Override
	public void start() {
		loadServerStatus();
		
	}

}
