package io.github.astahovtech.maxbot.core.model;

public sealed interface Attachment {

    String token();

    record Photo(String token) implements Attachment {
    }

    record Video(String token) implements Attachment {
    }

    record Audio(String token) implements Attachment {
    }

    record BotFile(String token) implements Attachment {
    }

    static Attachment photo(String token) {
        return new Photo(token);
    }

    static Attachment video(String token) {
        return new Video(token);
    }

    static Attachment audio(String token) {
        return new Audio(token);
    }

    static Attachment file(String token) {
        return new BotFile(token);
    }
}
