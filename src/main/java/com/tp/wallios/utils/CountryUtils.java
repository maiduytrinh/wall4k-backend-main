package com.tp.wallios.utils;

import com.tp.wallios.common.config.AppConfiguration;

import java.util.*;

public class CountryUtils {
    
    private final static Map<String, DBLocal> localMap = new HashMap<String, DBLocal>();

    public static Map<String, String> countrySupportedTrendMap = new HashMap<>();
    public static Map<String, DBLocal> countrySupportedMap = new HashMap<String, DBLocal>();

    static {
        localMap.put("VN", new DBLocal("vi", "VN"));
        localMap.put("US", new DBLocal("en", "US"));
        localMap.put("JP", new DBLocal("ja", "JP"));
        localMap.put("KR", new DBLocal("ko", "KR"));
        localMap.put("DE", new DBLocal("de", "DE"));
        localMap.put("FR", new DBLocal("fr", "FR"));
        localMap.put("GB", new DBLocal("en", "GB"));
        localMap.put("UK", new DBLocal("en", "GB"));
        localMap.put("MX", new DBLocal("es", "MX"));
        localMap.put("BR", new DBLocal("pt", "BR"));
        localMap.put("PH", new DBLocal("en", "PH"));
        localMap.put("RU", new DBLocal("ru", "RU"));
        localMap.put("TH", new DBLocal("th", "TH"));
        localMap.put("TW", new DBLocal("zh", "TW"));
        localMap.put("IN", new DBLocal("en", "IN"));
        localMap.put("TR", new DBLocal("tr", "TR"));
        localMap.put("MY", new DBLocal("ms", "MY"));
        localMap.put("AU", new DBLocal("en", "AU"));
        localMap.put("IT", new DBLocal("it", "IT"));
        localMap.put("NL", new DBLocal("nl", "NL"));
        localMap.put("PT", new DBLocal("pt", "PT"));
        localMap.put("ES", new DBLocal("es", "ES"));
        localMap.put("PL", new DBLocal("pl", "PL"));
        localMap.put("CZ", new DBLocal("cs", "CZ"));
        localMap.put("CA", new DBLocal("en", "CA"));
        localMap.put("CO", new DBLocal("es", "CO"));
        localMap.put("UA", new DBLocal("uk", "UA"));
        localMap.put("ZA", new DBLocal("en", "ZA"));
        localMap.put("HU", new DBLocal("hu", "HU"));
        localMap.put("ID", new DBLocal("id", "ID"));
        localMap.put("AR", new DBLocal("es", "AR"));
        List<String> supportCountry = Arrays.asList(AppConfiguration.get(AppConfiguration.SUPPORT_COUNTRY, "VN").split(";"));
        for (String country : supportCountry) {
            countrySupportedMap.put(country, localMap.get(country));
        }
    }
    
    public static List<String> getDBCountries() {
        List<String> result = new ArrayList<>();
        localMap.forEach((key, value) -> result.add(value.getCountry()));
        return result;
    }
    public static String getCountrySupportedTrend(String country) {
        String newCountry = countrySupportedTrendMap.get(country);
        return newCountry != null ? newCountry : "US";
    }

    public static String getDBCountry(String country) {
        String myCountry = country.toUpperCase();
        //
        DBLocal dbLocal = countrySupportedMap.get(myCountry);
        if (dbLocal != null) {
            return dbLocal.getCountry();
        }
        return "OT";
    }

    public static String getDBLocal(String country) {
        String myCountry = country.toUpperCase();
        //
        DBLocal dbLocal = localMap.get(myCountry);
        if (dbLocal != null) {
            return dbLocal.getLocal();
        }

        return AppUtils.DEFAULT_LOCALE.toString();
    }

    private static class DBLocal {
        String language;
        String country;

        private DBLocal(String language, String country) {
            this.language = language;
            this.country = country;
        }

        private String getLanguage() {
            return language;
        }

        private String getCountry() {
            return country;
        }

        private String getLocal() {
            return this.language.toLowerCase() + "_" + this.getCountry().toUpperCase();
        }

    }

}
