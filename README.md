# exchange-client

## Idea
Simplify the process of creating web clients based on the interface of an external service. 

This project allows you to build clients without creating additional configurations and beans

## Examples

### Annotation based client
```java
@ExchangeClient(name = "testClient", url = "http://localhost")
public interface TestClient {

    @GetExchange("/test")
    String test();
}
```

### Annotation and properties
```java
@ExchangeClient(name = "testClient")
public interface TestClient {

    @GetExchange("/test")
    String test();
}
```
```yaml
exchange.client.config:
  testClient:
    url: http://localhost
```


### Customised client
Interface: 
```java
@ExchangeClient(name = "testClient", url = "http://localhost", configClass = TestClientConfig.class)
public interface TestClient {

    @GetExchange("/test")
    String test();
}
```

Configuration class;
```java
@Configuration
public class TestClientConfig implements WebClientConfigProvider {

    @Override
    public WebClient.Builder clientBuilder(String name, String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl);
    }

    @Override
    public Collection<ExchangeFilterFunction> clientFilters() {
        return List.of((request, next) -> {
            System.out.println("Custom filter");
            return next.exchange(request);
        });
    }
}
```

# Project is still under development and release version will be available soon