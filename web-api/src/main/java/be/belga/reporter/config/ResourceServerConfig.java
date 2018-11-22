package be.belga.reporter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @author Rohit.Kumar
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceServerConfiguration.class);

    @Value("${config.oauth2.publicKey}")
    private String publicKey;

    @Value("${config.oauth2.privateKey}")
    private String privateKey;

    @Value("${config.oauth2.resource.id}")
    private String resourceId;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
        .anonymous().disable()
        .authorizeRequests()
                // .antMatchers("/fileupload/**").authenticated()
                // .antMatchers("/metadata/**").authenticated()
                // .antMatchers("/post/**").authenticated()
                // .antMatchers("/user/**").authenticated()
        .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers("/oauth/**").authenticated();

    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(resourceId).tokenServices(tokenServices()).tokenStore(tokenStore());
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        return defaultTokenServices;
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {

        LOGGER.info("Initializing JWT with public key: " + publicKey);

        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(privateKey);

        return converter;
    }

    @Bean
    public JwtTokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

}