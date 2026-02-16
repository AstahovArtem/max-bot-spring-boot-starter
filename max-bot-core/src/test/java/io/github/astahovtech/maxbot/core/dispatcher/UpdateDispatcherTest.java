package io.github.astahovtech.maxbot.core.dispatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.handler.impl.CommandHandler;
import io.github.astahovtech.maxbot.core.interceptor.ErrorHandler;
import io.github.astahovtech.maxbot.core.interceptor.HandlerInterceptor;
import io.github.astahovtech.maxbot.core.model.BotChat;
import io.github.astahovtech.maxbot.core.model.BotChatMember;
import io.github.astahovtech.maxbot.core.model.BotUser;
import io.github.astahovtech.maxbot.core.model.Update;
import io.github.astahovtech.maxbot.core.outgoing.OutgoingMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateDispatcherTest {

    private final MaxApi noopApi = new NoopMaxApi();

    @Test
    void interceptorPreHandleCanRejectUpdate() {
        var handled = new AtomicBoolean(false);
        var handler = new CommandHandler("test", ctx -> handled.set(true));

        var rejectingInterceptor = new HandlerInterceptor() {
            @Override
            public boolean preHandle(Ctx ctx) {
                return false;
            }
        };

        var dispatcher = new UpdateDispatcher(List.of(handler), List.of(rejectingInterceptor), null);
        dispatcher.dispatch(noopApi, Update.of(1L, "/test", null));

        assertFalse(handled.get());
    }

    @Test
    void interceptorPostHandleCalled() {
        var postHandled = new AtomicBoolean(false);
        var handler = new CommandHandler("test", ctx -> {});

        var interceptor = new HandlerInterceptor() {
            @Override
            public void postHandle(Ctx ctx) {
                postHandled.set(true);
            }
        };

        var dispatcher = new UpdateDispatcher(List.of(handler), List.of(interceptor), null);
        dispatcher.dispatch(noopApi, Update.of(1L, "/test", null));

        assertTrue(postHandled.get());
    }

    @Test
    void errorHandlerReceivesException() {
        var caughtEx = new AtomicReference<Exception>();
        var handler = new CommandHandler("test", ctx -> {
            throw new RuntimeException("boom");
        });

        ErrorHandler errorHandler = (ctx, ex) -> caughtEx.set(ex);

        var dispatcher = new UpdateDispatcher(List.of(handler), List.of(), errorHandler);
        dispatcher.dispatch(noopApi, Update.of(1L, "/test", null));

        assertTrue(caughtEx.get() instanceof RuntimeException);
        assertEquals("boom", caughtEx.get().getMessage());
    }

    @Test
    void interceptorOnErrorCalledOnException() {
        var onErrorCalled = new AtomicBoolean(false);
        var handler = new CommandHandler("test", ctx -> {
            throw new RuntimeException("fail");
        });

        var interceptor = new HandlerInterceptor() {
            @Override
            public void onError(Ctx ctx, Exception ex) {
                onErrorCalled.set(true);
            }
        };

        var dispatcher = new UpdateDispatcher(List.of(handler), List.of(interceptor), null);
        dispatcher.dispatch(noopApi, Update.of(1L, "/test", null));

        assertTrue(onErrorCalled.get());
    }

    static class NoopMaxApi implements MaxApi {
        @Override public void sendMessage(long chatId, String text) {}
        @Override public void sendMessage(long chatId, OutgoingMessage message) {}
        @Override public void editMessage(String messageId, String text) {}
        @Override public void editMessage(String messageId, OutgoingMessage message) {}
        @Override public void deleteMessage(String messageId) {}
        @Override public void answerCallback(String callbackId, String notification) {}
        @Override public void answerCallback(String callbackId, OutgoingMessage message) {}
        @Override public BotUser getMe() { return null; }
        @Override public String uploadImage(File file) { return null; }
        @Override public String uploadVideo(File file) { return null; }
        @Override public String uploadAudio(File file) { return null; }
        @Override public String uploadFile(File file) { return null; }
        @Override public BotChat getChat(long chatId) { return null; }
        @Override public List<BotChatMember> getChatMembers(long chatId) { return List.of(); }
        @Override public void leaveChat(long chatId) {}
    }
}
