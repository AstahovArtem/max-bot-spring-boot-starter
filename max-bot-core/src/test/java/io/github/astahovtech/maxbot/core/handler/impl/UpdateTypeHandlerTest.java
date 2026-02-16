package io.github.astahovtech.maxbot.core.handler.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import io.github.astahovtech.maxbot.core.model.BotUser;
import io.github.astahovtech.maxbot.core.model.Update;
import io.github.astahovtech.maxbot.core.model.UpdateType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateTypeHandlerTest {

    @Test
    void supportsBotAddedUpdate() {
        var handler = new UpdateTypeHandler(UpdateType.BOT_ADDED, ctx -> {});
        var user = new BotUser(1L, "Test", null, "test", false);
        assertTrue(handler.supports(Update.ofBotAdded(10L, user, System.currentTimeMillis())));
    }

    @Test
    void doesNotSupportWrongType() {
        var handler = new UpdateTypeHandler(UpdateType.BOT_REMOVED, ctx -> {});
        assertFalse(handler.supports(Update.of(1L, "/start", null)));
    }

    @Test
    void supportsUserAdded() {
        var handler = new UpdateTypeHandler(UpdateType.USER_ADDED, ctx -> {});
        var user = new BotUser(2L, "Alice", null, "alice", false);
        assertTrue(handler.supports(Update.ofUserAdded(10L, user, 0L)));
    }

    @Test
    void supportsMessageEdited() {
        var handler = new UpdateTypeHandler(UpdateType.MESSAGE_EDITED, ctx -> {});
        var user = new BotUser(1L, "Test", null, "t", false);
        assertTrue(handler.supports(Update.ofMessageEdited(1L, "mid1", "new text", user, 0L)));
    }

    @Test
    void supportsMessageRemoved() {
        var handler = new UpdateTypeHandler(UpdateType.MESSAGE_REMOVED, ctx -> {});
        assertTrue(handler.supports(Update.ofMessageRemoved(1L, "mid1", 0L)));
    }

    @Test
    void supportsChatTitleChanged() {
        var handler = new UpdateTypeHandler(UpdateType.CHAT_TITLE_CHANGED, ctx -> {});
        var user = new BotUser(1L, "Admin", null, "admin", false);
        assertTrue(handler.supports(Update.ofChatTitleChanged(1L, "New Title", user, 0L)));
    }

    @Test
    void constructorRejectsNullType() {
        assertThrows(IllegalArgumentException.class, () -> new UpdateTypeHandler(null, ctx -> {}));
    }

    @Test
    void constructorRejectsNullAction() {
        assertThrows(IllegalArgumentException.class, () -> new UpdateTypeHandler(UpdateType.BOT_ADDED, null));
    }
}
