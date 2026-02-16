package io.github.astahovtech.maxbot.starter;

import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.impl.RuMaxApi;
import io.github.astahovtech.maxbot.core.retry.RetryPolicy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.max.botapi.MaxBotAPI;
import ru.max.botapi.MaxUploadAPI;
import ru.max.botapi.client.MaxClient;

@Configuration
@EnableConfigurationProperties(MaxBotProperties.class)
@ConditionalOnProperty(name = "max.bot.access-token")
public class MaxBotAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MaxClient maxClient(MaxBotProperties props) {
        return MaxClient.create(props.getAccessToken());
    }

    @Bean
    @ConditionalOnMissingBean
    public MaxBotAPI maxBotAPI(MaxClient client) {
        return new MaxBotAPI(client);
    }

    @Bean
    @ConditionalOnMissingBean
    public MaxUploadAPI maxUploadAPI(MaxClient client) {
        return new MaxUploadAPI(client);
    }

    @Bean
    @ConditionalOnMissingBean
    public RetryPolicy retryPolicy() {
        return RetryPolicy.defaultPolicy();
    }

    @Bean
    @ConditionalOnMissingBean
    public MaxApi maxApi(MaxBotAPI botApi,
                         ObjectProvider<MaxUploadAPI> uploadApiProvider,
                         ObjectProvider<RetryPolicy> retryPolicyProvider) {
        MaxUploadAPI uploadApi = uploadApiProvider.getIfAvailable();
        RetryPolicy retryPolicy = retryPolicyProvider.getIfAvailable(RetryPolicy::defaultPolicy);
        return new RuMaxApi(botApi, uploadApi, retryPolicy);
    }
}
