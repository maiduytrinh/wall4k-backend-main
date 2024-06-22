package com.tp.wallios.index;

import com.tp.wallios.entity.Data;
import com.tp.wallios.entity.SpecialData;
import com.tp.wallios.index.service.ImageIndexService;
import com.tp.wallios.index.service.SpecialDataIndexService;
import com.tp.wallios.index.service.VideoIndexService;

public class IndexingService {
	public static void initImage() {
		final AbstractIndex<Data, Data> imageIndex = new ImageIndexService();
		imageIndex.init();
	}

	public static void initVideo() {
		final AbstractIndex<Data, Data> videoIndex = new VideoIndexService();
		videoIndex.init();
	}

	public static void initSpecialData() {
		final AbstractIndex<SpecialData, SpecialData> specialDataIndex = new SpecialDataIndexService();
		specialDataIndex.init();
	}

}
