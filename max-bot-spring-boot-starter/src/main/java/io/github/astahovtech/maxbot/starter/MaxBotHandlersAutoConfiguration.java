package io.github.astahovtech.maxbot.starter;

import java.util.List;

import io.github.astahovtech.maxbot.core.dispatcher.UpdateDispatcher;
import io.github.astahovtech.maxbot.core.handler.Handler;
import io.github.astahovtech.maxbot.core.interceptor.ErrorHandler;
import io.github.astahovtech.maxbot.core.interceptor.HandlerInterceptor;
import io.github.astahovtech.maxbot.core.state.InMemoryStateStore;
import io.github.astahovtech.maxbot.core.state.StateStore;
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
    public StateStore stateStore() {
        return new InMemoryStateStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public UpdateDispatcher updateDispatcher(
            List<Handler> handlers,
            ObjectProvider<List<HandlerInterceptor>> interceptorsProvider,
            ObjectProvider<ErrorHandler> errorHandlerProvider,
            ObjectProvider<StateStore> stateStoreProvider) {
        List<HandlerInterceptor> interceptors = interceptorsProvider.getIfAvailable(List::of);
        ErrorHandler errorHandler = errorHandlerProvider.getIfAvailable();
        StateStore stateStore = stateStoreProvider.getIfAvailable();
        return new UpdateDispatcher(handlers, interceptors, errorHandler, stateStore);
    }
}
