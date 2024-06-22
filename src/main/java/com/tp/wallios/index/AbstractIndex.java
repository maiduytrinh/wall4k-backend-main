package com.tp.wallios.index;

import com.tp.wallios.Utils;
import com.tp.wallios.common.config.AppConfiguration;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public abstract class AbstractIndex<T, R> {
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractIndex.class);
	protected final String parentDirectory = AppConfiguration.get(AppConfiguration.REPOSITORY_INDEX, "./home/repository/indexes/");
	protected final String indexName;
	protected final List<String> stopWords = Arrays.asList(" ", ","); //Filters both words
	protected final CharArraySet stopSet = new CharArraySet(stopWords, true);
	private final int DEFAULT_LIMIT = 10000;


	public AbstractIndex(String indexName) {
		this.indexName = indexName;
	}

	private static BooleanQuery.Builder buildBooleanQuery(Map<String, String> conditionsMap) {
		final BooleanQuery.Builder builder = new BooleanQuery.Builder();
		if (conditionsMap.isEmpty()) {
			LOG.error("Conditions map is empty!");
		}
		conditionsMap.forEach((key, value) -> {
			boolean isMustNot = false;
			String processedKey = key;
			if (key.startsWith("!")) {
				isMustNot = true;
				processedKey = key.substring(1);
			}

			if (value.isEmpty() || processedKey.length() == 1) {
				return;
			}

			String[] conditions = value.split(";");
			if (conditions.length > 1) {
				BooleanQuery.Builder subQuery = new BooleanQuery.Builder();
				for (String condition : conditions) {
					Term term = new Term(processedKey, condition);
					subQuery.add(new TermQuery(term), BooleanClause.Occur.SHOULD);
				}
				builder.add(subQuery.build(), isMustNot ? BooleanClause.Occur.MUST_NOT : BooleanClause.Occur.MUST);
			} else {
				Term term = new Term(processedKey, value);
				builder.add(new TermQuery(term), isMustNot ? BooleanClause.Occur.MUST_NOT : BooleanClause.Occur.MUST);
			}
		});
		return builder;
	}

	/**
	 * Init the indexing import
	 */
	public void init() {
		try {
			if (DirectoryReader.indexExists(getDirectory()) || getIsIndexing()) {
				LOG.info(getClassName() + "::Directory index was indexed");
				return;
			}
			setIsIndexing(true);
			this.initAllIndexing();
		} catch (IOException e) {
			LOG.error("Error init question: {}", e.getMessage(), e);
		} finally {
			if (getIsIndexing()) {
				setIsIndexing(false);
			}
		}
	}

	public abstract JsonObject initAllIndexing();

	/**
	 * Creates a page index for the given list of data.
	 *
	 * @param listData the list of data to create the index for
	 * @return the number of documents indexed
	 */
	public Long createPageIndex(List<T> listData) {
		if (listData == null || listData.isEmpty()) return 0L;
		List<Document> docs = new ArrayList<>();
		for (T element : listData) {
			Document tmpDocument = this.create(element);
			if (tmpDocument == null) continue;
			docs.add(tmpDocument);
		}
		if (docs.isEmpty()) return 0L;

		LOG.info("Index with size {} documents", docs.size());
		return getIndexer().indexDocuments(docs);
	}

	public void createIndex(T model) {
		Document document = this.create(model);
		if (document == null) {
			LOG.error("Indexing question fail {}", model);
			LOG.error("Indexing question fail {}", model);
		}
		getIndexer().indexDocument(document);
	}

	/**
	 * Update document from model
	 *
	 * @param model
	 */
	public abstract Long updateIndex(T model);

	/**
	 * Update document field value in doc values
	 *
	 * @param id         the document id
	 * @param fieldName  the field name
	 * @param fieldValue the field value
	 * @return the number of updated documents
	 */
	public Long updateDocValues(Integer id, String fieldName, String fieldValue) {
		if (id == null || id < 1 || StringUtils.isEmpty(fieldName) || StringUtils.isEmpty(fieldName)) {
			return -1L;
		}
		Term term = new Term("id", id.toString());
		return getIndexer().updateNumericDocValue(term, fieldName, Utils.parseLong(fieldValue));
	}

	/**
	 * Create document from model list
	 *
	 * @param models
	 */
	public Long createIndexes(List<T> models) {
		return this.createPageIndex(models);
	}

	/**
	 * Delete document
	 *
	 * @param term
	 */
	public Long deleteDocument(Term term) {
		if (term == null) return -1L;
		return getIndexer().deleteDocument(term);
	}

	public abstract Directory getDirectory();

	public abstract Boolean getIsIndexing();

	public abstract void setIsIndexing(Boolean isIndexing);

	public abstract String getClassName();

	public abstract Document create(T model);

	/**
	 * Delete document
	 *
	 * @param query the query to delete the document
	 * @return the number of deleted documents
	 */
	public Long deleteDocument(Query query) {
		if (query == null) return -1L;
		return getIndexer().deleteDocument(query);
	}

	public abstract LuceneIndex getIndexer();

	/**
	 * Get all documents from the index
	 *
	 * @return a list of all documents
	 */
	public List<T> getAll() {
		Query query = new MatchAllDocsQuery();
		List<Document> documents = getIndexer().searchIndex(query, DEFAULT_LIMIT);
		return this.convertDocuments(documents);
	}

	public abstract List<T> convertDocuments(List<Document> documents);

	/**
	 * Search hashtag with offset and limit
	 *
	 * @param conditionsMap the conditions to search with
	 * @param sort          the sort order
	 * @param offset        the offset to start from
	 * @param limit         the maximum number of results to return
	 * @param country       the country to search in
	 * @return a list of results
	 */
	public List<T> search(Map<String, String> conditionsMap, Sort sort, Integer offset, Integer limit, String country) {
		final BooleanQuery.Builder builder = buildBooleanQuery(conditionsMap);
		List<Document> documents;
		if (sort == null) {
			documents = getIndexer().searchIndex(builder.build(), offset, limit);
		} else {
			documents = getIndexer().searchIndex(builder.build(), sort, offset, limit);
		}
		return this.convertDocuments(documents);
	}

	/**
	 * Search with a given key and a list of values
	 *
	 * @param key    the field to search in
	 * @param values the values to search for
	 * @return a list of results
	 */
	public List<T> search(String key, List<String> values) {
		BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		for (String value : values) {
			Query query;
			if (key.contains("!")) {
				query = new TermQuery(new Term(key.replace("!", "").trim(), value));
				builder.add(query, BooleanClause.Occur.MUST_NOT);
			} else {
				query = new TermQuery(new Term(key, value));
				builder.add(query, BooleanClause.Occur.SHOULD);
			}
			LOG.info("build value: {}", builder.build());
		}
		List<Document> doc = getIndexer().searchIndex(builder.build(), DEFAULT_LIMIT);
		return convertDocuments(doc);
	}

	/**
	 * Search hashtag with offset and limit
	 *
	 * @param key     the field to search in
	 * @param values  the values to search for
	 * @param country the country to search in
	 * @return a list of results
	 */
	public List<T> search(String key, List<Integer> values, String country) {
		BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		for (Integer value : values) {
			Query query = new TermQuery(new Term(key, value.toString().trim()));
			builder.add(query, BooleanClause.Occur.MUST);
			List<Document> doc = getIndexer().searchIndex(builder.build(), DEFAULT_LIMIT);
			return convertDocuments(doc);
		}
		return new ArrayList<>();
	}

	/**
	 * Search with sort and pagination
	 *
	 * @param conditionsMap the conditions to search with
	 * @param limit         the maximum number of results to return
	 * @return a list of results
	 */
	public List<T> search(Map<String, String> conditionsMap, Integer limit) {
		final BooleanQuery.Builder builder = buildBooleanQuery(conditionsMap);
		List<Document> documents = getIndexer().searchIndex(builder.build(), limit);
		return this.convertDocuments(documents);
	}

	/**
	 * Search with sort and pagination
	 *
	 * @param conditionsMap the conditions to search with
	 * @param offset        the offset to start from
	 * @param limit         the maximum number of results to return
	 * @return a list of results
	 */
	public List<T> search(Map<String, String> conditionsMap, Integer offset, Integer limit) {
		return this.search(conditionsMap, offset, limit, "");
	}

	/**
	 * Search with sort and pagination
	 *
	 * @param conditions the conditions to search with
	 * @param sort       the sort order
	 * @param offset     the offset to start from
	 * @param limit      the maximum number of results to return
	 * @return a list of results
	 */
	public List<T> search(Map<String, String> conditions, Sort sort, Integer offset, Integer limit) {
		final BooleanQuery.Builder builder = buildBooleanQuery(conditions);
		List<Document> documents = getIndexer().searchIndex(builder.build(), sort, offset, limit);
		return this.convertDocuments(documents);
	}

	/**
	 * Search with sort and pagination
	 *
	 * @param conditions the conditions to search with
	 * @param offset     the offset to start from
	 * @param limit      the maximum number of results to return
	 * @param country    the country to search in
	 * @return a list of results
	 */
	public List<T> search(Map<String, String> conditions, Integer offset, Integer limit, String country) {
		final BooleanQuery.Builder builder = buildBooleanQuery(conditions);
		LOG.info("builder value: " + builder.build());
		List<Document> documents = getIndexer().searchIndex(builder.build(), offset, limit);
		return this.convertDocuments(documents);
	}

	/**
	 * Rebuild all document indexes
	 */
	public JsonObject rebuildIndexes() {
		Long deleted = getIndexer().deleteAll();
		JsonObject result = initAllIndexing();
		result.put("deleted", deleted);
		return result;
	}

    public List<T> getByCountry(String country, int offset, int limit) {
        return null;
    }

	public List<T> getByCategory(String categories, int offset, int limit, Integer type, Boolean isIpad) {
		return null;
	}

    // SpecialDataIndex
	public String getSpecialDataIds(String contentTypes, String country, String specialType, int limit) {
		return "";
	}

	public List<T> getDataByIds(String ids, String country, Boolean isIpad) {
		return null;
	};

	public List<T> searchData(String hashtags, String country, int offset, int limit) {
		return Collections.EMPTY_LIST;
	}

	public List<T> searchKeyword(String hashtags, String country, int offset, int limit) {
		return Collections.EMPTY_LIST;
	}
}