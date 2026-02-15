package io.github.astahovtech.maxbot.core.handler;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.model.Update;

public interface Handler {

    boolean supports(Update update);

    void handle(Ctx ctx);
}
