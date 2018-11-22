package be.belga.reporter.config;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ComponentScan(basePackages = "be.belga.reporter.controller")
public class SwaggerConfig {

    @Value("${config.oauth2.accessTokenUri}")
    private String accessTokenUri;

    public static final String securitySchemaOAuth2 = "oauth2schema";
    public static final String authorizationScopeGlobal = "global";
    public static final String authorizationScopeGlobalDesc = "accessEverything";

    @Bean
    public Docket productApi() {

        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any()).paths(paths()).build()
                .securityContexts(Collections.singletonList(securityContext())).securitySchemes(Arrays.asList(securitySchema(), apiKey(), apiCookieKey()))
                .apiInfo(apiInfo());
    }

    private Predicate<String> paths() {
        return Predicates.or(PathSelectors.ant("/user/**"),
                             PathSelectors.ant("/fileupload/**"),
                             PathSelectors.ant("/file/**"),
                             PathSelectors.ant("/metadata/**"),
                             PathSelectors.ant("/post/**"));
    }

    @Bean
    public SecurityScheme apiKey() {
        return new ApiKey(HttpHeaders.AUTHORIZATION, "apiKey", "header");
    }

    @Bean
    public SecurityScheme apiCookieKey() {
        return new ApiKey(HttpHeaders.COOKIE, "apiKey", "cookie");
    }

    private OAuth securitySchema() {
        List<AuthorizationScope> authorizationScopeList = newArrayList();
        authorizationScopeList.add(new AuthorizationScope("read", "read all"));
        authorizationScopeList.add(new AuthorizationScope("write", "access all"));

        List<GrantType> grantTypes = newArrayList();
        GrantType passwordCredentialsGrant = new ResourceOwnerPasswordCredentialsGrant(accessTokenUri);
        grantTypes.add(passwordCredentialsGrant);

        return new OAuth("oauth2", authorizationScopeList, grantTypes);
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        final AuthorizationScope[] authorizationScopes = new AuthorizationScope[3];
        authorizationScopes[0] = new AuthorizationScope("read", "read all");
        authorizationScopes[1] = new AuthorizationScope("trust", "trust all");
        authorizationScopes[2] = new AuthorizationScope("write", "write all");

        return Collections.singletonList(new SecurityReference("oauth2", authorizationScopes));
    }

    @Bean
    public SecurityConfiguration security() {
        return new SecurityConfiguration("client", "secret", "", "", "Bearer access token", ApiKeyVehicle.HEADER, HttpHeaders.AUTHORIZATION, "");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Authentication API").description("").termsOfServiceUrl("https://www.example.com/api")
                .contact(new Contact("Developers", "https://projects.spring.io/spring-boot/", "")).license("Open Source")
                .licenseUrl("\"https://www.apache.org/licenses/LICENSE-2.0").version("1.0.0").build();

    }

    public static final String[] METHODS = {"head", "option", "get", "post", "put", "patch", "delete"};

    @Bean
    public UiConfiguration uiConfiguration() {
        return new UiConfiguration(null, METHODS);
    }
}
