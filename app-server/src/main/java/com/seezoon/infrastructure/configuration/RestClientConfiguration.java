package com.seezoon.infrastructure.configuration;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfiguration {

    private static final Timeout CONNECT_TIMEOUT = Timeout.ofSeconds(3);
    private static final Timeout SOCKET_TIMEOUT = Timeout.ofSeconds(10);

    /**
     * 创建信任所有证书的 TLS 策略
     * <p>
     * 使用示例：
     * <pre>
     *     PoolingHttpClientConnectionManager connectionManager =
     *         PoolingHttpClientConnectionManagerBuilder.create()
     *             .setTlsSocketStrategy(createTrustAllTlsStrategy())
     *             .build();
     * </pre>
     *
     * @return TlsSocketStrategy
     */
    private TlsSocketStrategy createTrustAllTlsStrategy()
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        // 信任所有证书
        final SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, (cert, authType) -> true)
                .build();

        return new DefaultClientTlsStrategy(sslContext, NoopHostnameVerifier.INSTANCE);
    }

    @Bean
    public RestClientCustomizer customizer() {

        return restClientBuilder -> {
            PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setMaxConnTotal(2000) // 最大连接数
                    .setMaxConnPerRoute(2000) // 每个路由的默认最大连接数
                    .setDefaultConnectionConfig(ConnectionConfig.custom()
                            .setConnectTimeout(CONNECT_TIMEOUT)
                            .setSocketTimeout(SOCKET_TIMEOUT)
                            .setTimeToLive(Timeout.ofMinutes(10))
                            .setValidateAfterInactivity(Timeout.ofSeconds(60 * 3)) // 60 * 3s未使用的连接，需要先检测
                            .build())
                    .setDefaultSocketConfig(
                            SocketConfig.custom().setSoKeepAlive(true).setSoTimeout(SOCKET_TIMEOUT).build())
                    .setDefaultTlsConfig(TlsConfig.DEFAULT)
                    .build();

            HttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectionRequestTimeout(Timeout.ofSeconds(6)) // 设置从连接池获取连接的超时时间（毫秒）
                            .build())
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            restClientBuilder.requestFactory(requestFactory);
        };
    }


    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        // 适配微信
        builder.configureMessageConverters((httpMessageConverters) -> {
            httpMessageConverters.addCustomConverter(new TextPlainMappingJackson2HttpMessageConverter());
        });
        // 默认会采用分块，Transfer-Encoding: chunked编码，微信后台比较挫不支持
        builder.requestInterceptor((request, body, execution) -> {
            request.getHeaders().setContentLength(body.length);
            return execution.execute(request, body);
        });
        return builder.build();
    }

    /**
     * 微信接口返回contentType 不规范
     */
    public static class TextPlainMappingJackson2HttpMessageConverter extends JacksonJsonHttpMessageConverter {

        public TextPlainMappingJackson2HttpMessageConverter() {
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.TEXT_PLAIN);
            setSupportedMediaTypes(mediaTypes);
        }
    }
}


