package io.github.astahovtech.maxbot.starter.transport;

import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.dispatcher.UpdateDispatcher;
import io.github.astahovtech.maxbot.core.mapper.UpdateMapper;
import io.github.astahovtech.maxbot.starter.MaxBotProperties;
import ru.max.botapi.MaxBotAPI;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnBean(MaxApi.class)
public class MaxBotTransportAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UpdateMapper updateMapper() {
        return new UpdateMapper();
    }

    @Bean
    @ConditionalOnProperty(name = "max.bot.webhook.enabled", havingValue = "true")
    public WebhookController webhookController(UpdateDispatcher dispatcher, UpdateMapper updateMapper,
                                               MaxApi maxApi, MaxBotProperties props) {
        return new WebhookController(dispatcher, updateMapper, maxApi,
                props.getWebhook().getSecret());
    }

    @Bean
    @ConditionalOnProperty(name = "max.bot.webhook.enabled", havingValue = "false", matchIfMissing = true)
    public LongPollingRunner longPollingRunner(MaxBotAPI botApi, UpdateDispatcher dispatcher,
                                              UpdateMapper updateMapper, MaxApi maxApi,
                                              MaxBotProperties props) {
        return new LongPollingRunner(botApi, dispatcher, updateMapper, maxApi,
                props.getPolling().getTimeout(), props.getPolling().getLimit());
    }
}
