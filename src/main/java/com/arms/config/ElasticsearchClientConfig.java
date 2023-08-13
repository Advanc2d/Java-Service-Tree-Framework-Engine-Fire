/**
 * 
 */
package com.arms.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author Pratik Das
 *
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.arms.elasticsearch.repositories")
@ComponentScan(basePackages = { "com.arms.elasticsearch" })
public class ElasticsearchClientConfig extends AbstractElasticsearchConfiguration {

	@Override
	@Bean
	public RestHighLevelClient elasticsearchClient() {

		final ClientConfiguration clientConfiguration = 
				ClientConfiguration
				.builder()
				.connectedTo("313.co.kr:9200")
				.withConnectTimeout(30000)
				.withSocketTimeout(30000)
				.build();

		return RestClients
				.create(clientConfiguration)
				.rest();
	}
	

}
