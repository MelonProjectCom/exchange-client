package com.melonproject.exchangeclient.annotation;

import com.melonproject.exchangeclient.config.WebClientConfigProvider;
import com.melonproject.exchangeclient.config.NoopConfig;
import org.springframework.aot.hint.annotation.Reflective;

import java.lang.annotation.*;

/**
 * Used to register new Exchange Client
 *
 * @author Albert Sikorski
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Reflective({ExchangeClientReflectiveProcessor.class})

public @interface ExchangeClient {

    String name() ;

    String url() default "";

    Class<? extends WebClientConfigProvider> configClass() default NoopConfig.class;
}
