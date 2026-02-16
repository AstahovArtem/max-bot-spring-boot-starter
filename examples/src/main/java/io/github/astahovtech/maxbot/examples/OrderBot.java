package io.github.astahovtech.maxbot.examples;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.starter.annotations.MaxBot;
import io.github.astahovtech.maxbot.starter.annotations.OnCommand;
import io.github.astahovtech.maxbot.starter.annotations.OnMessage;

@MaxBot
public class OrderBot {

    @OnCommand("order")
    public void startOrder(Ctx ctx) {
        ctx.reply("Введите название товара:");
        ctx.setState("WAITING_PRODUCT");
    }

    @OnMessage(textRegex = ".*", state = "WAITING_PRODUCT")
    public void receiveProduct(Ctx ctx) {
        ctx.reply("Товар: " + ctx.text() + "\nСколько штук?");
        ctx.setState("WAITING_QUANTITY");
    }

    @OnMessage(textRegex = "\\d+", state = "WAITING_QUANTITY")
    public void receiveQuantity(Ctx ctx) {
        ctx.reply("Заказ оформлен! Количество: " + ctx.text());
        ctx.clearState();
    }

    @OnCommand("cancel")
    public void cancel(Ctx ctx) {
        ctx.clearState();
        ctx.reply("Заказ отменён.");
    }
}
