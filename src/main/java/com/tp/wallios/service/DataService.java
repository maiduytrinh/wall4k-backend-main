package com.tp.wallios.service;

import com.tp.wallios.common.DataResult;
import com.tp.wallios.model.common.CategoryItem;
import com.tp.wallios.model.common.DataItem;
import com.tp.wallios.service.filter.DataFilter;

import java.util.List;

public interface DataService {
    int IMAGE_RATIO = 3;
    int VIDEO_RATIO = 1;

    DataResult<CategoryItem> getDataHomePage(DataFilter filter, String country);
    DataResult<DataItem> getData(DataFilter filter);
    DataResult<DataItem> getDataLive(DataFilter filter);
    DataResult<DataItem> getDataDepthEffect(DataFilter filter);
    DataResult<DataItem> getDataByCategory(DataFilter filter);
    DataResult<DataItem> getTopTrending(DataFilter filter);
    DataResult<DataItem> getTopDown(DataFilter filter);
}
