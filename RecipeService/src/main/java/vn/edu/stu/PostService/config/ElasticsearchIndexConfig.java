package vn.edu.stu.PostService.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

@Component
public class ElasticsearchIndexConfig {

  private final ElasticsearchClient elasticsearchClient;

  public ElasticsearchIndexConfig(ElasticsearchClient elasticsearchClient) {
    this.elasticsearchClient = elasticsearchClient;
  }

  @PostConstruct
  public void createIndex() {
    int maxRetries = 5;
    int retryDelaySeconds = 10;

    for (int attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        boolean indexExists = elasticsearchClient.indices()
            .exists(ExistsRequest.of(e -> e.index("recipes"))).value();

        if (indexExists) {
          System.out.println("Index 'recipes' already exists. Skipping creation.");
          return;
        }

        CreateIndexRequest request = new CreateIndexRequest.Builder()
            .index("recipes")
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
                          "id": { "type": "long" },
                          "title": {
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
                          },
                          "content": {
                            "type": "text",
                            "analyzer": "vietnamese_analyzer"
                          },
                          "difficulty": {
                            "properties": {
                              "name": {
                                "type": "keyword",
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
                          },
                          "category": {
                            "properties": {
                              "name": {
                                "type": "keyword",
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
                      }
                    }
                    """))
            .build();

        elasticsearchClient.indices().create(request);
        System.out.println("Index 'recipes' created successfully.");
        return;

      } catch (IOException e) {
        System.err.println("Attempt " + attempt + " failed to create index: " + e.getMessage());
        if (attempt == maxRetries) {
          throw new RuntimeException("Failed to create Elasticsearch index after " + maxRetries + " attempts", e);
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
