package com.tp.wallios.storage.impl;

import com.tp.wallios.Utils;
import com.tp.wallios.entity.Data;
import com.tp.wallios.logger.TPLogger;
import com.tp.wallios.rdbms.PageImpl;
import com.tp.wallios.rdbms.api.Pageable;
import com.tp.wallios.storage.ImageStorage;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ImageStorageImpl implements ImageStorage {
    private static final Logger LOG = TPLogger.getLogger(ImageStorageImpl.class);
    public static Pageable<Data> getPageable(Pageable<Data> pageable) {
        final Pageable<Data> result = new PageImpl<>(pageable.getOffset(), pageable.getLimit());
        result.setList(new ArrayList<>());
        try {
            List<Data> listData = Utils.mysqlMainUtility.executeQuery(String.format("SELECT * FROM images where isApproved = 1 LIMIT %s, %s",pageable.getOffset(), pageable.getLimit()), Data.class);
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
