package com.tp.wallios.service;

import com.tp.wallios.common.ResultContext;
import com.tp.wallios.entity.Data;
import com.tp.wallios.service.filter.DataFilter;

import java.util.List;

public interface VideoService {
    List<Data> getDataLive(String country, int offset, int limit, boolean isForYou, boolean firstopen, Boolean isIpad);

    List<Data> getDataLiveSpecial(String country, int limit, String contentType, Boolean isIpad);

    ResultContext<List<Data>> getDataByCategories (String categories, Integer offset, Integer limit, Integer typeVideo, Boolean isIpad);
}
