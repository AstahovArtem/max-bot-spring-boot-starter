package io.github.astahovtech.maxbot.starter;

import java.util.List;

import io.github.astahovtech.maxbot.core.dispatcher.UpdateDispatcher;
import io.github.astahovtech.maxbot.core.handler.Handler;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class MaxBotHandlersAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UpdateDispatcher updateDispatcher(List<Handler> handlers) {
        return new UpdateDispatcher(handlers);
    }
}
