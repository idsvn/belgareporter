package be.belga.reporter.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan
public class ReporterWebMvcConfig implements WebMvcConfigurer {

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {"/static/"};
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
    	/*registry.addViewController("/");*/
    	registry.addRedirectViewController("/", "/dashboard");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS).setCachePeriod(31556926);
    }
    

}
