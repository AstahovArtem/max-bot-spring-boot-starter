package io.github.astahovtech.maxbot.starter.internal;

import java.lang.reflect.Method;

import io.github.astahovtech.maxbot.core.Ctx;

public final class HandlerMethodValidator {

    private HandlerMethodValidator() {
    }

    public static void validate(Method method) {
        Class<?>[] params = method.getParameterTypes();
        if (method.getReturnType() != void.class || params.length != 1 || params[0] != Ctx.class) {
            throw new IllegalStateException(
                    "Invalid handler method " + method.getDeclaringClass().getName() + "#" + method.getName()
                            + ": expected void method with single parameter Ctx");
        }
    }
}
