package com.melonproject.exchangeclient.utils;

import com.melonproject.exchangeclient.AnnotationClientProperties;
import com.melonproject.exchangeclient.ClientDefinition;
import com.melonproject.exchangeclient.config.NoopConfig;
import com.melonproject.exchangeclient.config.ExchangeClientProperties;
import com.melonproject.exchangeclient.config.WebClientConfigProvider;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;

public class ClientConfigUtil {
    private final static Logger logger = LoggerFactory.getLogger(ClientConfigUtil.class);

    public static ClientDefinition getClientDefinition(
            AnnotationClientProperties annotationClientProperties,
            ExchangeClientProperties exchangeClientProperties,
            ApplicationContext applicationContext
    ){

        String clientName = annotationClientProperties.name();
        String baseUrl = getUrl(annotationClientProperties, exchangeClientProperties);
        WebClientConfigProvider webClientConfigProvider = null;
        if(!annotationClientProperties.configClass().isAssignableFrom(NoopConfig.class)){
            webClientConfigProvider = applicationContext.getBean(annotationClientProperties.configClass());
        }


        return new ClientDefinition(
                baseUrl,
                getFilters(webClientConfigProvider),
                getHeaders(clientName, exchangeClientProperties),
                getWebClientBuilder(clientName, baseUrl,webClientConfigProvider, applicationContext));
    }

    private static Set<ExchangeFilterFunction> getFilters(WebClientConfigProvider webClientConfigProvider) {
        if(webClientConfigProvider != null){
            Collection<ExchangeFilterFunction> customFilters = webClientConfigProvider.clientFilters();
            logger.debug("Found: {} custom filters", customFilters.size());
            return Set.copyOf(customFilters);
        }
        return Collections.emptySet();
    }


    private static String getUrl(AnnotationClientProperties annotationClientProperties, ExchangeClientProperties exchangeClientProperties) {
        String url = annotationClientProperties.url();
        ExchangeClientProperties.ClientConfig clientConfig = exchangeClientProperties.getConfig().get(annotationClientProperties.name());
        if(clientConfig != null && StringUtils.isBlank(url)){
            url = clientConfig.getUrl();
        }

        if(StringUtils.isBlank(url)){
            throw new IllegalArgumentException("Invalid URL for exchange client: " + annotationClientProperties.name());
        }
        return url;
    }

    private static HttpHeaders getHeaders(String clientName, ExchangeClientProperties exchangeClientProperties) {
        HttpHeaders headers = new HttpHeaders();
        ExchangeClientProperties.ClientConfig clientConfig = exchangeClientProperties.getConfig().get(clientName);
        if(clientConfig != null){
            clientConfig.getDefaultHeaders().forEach(headers::add);
        }
        return headers;
    }

    private static WebClient.Builder getWebClientBuilder(String name,
                                                         String baseUrl,
                                                         WebClientConfigProvider webClientConfigProvider,
                                                         ApplicationContext applicationContext) {
        WebClient.Builder webClientBuilder = null;
        if(webClientConfigProvider != null){
            webClientBuilder = webClientConfigProvider.clientBuilder(name, baseUrl);
        }
        return webClientBuilder;
    }
}
