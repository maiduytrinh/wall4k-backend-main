package com.tp.wallios.rest.writers;

import com.tp.wallios.rest.common.Response;
import com.zandero.rest.writer.HttpResponseWriter;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.jackson.DatabindCodec;

public final class MyResponseWriter implements HttpResponseWriter<Response> {

	@Override
	public void write(Response result, HttpServerRequest request, HttpServerResponse response) throws Throwable {
		if (result != null) {
			final String jsonResult = JsonUtils.toJson(result, DatabindCodec.mapper());
			response.setStatusCode(result.getStatus().getStatusCode());
			response.putHeader("Accept-Encoding", "gzip");
			response.end(jsonResult);
		}
	}
}
