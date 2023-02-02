package com.melonproject.exchangeclient.annotation;

import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.annotation.ReflectiveProcessor;

import java.lang.reflect.AnnotatedElement;

/**
 * ReflectiveProcessor implementation for ExchangeClient.
 *
 * @author Albert Sikorski
 */
class ExchangeClientReflectiveProcessor implements ReflectiveProcessor {
    private final BindingReflectionHintsRegistrar bindingRegistrar = new BindingReflectionHintsRegistrar();

    @Override
    public void registerReflectionHints(ReflectionHints hints, AnnotatedElement element) {
        hints.registerType(element.getClass());
    }
}
