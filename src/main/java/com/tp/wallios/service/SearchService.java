package com.tp.wallios.service;

import com.tp.wallios.model.common.DataItem;
import com.tp.wallios.service.filter.DataFilter;

import java.util.List;

public interface SearchService {

    /**
     * Fulltext search for hashtags
     *
     * @param dataFilter
     * @param country
     * @return
     */
    List<DataItem> searchByHashtag(DataFilter dataFilter, String country);

    /**
     * Fulltext search for hashtags
     *
     * @param dataFilter
     * @return
     */
    List<DataItem> searchByKeyword(DataFilter dataFilter);
}
