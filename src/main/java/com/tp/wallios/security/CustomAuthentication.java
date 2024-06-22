package com.tp.wallios.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.wallios.common.config.AppConfiguration;
import com.tp.wallios.utils.AppUtils;
import com.tp.wallios.utils.AuthUtils;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthentication {
	private static final Logger LOG = LoggerFactory.getLogger(CustomAuthentication.class);
	private static final String SECRET_FILE = "secret.json";

	private final static String USER_AGENT_HEADER = "User-Agent";
	private final static String AUTHORIZATION_HEADER = "X-Token";
	private final static String APP_ID_HEADER = "X-AppId";
	private final static String APP_VERSION_HEADER = "X-AppVersion";
	private final static String APP_TYPE_HEADER = "X-AppType";
	private final static String IP_WHITELIST = "base.auth.ip.allow";
	private final static String URI_WHITELIST = "base.auth.url.allow";
	private final static String AUTH_ACTIVE = "base.auth.activate";
	private final static String ANDROID = "android";

	private static String PUBLIC_KEY = "";
	private static String PRIVATE_KEY = "";
	private static String SECRET = "";
	private static int EXPIRED_IN = 0;
	private static final String ENVIRONMENT = AppConfiguration.get(AppConfiguration.BASE_VERSION, "development");

	private static final CustomAuthentication instance = null;

	private CustomAuthentication(){}

	private static void readSecret() {
		if (PUBLIC_KEY.isEmpty() || PRIVATE_KEY.isEmpty() || SECRET.isEmpty() || EXPIRED_IN == 0) {
			try {
				final InputStream is = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(SECRET_FILE);
				Map<String, Object> result = new ObjectMapper().readValue(is, HashMap.class);
				PUBLIC_KEY = String.valueOf(result.get("publicKey"));
				PRIVATE_KEY = String.valueOf(result.get("privateKey"));
				SECRET = String.valueOf(result.get("secret"));
				EXPIRED_IN = (int) result.get("expiredIn");
			} catch (Exception e) {
				LOG.error("Error while reading file");
			}
		}
	}

	public static CustomAuthentication getInstance(){
		readSecret();
		if (instance == null){
			return new CustomAuthentication();
		}
		return instance;
	}

	private boolean checkWhitelist(String whitelistEnvName, String target) {
		String whitelistEnv = AppConfiguration.get(whitelistEnvName);
		if (null == whitelistEnv || null == target) return false;

		String[] whitelist = whitelistEnv.split(",");
		String regex = "^"+String.join("|", whitelist)+"$";
		boolean result = target.matches(regex);
		LOG.debug("checkWhitelist:whitelistEnvName = {}, target: {} has {} value", whitelistEnvName, target, result);
		return result;
	}

	public void validate(RoutingContext context) {
		try {
			Boolean authenActivate = AppConfiguration.getBoolean(AUTH_ACTIVE, "true");
			if (!authenActivate) {
				context.next();
				return;
			}
			String inComingIp = AppUtils.getIpFromRequest(context);
			String normalizedIp = InetAddress.getByName(inComingIp).getHostAddress();
			LOG.info("CustomAuthentication::validate: incoming ip: {}, normalized ip: {}, path: {}", inComingIp, normalizedIp, context.request().path());
			//
			boolean isDevelop = ENVIRONMENT.equals("development");
			if (
					isDevelop 
					|| checkWhitelist(IP_WHITELIST, normalizedIp)
					|| checkWhitelist(URI_WHITELIST, context.request().path())
					) {
				context.next();
				return;
			}

			context.next();
		} catch (Exception e) {
			LOG.info("CustomAuthentication::validate: error: {}", e);
			context.response().setStatusCode(HttpResponseStatus.FORBIDDEN.code()).setStatusMessage(HttpResponseStatus.FORBIDDEN.reasonPhrase()).end();
		}
	}

	private boolean validateContent(String secret, int expiredIn, String content) {
		long now = new java.util.Date().getTime();
		String[] contentElements = content.split("\\|");
		if (contentElements.length < 2) {
			return false;
		}
		if (!contentElements[0].equals(secret)) {
			LOG.info("validateContent()::wrong not equal.");
			return false;
		}
		if (Long.parseLong(contentElements[1]) + expiredIn < now){
			LOG.info("validateContent()::wrong due to > expired.");
			return false;
		}
		return true;
	}

	private boolean isValidToken(String token) {
		token = token.substring(7); // remove Bearer prefix
		String decryptedContent = AuthUtils.decrypt(PRIVATE_KEY, token);
		if (null == decryptedContent) {
			return false;
		}
		if (!validateContent(SECRET, EXPIRED_IN, decryptedContent)){
			LOG.info("isValidToken()::invalid token = " + token);
			return false;
		}
		return true;
	}

}
