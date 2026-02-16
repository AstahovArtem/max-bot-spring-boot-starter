package io.github.astahovtech.maxbot.examples.pizza.bot;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.keyboard.Button;
import io.github.astahovtech.maxbot.core.keyboard.InlineKeyboard;
import io.github.astahovtech.maxbot.core.outgoing.OutgoingMessage;
import io.github.astahovtech.maxbot.starter.annotations.MaxBot;
import io.github.astahovtech.maxbot.starter.annotations.OnBotStarted;
import io.github.astahovtech.maxbot.starter.annotations.OnCallback;
import io.github.astahovtech.maxbot.starter.annotations.OnCommand;

@MaxBot
public class MainMenuBot {

    private static final InlineKeyboard MAIN_MENU = InlineKeyboard.builder()
            .row(Button.callback("Заказать пиццу", "menu:order"))
            .row(Button.callback("Наше меню", "menu:catalog"),
                 Button.callback("Контакты", "menu:contacts"))
            .build();

    @OnBotStarted
    public void onBotStarted(Ctx ctx) {
        showMainMenu(ctx, "Добро пожаловать в PizzaBot!\nВыберите действие:");
    }

    @OnCommand(value = "start", order = -100)
    public void start(Ctx ctx) {
        ctx.clearState();
        showMainMenu(ctx, "Главное меню:");
    }

    @OnCommand("help")
    public void help(Ctx ctx) {
        ctx.reply("Команды:\n"
                + "/start — главное меню\n"
                + "/order — заказать пиццу\n"
                + "/cancel — отменить текущий заказ\n"
                + "/help — помощь");
    }

    @OnCallback(prefix = "menu:main")
    public void backToMain(Ctx ctx) {
        ctx.clearState();
        showMainMenu(ctx, "Главное меню:");
    }

    @OnCallback(prefix = "menu:catalog")
    public void catalog(Ctx ctx) {
        ctx.answerCallback("Наше меню:");
        ctx.reply("Маргарита — от 450 руб.\n"
                + "Пепперони — от 550 руб.\n"
                + "4 сыра — от 650 руб.\n"
                + "Гавайская — от 500 руб.\n\n"
                + "Размеры: 25 / 30 / 35 см");
    }

    @OnCallback(prefix = "menu:contacts")
    public void contacts(Ctx ctx) {
        ctx.answerCallback("Контакты:");
        ctx.reply("Телефон: +7-999-123-45-67\n"
                + "Время работы: 10:00 — 23:00\n"
                + "Адрес: ул. Пиццерийная, д. 1");
    }

    private void showMainMenu(Ctx ctx, String text) {
        ctx.reply(OutgoingMessage.text(text)
                .keyboard(MAIN_MENU)
                .build());
    }
}
