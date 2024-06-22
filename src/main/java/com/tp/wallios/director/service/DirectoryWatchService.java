/*
 * Copyright (C) 2003-2015 HayMetric.
 */
package com.tp.wallios.director.service;

/**
 * Created by The HayMetric
 * Author : Vu Duy Tu
 *          duytucntt@gmail.com
 * Dec 27, 2015  
 */
import java.io.IOException;

/**
 * Interface definition of a simple directory watch service. Implementations of
 * this interface allow interested parties to <em>listen</em> to file system
 * events coming from a specific directory.
 */
public interface DirectoryWatchService {

	void start(); /* Suppress Exception */

	/** 
	 * Stops the service. This method blocks until the service has completely shut down. 
	 */ 
	void stop(); 

	/**
	 * Notifies the implementation of <em>this</em> interface that
	 * <code>dirPath</code> should be monitored for file system events. If the
	 * changed file matches any of the <code>globPatterns</code>,
	 * <code>listener</code> should be notified.
	 * 
	 * @param listener     The listener.
	 * @param dirPath      The directory path.
	 * @param globPatterns Zero or more file patterns to be matched against file
	 *                     names. If none provided, matches <em>any</em> file.
	 * @throws IOException If <code>dirPath</code> is not a directory.
	 */
	void register(OnFileChangeListener listener, String dirPath, String... globPatterns) throws IOException;

	/**
	 * Interface definition for a callback to be invoked when a file under watch is
	 * changed.
	 */
	interface OnFileChangeListener {

		/**
		 * Called when the file is created.
		 * 
		 * @param filePath The file path.
		 */
		default void onFileCreate(String filePath) {
		}

		/**
		 * Called when the file is modified.
		 * 
		 * @param filePath The file path.
		 */
		default void onFileModify(String filePath) {
		}

		/**
		 * Called when the file is deleted.
		 * 
		 * @param filePath The file path.
		 */
		default void onFileDelete(String filePath) {
		}
	}
}
