package com.tp.wallios.service.impl;

import com.tp.wallios.common.ResultContext;
import com.tp.wallios.entity.Data;
import com.tp.wallios.entity.reference.DataType;
import com.tp.wallios.entity.SpecialData;
import com.tp.wallios.index.AbstractIndex;
import com.tp.wallios.index.DocumentConstant;
import com.tp.wallios.index.service.SpecialDataIndexService;
import com.tp.wallios.index.service.VideoIndexService;
import com.tp.wallios.service.VideoService;
import com.tp.wallios.storage.VideoStorage;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortedNumericSortField;
import org.jline.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

public class VideoServiceImpl implements VideoService {
	private static final Logger LOG = LoggerFactory.getLogger(VideoServiceImpl.class);
	private final AbstractIndex<Data, Data> videoIndex = new VideoIndexService();
	private final AbstractIndex<SpecialData, SpecialData> specialDataIndex = new SpecialDataIndexService();
	private Map<String, String> specialTypes = new LinkedHashMap<>();
	{
		specialTypes.put("S1", "specialwall1");
		specialTypes.put("S2", "specialwall2");
		specialTypes.put("S4", "specialwall4");
		specialTypes.put("S3", "specialwall3");
	}

	@Inject
	private VideoStorage videoStorage;


	@Override
	public List<Data> getDataLive(String country, int offset, int limit, boolean isForYou, boolean firstopen, Boolean isIpad) {
		StringBuilder idIgnore = new StringBuilder();
		if (firstopen) {
			for (String specialType : specialTypes.keySet()) {
				String specialData = specialDataIndex.getSpecialDataIds(isForYou ? DataType.VIDEO.specialType : DataType.LIVE.specialType, country, specialType, 1000);
				if (!StringUtils.isEmpty(specialData))
					idIgnore.append(specialData).append(";");
			}
		}
		Map<String, String> conditionsMap = new HashMap<>();
		conditionsMap.put(DocumentConstant.COUNTRIES, country);
		if (isIpad) {
			conditionsMap.put(DocumentConstant.SUPPORT_IPAD, "1");
		}
		conditionsMap.put("!id", idIgnore.toString());

		SortField sortFieldDate =  new SortedNumericSortField(DocumentConstant.CREATED_DATE, SortField.Type.LONG, true);
		Sort sort = new Sort(sortFieldDate);

		return videoIndex.search(conditionsMap, sort, offset, limit);
	}

	@Override
	public List<Data> getDataLiveSpecial(String country, int limit, String contentType, Boolean isIpad) {
		List<Data> result = new ArrayList<>();
		try {
			for (String specialType : specialTypes.keySet()) {
				String specialDataIds = specialDataIndex.getSpecialDataIds(contentType, country, specialType, limit);
				if (StringUtils.isEmpty(specialDataIds)) continue;
				List<Data> dataByIds = videoIndex.getDataByIds(specialDataIds, country, isIpad);
				if (dataByIds == null || dataByIds.isEmpty()) continue;
				dataByIds.sort(Comparator.comparing(wall -> specialDataIds.indexOf(String.valueOf(wall.getId()))));
				dataByIds.forEach(dataVideo -> dataVideo.setHomeType(specialTypes.get(specialType)));
				limit -= dataByIds.size();
				result.addAll(dataByIds);
			}
		} catch (Exception ex) {
			Log.error("getDataLiveSpecial error: {}", ex.getMessage());
		}
		return result;
	}

	@Override
	public ResultContext<List<Data>> getDataByCategories(String category, Integer offset, Integer limit, Integer typeVideo, Boolean isIpad) {
		List<Data> result = videoIndex.getByCategory(category, offset, limit, typeVideo, isIpad);
		return ResultContext.succeededResult(result);
	}
}
