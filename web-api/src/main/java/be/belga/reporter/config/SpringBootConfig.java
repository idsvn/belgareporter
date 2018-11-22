package be.belga.reporter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("deprecation")
@Configuration
@ComponentScan(basePackages = { "be.belga.reporter" })
public class SpringBootConfig extends WebMvcConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SpringBootConfig.class);

    @Value("${app.path.data}")
    String dataPathString;

    @Bean
    public MessageSource messageSource() {

        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(new String[] { "classpath:messages" });
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean("dataPath")
    public Path dataPath() {
        logger.debug("Data path: " + dataPathString);
        return Paths.get(dataPathString);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/data/**").addResourceLocations("file:///" + dataPathString);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
