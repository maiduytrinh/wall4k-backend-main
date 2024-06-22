package com.tp.wallios.service.impl;

import com.tp.wallios.common.ResultContext;
import com.tp.wallios.entity.Data;
import com.tp.wallios.entity.reference.DataType;
import com.tp.wallios.entity.SpecialData;
import com.tp.wallios.index.AbstractIndex;
import com.tp.wallios.index.DocumentConstant;
import com.tp.wallios.index.service.ImageIndexService;
import com.tp.wallios.index.service.SpecialDataIndexService;
import com.tp.wallios.service.ImageService;
import com.tp.wallios.service.filter.DataFilter;
import com.tp.wallios.storage.ImageStorage;
import com.tp.wallios.storage.TrendingStorage;
import io.vertx.core.json.JsonObject;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortedNumericSortField;
import org.jline.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

public class ImageServiceImpl implements ImageService {
	private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);
	private final AbstractIndex<Data, Data> imageIndex = new ImageIndexService();
	private final AbstractIndex<SpecialData, SpecialData> specialDataIndex = new SpecialDataIndexService();
	private final String DOMAIN_TRAKING = "http://localhost:8888/wallpaper/api/";
	private Map<String, String> specialTypes = new LinkedHashMap<>();
	{
		specialTypes.put("S1", "specialwall1");
		specialTypes.put("S2", "specialwall2");
		specialTypes.put("S4", "specialwall4");
		specialTypes.put("S3", "specialwall3");
	}

	@Inject
	private ImageStorage imageStorage;
	@Inject
    private TrendingStorage trendingStorage;

	@Override
	public List<Data> getDataImage(String country, int offset, int limit, boolean firstOpen, Integer type, Boolean isIpad) {
		StringBuilder idIgnore = new StringBuilder();
		String contentTypes = DataType.DEPTH_EFFECT.specialType + ";" + DataType.IMAGE.specialType;
		if (firstOpen) {
			for (String specialType : specialTypes.keySet()) {
				String specialData = specialDataIndex.getSpecialDataIds(contentTypes, country, specialType, 1000);
				if (!StringUtils.isEmpty(specialData))
					idIgnore.append(specialData).append(";");
			}
		}
		Map<String, String> conditionsMap = new HashMap<>();
		conditionsMap.put(DocumentConstant.COUNTRIES, country);
		if (type != null) {
			conditionsMap.put(DocumentConstant.TYPE, type.toString());
		}
		if (isIpad != null && isIpad) {
			conditionsMap.put(DocumentConstant.SUPPORT_IPAD, "1");
		}
		conditionsMap.put("!id", idIgnore.toString());

		SortField sortFieldDate =  new SortedNumericSortField(DocumentConstant.CREATED_DATE, SortField.Type.LONG, true);
		Sort sort = new Sort(sortFieldDate);

		return imageIndex.search(conditionsMap, sort, offset, limit);
	}

	@Override
	public List<Data> getDataImageSpecial(String country, int limit, String contentType, Boolean isIpad) {
		List<Data> result = new ArrayList<>();
		try {
			for (String specialType : specialTypes.keySet()) {
				String specialDataIds = specialDataIndex.getSpecialDataIds(contentType, country, specialType, limit);
				if (StringUtils.isEmpty(specialDataIds)) continue;
				List<Data> dataByIds = imageIndex.getDataByIds(specialDataIds, country, isIpad);
				if (dataByIds == null || dataByIds.isEmpty()) continue;
				dataByIds.sort(Comparator.comparing(wall -> specialDataIds.indexOf(String.valueOf(wall.getId()))));
				dataByIds.forEach(dataVideo -> dataVideo.setHomeType(specialTypes.get(specialType)));
				limit -= dataByIds.size();
				result.addAll(dataByIds);
			}
		} catch (Exception ex) {
			Log.error("getDataImageSpecial error: {}", ex.getMessage());
		}
		return result;
	}

	@Override
	public ResultContext<List<Data>> getDataByCategories(String category, Integer offset, Integer limit, Integer typeImage, Boolean isIpad) {
		List<Data> result = imageIndex.getByCategory(category, offset, limit, typeImage, isIpad);
		return ResultContext.succeededResult(result);
	}

	@Override
	public List<Data> getDataTrending(DataFilter filter) {
		List <Data> resulrs = new ArrayList<>();
		String ids = callApiTop(DOMAIN_TRAKING + "tracking/get-trending", filter.getCountry(), filter.getPageNumber(), filter.getSizeConfig());
		if (!ids.isEmpty()) {
			resulrs = imageIndex.getDataByIds(ids, filter.getLocale(), filter.getIpad());
			// sort by ids
			resulrs.sort(Comparator.comparing(wall -> ids.indexOf(String.valueOf(wall.getId()))));
		}
        return resulrs;
	}

	@Override
	public List<Data> getDataTopDown(DataFilter filter) {
		List <Data> resulrs = new ArrayList<>();
		String ids = callApiTop(DOMAIN_TRAKING + "tracking/get-topdown", filter.getCountry(), filter.getPageNumber(), filter.getSizeConfig());
		if (!ids.isEmpty()) {
			resulrs = imageIndex.getDataByIds(ids, filter.getLocale(), filter.getIpad());
			// sort by ids
			resulrs.sort(Comparator.comparing(wall -> ids.indexOf(String.valueOf(wall.getId()))));
		}
		return resulrs;
	}

	// function call api
	public String callApiTop (String url, String country, int pageNumber, int limit) {
		String responseData = "";
		OkHttpClient client = new OkHttpClient();
		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
		urlBuilder.addQueryParameter("pagenumber", String.valueOf(pageNumber));
		urlBuilder.addQueryParameter("sizeconfig", String.valueOf(limit));

		Request request = new Request.Builder()
				.url(urlBuilder.build())
				.header("country", country)
				.get()
				.build();
		LOG.info("Request API::" + request);
		try {
			// Gửi yêu cầu và nhận phản hồi từ API
			Response response = client.newCall(request).execute();
			JsonObject responseJson = new JsonObject(response.body().string());
			responseData = responseJson.getString("data");
		} catch (IOException e) {
            LOG.error("callApiTop error: {}", e.getMessage());
        }
        return responseData;
	}
}
