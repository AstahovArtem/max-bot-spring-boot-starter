package io.github.astahovtech.maxbot.starter;

import java.util.List;

import io.github.astahovtech.maxbot.core.dispatcher.UpdateDispatcher;
import io.github.astahovtech.maxbot.core.handler.Handler;
import io.github.astahovtech.maxbot.core.interceptor.ErrorHandler;
import io.github.astahovtech.maxbot.core.interceptor.HandlerInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(MaxBotAnnotationHandlersAutoConfiguration.class)
public class MaxBotHandlersAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UpdateDispatcher updateDispatcher(
            List<Handler> handlers,
            ObjectProvider<List<HandlerInterceptor>> interceptorsProvider,
            ObjectProvider<ErrorHandler> errorHandlerProvider) {
        List<HandlerInterceptor> interceptors = interceptorsProvider.getIfAvailable(List::of);
        ErrorHandler errorHandler = errorHandlerProvider.getIfAvailable();
        return new UpdateDispatcher(handlers, interceptors, errorHandler);
    }
}
