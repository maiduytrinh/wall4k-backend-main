package com.tp.wallios.service.filter;


import com.tp.wallios.utils.CountryUtils;

import java.util.Locale;

public class SimpleFilter {
    
    private Locale locale;

    public SimpleFilter() {
    }
    public SimpleFilter(Locale locale) {
        this.locale = locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getLanguage() {
        return this.locale.getLanguage();
    }
    
    public String getCountry() {
        return this.locale.getCountry();
    }
    
    public String getLocale() {
        return CountryUtils.getDBLocal(this.locale.getCountry());
    }
}
