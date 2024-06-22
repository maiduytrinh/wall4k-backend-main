package com.tp.wallios.injection;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.zandero.rest.injection.InjectionProvider;

import java.lang.annotation.Annotation;

/**
 *
 */
public class GuiceInjectionProvider implements InjectionProvider {

	private Injector injector;

	public GuiceInjectionProvider(Module[] modules) {
		injector = Guice.createInjector(modules);
	}
	
	/**
	 * Gets the instance by Guice injector
	 * 
	 * @param type
	 *            type of class
	 * @param <T>
	 *            generic type
	 * @return generic type
	 */
	public <T> T getInstance(Class<T> type) {
		return injector.getInstance(type);
	}

	/**
	 * Gets the instance by Guice injector by Key
	 * 
	 * Example: injector.getInstance(Key.get(String.class,
	 * Names.named("annotation")));
	 * 
	 * @param key
	 *            {@link Key}
	 * @param <T>
	 *            generic type
	 * @return generic type
	 */
	public <T> T getComponent(Key<T> key) {
		return injector.getInstance(key);
	}

	/**
	 * Gets the instance by Guice injector by Key Example:
	 * injector.getInstance(Key.get(MyLifecycleService.class, PosConstruct.class);
	 * 
	 * @param type
	 *            type of class
	 * @param option
	 *            options
	 * @param <T>
	 *            generic type
	 * @return generic type
	 */
	public <T> T getComponent(Class<T> type, Class<? extends Annotation> option) {
		final Key<T> key = Key.get(type, option);
		return injector.getInstance(key);
	}
}
