package vn.edu.stu.WebBlogNauAn.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import lombok.RequiredArgsConstructor;
import vn.edu.stu.WebBlogNauAn.dto.AccountDocumentDto;
import vn.edu.stu.WebBlogNauAn.model.Account;

@Service
@RequiredArgsConstructor
public class AccountIndexerService {
    private final ElasticsearchClient elasticsearchClient;

    public void indexAccount(Account account) throws IOException {
        AccountDocumentDto dto = AccountDocumentDto.builder()
                .id(account.getId())
                .username(account.getUsername())
                .build();

        IndexRequest<AccountDocumentDto> request = new IndexRequest.Builder<AccountDocumentDto>()
                .index("registered")
                .id(String.valueOf(account.getId()))
                .document(dto)
                .refresh(Refresh.True)
                .build();

        elasticsearchClient.index(request);
    }
}
