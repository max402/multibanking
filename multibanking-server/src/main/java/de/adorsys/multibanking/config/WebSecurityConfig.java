package de.adorsys.multibanking.config;

import de.adorsys.multibanking.domain.UserSecret;
import de.adorsys.multibanking.service.SecretClaimDecryptionService;
import de.adorsys.sts.filter.JWTAuthenticationFilter;
import de.adorsys.sts.token.authentication.TokenAuthenticationService;
import de.adorsys.sts.tokenauth.BearerToken;
import de.adorsys.sts.tokenauth.BearerTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String RULES_ADMIN_ROLE = "rules_admin";

    private final Environment environment;
    private final SecretClaimDecryptionService secretClaimDecryptionService;

    @Autowired(required = false)
    private TokenAuthenticationService tokenAuthenticationService;

    @Autowired(required = false)
    private BearerTokenValidator bearerTokenValidator;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/management/health").permitAll()
            .antMatchers("/management/info").permitAll()
            .antMatchers("/management/**").hasAuthority("admin")
            .antMatchers("/").permitAll()
            .antMatchers("/pop").permitAll()
            .antMatchers("/swagger-ui.html").permitAll()
            .antMatchers("/webjars/**").permitAll()
            .antMatchers("/swagger-resources/**").permitAll()
            .antMatchers("/v2/api-docs/**").permitAll()
            .antMatchers("/api/v1/direct/**").permitAll()
            .antMatchers("/status").permitAll()
            .antMatchers(HttpMethod.GET, "/api/v1/bank/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/v1/banks/**").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/bank/**").hasAuthority(RULES_ADMIN_ROLE)
            .antMatchers(HttpMethod.POST, "/api/v1/banks/**").hasAuthority(RULES_ADMIN_ROLE)
            .antMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()
            .antMatchers(HttpMethod.POST, "/api/v1/images/**").hasAuthority(RULES_ADMIN_ROLE)
            .antMatchers(HttpMethod.GET, "/api/v1/config/booking-categories").authenticated()
            .antMatchers("/api/v1/config/**").hasAuthority(RULES_ADMIN_ROLE)
            .antMatchers("/api/v1/**").authenticated()
            .anyRequest().denyAll()
            .and().cors();

        if (environment.acceptsProfiles(Profiles.of("sts-enable"))) {
            http.addFilterBefore(new JWTAuthenticationFilter(tokenAuthenticationService),
                BasicAuthenticationFilter.class);
        }
    }

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public BearerToken getBearerToken(HttpServletRequest request,
                                      @Autowired(required = false) BearerTokenValidator bearerTokenValidator) {
        String token = request.getHeader(BearerTokenValidator.HEADER_KEY);
        return bearerTokenValidator.extract(token);
    }

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Principal getPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Bean
    @Primary
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UserSecret getRequestScopeUserSecret() {
        return new UserSecret(secretClaimDecryptionService.decryptSecretClaim());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when
        // the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setExposedHeaders(Collections.singletonList("Location"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
