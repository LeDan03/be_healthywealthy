package vn.edu.stu.WebBlogNauAn.config;

import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ElasticsearchIndexConfig {
    private final ElasticsearchClient elasticsearchClient;
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchIndexConfig.class);

    @PostConstruct
    public void createIndex() {
        int maxRetries = 5;
        int retryDelaySeconds = 10;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                boolean indexExists = elasticsearchClient.indices()
                        .exists(ExistsRequest.of(e -> e.index("registered"))).value();

                if (indexExists) {
                    logger.info("Index 'registered' already exists. Skip creation");
                    return;
                }
                // "type": "asciifolding": cho timf kiesm khong dau
                CreateIndexRequest request = new CreateIndexRequest.Builder()
                        .index("registered")
                        .withJson(new StringReader(
                                """
                                        {
                                          "settings": {
                                            "analysis": {
                                              "tokenizer": {
                                                "autocomplete_tokenizer": {
                                                  "type": "edge_ngram",
                                                  "min_gram": 1,
                                                  "max_gram": 20,
                                                  "token_chars": ["letter", "digit"]
                                                }
                                              },
                                              "filter": {
                                                "vietnamese_folding": {
                                                  "type": "asciifolding"
                                                }
                                              },
                                              "analyzer": {
                                                "vietnamese_analyzer": {
                                                  "tokenizer": "standard",
                                                  "filter": ["lowercase", "vietnamese_folding"]
                                                },
                                                "autocomplete": {
                                                  "tokenizer": "autocomplete_tokenizer",
                                                  "filter": ["lowercase", "vietnamese_folding"]
                                                }
                                              }
                                            }
                                          },
                                          "mappings": {
                                            "dynamic": "strict",
                                            "properties": {
                                              "id": {
                                                "type": "long"
                                              },
                                              "username": {
                                                "type": "text",
                                                "fields": {
                                                  "folded": {
                                                    "type": "text",
                                                    "analyzer": "vietnamese_analyzer"
                                                  },
                                                  "autocomplete": {
                                                    "type": "text",
                                                    "analyzer": "autocomplete"
                                                  }
                                                }
                                              }
                                            }
                                          }
                                        }
                                        """))
                        .build();
                elasticsearchClient.indices().create(request);
                logger.info("Index 'registered' created successfully.");
                return;
            } catch (Exception e) {
                logger.error("Attempt " + attempt + " failed to create index: " + e.getMessage());
                if (attempt == maxRetries) {
                    throw new RuntimeException("Failed to create Elasticsearch index after " + maxRetries + " attempts",
                            e);
                }
                try {
                    TimeUnit.SECONDS.sleep(retryDelaySeconds);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting to retry Elasticsearch index creation", ie);
                }
            }
        }
    }
}
