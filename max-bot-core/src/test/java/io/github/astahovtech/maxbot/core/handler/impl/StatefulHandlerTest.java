package io.github.astahovtech.maxbot.core.handler.impl;

import io.github.astahovtech.maxbot.core.model.Update;
import io.github.astahovtech.maxbot.core.state.InMemoryStateStore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatefulHandlerTest {

    @Test
    void matchesWhenStateAndDelegateMatch() {
        var store = new InMemoryStateStore();
        store.setState(1L, "STEP_1");

        var delegate = new MessageHandler(".*", ctx -> {});
        var handler = new StatefulHandler("STEP_1", delegate, store);

        assertTrue(handler.supports(Update.of(1L, "hello", null)));
    }

    @Test
    void doesNotMatchWhenStateDiffers() {
        var store = new InMemoryStateStore();
        store.setState(1L, "STEP_2");

        var delegate = new MessageHandler(".*", ctx -> {});
        var handler = new StatefulHandler("STEP_1", delegate, store);

        assertFalse(handler.supports(Update.of(1L, "hello", null)));
    }

    @Test
    void doesNotMatchWhenNoState() {
        var store = new InMemoryStateStore();

        var delegate = new MessageHandler(".*", ctx -> {});
        var handler = new StatefulHandler("STEP_1", delegate, store);

        assertFalse(handler.supports(Update.of(1L, "hello", null)));
    }

    @Test
    void doesNotMatchWhenDelegateDoesNotSupport() {
        var store = new InMemoryStateStore();
        store.setState(1L, "STEP_1");

        var delegate = new CommandHandler("test", ctx -> {});
        var handler = new StatefulHandler("STEP_1", delegate, store);

        assertFalse(handler.supports(Update.of(1L, "hello", null)));
    }
}
