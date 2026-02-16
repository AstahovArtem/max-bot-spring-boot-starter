package io.github.astahovtech.maxbot.core.handler.impl;

import io.github.astahovtech.maxbot.core.model.Update;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandHandlerTest {

    @Test
    void supportsExactCommand() {
        var handler = new CommandHandler("start", ctx -> {});
        assertTrue(handler.supports(Update.of(1L, "/start", null)));
    }

    @Test
    void supportsCommandWithArgs() {
        var handler = new CommandHandler("start", ctx -> {});
        assertTrue(handler.supports(Update.of(1L, "/start 123", null)));
    }

    @Test
    void doesNotSupportDifferentCommand() {
        var handler = new CommandHandler("start", ctx -> {});
        assertFalse(handler.supports(Update.of(1L, "/help", null)));
    }

    @Test
    void doesNotSupportNullText() {
        var handler = new CommandHandler("start", ctx -> {});
        assertFalse(handler.supports(Update.of(1L, null, null)));
    }

    @Test
    void normalizesLeadingSlashInConstructor() {
        var handler = new CommandHandler("/start", ctx -> {});
        assertTrue(handler.supports(Update.of(1L, "/start", null)));
    }
}
