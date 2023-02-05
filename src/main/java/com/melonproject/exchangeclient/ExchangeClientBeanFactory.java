package com.melonproject.exchangeclient;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.melonproject.exchangeclient.annotation.EnableExchangeClients;
import com.melonproject.exchangeclient.annotation.ExchangeClient;
import com.melonproject.exchangeclient.config.ExchangeClientProperties;
import com.melonproject.exchangeclient.utils.ClientConfigUtil;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.*;

@Configuration
public class ExchangeClientBeanFactory {

    private final static Logger logger = LoggerFactory.getLogger(ExchangeClientBeanFactory.class);

    private final static String BEAN_NAME_TEMPLATE = "%s_exchange_client";

    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor(ApplicationContext applicationContext) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(Reflections.class).setLevel(Level.WARN);
        return factory -> {
            Binder binder = Binder.get(applicationContext.getEnvironment());
            ExchangeClientProperties exchangeClientProperties = binder.bind("exchange.client", ExchangeClientProperties.class).get();
            registerClients(applicationContext, factory, exchangeClientProperties);

        };
    }

    private static void registerClients(ApplicationContext applicationContext, ConfigurableBeanFactory factory,
                                        ExchangeClientProperties properties) {
        Set<String> packages = getPackages(applicationContext);

        packages.forEach(basePackage -> {
                    Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(basePackage));
                    Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(ExchangeClient.class);
                    logger.trace("Found Annotated classes: {}", typesAnnotatedWith);
                    registerExchangeClients(typesAnnotatedWith, applicationContext, factory, properties);
                }
        );
    }

    private static void registerExchangeClients(Set<Class<?>> typesAnnotatedWith, ApplicationContext applicationContext, ConfigurableBeanFactory configurableBeanFactory,
                                                ExchangeClientProperties exchangeClientProperties) {

        typesAnnotatedWith.forEach(aClass -> {
            ExchangeClient annotationDetails = aClass.getAnnotation(ExchangeClient.class);
            AnnotationClientProperties annotationClientProperties = new AnnotationClientProperties(annotationDetails.name(), annotationDetails.url(), annotationDetails.configClass());

            ClientDefinition clientDefinition = ClientConfigUtil.getClientDefinition(annotationClientProperties, exchangeClientProperties, applicationContext);

            String beanName = String.format(BEAN_NAME_TEMPLATE, aClass.getSimpleName().toLowerCase(Locale.ROOT));
            configurableBeanFactory.registerSingleton(beanName, createClientFor(aClass, clientDefinition));
        });

    }

    private static Object createClientFor(Class<?> clazz, ClientDefinition clientDefinition){
        WebClient.Builder clientBuilder = clientDefinition.customClientBuilder();

        if(clientBuilder == null){
            clientBuilder = WebClient.builder()
                    .baseUrl(clientDefinition.url());
        }
        clientBuilder.filters(exchangeFilterFunctions -> exchangeFilterFunctions.addAll(clientDefinition.filters()))
                .defaultHeaders(httpHeaders -> httpHeaders.addAll(clientDefinition.headers()));

        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory.builder(WebClientAdapter.forClient(clientBuilder.build())).build();
        return factory.createClient(clazz);
    }

    private static Set<String> getPackages(ApplicationContext applicationContext){
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(EnableExchangeClients.class);

        List<String[]> packageList = beansWithAnnotation.values().stream()
                .map(bean -> {
                    Class<?> userClass = ClassUtils.getUserClass(bean.getClass());
                    EnableExchangeClients annotation = userClass.getAnnotation(EnableExchangeClients.class);
                    String[] basePackages = annotation.basePackages();
                    if(basePackages.length == 0){
                        basePackages = new String[]{userClass.getPackage().getName()};
                    }
                    return basePackages;
                }).toList();

        Set<String> packages = new HashSet<>();
        packageList.forEach(p -> packages.addAll(Arrays.asList(p)));
        return packages;
    }
}
