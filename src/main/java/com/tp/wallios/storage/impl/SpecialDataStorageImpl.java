package com.tp.wallios.storage.impl;

import com.tp.wallios.Utils;
import com.tp.wallios.entity.SpecialData;
import com.tp.wallios.logger.TPLogger;
import com.tp.wallios.rdbms.PageImpl;
import com.tp.wallios.rdbms.api.Pageable;
import com.tp.wallios.storage.SpecialDataStorage;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class SpecialDataStorageImpl implements SpecialDataStorage {
    private static final Logger LOG = TPLogger.getLogger(SpecialDataStorageImpl.class);
    public static Pageable<SpecialData> getPageable(Pageable<SpecialData> pageable) {
        final Pageable<SpecialData> result = new PageImpl<>(pageable.getOffset(), pageable.getLimit());
        result.setList(new ArrayList<>());
        try {
            List<SpecialData> listData = Utils.mysqlMainUtility.executeQuery(String.format("SELECT * FROM specialData LIMIT %s, %s",pageable.getOffset(), pageable.getLimit()), SpecialData.class);
            if (listData == null || listData.isEmpty()) {
                return result;
            }
            result.setList(listData);
            return result;
        }catch (Exception e){
            LOG.error("Fail to pageable collections: {}", e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
