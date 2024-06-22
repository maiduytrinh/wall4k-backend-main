package com.tp.wallios.utils;

import com.tp.wallios.common.VersionException;
import com.tp.wallios.rest.dto.VersionDto;
import com.zandero.utils.InstantTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public final class VersionUtils {

	private static final Logger LOG = LoggerFactory.getLogger(VersionUtils.class);

	static final String VERSION_FILE = "version.txt";

	static public VersionDto version() {

		InputStream inputStream = VersionUtils.class.getClassLoader().getResourceAsStream(VERSION_FILE);

		try {

			if (inputStream != null) {
				Properties properties = new Properties();
				properties.load(inputStream);

				String version = properties.getProperty("version");
				String buildDate = properties.getProperty("build.date");

				Instant time = InstantTimeUtils.getTimestamp(buildDate, InstantTimeUtils.SHORT_TIME_FORMAT);

				return new VersionDto(version, time);

			} else {
				throw new FileNotFoundException("Property file '" + VERSION_FILE + "' not found in the classpath");
			}
		}
		catch (IOException e) {
			LOG.error("Failed to load version information.", e);
			throw new IllegalArgumentException("Missing version.txt property file!");
		}
	}
	// -1: v1 is smaller than v2; 0: v1 is equal v2; 1: v1 is bigger than v2
	public static int compareVersion(String v1, String v2) throws VersionException {
		if (null == v1 || null == v2)
			throw new VersionException("v1 = " + v1 + " or v2 = " + v2 + " is null");

		String checkVersionRegex = "^\\d+([\\W|_]\\d+|[\\W|_]beta)*$";
		if (!v1.matches(checkVersionRegex) || !v2.matches(checkVersionRegex))
			throw new VersionException("Invalid version format: v1 = " + v1 + " and v2 = " + v2 + " must match " + checkVersionRegex);

		// notice: order of elements in versionSpecialElements will affect to comparison result
		List<String> versionSpecialElements = Arrays.asList(new String[]{"beta"});
		String[] v1Array = v1.split("\\W|_");
		String[] v2Array = v2.split("\\W|_");

		int i = 0;
		while (i < v1Array.length && i < v2Array.length) {
			int v1Element = -1;
			int v2Element = -1;
			int v1SpecialElement = -1;
			int v2SpecialElement = -1;

			// parse v1's i-th element
			try {
				v1Element = Integer.parseInt(v1Array[i]);
			} catch (Exception e) {
				v1SpecialElement = versionSpecialElements.indexOf(v1Array[i]);
			}
			// parse v2's i-th element
			try {
				v2Element = Integer.parseInt(v2Array[i]);
			} catch (Exception e) {
				v2SpecialElement = versionSpecialElements.indexOf(v2Array[i]);
			}

			// compare parsed results
			// if v1's i-th element and v2's i-th element are both number
			if (v1Element > -1 && v2Element > -1) {
				if (v1Element < v2Element) return -1;
				if (v1Element > v2Element) return 1;
			}
			// else if v1's i-th element or v2's i-th element is special element like "beta"
			// then element contains special element will be considered smaller
			else if (v1SpecialElement == -1 || v2SpecialElement == -1) {
				if (v1SpecialElement > -1) return -1;
				if (v2SpecialElement > -1) return 1;
			}
			// else if v1's i-th element and v2's i-th element are both special element
			else {
				if (v1SpecialElement < v2SpecialElement) return -1;
				if (v1SpecialElement > v2SpecialElement) return 1;
			}
			i++;
		}
		if (v1Array.length < v2Array.length) {
			// if v2's i-th element is special element then it's considered smaller
			if (versionSpecialElements.indexOf(v2Array[i]) > -1) {
				return 1;
			}
			return -1;
		}
		if (v1Array.length > v2Array.length) {
			// if v1's i-th element is special element then it's considered smaller
			if (versionSpecialElements.indexOf(v1Array[i]) > -1) {
				return -1;
			}
			return 1;
		}
		return 0;
	}
}
