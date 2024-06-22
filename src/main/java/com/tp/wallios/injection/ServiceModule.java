package com.tp.wallios.injection;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.tp.wallios.service.*;
import com.tp.wallios.service.impl.*;
import com.tp.wallios.director.service.*;
import com.tp.wallios.director.service.impl.*;

import io.vertx.core.Vertx;

/**
 *
 */
public class ServiceModule implements Module {

	private Vertx vertx;
	public ServiceModule(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	public void configure(Binder binder) {
		binder.bind(StatusService.class).to(StatusServiceImpl.class);
		binder.bind(DataService.class).to(DataServiceImpl.class);
		binder.bind(VideoService.class).to(VideoServiceImpl.class);
		binder.bind(ImageService.class).to(ImageServiceImpl.class);
		binder.bind(SearchService.class).to(SearchServiceImpl.class);
		binder.bind(CategoryService.class).to(CategoryServiceImpl.class);
		binder.bind(DirectoryWatchService.class).to(SimpleDirectoryWatchService.class);
	}
}
