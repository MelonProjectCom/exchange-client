package com.melonproject.exchangeclient;

import com.melonproject.exchangeclient.config.ExchangeClientProperties;
import com.melonproject.exchangeclient.config.NoopConfig;
import com.melonproject.exchangeclient.config.WebClientConfigProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;


@AutoConfiguration
public class ExchangeClientAutoConfiguration {

    @Bean
    public ExchangeClientProperties exchangeClientProperties(){
        return new ExchangeClientProperties();
    }

    @Bean
    public WebClientConfigProvider noopConfig(){
        return new NoopConfig();
    }
}
