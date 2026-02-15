package io.github.astahovtech.maxbot.starter;

import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.impl.RuMaxApi;
import ru.max.botapi.MaxBotAPI;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(MaxBotProperties.class)
public class MaxBotAutoConfiguration {

    @Bean
    public MaxBotAPI maxBotAPI(MaxBotProperties props) {
        return MaxBotAPI.create(props.getAccessToken());
    }

    @Bean
    public MaxApi maxApi(MaxBotAPI botApi) {
        return new RuMaxApi(botApi);
    }
}
