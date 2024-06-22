package com.tp.wallios.injection;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.tp.wallios.storage.*;
import com.tp.wallios.storage.impl.*;

public class StorageModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(ImageStorage.class).to(ImageStorageImpl.class);
		binder.bind(VideoStorage.class).to(VideoStorageImpl.class);
		binder.bind(CategoryStorage.class).to(CategoryStorageImpl.class);
		binder.bind(SpecialDataStorage.class).to(SpecialDataStorageImpl.class);
		binder.bind(TrendingStorage.class).to(TrendingStorageImpl.class);
	}
}