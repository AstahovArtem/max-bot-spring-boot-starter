package io.github.astahovtech.maxbot.core.handler.impl;

import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.model.Update;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandHandlerTest {

    private static final MaxApi NOOP_API = (chatId, text) -> {};

    @Test
    void supportsExactCommand() {
        var handler = new CommandHandler("start", ctx -> {});
        assertTrue(handler.supports(new Update(1L, "/start")));
    }

    @Test
    void supportsCommandWithArgs() {
        var handler = new CommandHandler("start", ctx -> {});
        assertTrue(handler.supports(new Update(1L, "/start 123")));
    }

    @Test
    void doesNotSupportDifferentCommand() {
        var handler = new CommandHandler("start", ctx -> {});
        assertFalse(handler.supports(new Update(1L, "/help")));
    }

    @Test
    void doesNotSupportNullText() {
        var handler = new CommandHandler("start", ctx -> {});
        assertFalse(handler.supports(new Update(1L, null)));
    }

    @Test
    void normalizesLeadingSlashInConstructor() {
        var handler = new CommandHandler("/start", ctx -> {});
        assertTrue(handler.supports(new Update(1L, "/start")));
    }
}
