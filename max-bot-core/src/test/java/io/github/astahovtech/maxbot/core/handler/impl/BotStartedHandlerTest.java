package io.github.astahovtech.maxbot.core.handler.impl;

import io.github.astahovtech.maxbot.core.model.BotUser;
import io.github.astahovtech.maxbot.core.model.Update;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BotStartedHandlerTest {

    @Test
    void supportsBotStartedUpdate() {
        var handler = new BotStartedHandler(ctx -> {});
        var user = new BotUser(1L, "Test", null, "test_user", false);
        assertTrue(handler.supports(Update.ofBotStarted(100L, user, System.currentTimeMillis(), null, "ru")));
    }

    @Test
    void doesNotSupportMessageUpdate() {
        var handler = new BotStartedHandler(ctx -> {});
        assertFalse(handler.supports(Update.of(1L, "/start", null)));
    }

    @Test
    void constructorRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BotStartedHandler(null));
    }
}
