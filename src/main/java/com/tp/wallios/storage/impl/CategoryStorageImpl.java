package com.tp.wallios.storage.impl;

import com.tp.wallios.Utils;
import com.tp.wallios.entity.Category;
import com.tp.wallios.logger.TPLogger;
import com.tp.wallios.service.filter.DataFilter;
import com.tp.wallios.storage.CategoryStorage;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CategoryStorageImpl implements CategoryStorage {
    private static final Logger LOG = TPLogger.getLogger(CategoryStorageImpl.class);
    private final String QUERY_GET_CATEGORY_BY_COUNTRY = "SELECT * FROM categories WHERE countries like '%s' and count > 0 ORDER BY `order`";

    @Override
    public List<Category> getCategoriesByCountry(DataFilter filter) {
        List<Category> result = new ArrayList<>();
        try {
            result = Utils.mysqlMainUtility.executeQuery(String.format(QUERY_GET_CATEGORY_BY_COUNTRY, filter.getLocale()), Category.class);
        } catch (Exception ex) {
            LOG.error("Fail to get categories by country: {}", ex.getMessage());
        }
        return result;
    }
}
