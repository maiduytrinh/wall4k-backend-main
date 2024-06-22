package com.tp.wallios.rest.api;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tp.wallios.utils.AppUtils;

public class BaseRest {

	private final static Logger LOG = LoggerFactory.getLogger(BaseRest.class);

	public void handle(RoutingContext context) {

		boolean ignoreLog = context.normalizedPath().contains("/resources/")
				|| context.normalizedPath().contains("/heartbeat") || context.normalizedPath().contains("/lib/")
				|| context.normalizedPath().contains("/css/") || context.normalizedPath().contains("/fonts/")
				|| context.normalizedPath().contains("/swagger-ui.js") || context.normalizedPath().contains("/images/");

		JsonObject json = new JsonObject();
		json.put("path", context.normalizedPath());
		json.put("token", getToken(context));
		json.put("appId", getAppId(context));
		json.put("appVersion", getAppVersion(context));
		json.put("appType", getAppType(context));
		json.put("mobileId", getMobileId(context));
		json.put("mobileName", getMobileName(context));
		json.put("language", getLanguage(context));
		String ip = context.request().getHeader("X-ORIGINATING-IP");
		json.put("ip", ip != null ? ip : context.request().remoteAddress().host());

		String logdata = log(json);
		
		if (!ignoreLog) {
			LOG.trace(logdata);
		}
	}
	
	protected String getToken(final RoutingContext context) {
		return context.request().getHeader("X-Token");
	}
	
	protected String getAppId(final RoutingContext context) {
		return context.request().getHeader("X-AppId");
	}
	
	protected String getAppVersion(final RoutingContext context) {
		return context.request().getHeader("X-AppVersion");
	}
	
	protected String getAppType(final RoutingContext context) {
		return context.request().getHeader("X-AppType");
	}
	
	protected String getMobileId(final RoutingContext context) {
		return context.request().getHeader("X-MobileId");
	}
	
	protected String getMobileName(final RoutingContext context) {
		return context.request().getHeader("X-MobileName");
	}
	
	protected String getLanguage(final RoutingContext context) {
		return context.request().getHeader("X-Language");
	}

	public String log(JsonObject json) {

		String row = String.join(",", json.getString("ip"), json.getString("path"), json.getString("appId"),
				json.getString("appVersion"), json.getString("appType"), json.getString("deviceId"),
				json.getString("deviceName"), json.containsKey("userId") ? json.getString("userId") : null);
		
		return row;

	}

	/**
	 * Allow the min data or not
	 *
	 * @param context
	 * @return true/false
	 */
	protected boolean isMinData(final RoutingContext context) {
		return AppUtils.isAllowMinData(getAppVersion(context));
	}

}
