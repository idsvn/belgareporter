//package be.belga.reporter.config;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
//import org.springframework.security.crypto.password.StandardPasswordEncoder;
//import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
//
///**
// * @author Rohit.Kumar
// */
//@Configuration
//@EnableAuthorizationServer
//public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationServerConfig.class);
//
//    @Autowired
//    @Qualifier("userDetailsService")
//    private UserDetailsService userDetailsService;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Value("${config.oauth2.tokenTimeout}")
//    private int expiration;
//
//    @Value("${config.oauth2.privateKey}")
//    private String privateKey;
//
//    @Value("${config.oauth2.publicKey}")
//    private String publicKey;
//
//    @Autowired
//    private ClientDetailsService clientDetailsService;
//
//    @SuppressWarnings({ "deprecation", "unchecked" })
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        String idForEncode = "bcrypt";
//        Map encoders = new HashMap<>();
//        encoders.put(idForEncode, new BCryptPasswordEncoder());
//        encoders.put(null, NoOpPasswordEncoder.getInstance());
//        encoders.put("noop", NoOpPasswordEncoder.getInstance());
//        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
//        encoders.put("scrypt", new SCryptPasswordEncoder());
//        encoders.put("sha256", new StandardPasswordEncoder());
//
//        return new DelegatingPasswordEncoder(idForEncode, encoders);
//        // return new BCryptPasswordEncoder();
//    }
//
//    @Override
//    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.inMemory().withClient("client").authorizedGrantTypes("client_credentials", "password", "refresh_token", "authorization_code")
//                .scopes("read", "write").resourceIds("oauth2-resource").accessTokenValiditySeconds(expiration).refreshTokenValiditySeconds(expiration)
//                .secret("secret");
//
//    }
//
//    @Bean
//    public JwtAccessTokenConverter accessTokenConverter() {
//
//        LOGGER.info("Initializing JWT with public key: " + publicKey);
//
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//        converter.setSigningKey(privateKey);
//
//        return converter;
//    }
//
//    @Bean
//    public JwtTokenStore tokenStore() {
//        return new JwtTokenStore(accessTokenConverter());
//    }
//
//    @Bean
//    @Primary
//    public DefaultTokenServices tokenServices() {
//        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
//        defaultTokenServices.setTokenStore(tokenStore());
//        defaultTokenServices.setClientDetailsService(clientDetailsService);
//        defaultTokenServices.setSupportRefreshToken(true);
//        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
//        return defaultTokenServices;
//    }
//
//    /**
//     * Defines the authorization and token endpoints and the token services
//     * 
//     * @param endpoints
//     * @throws Exception
//     */
//    @Override
//    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//
//        endpoints.authenticationManager(authenticationManager).userDetailsService(userDetailsService)
//                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST).tokenStore(tokenStore()).tokenServices(tokenServices())
//                .accessTokenConverter(accessTokenConverter());
//    }
//
//}
