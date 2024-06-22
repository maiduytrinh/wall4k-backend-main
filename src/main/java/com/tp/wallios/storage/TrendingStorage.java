package com.tp.wallios.storage;

public interface TrendingStorage {
    String getTrending(String country, Integer contentType, Integer offset, Integer limit);
    String getTopDown(String country, Integer contentType, Integer offset, Integer limit);
}
