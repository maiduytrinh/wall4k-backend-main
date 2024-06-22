package com.tp.wallios.service;

import com.tp.wallios.common.ResultContext;
import com.tp.wallios.entity.Data;
import com.tp.wallios.service.filter.DataFilter;

import java.util.List;

public interface ImageService {
    // -1 -> get tat ca type
    List<Data> getDataImage(String country, int offset, int limit, boolean firstOpen, Integer type, Boolean isIpad);
    List<Data> getDataImageSpecial(String country, int limit, String contentType, Boolean isIpad);
    ResultContext<List<Data>> getDataByCategories (String categories, Integer offset, Integer limit, Integer typeImage, Boolean isIpad);
    List<Data> getDataTrending(DataFilter filter);
    List<Data> getDataTopDown(DataFilter filter);
}
