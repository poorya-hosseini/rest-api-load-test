package com.poorya.test.service;

import com.poorya.test.util.SeptaFunction;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Component
public class RestApiInvoker<B, R> {

    public Mono<R> call(
            HttpMethod httpMethod,
            String uri,
            B body,
            Map<String, String> pathVariables,
            Map<String, String> requestParameters,
            Map<String, String> requestHeaders,
            WebClient webClient,
            int callNumber) {

        return getMethodMapping(httpMethod)
                .apply(webClient, uri, body, pathVariables, requestParameters, requestHeaders, callNumber);
    }

    public SeptaFunction<WebClient, String, B, Map<String, String>, Map<String, String>, Map<String, String>, Integer, Mono<R>> getMethodMapping(HttpMethod httpMethod) {
        if (HttpMethod.GET.equals(httpMethod)) {
            return this::get;
        } else if (HttpMethod.POST.equals(httpMethod)) {
            return this::post;
        } else {
            throw new IllegalArgumentException("Not support " + httpMethod + "!");
        }
    }

    private Mono<R> get(
            WebClient webClient, String uri,
            B body,
            Map<String, String> pathVariables,
            Map<String, String> requestParameters,
            Map<String, String> requestHeaders,
            int callingNumber) {

        return webClient
                .get()
                .uri(uriBuilder -> createUri(uriBuilder, uri, requestParameters, pathVariables))
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(Object.class)
                .map(o -> (R) o)
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed callingNumber: " + callingNumber + ". Reason: " + e.getMessage())));
    }

    private Mono<R> post(
            WebClient webClient,
            String uri,
            B body,
            Map<String, String> requestParameters,
            Map<String, String> pathVariables,
            Map<String, String> requestHeaders,
            int callingNumber) {

        return webClient
                .post()
                .uri(uriBuilder -> createUri(uriBuilder, uri, requestParameters, pathVariables))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .bodyValue(body)
                .headers(httpHeaders -> Optional.ofNullable(requestHeaders)
                        .ifPresent(httpHeaders::setAll))
                .retrieve()
                .bodyToMono(Object.class)
                .map(o -> (R) o)
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed callingNumber: " + callingNumber + ". Reason: " + e.getMessage())));
    }

    private URI createUri(
            UriBuilder uriBuilder,
            String uri,
            Map<String, String> requestParameters,
            Map<String, String> pathVariables) {

        UriBuilder parameterBuilder = uriBuilder.path(uri);

        Optional.ofNullable(requestParameters)
                .map(Map::entrySet)
                .orElseGet(Collections::emptySet)
                .forEach(entry -> parameterBuilder.queryParam(entry.getKey(), entry.getKey()));

        return Optional.ofNullable(pathVariables)
                .map(parameterBuilder::build)
                .orElseGet(parameterBuilder::build);
    }
}
