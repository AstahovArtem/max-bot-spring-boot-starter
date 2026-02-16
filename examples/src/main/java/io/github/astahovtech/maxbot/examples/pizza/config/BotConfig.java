package io.github.astahovtech.maxbot.examples.pizza.config;

import io.github.astahovtech.maxbot.core.interceptor.ErrorHandler;
import io.github.astahovtech.maxbot.core.interceptor.HandlerInterceptor;
import io.github.astahovtech.maxbot.core.Ctx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    private static final Logger log = LoggerFactory.getLogger(BotConfig.class);

    @Bean
    public ErrorHandler errorHandler() {
        return (ctx, ex) -> {
            log.error("Bot error in chatId={}: {}", ctx.chatId(), ex.getMessage(), ex);
            ctx.clearState();
            ctx.reply("Произошла ошибка. Попробуйте /start");
        };
    }

    @Bean
    public HandlerInterceptor loggingInterceptor() {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(Ctx ctx) {
                log.info("[{}] {} from chatId={}",
                        ctx.update().type(), textPreview(ctx), ctx.chatId());
                return true;
            }

            private String textPreview(Ctx ctx) {
                if (ctx.callbackData() != null) return "callback=" + ctx.callbackData();
                if (ctx.text() != null) return "text=" + truncate(ctx.text(), 50);
                return "";
            }

            private String truncate(String s, int max) {
                return s.length() <= max ? s : s.substring(0, max) + "...";
            }
        };
    }
}
