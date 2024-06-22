package com.tp.wallios.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfiguration {
	private static final Logger log = LoggerFactory.getLogger(AppConfiguration.class);

	public final static String SERVICE_FUZZY_SEARCH_ACTIVATE = "base.service.fuzzy.search.activate";
	public final static String REPOSITORY_INDEX = "base.repository.index";
	public final static String AUTH_MAX_FAILED_LOGIN = "base.auth.max-failed-login";
	public final static String AUTH_LOGIN_RETRY_TIME = "base.auth.login-retry-time";
	public final static String BASE_ENVIRONMENT = "base.environment";
	public final static String BASE_VERSION = "base.version";
	public final static String BASE_COUNTRY_ALLOW_DATA_MIN = "base.country.allow.data.min";
	public final static String WALL_HOME_DAILY_TAB_OFFSET = "wall.daily.tab.offset";
	public final static String SUPPORT_COUNTRY = "support.country";

	private static Properties configuration = new Properties();

	private static Properties getConfiguration() {
		return configuration;
	}

	/**
	 * Load file properties
	 * @param file
	 */
	public static void initilize(final String file) {
		InputStream in = null;
		try {
			in = new FileInputStream(new File(file));
			configuration.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			print();
		}
	}

	/**
	 * Load file properties
	 * @param in
	 */
	public static void initilize(final InputStream in) {
		try {
			if (in != null)
				configuration.load(in);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			print();
		}
	}

	private static void print() {
		log.info("#################################################");
		log.info("## Base configuration");
		log.info("##  - environment: {}", get(BASE_ENVIRONMENT));
		log.info("#################################################");
	}


	public static String get(final String key) {
		return (String) getConfiguration().get(key);
	}

	public static String get(final String key, final String defaultValue) {
		return (String) getConfiguration().getProperty(key, defaultValue);
	}


	public static Integer getInteger(final String key) {
		final String value = get(key); 
		Integer result = Integer.parseInt(value);
		return result; 
	}

	public static Integer getInteger(final String key, final String defaultValue) {
		final String value = get(key, defaultValue); 
		Integer result = Integer.parseInt(value);
		return result; 
	}

	public static Boolean getBoolean(final String key) {
		final String value = get(key); 
		Boolean result = Boolean.valueOf(value);
		return result; 
	}

	public static Boolean getBoolean(final String key, final String defaultValue) {
		final String value = get(key, defaultValue); 
		Boolean result = Boolean.valueOf(value);
		return result; 
	}

	public static Float getFloat(final String key) {
		final String value = get(key);
		Float result = Float.parseFloat(value);
		return result;
	}

	public static Float getFloat(final String key, final String defaultValue) {
		final String value = get(key, defaultValue);
		Float result = Float.parseFloat(value);
		return result;
	}
}