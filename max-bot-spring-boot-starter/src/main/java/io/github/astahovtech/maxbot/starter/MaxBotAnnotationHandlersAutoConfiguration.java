package io.github.astahovtech.maxbot.starter;

import io.github.astahovtech.maxbot.starter.internal.MaxBotHandlerRegistrar;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class MaxBotAnnotationHandlersAutoConfiguration {

    @Bean
    static MaxBotHandlerRegistrar maxBotHandlerRegistrar() {
        return new MaxBotHandlerRegistrar();
    }
}
