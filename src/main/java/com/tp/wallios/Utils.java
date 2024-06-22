package com.tp.wallios;

import com.google.common.io.FileWriteMode;
import com.tp.wallios.logger.TPLogger;
import com.tptech.mysql.MysqlUtility;
import com.zaxxer.hikari.HikariConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class Utils {
	private static final Logger LOG = TPLogger.getLogger(Utils.class);
	private static final String[] REMOVED = "`,.,|,!,#,$,%,^,&,_,=,-,+,*,@,~,[,],{,},(,),\\,\",<,>,?,/,:,;".split(",");
	private static final FastDateFormat dateFormatReturn = FastDateFormat.getInstance("EEE, dd MMM yyyy HH:mm:ss z", TimeZone.getTimeZone("GMT"));
	public static MysqlUtility mysqlMainUtility;

	@SuppressWarnings("serial")

	public static void startMysqlUtility() {
		Long t = System.currentTimeMillis();
		//
		if (Boolean.parseBoolean(System.getProperty("searchData", "true"))) {
			LOG.info("==========Starting MysqlUtility Utils===========");
			try {
				HikariConfig inConfig = new HikariConfig();
				inConfig.setUsername(System.getProperty("db.username", "tpcom"));
				inConfig.setPassword(System.getProperty("db.password", "thangtu8384"));
				inConfig.getDataSourceProperties().setProperty("url", "jdbc:mysql://127.0.0.1:3306/");
				inConfig.setConnectionInitSql("SET NAMES utf8mb4");
				String dbSearchName = System.getProperty("dbSearch", "utf8wall4k");
				mysqlMainUtility = new MysqlUtility(inConfig, dbSearchName, true);
				mysqlMainUtility.executeUpdate("SET session sql_mode='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION'");
				LOG.info("Initialization done");
			} catch (Exception e) {
				LOG.error("Error start mysqlUtility: {}", e.getMessage());
			}
		}
	}

	public static void writeAppend(String filePath, CharSequence line) {
		writeAppend(new File(filePath), line);
	}

	public static void writeAppend(File file, CharSequence line) {
		try {
			if (!file.exists()) {
				file.createNewFile();
				file.setWritable(true);
			}
			com.google.common.io.Files.asCharSink(file, Charset.defaultCharset(), FileWriteMode.APPEND).write(line + "\n");
		} catch (Exception e) {
			LOG.error("writeAppend error: file " + file.getPath(), e, false);
		}
	}

	public static void sendOnThreadReportServerStatus(final int status) {
		sendOnThreadReportServerStatus(status, "cao");
	}

	public static void sendOnThreadReportServerStatus(final int status, final String type) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				sendReportServerStatus(status, type);
			}
		});
		t.setName("ThreadReportServer");
		t.start();
	}

	public static void sendReportServerStatus(int status, String type) {
		try {
			//
			if ("true".equals(System.getProperty("reportServer", "true"))) {
				LOG.info("sendReportServerStatus ...");
				//
				String home = System.getProperty("user.home", "/home/ubuntu");
				String port = System.getProperty("port", "8282");
				if (port.length() > 2) {
					port = port.substring(0, 2);
				}
				String endIP = getSIPFromFile(new File(home + "/status/SIP"));
				// send report
				String url = new StringBuilder("http://www.wallstorage.net/infoserver/hight-cpu.php?server=")
						.append(System.getProperty("serverCode", "8")).append(status).append(endIP).append(port).append("&type=")
						.append(type).toString();
				List<String> rs = executeCommandOut("curl -s -k '" + url + "'");
				LOG.info("sendReportServerStatus " + url + " result " + rs);
			}

		} catch (Exception e) {
			LOG.error("sendReportServerStatus error: {}, {}", e, false);
		}
	}

	public static String readFirstLine(File file) {
		try {
			return com.google.common.io.Files.asCharSource(file, Charset.defaultCharset()).readFirstLine();
		} catch (Exception e) {
			LOG.error("readFirstLine error: {}, {}", e, false);
			return "";
		}
	}

	public static String getSIPFromFile(File file) {
		try {
			if (file.getName().equals("SIP")) {
				String endIP = readFirstLine(file);
				if (!StringUtils.isEmpty(endIP)) {
					return endIP;
				}
			}
		} catch (Exception e) {
			LOG.error("getSIPFromFile error: {}, {}", e, false);
		}
		return "SIP";
	}

	public static List<String> executeCommandOut(CharSequence command) {
		try {
			LOG.info("exec: " + command);
			String[] cmds = {"/bin/sh", "-c", command.toString()};
			//
			Process proc = Runtime.getRuntime().exec(cmds);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			// read the output from the command
			List<String> result = new ArrayList<>();
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				if (!s.trim().isEmpty()) {
					result.add(s.trim());
				}
			}
			// read any errors from the attempted command
			boolean first = true;
			while ((s = stdError.readLine()) != null) {
				if (s.isEmpty()) {
					continue;
				}
				if (first) {
					first = false;
					LOG.info("Here is the standard error of the command (if any):\n");
				}
				LOG.info(s);
			}
			return result;
		} catch (Exception e) {
			LOG.error("Error executeCommandOut", e);
		}
		return new ArrayList<>();
	}

	public static String getCountry(String fullLang) {
		if (fullLang != null && !fullLang.isEmpty()) {
			fullLang = fullLang.replace("-", "_");
			if (fullLang.contains("_")) {
				String info[] = fullLang.split("_");
				if (info.length > 1 && info[1].length() == 2) {
					return (info[1]).toUpperCase();
				}
				return (info[0]).toUpperCase();
			}
			return fullLang.toUpperCase();
		}
		return "OT";
	}

	public static String getQueryInput(String in) {
		return getQueryInput(in, true);
	}

	public static String getQueryInput(String in, boolean javaEscape) {
		return getQueryInput(in, "us", javaEscape);
	}

	public static String getQueryInput(String in, String country, boolean javaEscape) {
		if (in == null || in.trim().isEmpty()) {
			return "";
		}
		String out = urlDecoder(in);
		if (!out.isEmpty()) {
			out = unAccent(out).toLowerCase();
			for (String s : REMOVED) {
				out = StringUtils.replace(out, s, " ");
			}
			out = StringUtils.replace(out, ",", " ");
			while (out.contains("  ")) {
				out = StringUtils.replace(out, "  ", " ");
			}
		}
		if (javaEscape) {
			out = StringEscapeUtils.escapeJava(out);
			out = StringUtils.replace(out, "\\u", "").toLowerCase();
		}
		if (!"ru,tw,jp".contains(country.toLowerCase())) {
			out = out.replace("0307", "").replace("0308", "");
		}
		return out.trim().replace("0300", "").replace("0301", "").replace("0302", "").replace("0303", "")
				.replace("0309", "").replace("031b", "").replace("0323", "");
	}

	public static String unAccent(String s) {
		String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
	}

	public static byte[] compress(final byte[] input) {
		try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
		     GZIPOutputStream gzipper = new GZIPOutputStream(bout)) {
			gzipper.write(input, 0, input.length);
			gzipper.close();
			return bout.toByteArray();
		} catch (Exception e) {
			return new ByteArrayOutputStream().toByteArray();
		}
	}

	public static byte[] uncompress(final byte[] input) throws Exception {
		try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
		     GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(input))) {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = gis.read(buffer)) > 0) {
				bout.write(buffer, 0, len);
			}
			gis.close();
			return bout.toByteArray();
		}
	}

	public static String urlDecoder(String in) {
		try {
			String out = URLDecoder.decode(in, "UTF-8").replace("+", " ").trim();
			while (out.contains("  ")) {
				out = out.replace("  ", " ");
			}
			return out;
		} catch (Exception e) {
			return in;
		}
	}

	public static String getServerTime() {
		return dateFormatReturn.format(System.currentTimeMillis());
	}

	public static Long parseLong(String value) {
		try {
			return Long.parseLong(value);
		} catch (Exception e) {
			return 0L;
		}
	}

	public static Integer parseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return 0;
		}
	}

	public static String parseString(Object value, String defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return value.toString().trim();
	}
}