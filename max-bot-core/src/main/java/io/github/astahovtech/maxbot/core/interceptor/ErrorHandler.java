package io.github.astahovtech.maxbot.core.interceptor;

import io.github.astahovtech.maxbot.core.Ctx;

@FunctionalInterface
public interface ErrorHandler {

    void handle(Ctx ctx, Exception ex);
}
