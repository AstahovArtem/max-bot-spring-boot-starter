package io.github.astahovtech.maxbot.starter;

import java.util.ArrayList;
import java.util.List;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.dispatcher.UpdateDispatcher;
import io.github.astahovtech.maxbot.core.model.Update;
import io.github.astahovtech.maxbot.starter.annotations.MaxBot;
import io.github.astahovtech.maxbot.starter.annotations.OnCallback;
import io.github.astahovtech.maxbot.starter.annotations.OnCommand;
import io.github.astahovtech.maxbot.starter.annotations.OnMessage;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaxBotAnnotationHandlersTest {

    @Test
    void annotationBasedHandlersAreRegisteredAndDispatched() {
        var capturingApi = new CapturingMaxApi();

        try (var ctx = new AnnotationConfigApplicationContext()) {
            ctx.registerBean(MaxApi.class, () -> capturingApi);
            ctx.register(MaxBotAnnotationHandlersAutoConfiguration.class);
            ctx.register(MaxBotHandlersAutoConfiguration.class);
            ctx.register(TestBot.class);
            ctx.refresh();

            var dispatcher = ctx.getBean(UpdateDispatcher.class);

            dispatcher.dispatch(capturingApi, new Update(1L, "/start", null));
            assertEquals(1, capturingApi.messages.size());
            assertEquals("Hello!", capturingApi.messages.get(0));

            capturingApi.messages.clear();

            dispatcher.dispatch(capturingApi, new Update(1L, "order please", null));
            assertEquals(1, capturingApi.messages.size());
            assertEquals("Order received", capturingApi.messages.get(0));

            capturingApi.messages.clear();

            dispatcher.dispatch(capturingApi, new Update(1L, null, "pay:123"));
            assertEquals(1, capturingApi.messages.size());
            assertEquals("Payment: pay:123", capturingApi.messages.get(0));
        }
    }

    @MaxBot
    static class TestBot {

        @OnCommand("start")
        public void start(Ctx ctx) {
            ctx.reply("Hello!");
        }

        @OnMessage(textRegex = "(?i)заказ|order")
        public void order(Ctx ctx) {
            ctx.reply("Order received");
        }

        @OnCallback(prefix = "pay:")
        public void pay(Ctx ctx) {
            ctx.reply("Payment: " + ctx.update().callbackData());
        }
    }

    static class CapturingMaxApi implements MaxApi {
        final List<String> messages = new ArrayList<>();

        @Override
        public void sendMessage(long chatId, String text) {
            messages.add(text);
        }
    }
}
