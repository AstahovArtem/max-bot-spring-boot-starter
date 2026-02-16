package io.github.astahovtech.maxbot.core.outgoing;

import io.github.astahovtech.maxbot.core.keyboard.InlineKeyboard;

public final class OutgoingMessage {

    private final String text;
    private final InlineKeyboard keyboard;
    private final Format format;
    private final boolean notify;
    private final String replyToMessageId;

    private OutgoingMessage(Builder builder) {
        this.text = builder.text;
        this.keyboard = builder.keyboard;
        this.format = builder.format;
        this.notify = builder.notify;
        this.replyToMessageId = builder.replyToMessageId;
    }

    public String text() {
        return text;
    }

    public InlineKeyboard keyboard() {
        return keyboard;
    }

    public Format format() {
        return format;
    }

    public boolean shouldNotify() {
        return notify;
    }

    public String replyToMessageId() {
        return replyToMessageId;
    }

    public static Builder text(String text) {
        return new Builder(text);
    }

    public enum Format {
        MARKDOWN, HTML
    }

    public static final class Builder {
        private final String text;
        private InlineKeyboard keyboard;
        private Format format;
        private boolean notify = true;
        private String replyToMessageId;

        private Builder(String text) {
            this.text = text;
        }

        public Builder keyboard(InlineKeyboard keyboard) {
            this.keyboard = keyboard;
            return this;
        }

        public Builder format(Format format) {
            this.format = format;
            return this;
        }

        public Builder markdown() {
            this.format = Format.MARKDOWN;
            return this;
        }

        public Builder html() {
            this.format = Format.HTML;
            return this;
        }

        public Builder disableNotify() {
            this.notify = false;
            return this;
        }

        public Builder replyTo(String messageId) {
            this.replyToMessageId = messageId;
            return this;
        }

        public OutgoingMessage build() {
            return new OutgoingMessage(this);
        }
    }
}
