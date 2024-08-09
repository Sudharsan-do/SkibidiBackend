package com.services;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.models.GraphQLRequest;

@Service
public class GraphQLService {
	
	private final WebClient webClient;

    public GraphQLService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://leetcode.com/graphql").build();
    }

    public String sendGraphQLRequest(String query, Map<String, Object> variables) {
        GraphQLRequest request = new GraphQLRequest();
        request.setQuery(query);
        if(variables!=null) request.setVariables(variables);

        return this.webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
	
}
