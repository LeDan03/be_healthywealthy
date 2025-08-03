package vn.edu.stu.PostService.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.stu.PostService.dto.RecipeDocumentDto;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeSearchService {

        private final ElasticsearchClient elasticsearchClient;

        public List<Long> searchRecipes(String keyword) throws IOException {
                Set<Long> resultIds = new HashSet<>();
                resultIds.addAll(searchByFields(keyword, List.of("title.autocomplete^2", "title.folded", "content")));
                resultIds.addAll(searchByField("difficulty.name.autocomplete", keyword));
                resultIds.addAll(searchByField("category.name.autocomplete", keyword));

                log.info("Search keyword: '{}', total matched recipe IDs: {}", keyword, resultIds.size());
                return new ArrayList<>(resultIds);
        }

        private List<Long> searchByFields(String keyword, List<String> fields) throws IOException {
                SearchRequest request = new SearchRequest.Builder()
                                .index("recipes")
                                .query(q -> q.multiMatch(m -> m.fields(fields).query(keyword)))
                                .build();

                return extractIdsFromSearch(request);
        }

        private List<Long> searchByField(String field, String value) throws IOException {
                SearchRequest request = new SearchRequest.Builder()
                                .index("recipes")
                                .query(q -> q.match(m -> m.field(field).query(value)))
                                .build();

                return extractIdsFromSearch(request);
        }

        private List<Long> extractIdsFromSearch(SearchRequest request) throws IOException {
                try {
                        SearchResponse<RecipeDocumentDto> response = elasticsearchClient.search(request,
                                        RecipeDocumentDto.class);
                        List<Long> ids = response.hits().hits().stream()
                                        .map(Hit::source)
                                        .filter(Objects::nonNull)
                                        .map(RecipeDocumentDto::getId)
                                        .collect(Collectors.toList());

                        log.debug("Found {} recipes matching query", ids.size());
                        return ids;

                } catch (IOException e) {
                        log.error("Elasticsearch search failed: {}", e.getMessage(), e);
                        throw e;
                }
        }
}
