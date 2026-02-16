package io.github.astahovtech.maxbot.starter.actuator;

import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.model.BotUser;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class MaxBotHealthIndicator implements HealthIndicator {

    private final MaxApi maxApi;

    public MaxBotHealthIndicator(MaxApi maxApi) {
        this.maxApi = maxApi;
    }

    @Override
    public Health health() {
        try {
            BotUser me = maxApi.getMe();
            return Health.up()
                    .withDetail("botName", me.firstName())
                    .withDetail("botUsername", me.username())
                    .withDetail("botId", me.userId())
                    .build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
