package com.tp.wallios.rest.handlers;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public final class LogHandler implements Handler<RoutingContext> {

	private final static Logger LOG = LoggerFactory.getLogger(LogHandler.class);

	@Override
	public void handle(RoutingContext context) {

		boolean ignoreLog =  context.normalizedPath().contains("/resources/") || 
				context.normalizedPath().contains("/heartbeat") ||
				context.normalizedPath().contains("/lib/") ||
				context.normalizedPath().contains("/css/") ||
				context.normalizedPath().contains("/fonts/") ||
				context.normalizedPath().contains("/swagger-ui.js") ||
				context.normalizedPath().contains("/images/");

		if (!ignoreLog) {
			JsonObject json = new JsonObject();
			json.put("path", context.normalizedPath());
			json.put("token", context.request().getHeader("X-Token"));
			json.put("appId", context.request().getHeader("X-AppId"));
			json.put("appVersion", context.request().getHeader("X-AppVersion"));
			json.put("appType", context.request().getHeader("X-AppType"));
			json.put("deviceId", context.request().getHeader("X-DeviceId"));
			json.put("deviceName", context.request().getHeader("X-DeviceName"));
			String ip = context.request().getHeader("X-ORIGINATING-IP");
			json.put("ip", ip  != null ? ip : context.request().remoteAddress().host());

			addParams(context, json);
			addQueryParams(context.queryParams().entries(), json);
			log(json);
		}


		context.next();
	}

	private void addParams(RoutingContext context, JsonObject json) {
		json.put("userId", context.pathParam("userId"));
		json.put("serviceProviderId", context.pathParam("serviceProviderId"));
		json.put("orderId", context.pathParam("orderId"));
		json.put("serviceId", context.pathParam("serviceId"));
	}

	private void addQueryParams(List<Entry<String, String>> params, JsonObject json) {
		Iterator<Entry<String, String>> it = params.iterator();
		while(it.hasNext()) {
			Entry<String, String> next = it.next();
			json.put(next.getKey(), next.getValue());
		}
	}

	public void log(JsonObject json) {

		String row = String.join(
				",", 
				json.getString("ip"),
				json.getString("path"),
				json.getString("appId"),
				json.getString("appVersion"),
				json.getString("appType"),
				json.getString("deviceId"),
				json.getString("deviceName"),
				json.containsKey("userId") ? json.getString("userId") : null,
						json.containsKey("serviceProviderId") ? json.getString("serviceProviderId") : null,
								json.containsKey("orderId") ? json.getString("orderId") : null,
										json.containsKey("serviceId") ? json.getString("serviceId") : null);

		LOG.trace(row);
	}
}