package io.github.astahovtech.maxbot.core.handler.impl;

import io.github.astahovtech.maxbot.core.model.Update;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CallbackHandlerTest {

    @Test
    void supportsWhenCallbackDataStartsWithPrefix() {
        var handler = new CallbackHandler("pay:", ctx -> {});
        assertTrue(handler.supports(Update.of(1L, null, "pay:123")));
    }

    @Test
    void doesNotSupportWhenPrefixDifferent() {
        var handler = new CallbackHandler("pay:", ctx -> {});
        assertFalse(handler.supports(Update.of(1L, null, "cancel:456")));
    }

    @Test
    void supportsFalseWhenCallbackDataNull() {
        var handler = new CallbackHandler("pay:", ctx -> {});
        assertFalse(handler.supports(Update.of(1L, "hello", null)));
    }

    @Test
    void constructorRejectBlankPrefix() {
        assertThrows(IllegalArgumentException.class,
                () -> new CallbackHandler("  ", ctx -> {}));
    }
}
