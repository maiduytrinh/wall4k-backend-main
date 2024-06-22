package com.tp.wallios.index.service;

import com.google.common.base.Stopwatch;
import com.tp.wallios.common.config.AppConfiguration;
import com.tp.wallios.entity.SpecialData;
import com.tp.wallios.index.AbstractIndex;
import com.tp.wallios.index.DocumentConstant;
import com.tp.wallios.index.LuceneIndex;
import com.tp.wallios.rdbms.PageImpl;
import com.tp.wallios.rdbms.api.Pageable;
import com.tp.wallios.storage.impl.SpecialDataStorageImpl;
import io.vertx.core.json.JsonObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SpecialDataIndexService extends AbstractIndex<SpecialData, SpecialData> {
	protected static final Logger LOG = LoggerFactory.getLogger(SpecialDataIndexService.class);

	protected final static String INDEX_NAME = "/specialData";

	protected LuceneIndex indexer = null;
	protected Directory directory = null;
	protected Boolean isIndexing = false;

	public SpecialDataIndexService() {
		super(INDEX_NAME);
		try {
			directory = FSDirectory.open(Paths.get(parentDirectory + INDEX_NAME));
			indexer = new LuceneIndex(directory, new StandardAnalyzer(stopSet));
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public SpecialDataIndexService(String indexName) {
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
				LOG.info("SpecialDataIndex::directory indexing is existing.");
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
		Pageable<SpecialData> pageable = new PageImpl<>(0, 1000);
		Pageable<SpecialData> nextPageList;
		int records = 0;
		do {
			nextPageList = pageable.nextPageable();
			pageable = SpecialDataStorageImpl.getPageable(nextPageList);
			int sizeList = pageable.getList().size();
			records += sizeList;
			inserted += sizeList;
			this.createPageIndex(pageable.getList());
		} while (pageable.hasNext());
		LOG.info("SpecialData::Index {} records", records);
		long minutes = timer.elapsed(TimeUnit.MINUTES);
		LOG.info("SpecialDataIndex::Indexing took(minute: {})", timer.stop());
		JsonObject result = new JsonObject();
		result.put("inserted", inserted);
		result.put("indexing took minute(s)", minutes);
		return result;
	}


	@Override
	public Long updateIndex(SpecialData model) {
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
		return SpecialDataIndexService.class.getSimpleName();
	}

	@Override
	public List<SpecialData> convertDocuments(List<Document> documents) {
		if (CollectionUtils.isEmpty(documents)) return new ArrayList<>();
		List<SpecialData> result = new ArrayList<>();
		for (Document document : documents) {
			try {
				SpecialData specialData = new SpecialData();
				specialData.setId(Long.parseLong(document.get(DocumentConstant.ID)));
				specialData.setContentId(Long.parseLong(document.get(DocumentConstant.CONTENT_ID)));
				specialData.setCountry(document.get(DocumentConstant.COUNTRIES));
				specialData.setContentType(document.get(DocumentConstant.CONTENT_TYPE));
				specialData.setSpecialType(document.get(DocumentConstant.SPECIAL_TYPE));
				result.add(specialData);
			} catch (Exception e) {
				LOG.error("Error convertToSpecialData::document: {}", document);
				LOG.error(e.getMessage());
			}
		}
		return result;
	}

	@Override
	public Document create(SpecialData model) {
		final Document document = new Document();

		document.add(new StringField(DocumentConstant.ID, model.getId().toString(), Field.Store.NO));
		document.add(new StringField(DocumentConstant.COUNTRIES, model.getCountry(), Field.Store.NO));
		document.add(new StringField(DocumentConstant.CONTENT_ID, String.valueOf(model.getContentId()), Field.Store.YES));
		document.add(new StringField(DocumentConstant.CONTENT_TYPE, String.valueOf(model.getContentType()), Field.Store.NO));
		document.add(new StringField(DocumentConstant.SPECIAL_TYPE, model.getSpecialType(), Field.Store.NO));

		document.add(new SortedNumericDocValuesField(DocumentConstant.ORDER, model.getOrder()));

		return document;
	}

	@Override
	public String getSpecialDataIds(String contentType, String country, String specialType, int limit) {
		final BooleanQuery.Builder builder = new BooleanQuery.Builder();

		Query query = new TermQuery(new Term(DocumentConstant.COUNTRIES, country));
		builder.add(query, BooleanClause.Occur.MUST);

		query = new TermQuery(new Term(DocumentConstant.SPECIAL_TYPE, specialType));
		builder.add(query, BooleanClause.Occur.MUST);

		String [] types = contentType.split(";");
		BooleanQuery.Builder typeBuilder = new BooleanQuery.Builder();
		for (String type : types) {
			typeBuilder.add(new TermQuery(new Term(DocumentConstant.CONTENT_TYPE, type)), BooleanClause.Occur.SHOULD);
		}
		builder.add(typeBuilder.build(), BooleanClause.Occur.MUST);

		SortField sortField = new SortedNumericSortField(DocumentConstant.ORDER, SortField.Type.INT, false);
		Sort sort = new Sort(sortField);

		final List<Document> documents = indexer.searchIndex(builder.build(), sort, limit);
		return StringUtils.join(documents.stream().map(doc -> doc.get(DocumentConstant.CONTENT_ID)).collect(Collectors.toList()), ";");
	}
}
