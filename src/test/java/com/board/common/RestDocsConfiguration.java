package com.board.common;

import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

@TestConfiguration
public class RestDocsConfiguration {

    @Bean
    public RestDocsMockMvcConfigurationCustomizer restDocsMockMvcConfigurationCustomizer() {
        return configurer -> configurer.operationPreprocessors()
                .withRequestDefaults(
                        prettyPrint(),
                        removeHeaders("Host", "Content-Length")
                )
                .withResponseDefaults(
                        prettyPrint(),
                        removeHeaders("X-Content-Type-Options", "X-XSS-Protection",
                                "Cache-Control", "Pragma", "Expires", "X-Frame-Options",
                                "Content-Length")
                );
    }
}
