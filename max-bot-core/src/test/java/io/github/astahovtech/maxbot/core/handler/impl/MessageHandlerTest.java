package io.github.astahovtech.maxbot.core.handler.impl;

import io.github.astahovtech.maxbot.core.model.Update;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageHandlerTest {

    @Test
    void matchesByRegexCaseInsensitive() {
        var handler = new MessageHandler("заказ|order", ctx -> {});
        assertTrue(handler.supports(Update.of(1L, "Хочу ЗАКАЗ", null)));
        assertTrue(handler.supports(Update.of(1L, "order please", null)));
    }

    @Test
    void doesNotMatchUnrelatedText() {
        var handler = new MessageHandler("заказ|order", ctx -> {});
        assertFalse(handler.supports(Update.of(1L, "привет", null)));
    }

    @Test
    void supportsFalseWhenTextIsNull() {
        var handler = new MessageHandler("заказ|order", ctx -> {});
        assertFalse(handler.supports(Update.of(1L, null, null)));
    }
}
