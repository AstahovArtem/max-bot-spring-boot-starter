package io.github.astahovtech.maxbot.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "max.bot")
public class MaxBotProperties {

    /**
     * Bot access token from @MasterBot.
     */
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
