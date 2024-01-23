package com.poorya.test.usecase;

import com.poorya.test.model.TestingModel;
import com.poorya.test.service.RestApiInvoker;
import com.poorya.test.service.WebClientProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.stream.IntStream;

@Component
public class RestApiLoadTestUseCase<B, R> {


    private final RestApiInvoker<B, R> restApiInvoker;
    private final WebClientProvider webClientProvider;


    public RestApiLoadTestUseCase(RestApiInvoker<B, R> restApiInvoker, WebClientProvider webClientProvider) {
        this.restApiInvoker = restApiInvoker;
        this.webClientProvider = webClientProvider;
    }

    public Flux<R> invoke(TestingModel<B> testingModel) {
        WebClient webClient = webClientProvider.getWebClient(testingModel.getBaseUrl(), this.getClass());

        return Flux.fromStream(IntStream.range(0, Math.max(1, testingModel.getNumberOfConcurrentCall()))
                        .parallel()
                        .boxed()
                        .map(callingNumber -> restApiInvoker.call(
                                testingModel.getHttpMethod(),
                                testingModel.getUri(),
                                testingModel.getBody(),
                                testingModel.getPathVariables(),
                                testingModel.getRequestParameters(),
                                testingModel.getRequestHeaders(),
                                webClient,
                                callingNumber)))
                .flatMap(mono -> mono);
    }

}
