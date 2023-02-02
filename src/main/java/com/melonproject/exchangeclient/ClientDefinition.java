package com.melonproject.exchangeclient;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

public record ClientDefinition(String url,
                               Set<ExchangeFilterFunction> filters,
                               HttpHeaders headers,
                               WebClient.Builder customClientBuilder) {
}
