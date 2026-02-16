package io.github.astahovtech.maxbot.examples;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.keyboard.Button;
import io.github.astahovtech.maxbot.core.keyboard.InlineKeyboard;
import io.github.astahovtech.maxbot.core.outgoing.OutgoingMessage;
import io.github.astahovtech.maxbot.starter.annotations.MaxBot;
import io.github.astahovtech.maxbot.starter.annotations.OnBotStarted;
import io.github.astahovtech.maxbot.starter.annotations.OnCallback;
import io.github.astahovtech.maxbot.starter.annotations.OnCommand;
import io.github.astahovtech.maxbot.starter.annotations.OnMessage;

@MaxBot
public class ExampleBot {

    @OnBotStarted
    public void onStart(Ctx ctx) {
        var keyboard = InlineKeyboard.builder()
                .row(Button.callback("Привет!", "greet"))
                .row(Button.link("Документация", "https://dev.max.ru"))
                .build();

        ctx.reply(OutgoingMessage.text("Добро пожаловать! Выберите действие:")
                .keyboard(keyboard)
                .build());
    }

    @OnCommand("help")
    public void help(Ctx ctx) {
        ctx.reply("Доступные команды:\n/start — начать\n/help — помощь");
    }

    @OnCallback(prefix = "greet")
    public void onGreet(Ctx ctx) {
        ctx.answerCallback("Привет!");
    }

    @OnMessage(textRegex = ".*")
    public void echo(Ctx ctx) {
        ctx.reply("Вы написали: " + ctx.text());
    }
}
