package ome.smuggler.config.wiring;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import ome.smuggler.web.RequestLoggingFilter;
import org.springframework.boot.autoconfigure.web.HttpEncodingProperties;
import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    @ConfigurationProperties(prefix = "spring.http.encoding")
    @Primary  // override built-in auto config bean
    public HttpEncodingProperties httpEncodingProperties() { // (*)
        HttpEncodingProperties props = new HttpEncodingProperties();
        props.setCharset(StandardCharsets.UTF_8);
        props.setForce(false);
        props.setForceRequest(false);
        props.setForceResponse(true);

        return props;
    }
    /* (*) Handling of HTTP encoding.
     * With these settings:
     *   - if an HTTP request has no char encoding, we use UTF-8;
     *   - all HTTP responses will have a char encoding of UTF-8.
     * To see why, look at these classes
     * - org.springframework.web.filter.CharacterEncodingFilter
     * - org.springframework.boot.autoconfigure.web.HttpEncodingAutoConfiguration
     *
     * Not what I wanted, but close enough. In fact, I think a better solution
     * is that documented in
     * - util.servlet.http.CharEncodingFilter
     * but adding my own filters didn't work:
     *
     *     @Bean
     *     public Filter utf8RequestFilter() {
     *         return CharEncodingFilter.Utf8Request();
     *     }
     *     @Bean
     *     public Filter utf8ResponseFilter() {
     *         return CharEncodingFilter.Utf8Response();
     *     }
     *
     * Not sure why (maybe cos of the Spring filter ordering?) but I'm not
     * gonna lose any more sleep on this. Like I said, for the moment, this
     * brutal handling of HTTP encoding will do cos:
     *
     * 1. All our requests expect JSON (which has to be UTF-8 encoded) or no
     * content (DELETE methods).
     * 2. Our responses contain JSON (so UTF-8 again), plain text (download),
     * or no body (DELETE).
     *
     * Now in the case of plain text, we end up with a
     *
     *     Content-Type: text/plain; charset=UTF-8
     *
     * which is what we want. (Without the above config, Spring doesn't append
     * the charset.) In the case of JSON, well, like I said it's supposed to
     * be UTF-8, so no harm done. But note even without the above config,
     * Spring would output
     *
     *     Content-Type: application/json;charset=UTF-8
     *
     * which I think it's wrong cos 'application/json' should have no charset
     * IIRC.
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
