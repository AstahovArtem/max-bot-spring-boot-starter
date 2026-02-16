package io.github.astahovtech.maxbot.starter;

import java.util.ArrayList;
import java.util.List;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.dispatcher.UpdateDispatcher;
import io.github.astahovtech.maxbot.core.model.BotUser;
import io.github.astahovtech.maxbot.core.model.Update;
import io.github.astahovtech.maxbot.core.outgoing.OutgoingMessage;
import io.github.astahovtech.maxbot.starter.annotations.MaxBot;
import io.github.astahovtech.maxbot.starter.annotations.OnBotStarted;
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

            dispatcher.dispatch(capturingApi, Update.of(1L, "/start", null));
            assertEquals(1, capturingApi.messages.size());
            assertEquals("Hello!", capturingApi.messages.get(0));

            capturingApi.messages.clear();

            dispatcher.dispatch(capturingApi, Update.of(1L, "order please", null));
            assertEquals(1, capturingApi.messages.size());
            assertEquals("Order received", capturingApi.messages.get(0));

            capturingApi.messages.clear();

            dispatcher.dispatch(capturingApi, Update.of(1L, null, "pay:123"));
            assertEquals(1, capturingApi.messages.size());
            assertEquals("Payment: pay:123", capturingApi.messages.get(0));

            capturingApi.messages.clear();

            var user = new BotUser(42L, "User", null, "user42", false);
            dispatcher.dispatch(capturingApi, Update.ofBotStarted(1L, user, System.currentTimeMillis(), null, "ru"));
            assertEquals(1, capturingApi.messages.size());
            assertEquals("Welcome, User!", capturingApi.messages.get(0));
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

        @OnBotStarted
        public void botStarted(Ctx ctx) {
            ctx.reply("Welcome, " + ctx.sender().firstName() + "!");
        }
    }

    static class CapturingMaxApi implements MaxApi {
        final List<String> messages = new ArrayList<>();

        @Override
        public void sendMessage(long chatId, String text) {
            messages.add(text);
        }

        @Override
        public void sendMessage(long chatId, OutgoingMessage message) {
            messages.add(message.text());
        }

        @Override
        public void editMessage(String messageId, String text) {
        }

        @Override
        public void editMessage(String messageId, OutgoingMessage message) {
        }

        @Override
        public void deleteMessage(String messageId) {
        }

        @Override
        public void answerCallback(String callbackId, String notification) {
        }

        @Override
        public void answerCallback(String callbackId, OutgoingMessage message) {
        }

        @Override
        public BotUser getMe() {
            return new BotUser(0L, "TestBot", null, "test_bot", true);
        }
    }
}
