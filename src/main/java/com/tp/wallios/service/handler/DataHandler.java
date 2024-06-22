package com.tp.wallios.service.handler;


import com.tp.wallios.entity.Data;
import com.tp.wallios.model.ImageItem;
import com.tp.wallios.model.VideoItem;
import com.tp.wallios.model.common.DataItem;
import com.tp.wallios.utils.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


public class DataHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(DataHandler.class);

	public static List<DataItem> merge(List<DataItem> proList, List<DataItem> videoList) {

		//
		if ((proList == null || proList.size() == 0) && (videoList == null || videoList.size() == 0)) return Collections.EMPTY_LIST; 
		
		//
		if (proList == null || proList.size() == 0) return videoList;
		
		//
		if (videoList == null || videoList.size() == 0) return proList;
		//ratio 3 image : 1 video
		final List<DataItem> result = ListUtils.merge(proList, videoList, 3,1);
		return result;
	}

	public static List<DataItem> mergeData(List<Data> proList, List<Data> videoList) {

		List<DataItem> pros = proList == null ? new ArrayList<>() : proList.stream().map(ImageItem::new).collect(Collectors.toList());
		List<DataItem> videos = videoList == null ? new ArrayList<>() : videoList.stream().map(VideoItem::new).collect(Collectors.toList());

		//
		if (pros.size() == 0 && videos.size() == 0) return Collections.EMPTY_LIST;

		//
		if (pros.size() == 0) return videos;

		//
		if (videos.size() == 0) return pros;
		//ratio 3 image : 1 video
		return ListUtils.merge(pros, videos, 3,1);
	}

	/**
     * Using like subList() but it take randoms elements.
     *
     * @param list
     * @param sizeOfSubList
     */
    public static <E> void randomSubList(List<E> list, int sizeOfSubList) {
        List<E> subList = Collections.emptyList();
        if (isNotEmpty(list) && list.size() > sizeOfSubList) {
            subList = new ArrayList<E>(sizeOfSubList);
            Random generator = new Random();
            for (int i = 0; i < sizeOfSubList; i++) {
                int random = generator.nextInt(list.size());
                subList.add(list.get(random));
                list.remove(random);
            }
        }
        list.clear();
        list.addAll(subList);
    }
    
    /**
     * Using like isEmpty but it check a collection is not empty or not.
     *
     * @return false if this collection is null or empty.
     */
    public static boolean isNotEmpty(List<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Check a collection is empty or not.
     *
     * @return true if this collection is null or empty.
     */
    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

}

