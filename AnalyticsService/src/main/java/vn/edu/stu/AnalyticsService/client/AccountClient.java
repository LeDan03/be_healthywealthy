package vn.edu.stu.AnalyticsService.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import vn.edu.stu.common_dto.dto.AccountResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountClient {

    private final WebClient.Builder clientBuilder;

    public List<AccountResponse> getAccountDetail(List<Long> ids, String accessToken) {
        return clientBuilder.build()
                .post()
                .uri("lb://account-service/api/accounts")
                .bodyValue(ids)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-type", MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AccountResponse>>() {})
                .block();
    }

}
