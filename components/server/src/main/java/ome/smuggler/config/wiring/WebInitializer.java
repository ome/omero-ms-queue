package ome.smuggler.config.wiring;

import static util.sequence.Arrayz.array;

import javax.servlet.Filter;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import util.servlet.http.CharEncodingFilter;

/**
 * Spring MVC bootstrap configuration.
 */
public class WebInitializer 
    extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return array(Wiring.class);
    }
    
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return array(WebWiring.class);
    }

    @Override
    protected String[] getServletMappings() {
        return array("/");
    }
    
    @Override
    protected Filter[] getServletFilters() {
        return array(CharEncodingFilter.Utf8Request(), 
                     CharEncodingFilter.Utf8Response());
    }

}
