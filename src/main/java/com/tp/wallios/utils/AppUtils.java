package com.tp.wallios.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.wallios.common.config.AppConfiguration;

import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public final class AppUtils {

	private static final Logger LOG = LoggerFactory.getLogger(AppUtils.class);

	final static DecimalFormat df = new DecimalFormat("#.#");
	final static SimpleDateFormat hoursFormat = new SimpleDateFormat("HH:mm");
	public final static SimpleDateFormat dateFomat = new SimpleDateFormat("dd-MM-yyyy");
	public final static Locale DEFAULT_LOCALE = new Locale("en", "OT");
	public final static Locale US_LOCALE = new Locale("en", "US");
	private final static ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


	private AppUtils() {

	}

	public static Timestamp getCurrentTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	public static Integer getCurrentWeek() {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(getCurrentTimestamp());
		
		return cal.get(Calendar.WEEK_OF_YEAR);
	}

	public static Integer getCurrentDay() {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(getCurrentTimestamp());
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	
	public static Integer getCurrentMonth() {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(getCurrentTimestamp());
		return cal.get(Calendar.MONTH);
	}
	
	public static Integer getCurrentYear() {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(getCurrentTimestamp());
		return cal.get(Calendar.YEAR);
	}

	public static Integer getDayOfMonth(Timestamp timestamp) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public static Integer getMonth(Timestamp timestamp) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		return cal.get(Calendar.MONTH) + 1;
	}

	public static Integer getYear(Timestamp timestamp) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		return cal.get(Calendar.YEAR);
	}

	public static String getCurrentTime() {
		hoursFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
		return hoursFormat.format(new java.util.Date());
	}

	public static Timestamp addDaysInCurrentTimestamp(Integer days) {
		if (days == null || days <= 0)
			return getCurrentTimestamp();

		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		final Calendar cal = Calendar.getInstance();
		cal.setTime(ts);
		cal.add(Calendar.DAY_OF_WEEK, days);
		ts.setTime(cal.getTime().getTime());
		return ts;
	}

	public static Timestamp addDaysInTimestamp(java.util.Date date, Integer days) {
		if (days == null || date == null)
			return null;

		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_WEEK, days);
		ts.setTime(cal.getTime().getTime());
		return ts;
	}

	public static Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}
	
	public static String getCurrentDateString() {
		return dateFomat.format(getCurrentDate());
	}

	public static Double formatDouble(Double value) {
		if (value == null)
			return 0.0;
		return new Double(df.format(value));
	}

	public static String myTrim(String s) {
		if (null == s) {
			return "";
		} else {
			return s.trim();
		}
	}

	/**
	 * Resolve the variables of type ${my.var:defaultValue} for the current context
	 * which is composed of the system properties and the portal container settings
	 * 
	 * @param input
	 *            the input value
	 * @return the resolve value
	 */
	public static String resolveVariables(String input) {
		return resolveVariables(input, null);
	}

	/**
	 * Resolve the variables of type ${my.var} for the current context which is
	 * composed of the system properties, the portal container settings and the
	 * given settings
	 * 
	 * @param input
	 *            the input value
	 * @param props
	 *            a set of parameters to add for the variable resolution
	 * @return the resolve value
	 */
	public static String resolveVariables(String input, Map<String, String> props) {
		final int NORMAL = 0;
		final int SEEN_DOLLAR = 1;
		final int IN_BRACKET = 2;
		if (input == null)
			return input;

		char[] chars = input.toCharArray();
		StringBuilder buffer = new StringBuilder();
		boolean properties = false;
		int state = NORMAL;
		int start = 0;
		for (int i = 0; i < chars.length; ++i) {
			char c = chars[i];
			if (c == '$' && state != IN_BRACKET)
				state = SEEN_DOLLAR;
			else if (c == '{' && state == SEEN_DOLLAR) {
				buffer.append(input.substring(start, i - 1));
				state = IN_BRACKET;
				start = i - 1;
			} else if (state == SEEN_DOLLAR)
				state = NORMAL;
			else if (c == '}' && state == IN_BRACKET) {
				if (start + 2 == i) {
					buffer.append("${}");
				} else {
					String value = null;
					String key = input.substring(start + 2, i);
					String defaultValue = null;
					int index = key.indexOf(':');
					if (index > -1) {
						defaultValue = key.substring(index + 1);
						key = key.substring(0, index);
					}

					if (props != null) {
						// Some parameters have been given thus we need to check inside first
						Object oValue = props.get(key);
						value = oValue == null ? null : oValue.toString();
					}
					if (value == null) {
						// No value could be found so far, thus we try to get it from the
						// system properties
						value = System.getProperty(key);
					}
					if (value == null && defaultValue != null) {
						value = defaultValue;
					}
					if (value != null) {
						properties = true;
						buffer.append(value);
					}
				}
				start = i + 1;
				state = NORMAL;
			}
		}
		if (properties == false)
			return input;
		if (start != chars.length)
			buffer.append(input.substring(start, chars.length));
		return buffer.toString();

	}

	public static String toChar(int num) {
		final int totalLetters = 26;
		final int charStart = 65;
		StringBuilder result = new StringBuilder();
		do {
			int currentChar = num % totalLetters;
			result.insert(0, (char) (currentChar + charStart));
			num /= totalLetters;
		} while (num > 0);
		return result.toString();
	}

	public static String getIpFromRequest(RoutingContext context) {
		String ip = context.request().getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = context.request().getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = context.request().getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = context.request().getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = context.request().getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = context.request().remoteAddress().hostAddress();
		}
		return ip;
	}

	public static boolean isNullOrEmpty(Object o) {
		return null == o ||
				(o instanceof String && ((String) o).trim().isEmpty()) ||
				(o instanceof Map && ((Map) o).isEmpty()) ||
				(o instanceof Collection && ((Collection) o).isEmpty());
	}
	
	/**
	 * Only get country and locale, in the case failed, return at the position = 0
	 * 
	 * @param country
	 * @param language
	 * @return
	 */
	public static Locale getLocale(String country, String language) {
	    if (country == null) return DEFAULT_LOCALE;
	    
	    final List<Locale> languagesByCountry = LocaleUtils.languagesByCountry(country);
	    
	    for(Locale locale : languagesByCountry) {
	        if (locale.getLanguage().equalsIgnoreCase(language)) {
	            return locale;
	        }
	    }
	    if (languagesByCountry.size() >0) {
			return  languagesByCountry.get(0);
		} else {
			LOG.info("Country not support: " + country);
			return DEFAULT_LOCALE;
		}
	}
	
	/**
	 * Get locale by language
	 * 
	 * @param language
	 * @return
	 */
	public static Locale getLocaleByLanguage(String language) {
		if (language == null) return DEFAULT_LOCALE;

		final List<Locale> languagesByCountry = LocaleUtils.countriesByLanguage(language);

		for(Locale locale : languagesByCountry) {
			if (locale.getLanguage().equalsIgnoreCase(language)) {
				return locale;
			}
		}

		return languagesByCountry != null && languagesByCountry.size() >0 ? languagesByCountry.get(0) : DEFAULT_LOCALE;
	}

	public static String removeAccent(String s) {
		String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		temp = pattern.matcher(temp).replaceAll("");
		return temp.replaceAll("đ", "d");
	}

	public static <T> T fromJson(String json, Class<T> classOfT) {
		try {
			return objectMapper.readValue(json, classOfT);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Check the app version is allowed to use the min data of wallpaper
	 *
	 * @param appVersion: ex: 1.0.0
	 * @return
	 */
	public static boolean isAllowMinData(String appVersion) {
		final Set<String> appVersionAllowMinData = new HashSet<>(Arrays.asList(AppConfiguration.get(AppConfiguration.BASE_COUNTRY_ALLOW_DATA_MIN,"").split(",")));
		LOG.info("Min data version: " + appVersionAllowMinData);
		return appVersionAllowMinData.contains(appVersion);
	}

}
