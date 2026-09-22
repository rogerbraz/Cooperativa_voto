package com.cooperativa.votacao.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "integracao.user-info")
@Getter
@Setter
public class IntegracaoProperties {

    private String url = "https://user-info.herokuapp.com/users";
    private int timeoutMs = 3000;
    private boolean mockFallbackEnabled = true;
}
