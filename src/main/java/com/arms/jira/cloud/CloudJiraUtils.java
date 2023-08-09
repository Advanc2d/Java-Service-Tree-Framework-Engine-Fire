package com.arms.jira.cloud;

import java.util.Base64;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class CloudJiraUtils {

    public static WebClient createJiraWebClient(String uri, String email, String apiToken) {

        return WebClient.builder()
                .baseUrl(uri)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "Basic " + getBase64Credentials(email, apiToken))
                .build();
    }

    private static String getBase64Credentials(String jiraID, String jiraPass) {

        String credentials = jiraID + ":" + jiraPass;
        return new String(Base64.getEncoder().encode(credentials.getBytes()));
    }

    public static <T> Mono<T> get(WebClient webClient, String uri, Class<T> responseType) {

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(responseType);
    }

    public static <T> Mono<T> post(WebClient webClient, String uri, Object requestBody, Class<T> responseType) {

        return webClient.post()
                .uri(uri)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(responseType);
    }

    public static <T> Mono<T> put(WebClient webClient, String uri, Object requestBody, Class<T> responseType) {

        return webClient.put()
                .uri(uri)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(responseType);
    }

    public static <T> Mono<T> delete(WebClient webClient, String uri, Class<T> responseType) {

        return webClient.delete()
                .uri(uri)
                .retrieve()
                .bodyToMono(responseType);
    }

    public static Optional<Boolean> executePost(WebClient webClient, String uri, Object requestBody) {

        Mono<ResponseEntity<Void>> response = webClient.post()
                                                .uri(uri)
                                                .body(BodyInserters.fromValue(requestBody))
                                                .retrieve()
                                                .toEntity(Void.class);

        return response.map(entity -> entity.getStatusCode() == HttpStatus.NO_CONTENT) // 결과가 204인가 확인
                .blockOptional();
    }

    public static Optional<Boolean> executePut(WebClient webClient, String uri, Object requestBody) {

        Mono<ResponseEntity<Void>> response = webClient.put()
                                                .uri(uri)
                                                .body(BodyInserters.fromValue(requestBody))
                                                .retrieve()
                                                .toEntity(Void.class);

        return response.map(entity -> entity.getStatusCode() == HttpStatus.NO_CONTENT) // 결과가 204인가 확인
                .blockOptional();
    }

    public static Optional<Boolean> executeDelete(WebClient webClient, String uri) {

        Mono<ResponseEntity<Void>> response = webClient.delete()
                                                .uri(uri)
                                                .retrieve()
                                                .toEntity(Void.class);

        return response.map(entity -> entity.getStatusCode() == HttpStatus.NO_CONTENT) // 결과가 204인가 확인
                .blockOptional();
    }
}
