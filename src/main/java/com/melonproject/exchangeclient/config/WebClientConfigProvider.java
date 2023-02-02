package com.melonproject.exchangeclient.config;

import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.Collections;

public interface WebClientConfigProvider {

    default WebClient.Builder clientBuilder(String name, String baseUrl){
        return null;
    }

    default Collection<ExchangeFilterFunction> clientFilters(){
        return Collections.emptyList();
    }
}
