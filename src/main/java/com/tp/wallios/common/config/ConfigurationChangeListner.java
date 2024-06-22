package com.tp.wallios.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class ConfigurationChangeListner implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigurationChangeListner.class);

	private String configFileName = null;
	private String fullFilePath = null;

	public ConfigurationChangeListner(final String filePath) {
		this.fullFilePath = filePath;
	}

	public void run() {
		try {
			register(this.fullFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void register(final String file) throws IOException {
		final int lastIndex = file.lastIndexOf("/");
		String dirPath = file.substring(0, lastIndex + 1);
		String fileName = file.substring(lastIndex + 1, file.length());
		this.configFileName = fileName;

		configurationChanged(file);
		startWatcher(dirPath, fileName);
	}

	private void startWatcher(String dirPath, String file) throws IOException {
		final WatchService watchService = FileSystems.getDefault().newWatchService();
		Path path = Paths.get(dirPath);
		path.register(watchService, ENTRY_MODIFY);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					watchService.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		WatchKey key = null;
		while (true) {
			try {
				key = watchService.take();
				for (WatchEvent<?> event : key.pollEvents()) {
					if (event.context().toString().equals(configFileName)) {
						LOG.info("File has been changed: " + event.context());
						configurationChanged(dirPath + file);
					}
				}
				boolean reset = key.reset();
				if (!reset) {
					break;
				}
			} catch (Exception e) {
				return;
			}
		}
	}

	public void configurationChanged(final String file) {
		LOG.info("Refreshing the tp configuration in config.properties file.");
		AppConfiguration.initilize(file);
	}
}