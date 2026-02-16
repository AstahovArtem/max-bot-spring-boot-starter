package io.github.astahovtech.maxbot.core.keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class InlineKeyboard {

    private final List<List<Button>> rows;

    private InlineKeyboard(List<List<Button>> rows) {
        this.rows = Collections.unmodifiableList(rows);
    }

    public List<List<Button>> rows() {
        return rows;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<List<Button>> rows = new ArrayList<>();

        private Builder() {
        }

        public Builder row(Button... buttons) {
            rows.add(List.of(buttons));
            return this;
        }

        public Builder row(List<Button> buttons) {
            rows.add(List.copyOf(buttons));
            return this;
        }

        public InlineKeyboard build() {
            return new InlineKeyboard(new ArrayList<>(rows));
        }
    }
}
