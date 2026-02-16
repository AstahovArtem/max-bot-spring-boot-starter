package io.github.astahovtech.maxbot.core.dispatcher;

import java.util.List;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.handler.Handler;
import io.github.astahovtech.maxbot.core.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UpdateDispatcher {

    private static final Logger log = LoggerFactory.getLogger(UpdateDispatcher.class);

    private final List<Handler> handlers;

    public UpdateDispatcher(List<Handler> handlers) {
        if (handlers == null) {
            throw new IllegalArgumentException("handlers must not be null");
        }
        this.handlers = List.copyOf(handlers);
    }

    public void dispatch(MaxApi api, Update update) {
        Ctx ctx = new Ctx(api, update);
        for (Handler handler : handlers) {
            if (handler.supports(update)) {
                log.debug("Dispatching update type={} chatId={} to {}",
                        update.type(), update.chatId(), handler.getClass().getSimpleName());
                try {
                    handler.handle(ctx);
                } catch (Exception e) {
                    log.error("Handler {} threw an exception for update type={} chatId={}",
                            handler.getClass().getSimpleName(), update.type(), update.chatId(), e);
                }
                return;
            }
        }
        log.debug("No handler found for update type={} chatId={}", update.type(), update.chatId());
    }
}
