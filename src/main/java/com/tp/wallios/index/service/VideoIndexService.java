package com.tp.wallios.index.service;

import com.google.common.base.Stopwatch;
import com.tp.wallios.common.config.AppConfiguration;
import com.tp.wallios.entity.Data;
import com.tp.wallios.index.AbstractIndex;
import com.tp.wallios.index.DocumentConstant;
import com.tp.wallios.index.LuceneIndex;
import com.tp.wallios.rdbms.PageImpl;
import com.tp.wallios.rdbms.api.Pageable;
import com.tp.wallios.storage.impl.ImageStorageImpl;
import com.tp.wallios.storage.impl.VideoStorageImpl;
import io.vertx.core.json.JsonObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VideoIndexService extends ImageIndexService {
	protected static final Logger LOG = LoggerFactory.getLogger(VideoIndexService.class);

	protected final static String INDEX_NAME = "/video";

	public VideoIndexService() {
		super(INDEX_NAME);
		try {
			directory = FSDirectory.open(Paths.get(parentDirectory + INDEX_NAME));
			indexer = new LuceneIndex(directory, new StandardAnalyzer(stopSet));
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void init() {
		try {
			boolean isFuzzyActivate = AppConfiguration.getBoolean(AppConfiguration.SERVICE_FUZZY_SEARCH_ACTIVATE, "true");
			if (!isFuzzyActivate) {
				return;
			}

			if (DirectoryReader.indexExists(directory) || isIndexing) {
				LOG.info("VideoIndex::directory indexing is existing.");
				return;
			}

			isIndexing = true;

			initAllIndexing();

		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			if (isIndexing) {
				isIndexing = false;
			}
		}
	}

	@Override
	public JsonObject initAllIndexing() {
		final Stopwatch timer = Stopwatch.createStarted();
		long inserted = 0;
		Pageable<Data> pageable = new PageImpl<>(0, 1000);
		Pageable<Data> nextPageList;
		int records = 0;
		do {
			nextPageList = pageable.nextPageable();
			pageable = VideoStorageImpl.getPageable(nextPageList);
			int sizeList = pageable.getList().size();
			records += sizeList;
			inserted += sizeList;
			this.createPageIndex(pageable.getList());
		} while (pageable.hasNext());
		LOG.info("Video::Index {} records", records);
		long minutes = timer.elapsed(TimeUnit.MINUTES);
		LOG.info("VideoIndex::Indexing took(minute: {})", timer.stop());
		JsonObject result = new JsonObject();
		result.put("inserted", inserted);
		result.put("indexing took minute(s)", minutes);
		return result;
	}
}
