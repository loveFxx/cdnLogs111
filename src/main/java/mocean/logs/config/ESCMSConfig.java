package mocean.logs.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mocean.logs.common.Common;

@Configuration
public class ESCMSConfig {

    @Value("${spring.elasticsearch.ip}")
    private String ipAddress;

    @Value("${spring.elasticsearch.user}")
    private String user;

    @Value("${spring.elasticsearch.password}")
    private String password;

    private static int connectTimeOut = 1000;
    private static int socketTimeOut = 30000;
    private static int connectionRequestTimeOut = 500;

    private static int maxConnectNum = 100;
    private static int maxConnectPerRoute = 100;

    @Bean
    public RestClientBuilder restClientBuilder() {
        HttpHost host = makeHttpHost(ipAddress);
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));


        return RestClient.builder(host).setHttpClientConfigCallback(new HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                httpClientBuilder.setMaxConnTotal(maxConnectNum);
                httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
        });
    }

    @Bean(name = "restClient")
    public RestHighLevelClient highLevelClient(@Autowired RestClientBuilder restClientBuilder) {
        restClientBuilder.setMaxRetryTimeoutMillis(180000);
        restClientBuilder.setRequestConfigCallback(new RequestConfigCallback() {
            @Override
            public Builder customizeRequestConfig(Builder requestConfigBuilder) {
                requestConfigBuilder.setConnectTimeout(connectTimeOut);
                requestConfigBuilder.setSocketTimeout(socketTimeOut);
                requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeOut);
                return requestConfigBuilder;
            }
        });
        return new RestHighLevelClient(restClientBuilder);
    }

    private HttpHost makeHttpHost(String s) {
        assert StringUtils.isNotEmpty(s);
        String[] address = s.split(":");
        if (address.length == Common.ADDRESS_LENGTH) {
            String ip = address[0];
            int port = Integer.parseInt(address[1]);
            return new HttpHost(ip, port, Common.HTTP_SCHEME);
        } else {
            return null;
        }
    }

}
