package learn.dhiman.spring.sample.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;


@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Value("${learn.dhiman.oauth.accessTokenValiditySeconds:10000}")
    private int accessTokenValiditySeconds;
    @Value("${learn.dhiman.oauth.refreshTokenValiditySeconds:30000}")
    private int refreshTokenValiditySeconds;
    @Value("${learn.dhiman.application.name:sample}")
    private String resourceId;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
            .withClient("web_app")
            .secret("$2a$10$/DA0ezUDf/R869SekwZKxel1/QEPO5aCeeRnNi10xJRiTqcycOd1i")
            .scopes("FOO")
            .autoApprove(true)
            //.authorities("FOO_READ", "FOO_WRITE")
            .authorizedGrantTypes("implicit", "refresh_token", "password", "authorization_code");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore())
            .tokenEnhancer(jwtTokenEnhancer())
            .authenticationManager(authenticationManager).userDetailsService(userDetailsService);
    }

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtTokenEnhancer());
    }

    @Bean
    protected JwtAccessTokenConverter jwtTokenEnhancer() {
        var keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("demodevjwt.jks"), "notASecret".toCharArray());
        var converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("demodevjwt"));
        return converter;
    }

}
