package ome.smuggler.config.wiring;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import ome.smuggler.web.RequestLoggingFilter;
import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import io.undertow.UndertowOptions;
import ome.smuggler.config.items.UndertowConfig;
import util.config.ConfigProvider;


/**
 * Additional Spring bean wiring and configuration for the web app.
 */
@Configuration
public class WebWiring extends WebMvcConfigurerAdapter {
    
    private void setStringConverterMediaTypes(HttpMessageConverter<?> x) {
        StringHttpMessageConverter converter = (StringHttpMessageConverter) x;
        List<MediaType> mediaTypes = Arrays.asList(
                new MediaType("text", "plain", StandardCharsets.UTF_8), 
                MediaType.ALL);
        converter.setSupportedMediaTypes(mediaTypes);
    }
    
    @Override 
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                  .filter(x -> x instanceof StringHttpMessageConverter)
                  .forEach(this::setStringConverterMediaTypes);
    }
    /* NB this overrides the default string converter which uses ISO-8859-1 as
     * default character encoding. The default encoding is what this converter
     * uses if the request doesn't specify one.
     * So with this set up, we can produce response strings defaulted to UTF-8
     * without having to specify UTF-8 as charset every time. 
     */
    /* NOTE. The method configureMessageConverters gives you an empty list to
     * which to add your converters; this list will be used in place of the
     * default Spring converters.
     */
    
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer cfg) {
        cfg.favorParameter(false)      // disable 'resource?format=xxx'
           .favorPathExtension(false)  // disable 'resource.xxx'
           .ignoreAcceptHeader(false)  // use Accept header
           .defaultContentType(MediaType.TEXT_PLAIN);  // (*)
    }
    /* (*) this way methods that return a string need to do nothing and the
     * client doesn't need to specify an accept header either.
     */
    
    @Bean
    public UndertowEmbeddedServletContainerFactory 
                embeddedServletContainerFactory(
                        ConfigProvider<UndertowConfig> cfg) {
        
        int port = cfg.defaultReadConfig().findFirst().get().getPort();
        
        UndertowEmbeddedServletContainerFactory factory = 
                new UndertowEmbeddedServletContainerFactory(port);
        
        factory.addBuilderCustomizers((UndertowBuilderCustomizer) builder -> {  // (*)
            builder.setServerOption(UndertowOptions.DECODE_URL, true);
            builder.setServerOption(UndertowOptions.URL_CHARSET,
                                    StandardCharsets.UTF_8.name());
        });
        
        factory.addDeploymentInfoCustomizers(
                (UndertowDeploymentInfoCustomizer) deployment -> {  // (*)
            deployment.setDefaultEncoding(StandardCharsets.UTF_8.name());
        });
        
        return factory;
    }
    /* (*) objects are passed in after Spring Boot has set most of the values, 
     * which allows us to add to or override the settings without having to 
     * redo the entire server configuration.
     */

    @Bean
    public RequestLoggingFilter requestLoggingFilter() {
        return new RequestLoggingFilter();
    }

}
