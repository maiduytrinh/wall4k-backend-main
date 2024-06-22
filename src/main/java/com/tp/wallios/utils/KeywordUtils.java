package com.tp.wallios.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.net.URLDecoder;

public class KeywordUtils {

    private static final String[] SPECIAL_CHARS = "`,.,|,!,#,$,%,^,&,_,=,-,+,*,@,~,[,],{,},(,),\\,\",<,>,?,/,:,;".split(",");

    private static final String[] REPLACE_KEY = ("top new,top downloads,top download,tainhieunhat,tai nhieu nhat,hai huoc,ngay le,giang sinh").split(",");

    private static final String[] REPLACE_VALUE = ("topnew,topdown,topdown,topdown,topdown,haihuoc,ngayle,giangsinh").split(",");

    public static String escapeKeyword(String keyword, String country, boolean javaEscape) {
        if (StringUtils.isBlank(keyword)) {
            return "";
        }

        String result = processKeyword(keyword);

        if (javaEscape) {
            result = escapeJava(result);
        }

        if (!"ru,tw,jp".contains(country.toLowerCase())) {
            result = removeSpecificCodes(result);
        }

        return removeRemainingCodes(result);
    }

    private static String processKeyword(String in) {
        String result = urlDecoder(in).toLowerCase();
        result = AppUtils.removeAccent(result);

        for (String specialChar : SPECIAL_CHARS) {
            result = StringUtils.replace(result, specialChar, " ");
        }

        result = StringUtils.replace(result, ",", " ");
        result = removeExtraSpaces(result);

        for (int i = 0; i < REPLACE_KEY.length; ++i) {
            result = StringUtils.replace(result, REPLACE_KEY[i], REPLACE_VALUE[i]);
        }

        return result;
    }

    private static String escapeJava(String keyword) {
        keyword = StringEscapeUtils.escapeJava(keyword);
        return StringUtils.replace(keyword, "\\u", "").toLowerCase();
    }

    private static String removeSpecificCodes(String keyword) {
        return keyword.replace("0307", "").replace("0308", "");
    }

    private static String removeRemainingCodes(String keyword) {
        return keyword.trim()
                .replace("0300", "")
                .replace("0301", "")
                .replace("0302", "")
                .replace("0303", "")
                .replace("0309", "")
                .replace("031b", "")
                .replace("0323", "");
    }

    private static String removeExtraSpaces(String keyword) {
        while (keyword.contains("  ")) {
            keyword = StringUtils.replace(keyword, "  ", " ");
        }
        return keyword;
    }

    private static String urlDecoder(String keyword) {
        try {
            String result = URLDecoder.decode(keyword, "UTF-8").replace("+", " ").trim();
            while (result.contains("  ")) {
                result = result.replace("  ", " ");
            }
            return result;
        } catch (Exception e) {
            return keyword;
        }
    }
}
