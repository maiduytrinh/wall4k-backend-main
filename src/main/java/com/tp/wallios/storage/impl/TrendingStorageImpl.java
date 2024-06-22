package com.tp.wallios.storage.impl;

import com.tp.wallios.Utils;
import com.tp.wallios.entity.Trending;
import com.tp.wallios.storage.TrendingStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class TrendingStorageImpl implements TrendingStorage {

    private static final Logger LOG = LoggerFactory.getLogger(TrendingStorageImpl.class);

    @Override
    public String getTrending(String country, Integer contentType, Integer offset, Integer limit) {
        String QUERY_GET_TRENDING = "SELECT contentId FROM trending WHERE country = '%s' AND contentType =%s ORDER BY trending DESC, createdDate DESC LIMIT %s,%s";
        String sql = String.format(QUERY_GET_TRENDING, country, contentType, offset, limit);
        System.out.println("sql: " + sql);
        try {
            List<Trending> trendings = Utils.mysqlMainUtility.executeQuery(sql, Trending.class);
            if (trendings!= null &&!trendings.isEmpty()) {
                return String.join(";", trendings.stream().map(Trending::getContentId).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            LOG.error("Fail to get trending: {}", e.getMessage());
        }
        return "";
    }

    @Override
    public String getTopDown(String country, Integer contentType, Integer offset, Integer limit) {
        String QUERY_GET_TOP_DOWN = "SELECT contentId FROM trending WHERE country = '%s' AND contentType =%s ORDER BY downloadCount DESC, createdDate DESC LIMIT %s,%s";
        String sql = String.format(QUERY_GET_TOP_DOWN, country, contentType, offset, limit);
        System.out.println("sql: " + sql);
        try {
            List<Trending> trendings = Utils.mysqlMainUtility.executeQuery(sql, Trending.class);
            if (trendings!= null &&!trendings.isEmpty()) {
                return String.join(";", trendings.stream().map(Trending::getContentId).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            LOG.error("Fail to get trending: {}", e.getMessage());
        }
        return "";
    }
}
