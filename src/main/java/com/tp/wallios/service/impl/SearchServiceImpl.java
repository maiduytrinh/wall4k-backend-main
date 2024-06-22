package com.tp.wallios.service.impl;

import com.tp.wallios.entity.Data;
import com.tp.wallios.index.AbstractIndex;
import com.tp.wallios.index.service.ImageIndexService;
import com.tp.wallios.index.service.VideoIndexService;
import com.tp.wallios.model.ImageItem;
import com.tp.wallios.model.VideoItem;
import com.tp.wallios.model.common.DataItem;
import com.tp.wallios.service.SearchService;
import com.tp.wallios.service.filter.DataFilter;
import com.tp.wallios.service.handler.DataHandler;
import com.tp.wallios.utils.AppUtils;
import com.tp.wallios.utils.CountryUtils;
import com.tp.wallios.utils.KeywordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchServiceImpl implements SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(SearchServiceImpl.class);
    private final AbstractIndex<Data, Data> imageIndex = new ImageIndexService();
    private final AbstractIndex<Data, Data> videoIndex = new VideoIndexService();


    @Override
    public List<DataItem> searchByHashtag(DataFilter dataFilter, String country) {
        int limit = dataFilter.getSizeConfig();
        int imageLimit = (int) (limit * 0.75);
        int videoLimit = (int) (limit * 0.25);

        List<Data> resultPro = imageIndex.searchData(dataFilter.getHashtags(), CountryUtils.getDBLocal(country), (dataFilter.getPageNumber() - 1) * imageLimit, imageLimit);
        List<DataItem> pros = resultPro.stream().map(ImageItem::new).collect(Collectors.toList());

        LOG.info("searchHashtags()::resultPro by hashtags " + dataFilter.getHashtags() + " size = " + resultPro.size());

//        List<Data> resultVideo = videoIndex.searchData(dataFilter.getHashtags(), CountryUtils.getDBLocal(country), (dataFilter.getPageNumber() - 1) * videoLimit, videoLimit);
        List<Data> resultVideo = new ArrayList<>();
        List<DataItem> videos = resultVideo.stream().map(VideoItem::new).collect(Collectors.toList());

        LOG.info("searchHashtags()::resultVideo by hashtags " + dataFilter.getHashtags() + " size = " + resultVideo.size());

        //
        List<DataItem> mergedList = DataHandler.merge(pros, videos);

        if (mergedList.size() <= 1 && !AppUtils.US_LOCALE.getCountry().equals(country)) {
            return searchByHashtag(dataFilter, AppUtils.US_LOCALE.getCountry());
        }

        return mergedList;
    }

    @Override
    public List<DataItem> searchByKeyword(DataFilter dataFilter) {
        int limit = dataFilter.getSizeConfig();
        int imageLimit = (int) (limit * 0.75);
        int videoLimit = (int) (limit * 0.25);

        String keywords = KeywordUtils.escapeKeyword(dataFilter.getHashtags(), dataFilter.getCountry(), true);

        List<Data> resultPro = imageIndex.searchKeyword(keywords, dataFilter.getLocale(), (dataFilter.getPageNumber() - 1) * imageLimit, imageLimit);
        List<DataItem> pros = resultPro.stream().map(ImageItem::new).collect(Collectors.toList());
        LOG.info("searchHashtags()::resultPro by hashtags " + dataFilter.getHashtags() + " size = " + resultPro.size());


//        List<Data> resultVideo = videoIndex.searchKeyword(keywords, dataFilter.getLocale(), (dataFilter.getPageNumber() - 1) * videoLimit, videoLimit);
        List<Data> resultVideo = new ArrayList<>();
        List<DataItem> videos = resultVideo.stream().map(VideoItem::new).collect(Collectors.toList());
        LOG.info("searchHashtags()::resultVideo by hashtags " + dataFilter.getHashtags() + " size = " + resultVideo.size());

        //
        return DataHandler.merge(pros, videos);

    }

}
