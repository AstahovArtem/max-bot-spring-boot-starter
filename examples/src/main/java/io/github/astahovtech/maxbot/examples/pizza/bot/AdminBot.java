package io.github.astahovtech.maxbot.examples.pizza.bot;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.examples.pizza.service.OrderService;
import io.github.astahovtech.maxbot.starter.annotations.MaxBot;
import io.github.astahovtech.maxbot.starter.annotations.OnCommand;

@MaxBot
public class AdminBot {

    private final OrderService orderService;

    public AdminBot(OrderService orderService) {
        this.orderService = orderService;
    }

    @OnCommand(value = "stats", order = -10)
    public void stats(Ctx ctx) {
        ctx.reply("Статистика:\n"
                + "Завершённых заказов: " + orderService.todayCount() + "\n"
                + "Активных оформлений: " + orderService.activeDraftsCount());
    }
}
