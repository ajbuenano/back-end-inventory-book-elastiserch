package com.unir.elastiserch.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.unir.elastiserch.data")
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String clusterEndpoint;

    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        String hostname = clusterEndpoint.replace("https://", "").split(":")[0]; // Extraer hostname
        int port = 443;

        return new ElasticsearchRestTemplate(
                new RestHighLevelClient(
                        RestClient.builder(new HttpHost(hostname, port, "https"))
                                .setHttpClientConfigCallback(httpClientBuilder ->
                                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                                )
                )
        );
    }
}
