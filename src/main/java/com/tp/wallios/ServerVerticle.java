package com.tp.wallios;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.tp.wallios.director.service.DirectoryWatchService;
import com.tp.wallios.director.service.impl.SimpleDirectoryWatchService;
import com.tp.wallios.listener.NameUpdaterOnFileChangeListener;
import com.tp.wallios.service.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;
import com.tp.wallios.injection.GuiceInjectionProvider;
import com.tp.wallios.injection.ServiceModule;
import com.tp.wallios.injection.StorageModule;
import com.tp.wallios.rest.api.*;
import com.tp.wallios.rest.handlers.NotFoundErrorHandler;
import com.tp.wallios.rest.handlers.RestErrorHandler;
import com.tp.wallios.rest.handlers.RestNotFoundHandler;
import com.tp.wallios.security.CustomAuthentication;
import com.tp.wallios.utils.LogUtils;
import com.zandero.rest.RestBuilder;
import com.zandero.utils.ResourceUtils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * This is a basic server set up to get REST.vertx going ...
 */
public class ServerVerticle extends AbstractVerticle {

	private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);

	/**
	 * Root URL for REST endpoints
	 */
	public static final String API_ROOT = "wallpaper/api";

	/**
	 * Command line setting holder
	 */
	private final ServerSettings settings;

	/**
	 * @param serverSettings
	 *            startup settings
	 */
	public ServerVerticle(ServerSettings serverSettings) {
		settings = serverSettings;
	}

	@Override
	public void start() throws InterruptedException {

		Set<String> allowedHeaders = new HashSet<>();
		allowedHeaders.add("Access-Control-Request-Method");
		allowedHeaders.add("Access-Control-Allow-Credentials");
		allowedHeaders.add("Access-Control-Allow-Origin");
		allowedHeaders.add("Access-Control-Allow-Headers");
		allowedHeaders.add("Content-Type");
		allowedHeaders.add("Origin");
		allowedHeaders.add("X-Token");

		// Create router ...
		GuiceInjectionProvider guiceInjectionProvider = new GuiceInjectionProvider(getModules(vertx));
		Router router = new RestBuilder(vertx)
				// set injector
				.injectWith(guiceInjectionProvider)

				//.routeHandler(LogHandler.class)

				// enable CORS calls
				.enableCors("*", false, -1, allowedHeaders, HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,  HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS)
//				.routeHandler(CustomAuthentication.getInstance()::validate)
				.bodyHandler(BodyHandler.create().setUploadsDirectory("uploads"))

				// register RESTs
				.register(BaseRest.class, SystemRest.class, PagesRest.class, DataRest.class, CategoryRest.class, IndexRest.class, SearchRest.class)
				// handle REST / page not found requests
				.notFound(API_ROOT, RestNotFoundHandler.class) // rest not found info - all under /api/*

				// add general purpose REST error handler
				.errorHandler(NotFoundErrorHandler.class)
				.errorHandler(RestErrorHandler.class)
				.build();

		router.route("/api/*").handler(StaticHandler.create("assets"));
		router.route("/resources/*").handler(StaticHandler.create("uploads"));

		// set logging
		String logFile = settings.getLog();
		if (logFile != null) {
			LogUtils.setConfig(logFile);
		} else {
			LogUtils.setConfig(ResourceUtils.getResourceAbsolutePath("/logback.xml"));
		}

		// use port
		int port = settings.getPort();
		System.setProperty("port", String.valueOf(port));
		LOG.info("Listening on port: " + port + " - vert.x thread pool size: " + settings.getPoolSize());

		//
		HttpServerOptions httpServerOptions = new HttpServerOptions();
		httpServerOptions.setCompressionSupported(true);

		// start up server
		vertx.createHttpServer(httpServerOptions).requestHandler(router).listen(port);

		//start service to watch status folder
		SimpleDirectoryWatchService watchService = (SimpleDirectoryWatchService) guiceInjectionProvider.getInstance(DirectoryWatchService.class);
		if(watchService != null) {
			watchService.start();
			//
			try {
				File file = new File(Server.WATCHED_DIR);
				if (!file.exists()) {
					file.mkdirs();
				}

				StatusService statusService = guiceInjectionProvider.getInstance(StatusService.class);
				if (statusService != null) {
					statusService.start();
					NameUpdaterOnFileChangeListener changeListener = new NameUpdaterOnFileChangeListener();
					changeListener.setStatusService(statusService);
					watchService.register(changeListener, Server.WATCHED_DIR);
				}
			} catch (Exception e) {
				LOG.error("Start service at error", e);
			}
		}
	}

	// provide injected services ...
	private Module[] getModules(Vertx vertx) {
		return new Module[] { new StorageModule(), new ServiceModule(vertx)};
	}
}
