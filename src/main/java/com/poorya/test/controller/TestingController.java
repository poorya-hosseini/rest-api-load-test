package com.poorya.test.controller;

import com.poorya.test.model.TestingModel;
import com.poorya.test.usecase.RestApiLoadTestUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
public class TestingController<B, R> {

    private final RestApiLoadTestUseCase<B, R> restApiLoadTestUseCase;

    public TestingController(RestApiLoadTestUseCase<B, R> restApiLoadTestUseCase) {
        this.restApiLoadTestUseCase = restApiLoadTestUseCase;
    }

    @PostMapping("/")
    public Flux<R> test(@RequestBody TestingModel<B> testingModel) {
        return restApiLoadTestUseCase.invoke(testingModel);
    }

}
