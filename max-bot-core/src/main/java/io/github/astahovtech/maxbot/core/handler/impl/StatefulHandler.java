package io.github.astahovtech.maxbot.core.handler.impl;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.handler.Handler;
import io.github.astahovtech.maxbot.core.model.Update;
import io.github.astahovtech.maxbot.core.state.StateStore;

public final class StatefulHandler implements Handler {

    private final String requiredState;
    private final Handler delegate;
    private final StateStore stateStore;

    public StatefulHandler(String requiredState, Handler delegate, StateStore stateStore) {
        if (requiredState == null || requiredState.isEmpty()) {
            throw new IllegalArgumentException("requiredState must not be empty");
        }
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        if (stateStore == null) {
            throw new IllegalArgumentException("stateStore must not be null");
        }
        this.requiredState = requiredState;
        this.delegate = delegate;
        this.stateStore = stateStore;
    }

    @Override
    public boolean supports(Update update) {
        if (!delegate.supports(update)) {
            return false;
        }
        String currentState = stateStore.getState(update.chatId());
        return requiredState.equals(currentState);
    }

    @Override
    public void handle(Ctx ctx) {
        delegate.handle(ctx);
    }

    public String requiredState() {
        return requiredState;
    }

    public Handler delegate() {
        return delegate;
    }
}
