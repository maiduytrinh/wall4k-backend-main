package com.tp.wallios.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LuceneIndex {
	protected static final Logger LOG = LoggerFactory.getLogger(LuceneIndex.class);
	private final Directory directoryIndex;
	private final Analyzer analyzer;


	public LuceneIndex(Directory directoryIndex, Analyzer analyzer) {
		super();
		this.directoryIndex = directoryIndex;
		this.analyzer = analyzer;
	}

	public void indexDocument(Document document) {
		try {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(directoryIndex, indexWriterConfig);
			writer.addDocument(document);
			writer.close();
		} catch (IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
	}

	public Long indexDocuments(List<Document> documents) {
		try {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(directoryIndex, indexWriterConfig);
			Long countDocs = writer.addDocuments(documents);
			writer.close();
			return countDocs;
		} catch (IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
		return 0L;
	}

	public Long updateDocument(Term term, Document document) {
		try {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(directoryIndex, indexWriterConfig);
			Long d = writer.updateDocument(term, document);
			writer.close();
			return d;
		} catch (IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
		return 0L;
	}

	public Long updateNumericDocValue(Term term, String field, long value) {
		try {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(directoryIndex, indexWriterConfig);
			Long d = writer.updateNumericDocValue(term, field, value);
			writer.close();
			return d;
		} catch (IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
		return 0L;
	}

	public Long updateDocValues(Term term, Field field) {
		try {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(directoryIndex, indexWriterConfig);
			Long d = writer.updateDocValues(term, field);
			writer.close();
			return d;
		} catch (IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
		return 0L;
	}

	public Long deleteAll() {
		try {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(directoryIndex, indexWriterConfig);
			final Long deletedCount = writer.deleteAll();
			writer.close();
			return deletedCount;
		} catch (IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
		return 0L;
	}

	public Long deleteDocument(Term term) {
		try {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(directoryIndex, indexWriterConfig);
			Long countDeleted = writer.deleteDocuments(term);
			writer.close();
			return countDeleted;
		} catch (IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
		return 0L;
	}

	public Long deleteDocument(Query query) {
		try {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(directoryIndex, indexWriterConfig);
			Long countDeleted = writer.deleteDocuments(query);
			writer.close();
			return countDeleted;
		} catch (IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
		return 0L;
	}

	public Document searchIndex(Query query) {
		try {
			IndexReader indexReader = DirectoryReader.open(directoryIndex);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			TopDocs topDocs = searcher.search(query, 1);
			Document document = null;

			if (topDocs.totalHits.value != 0) {
				document = searcher.doc(topDocs.scoreDocs[0].doc);
			}

			return document;
		} catch (IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
		return null;

	}

	public List<Document> searchIndex(Query query, int limit) {
		if (limit <= 0) {
			return new ArrayList<>();
		}
		try {
			IndexReader indexReader = DirectoryReader.open(directoryIndex);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			TopDocs topDocs = searcher.search(query, limit);
			List<Document> documents = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				documents.add(searcher.doc(scoreDoc.doc));
			}
			return documents;
		} catch (IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
		return new ArrayList<>();

	}

	public List<Document> searchIndex(Query query, int offset, int limit) {
		if (limit <= 0) {
			return new ArrayList<>();
		}
		try {
			IndexReader indexReader = DirectoryReader.open(directoryIndex);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			//Get the last element of the previous page
			ScoreDoc lastSd = getLastScoreDoc(query, searcher, offset, limit);
			if (offset > 0 && lastSd == null) {
				return new ArrayList<>();
			}
			//Search for elements on the next page through the last element
			TopDocs topDocs = searcher.searchAfter(lastSd, query, limit);
			List<Document> documents = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				documents.add(searcher.doc(scoreDoc.doc));
			}
			return documents;
		} catch (IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
		return null;

	}

	public List<Document> searchIndexDistinct(Query query, int offset, int limit, String field) {
		if (limit <= 0) {
			return new ArrayList<>();
		}
		try {
			IndexReader indexReader = DirectoryReader.open(directoryIndex);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			//Get the last element of the previous page
			ScoreDoc lastSd = getLastScoreDoc(query, searcher, offset, limit);
			if (offset > 0 && lastSd == null) {
				return new ArrayList<>();
			}
			//Search for elements on the next page through the last element
			TopDocs topDocs = searcher.searchAfter(lastSd, query, limit);
			List<Document> documents = new ArrayList<>();
			List<String> values = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				if (values.contains(doc.get(field))) continue;
				documents.add(searcher.doc(scoreDoc.doc));
				values.add(doc.get(field));
			}
			return documents;
		} catch (IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
		return null;

	}

	protected ScoreDoc getLastScoreDoc(Query query, IndexSearcher indexer, int offset, int limit) throws IOException {
		if (offset == 0) return null;//If it is the first page, return empty
		TopDocs tds = indexer.search(query, offset);
		if (tds.scoreDocs.length < offset) return null;
		return tds.scoreDocs[offset - 1];
	}

	protected ScoreDoc getLastScoreDoc(Query query, IndexSearcher indexer, int offset, int limit, Sort sort) throws IOException {
		if (offset == 0) return null;//If it is the first page, return empty
		TopDocs tds = indexer.search(query, offset, sort);
		if (tds.scoreDocs.length < offset) return null;
		return tds.scoreDocs[offset - 1];
	}

	public List<Document> searchIndex(Query query, Sort sort, int limit) {
		if (limit <= 0) {
			return new ArrayList<>();
		}
		try {
			IndexReader indexReader = DirectoryReader.open(directoryIndex);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			TopDocs topDocs = searcher.search(query, limit, sort);
			List<Document> documents = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				documents.add(searcher.doc(scoreDoc.doc));
			}

			return documents;
		} catch (IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
		return null;

	}

	public List<Document> searchIndex(Query query, Sort sort, int offset, int limit) {
		if (limit <= 0) {
			return new ArrayList<>();
		}
		try {
			IndexReader indexReader = DirectoryReader.open(directoryIndex);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			//Get the last element of the previous page
			ScoreDoc lastSd = getLastScoreDoc(query, searcher, offset, limit, sort);
			if (offset > 0 && lastSd == null) {
				return new ArrayList<>();
			}
			//Search for elements on the next page through the last element
			TopDocs topDocs = searcher.searchAfter(lastSd, query, limit, sort);
			List<Document> documents = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				documents.add(searcher.doc(scoreDoc.doc));
			}

			return documents;
		} catch (IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
		return null;

	}

	public int count(Query query) {
		try {
			IndexReader indexReader = DirectoryReader.open(directoryIndex);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			return searcher.count(query);
		} catch (IOException e) {
			LOG.error("An error occurred: {}", e.getMessage());
		}
		return 0;
	}

}