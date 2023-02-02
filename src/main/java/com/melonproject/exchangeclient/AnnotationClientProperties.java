package com.melonproject.exchangeclient;

import com.melonproject.exchangeclient.config.WebClientConfigProvider;

public record AnnotationClientProperties(String name, String url, Class<? extends WebClientConfigProvider> configClass) {
}
