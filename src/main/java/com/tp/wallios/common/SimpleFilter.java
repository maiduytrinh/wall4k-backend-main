package com.tp.wallios.common;

import java.util.Locale;

public class SimpleFilter {

	private Locale locale;
	private boolean isMin = false;

	public SimpleFilter(Locale locale) {
		this.locale = locale;
	}

	public String getLanguage() {
		return this.locale.getLanguage();
	}

	public String getCountry() {
		return this.locale.getCountry();
	}

	public String getLocale() {
		return this.locale.toString();
	}

	public boolean isMin() {
		return isMin;
	}

	public void setMin(boolean isMin) {
		this.isMin = isMin;
	}

}
