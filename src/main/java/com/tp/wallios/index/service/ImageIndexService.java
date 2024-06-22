package com.tp.wallios.index.service;

import com.google.common.base.Stopwatch;
import com.mysql.cj.util.StringUtils;
import com.tp.wallios.common.config.AppConfiguration;
import com.tp.wallios.entity.Data;
import com.tp.wallios.index.AbstractIndex;
import com.tp.wallios.index.DocumentConstant;
import com.tp.wallios.index.LuceneIndex;
import com.tp.wallios.rdbms.PageImpl;
import com.tp.wallios.rdbms.api.Pageable;
import com.tp.wallios.storage.impl.ImageStorageImpl;
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

public class ImageIndexService extends AbstractIndex<Data, Data> {
	protected static final Logger LOG = LoggerFactory.getLogger(ImageIndexService.class);

	protected final static String INDEX_NAME = "/image";

	protected LuceneIndex indexer = null;
	protected Directory directory = null;
	protected Boolean isIndexing = false;

	public ImageIndexService() {
		super(INDEX_NAME);
		try {
			directory = FSDirectory.open(Paths.get(parentDirectory + INDEX_NAME));
			indexer = new LuceneIndex(directory, new StandardAnalyzer(stopSet));
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public ImageIndexService(String indexName) {
		super(indexName);
		try {
			directory = FSDirectory.open(Paths.get(parentDirectory + indexName));
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
				LOG.info("ImageIndex::directory indexing is existing.");
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
			pageable = ImageStorageImpl.getPageable(nextPageList);
			int sizeList = pageable.getList().size();
			records += sizeList;
			inserted += sizeList;
			this.createPageIndex(pageable.getList());
		} while (pageable.hasNext());
		LOG.info("Image::Index {} records", records);
		long minutes = timer.elapsed(TimeUnit.MINUTES);
		LOG.info("ImageIndex::Indexing took(minute: {})", timer.stop());
		JsonObject result = new JsonObject();
		result.put("inserted", inserted);
		result.put("indexing took minute(s)", minutes);
		return result;
	}


	@Override
	public Long updateIndex(Data model) {
		if (model.getId() == null) return -1L;
		Term term = new Term("id", model.getId().toString());
		Document document = this.create(model);
		if (document == null) return -1L;
		return indexer.updateDocument(term, document);
	}

	@Override
	public LuceneIndex getIndexer() {
		return indexer;
	}

	@Override
	public Directory getDirectory() {
		return directory;
	}

	@Override
	public Boolean getIsIndexing() {
		return isIndexing;
	}

	@Override
	public void setIsIndexing(Boolean isIndexing) {
		this.isIndexing = isIndexing;
	}

	@Override
	public String getClassName() {
		return ImageIndexService.class.getSimpleName();
	}

	@Override
	public List<Data> convertDocuments(List<Document> documents) {
		if (CollectionUtils.isEmpty(documents)) return new ArrayList<>();
		List<Data> result = new ArrayList<>();
		for (Document document : documents) {
			try {
				Data dataPro = new Data();
				dataPro.setId(Integer.parseInt(document.get(DocumentConstant.ID)));
				dataPro.setName(document.get(DocumentConstant.NAME));
				dataPro.setHashtag(document.get(DocumentConstant.HASHTAG));
				dataPro.setSearchName(document.get(DocumentConstant.SEARCH_NAME));
				dataPro.setCountries(document.get(DocumentConstant.COUNTRIES));
				dataPro.setCategories(document.get(DocumentConstant.CATEGORIES));
				dataPro.setUrl(document.get(DocumentConstant.URL));
				dataPro.setScreenRatio(Integer.parseInt(document.get(DocumentConstant.SCREEN_RATIO)));
				dataPro.setImgSize(document.get(DocumentConstant.IMG_SIZE));
				dataPro.setDownCount(Integer.parseInt(document.get(DocumentConstant.DOWN_COUNT)));
				dataPro.setCreatedDate(Long.valueOf(document.get(DocumentConstant.CREATED_DATE)));
				dataPro.setType(Integer.parseInt(document.get(DocumentConstant.TYPE)));
				dataPro.setSupportIpad(Integer.parseInt(document.get(DocumentConstant.SUPPORT_IPAD)));
				result.add(dataPro);
			} catch (Exception e) {
				LOG.error("Error convertToImage::document: {}", document);
				LOG.error(e.getMessage());
			}
		}
		return result;
	}

	@Override
	public Document create(Data model) {
		final Document document = new Document();
		String hashtag = model.getHashtag().replace(",", " ");
		String category = model.getCategories().replace(",", " ");

		document.add(new StringField(DocumentConstant.ID, model.getId().toString(), Field.Store.YES));
		document.add(new StringField(DocumentConstant.NAME, model.getName(), Field.Store.YES));
		document.add(new TextField(DocumentConstant.KEYWORD, hashtag.trim(), Field.Store.YES));
		document.add(new TextField(DocumentConstant.HASHTAG, model.getHashtag(), Field.Store.YES));
		document.add(new TextField(DocumentConstant.SEARCH_NAME, model.getSearchName(), Field.Store.YES));
		document.add(new StringField(DocumentConstant.COUNTRIES, model.getCountries(), Field.Store.YES));
		document.add(new StringField(DocumentConstant.CATEGORIES, model.getCategories(), Field.Store.YES));
		document.add(new TextField(DocumentConstant.CATEGORY, category, Field.Store.NO));
		document.add(new StringField(DocumentConstant.URL, model.getUrl(), Field.Store.YES));
		document.add(new StringField(DocumentConstant.SCREEN_RATIO, model.getScreenRatio() == null ? "0" : model.getScreenRatio().toString(), Field.Store.YES));
		document.add(new StringField(DocumentConstant.IMG_SIZE, model.getImgSize() == null ? "" : model.getImgSize(), Field.Store.YES));
		document.add(new StringField(DocumentConstant.DOWN_COUNT, model.getDownCount() == null ? "0" : model.getDownCount().toString(), Field.Store.YES));
		document.add(new StringField(DocumentConstant.CREATED_DATE, model.getCreatedDate() == null ? "0L" : model.getCreatedDate().toString(), Field.Store.YES));
		document.add(new StringField(DocumentConstant.TYPE, model.getType() == null ? "0" : model.getType().toString(), Field.Store.YES));
		document.add(new StringField(DocumentConstant.SUPPORT_IPAD, model.getSupportIpad() == null ? "0" : model.getSupportIpad().toString(), Field.Store.YES));

		document.add(new SortedDocValuesField(DocumentConstant.NAME, new BytesRef(model.getName())));
		document.add(new SortedNumericDocValuesField(DocumentConstant.ID, model.getId()));
		document.add(new SortedNumericDocValuesField(DocumentConstant.DOWN_COUNT, model.getDownCount() == null ? 0 : model.getDownCount()));
		document.add(new SortedNumericDocValuesField(DocumentConstant.CREATED_DATE, model.getCreatedDate()));
		return document;
	}

	@Override
	public List<Data> getByCountry(String country, int offset, int limit) {
			Map<String, String> conditionsMap = new HashMap<>();
			conditionsMap.put(DocumentConstant.COUNTRIES, country);

			SortField sortFieldDate =  new SortedNumericSortField(DocumentConstant.CREATED_DATE, SortField.Type.LONG, true);
			Sort sort = new Sort(sortFieldDate);

			return search(conditionsMap, sort, offset, limit);
	}

	@Override
	public List<Data> getByCategory(String category, int offset, int limit, Integer type, Boolean isIpad) {
		if (category == null || category.isEmpty()) {
			return null;
		}

		Map<String, String> conditionsMap = new HashMap<>();
		if (type != null) {
			conditionsMap.put(DocumentConstant.TYPE, type.toString());
		}
		if (isIpad) {
			conditionsMap.put(DocumentConstant.SUPPORT_IPAD, "1");
		}
		conditionsMap.put(DocumentConstant.CATEGORIES, category);

		SortField sortFieldDate =  new SortedNumericSortField(DocumentConstant.CREATED_DATE, SortField.Type.LONG, true);
		Sort sort = new Sort(sortFieldDate);
		return search(conditionsMap, sort, offset, limit);
	}

	@Override
	public List<Data> getDataByIds(String ids, String country, Boolean isIpad) {
		Map<String, String> conditionsMap = new HashMap<>();
		conditionsMap.put(DocumentConstant.COUNTRIES, country);
		conditionsMap.put(DocumentConstant.ID, ids);
		if (isIpad != null && isIpad) {
			conditionsMap.put(DocumentConstant.SUPPORT_IPAD, "1");
		}
		return search(conditionsMap, ids.split(";").length);
	}

	@Override
	public List<Data> searchData(String hashtag, String country, int offset, int limit) {
		Map<String, String> conditionsMap = new HashMap<>();
		conditionsMap.put(DocumentConstant.KEYWORD, hashtag);
		conditionsMap.put(DocumentConstant.COUNTRIES, country);

		SortField sortField = new SortedNumericSortField(DocumentConstant.CREATED_DATE, SortField.Type.LONG, true);
		Sort sort = new Sort(sortField);

		return search(conditionsMap, sort, offset, limit);
	}

	@Override
	public List<Data> searchKeyword(String keyword, String country, int offset, int limit) {
		LOG.info("search by keywords {}, offset {}, limit {}", keyword, offset, limit);

		List<Data> extractResult = searchExtract(keyword, country, offset, limit);

		if (offset == 0 && extractResult.size() < limit) {
			LOG.info("search keyword {} with FuzzyQuery", keyword);
			final String[] queryData =keyword.toLowerCase().split(" ");
			final BooleanQuery.Builder builder = new BooleanQuery.Builder();
			Query query;

			for (String queryDatum : queryData) {
				query = new FuzzyQuery(new Term(DocumentConstant.SEARCH_NAME, queryDatum));
				builder.add(query, BooleanClause.Occur.SHOULD);
			}

			query = new TermQuery(new Term(DocumentConstant.COUNTRIES, country));
			builder.add(query, BooleanClause.Occur.MUST);

			final List<Document> result = indexer.searchIndex(builder.build(), offset, !extractResult.isEmpty() ? limit : limit - 1);
			List<Data> searchKeywords = convertDocuments(result);
			for (Data data : searchKeywords) {
				if (!extractResult.contains(data) && extractResult.size() < limit) {
					extractResult.add(data);
				}
			}
		}
		return extractResult;
	}

	private List<Data> searchExtract(String keyword, String country, int offset, int limit) {
		Map<String, String> conditionsMap = new HashMap<>();
		String keywordCondition = String.join(";", keyword.toLowerCase().split(" "));
		conditionsMap.put(DocumentConstant.COUNTRIES, country);
		conditionsMap.put(DocumentConstant.SEARCH_NAME, keywordCondition);

		return search(conditionsMap, offset, limit);
	}
}
