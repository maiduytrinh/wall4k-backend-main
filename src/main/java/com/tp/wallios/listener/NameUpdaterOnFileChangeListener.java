/*
 * Copyright (C) 2003-2015 TP Entertainment.
 */
package com.tp.wallios.listener;


import com.tp.wallios.Server;
import com.tp.wallios.director.service.DirectoryWatchService.OnFileChangeListener;
import com.tp.wallios.service.StatusService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by The TP Entertainment
 * Author : Vu Duy Tu
 *          duytucntt@gmail.com
 * Dec 29, 2015  
 */
public class NameUpdaterOnFileChangeListener implements OnFileChangeListener {
	private static final Logger LOG = LoggerFactory.getLogger(NameUpdaterOnFileChangeListener.class);
	
	private StatusService statusService;

	public NameUpdaterOnFileChangeListener() {
	}

	public void setStatusService(StatusService statusService) {
		this.statusService = statusService;
		//
		loadServerStatus();
	}

	@Override
	public void onFileCreate(String filePath) {
		setServerStatus(filePath, "cao");
	}

	@Override
	public void onFileDelete(String filePath) {
		setServerStatus(filePath, "");
	}

	private void setServerStatus(String filePath, String status) {
		try {
			String serverName = filePath.substring(filePath.lastIndexOf("/") + 1);
			if (StringUtils.isNumeric(serverName)) {
				statusService.setServerStatus(Integer.valueOf(serverName), status);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e, false);
		}
	}

	@Override
	public void onFileModify(String filePath) {
	}

	private void loadServerStatus() {
		try {
			if (!StringUtils.isEmpty(Server.WATCHED_DIR) && new File(Server.WATCHED_DIR).isDirectory()) {
				File file[] = new File(Server.WATCHED_DIR).listFiles();
				for (File f : file) {
					if (StringUtils.isNumeric(f.getName())) {
						statusService.setServerStatus(Integer.valueOf(f.getName()), "cao");
					}
				}
			}
			//
		} catch (Exception e) {
			LOG.error("Error loadServerStatus ", e);
		}
	}

}
