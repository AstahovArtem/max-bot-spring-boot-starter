package io.github.astahovtech.maxbot.starter;

import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.impl.RuMaxApi;
import ru.max.botapi.MaxBotAPI;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(MaxBotProperties.class)
@ConditionalOnProperty(name = "max.bot.access-token")
public class MaxBotAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MaxBotAPI maxBotAPI(MaxBotProperties props) {
        return MaxBotAPI.create(props.getAccessToken());
    }

    @Bean
    @ConditionalOnMissingBean
    public MaxApi maxApi(MaxBotAPI botApi) {
        return new RuMaxApi(botApi);
    }
}
