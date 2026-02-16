package io.github.astahovtech.maxbot.core.dispatcher;

import java.util.List;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.handler.Handler;
import io.github.astahovtech.maxbot.core.interceptor.ErrorHandler;
import io.github.astahovtech.maxbot.core.interceptor.HandlerInterceptor;
import io.github.astahovtech.maxbot.core.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UpdateDispatcher {

    private static final Logger log = LoggerFactory.getLogger(UpdateDispatcher.class);

    private final List<Handler> handlers;
    private final List<HandlerInterceptor> interceptors;
    private final ErrorHandler errorHandler;

    public UpdateDispatcher(List<Handler> handlers) {
        this(handlers, List.of(), null);
    }

    public UpdateDispatcher(List<Handler> handlers,
                            List<HandlerInterceptor> interceptors,
                            ErrorHandler errorHandler) {
        if (handlers == null) {
            throw new IllegalArgumentException("handlers must not be null");
        }
        this.handlers = List.copyOf(handlers);
        this.interceptors = interceptors != null ? List.copyOf(interceptors) : List.of();
        this.errorHandler = errorHandler;
    }

    public void dispatch(MaxApi api, Update update) {
        Ctx ctx = new Ctx(api, update);

        for (HandlerInterceptor interceptor : interceptors) {
            try {
                if (!interceptor.preHandle(ctx)) {
                    log.debug("Interceptor {} rejected update type={} chatId={}",
                            interceptor.getClass().getSimpleName(), update.type(), update.chatId());
                    return;
                }
            } catch (Exception e) {
                log.error("Interceptor preHandle failed", e);
                return;
            }
        }

        for (Handler handler : handlers) {
            if (handler.supports(update)) {
                log.debug("Dispatching update type={} chatId={} to {}",
                        update.type(), update.chatId(), handler.getClass().getSimpleName());
                try {
                    handler.handle(ctx);
                } catch (Exception e) {
                    handleError(ctx, e, handler);
                }

                for (HandlerInterceptor interceptor : interceptors) {
                    try {
                        interceptor.postHandle(ctx);
                    } catch (Exception e) {
                        log.error("Interceptor postHandle failed", e);
                    }
                }
                return;
            }
        }

        log.debug("No handler found for update type={} chatId={}", update.type(), update.chatId());
    }

    private void handleError(Ctx ctx, Exception ex, Handler handler) {
        for (HandlerInterceptor interceptor : interceptors) {
            try {
                interceptor.onError(ctx, ex);
            } catch (Exception e) {
                log.error("Interceptor onError failed", e);
            }
        }

        if (errorHandler != null) {
            try {
                errorHandler.handle(ctx, ex);
            } catch (Exception e) {
                log.error("ErrorHandler failed", e);
            }
        } else {
            log.error("Handler {} threw an exception for update type={} chatId={}",
                    handler.getClass().getSimpleName(), ctx.update().type(), ctx.update().chatId(), ex);
        }
    }
}
