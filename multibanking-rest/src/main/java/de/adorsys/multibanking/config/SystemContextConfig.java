package de.adorsys.multibanking.config;

import de.adorsys.multibanking.auth.SystemContext;
import de.adorsys.multibanking.auth.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by peter on 13.04.18 at 11:27.
 */
@Configuration
public class SystemContextConfig {
    @Autowired
    UserContext user;

    @Bean
    SystemContext systemContext() {
        return new SystemContext(user);
    }
}
