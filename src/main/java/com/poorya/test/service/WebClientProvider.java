package com.poorya.test.service;

import com.poorya.test.config.ApplicationProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.time.Duration;

@EnableConfigurationProperties(ApplicationProperties.class)
@Component
public class WebClientProvider {

    private final ApplicationProperties properties;

    public WebClientProvider(ApplicationProperties properties) {
        this.properties = properties;
    }

    public <C> WebClient getWebClient(String basePath, Class<C> invoker) {
        return getWebClient(basePath, null, null, invoker);
    }

    public <C> WebClient getWebClient(String basePath, String username, String password, Class<C> invoker) {
        WebClient.Builder webClientBuilder = WebClient.builder();

        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            webClientBuilder.defaultHeaders(header -> header.setBasicAuth(username, password));
        }

        return webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(buildHttpClient(invoker)))
                .baseUrl(basePath)
                .build();
    }

    private <C> HttpClient buildHttpClient(Class<C> invoker) {
        return HttpClient.create(ConnectionProvider.builder("testing")
                        .maxConnections(1000)
                        .build())
                .wiretap(invoker.getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
                .responseTimeout(Duration.ofMillis(properties.getResponseTimeout()))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectionTimeout())
                .resolver(DefaultAddressResolverGroup.INSTANCE);
//                .resolver(nameResolverSpec -> new DnsAddressResolverGroup(
//                                new DnsNameResolverBuilder(
//                                        new NioEventLoopGroup().next())
//                                        .channelType(NioDatagramChannel.class)
//                                        .nameServerProvider(new SingletonDnsServerAddressStreamProvider(
//                                                new InetSocketAddress("10.254.5.27", 53)))
//                        )
//                );
    }
}
