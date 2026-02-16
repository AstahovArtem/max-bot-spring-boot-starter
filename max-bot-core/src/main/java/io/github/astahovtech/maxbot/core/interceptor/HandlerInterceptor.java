package io.github.astahovtech.maxbot.core.interceptor;

import io.github.astahovtech.maxbot.core.Ctx;

public interface HandlerInterceptor {

    default boolean preHandle(Ctx ctx) {
        return true;
    }

    default void postHandle(Ctx ctx) {
    }

    default void onError(Ctx ctx, Exception ex) {
    }
}
