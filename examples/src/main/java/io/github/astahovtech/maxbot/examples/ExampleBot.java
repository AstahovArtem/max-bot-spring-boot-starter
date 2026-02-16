package io.github.astahovtech.maxbot.examples;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.keyboard.Button;
import io.github.astahovtech.maxbot.core.keyboard.InlineKeyboard;
import io.github.astahovtech.maxbot.core.outgoing.OutgoingMessage;
import io.github.astahovtech.maxbot.starter.annotations.MaxBot;
import io.github.astahovtech.maxbot.starter.annotations.OnBotAdded;
import io.github.astahovtech.maxbot.starter.annotations.OnBotStarted;
import io.github.astahovtech.maxbot.starter.annotations.OnCallback;
import io.github.astahovtech.maxbot.starter.annotations.OnCommand;
import io.github.astahovtech.maxbot.starter.annotations.OnMessage;
import io.github.astahovtech.maxbot.starter.annotations.OnMessageEdited;
import io.github.astahovtech.maxbot.starter.annotations.OnUserAdded;

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
        ctx.reply("Доступные команды:\n/start — начать\n/help — помощь\n/chat — инфо о чате");
    }

    @OnCommand("chat")
    public void chatInfo(Ctx ctx) {
        var chat = ctx.getChat();
        ctx.reply("Чат: " + chat.title() + "\nУчастников: " + chat.participantsCount());
    }

    @OnCallback(prefix = "greet")
    public void onGreet(Ctx ctx) {
        ctx.answerCallback("Привет, " + ctx.sender().firstName() + "!");
    }

    @OnBotAdded
    public void onBotAdded(Ctx ctx) {
        ctx.reply("Спасибо за добавление в чат! Напишите /help для списка команд.");
    }

    @OnUserAdded
    public void onUserAdded(Ctx ctx) {
        ctx.reply("Добро пожаловать, " + ctx.sender().firstName() + "!");
    }

    @OnMessageEdited
    public void onEdited(Ctx ctx) {
        ctx.reply("Заметил редактирование сообщения!");
    }

    @OnMessage(textRegex = ".*")
    public void echo(Ctx ctx) {
        ctx.reply("Вы написали: " + ctx.text());
    }
}
