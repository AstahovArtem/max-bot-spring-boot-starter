package io.github.astahovtech.maxbot.core.keyboard;

public sealed interface Button {

    String text();

    record Callback(String text, String payload) implements Button {
    }

    record Link(String text, String url) implements Button {
    }

    static Button callback(String text, String payload) {
        return new Callback(text, payload);
    }

    static Button link(String text, String url) {
        return new Link(text, url);
    }
}
