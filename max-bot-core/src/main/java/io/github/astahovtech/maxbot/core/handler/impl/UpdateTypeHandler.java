package io.github.astahovtech.maxbot.core.handler.impl;

import java.util.function.Consumer;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.handler.Handler;
import io.github.astahovtech.maxbot.core.model.Update;
import io.github.astahovtech.maxbot.core.model.UpdateType;

public final class UpdateTypeHandler implements Handler {

    private final UpdateType targetType;
    private final Consumer<Ctx> action;

    public UpdateTypeHandler(UpdateType targetType, Consumer<Ctx> action) {
        if (targetType == null) {
            throw new IllegalArgumentException("targetType must not be null");
        }
        if (action == null) {
            throw new IllegalArgumentException("action must not be null");
        }
        this.targetType = targetType;
        this.action = action;
    }

    @Override
    public boolean supports(Update update) {
        return update.type() == targetType;
    }

    @Override
    public void handle(Ctx ctx) {
        action.accept(ctx);
    }

    public UpdateType targetType() {
        return targetType;
    }
}
