package io.github.astahovtech.maxbot.core.dispatcher;

import java.util.List;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.handler.Handler;
import io.github.astahovtech.maxbot.core.model.Update;

public final class UpdateDispatcher {

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
                handler.handle(ctx);
                return;
            }
        }
    }
}
