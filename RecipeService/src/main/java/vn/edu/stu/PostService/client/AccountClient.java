package vn.edu.stu.PostService.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.PostService.client.clientResponse.AccountResponse;
import vn.edu.stu.PostService.dto.CommenterDto;

@Service
@RequiredArgsConstructor
public class AccountClient {
    private final WebClient.Builder clientBuilder;

    public List<AccountResponse> getAccountsByIds(List<Long> accountIds, String accessToken) {
        return clientBuilder.build()
                .post()
                .uri("lb://account-service/api/accounts")
                .bodyValue(accountIds)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AccountResponse>>() {
                })
                .block();
    }

    public List<CommenterDto> getCommentersDetail(List<Long> accountIds, String token) {
        return clientBuilder.build()
                .post()
                .uri("lb://account-service/api/accounts/commenters")
                .bodyValue(accountIds)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CommenterDto>>() {
                })
                .block();
    }
}
