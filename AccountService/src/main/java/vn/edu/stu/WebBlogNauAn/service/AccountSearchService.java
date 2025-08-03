package vn.edu.stu.WebBlogNauAn.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import vn.edu.stu.WebBlogNauAn.dto.AccountDocumentDto;

@Service
@RequiredArgsConstructor
public class AccountSearchService {
        private final ElasticsearchClient elasticsearchClient;
        public static final Logger logger = LoggerFactory.getLogger(AccountSearchService.class);

        // should giúp tăng điểm cho kết quả khớp.
        // .boost(2.0f) ưu tiên username.folded gấp đôi
        public List<Long> searchAccountByUsername(String username) throws IOException {
                SearchRequest request = new SearchRequest.Builder()
                                .index("registered")
                                .query(q -> q
                                                .bool(b -> b
                                                                .should(s1 -> s1
                                                                                .match(m -> m
                                                                                                .field("username.folded")
                                                                                                .query(username)
                                                                                                .boost(2.0f)))
                                                                .should(s2 -> s2
                                                                                .match(m -> m
                                                                                                .field("username.autocomplete")
                                                                                                .query(username)
                                                                                                .boost(1.0f)))))

                                .build();
                SearchResponse<AccountDocumentDto> response = elasticsearchClient.search(request,
                                AccountDocumentDto.class);
                return response.hits().hits().stream()
                                .map(Hit::source)
                                .filter(Objects::nonNull)
                                .map(AccountDocumentDto::getId)
                                .collect(Collectors.toList());
        }
}
