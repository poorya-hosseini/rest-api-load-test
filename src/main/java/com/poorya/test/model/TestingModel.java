package com.poorya.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestingModel<B> {
    private int numberOfConcurrentCall;
    private String baseUrl;
    private String uri;
    private HttpMethod httpMethod;
    private B body;
    private Map<String, String> pathVariables;
    private Map<String, String> requestParameters;
    private Map<String, String> requestHeaders;
}
