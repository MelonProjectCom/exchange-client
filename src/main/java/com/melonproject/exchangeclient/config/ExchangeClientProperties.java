package com.melonproject.exchangeclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "exchange.client")
public class ExchangeClientProperties {

    public static class ClientConfig{
        private String url;
        private String configClass;

        private Map<String, String> defaultHeaders = new HashMap<>();

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getConfigClass() {
            return configClass;
        }

        public void setConfigClass(String configClass) {
            this.configClass = configClass;
        }

        public Map<String, String> getDefaultHeaders() {
            return defaultHeaders;
        }

        public void setDefaultHeaders(Map<String, String> defaultHeaders) {
            this.defaultHeaders = defaultHeaders;
        }
    }


    private Map<String, ClientConfig> config = new HashMap<>();

    public Map<String, ClientConfig> getConfig() {
        return config;
    }

    public void setConfig(Map<String, ClientConfig> config) {
        this.config = config;
    }
}
