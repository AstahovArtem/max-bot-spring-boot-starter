package io.github.astahovtech.maxbot.examples.pizza.bot;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.keyboard.Button;
import io.github.astahovtech.maxbot.core.keyboard.InlineKeyboard;
import io.github.astahovtech.maxbot.core.outgoing.OutgoingMessage;
import io.github.astahovtech.maxbot.examples.pizza.model.OrderDraft;
import io.github.astahovtech.maxbot.examples.pizza.service.OrderService;
import io.github.astahovtech.maxbot.starter.annotations.MaxBot;
import io.github.astahovtech.maxbot.starter.annotations.OnCallback;
import io.github.astahovtech.maxbot.starter.annotations.OnCommand;
import io.github.astahovtech.maxbot.starter.annotations.OnMessage;

@MaxBot
public class OrderBot {

    private static final InlineKeyboard PIZZA_KEYBOARD = InlineKeyboard.builder()
            .row(Button.callback("Маргарита — 450р", "pizza:margherita"),
                 Button.callback("Пепперони — 550р", "pizza:pepperoni"))
            .row(Button.callback("4 сыра — 650р", "pizza:quattro"),
                 Button.callback("Гавайская — 500р", "pizza:hawaiian"))
            .row(Button.callback("Назад", "menu:main"))
            .build();

    private static final InlineKeyboard SIZE_KEYBOARD = InlineKeyboard.builder()
            .row(Button.callback("25 см", "size:S"),
                 Button.callback("30 см (+30%)", "size:M"),
                 Button.callback("35 см (+60%)", "size:L"))
            .row(Button.callback("Назад", "order:back_pizza"))
            .build();

    private static final InlineKeyboard BACK_MENU = InlineKeyboard.builder()
            .row(Button.callback("В главное меню", "menu:main"))
            .build();

    private final OrderService orderService;

    public OrderBot(OrderService orderService) {
        this.orderService = orderService;
    }

    // --- Точки входа ---

    @OnCallback(prefix = "menu:order")
    public void startFromMenu(Ctx ctx) {
        startOrderFlow(ctx);
    }

    @OnCommand("order")
    public void startFromCommand(Ctx ctx) {
        startOrderFlow(ctx);
    }

    // --- Шаг 1: выбор пиццы ---

    @OnCallback(prefix = "pizza:", state = "ORDER_PIZZA")
    public void choosePizza(Ctx ctx) {
        String pizza = ctx.callbackData().substring("pizza:".length());
        orderService.setPizza(ctx.chatId(), pizza);

        ctx.reply(OutgoingMessage.text("Выберите размер:")
                .keyboard(SIZE_KEYBOARD)
                .build());
        ctx.setState("ORDER_SIZE");
    }

    // --- Шаг 2: выбор размера ---

    @OnCallback(prefix = "size:", state = "ORDER_SIZE")
    public void chooseSize(Ctx ctx) {
        String size = ctx.callbackData().substring("size:".length());
        orderService.setSize(ctx.chatId(), size);

        ctx.reply("Введите адрес доставки:");
        ctx.setState("ORDER_ADDRESS");
    }

    @OnCallback(prefix = "order:back_pizza", state = "ORDER_SIZE")
    public void backToPizza(Ctx ctx) {
        ctx.reply(OutgoingMessage.text("Выберите пиццу:")
                .keyboard(PIZZA_KEYBOARD)
                .build());
        ctx.setState("ORDER_PIZZA");
    }

    // --- Шаг 3: ввод адреса (текст) ---

    @OnMessage(textRegex = ".{5,}", state = "ORDER_ADDRESS")
    public void enterAddress(Ctx ctx) {
        orderService.setAddress(ctx.chatId(), ctx.text());
        OrderDraft draft = orderService.getDraft(ctx.chatId());

        var confirmKb = InlineKeyboard.builder()
                .row(Button.callback("Подтвердить", "order:confirm"),
                     Button.callback("Отменить", "order:cancel"))
                .build();

        ctx.reply(OutgoingMessage.text("Ваш заказ:\n" + draft.summary() + "\n\nВсё верно?")
                .keyboard(confirmKb)
                .build());
        ctx.setState("ORDER_CONFIRM");
    }

    @OnMessage(textRegex = ".{0,4}", state = "ORDER_ADDRESS")
    public void addressTooShort(Ctx ctx) {
        ctx.reply("Адрес слишком короткий. Введите полный адрес доставки:");
    }

    // --- Шаг 4: подтверждение ---

    @OnCallback(prefix = "order:confirm", state = "ORDER_CONFIRM")
    public void confirm(Ctx ctx) {
        orderService.placeOrder(ctx.chatId());
        ctx.clearState();
        ctx.reply(OutgoingMessage.text("Заказ принят! Ожидайте доставку в течение 40 минут.")
                .keyboard(BACK_MENU)
                .build());
    }

    // --- Отмена (работает из любого шага) ---

    @OnCallback(prefix = "order:cancel")
    @OnCommand("cancel")
    public void cancel(Ctx ctx) {
        orderService.cancelDraft(ctx.chatId());
        ctx.clearState();
        ctx.reply(OutgoingMessage.text("Заказ отменён.")
                .keyboard(BACK_MENU)
                .build());
    }

    // --- Приватные ---

    private void startOrderFlow(Ctx ctx) {
        orderService.createDraft(ctx.chatId());
        ctx.reply(OutgoingMessage.text("Выберите пиццу:")
                .keyboard(PIZZA_KEYBOARD)
                .build());
        ctx.setState("ORDER_PIZZA");
    }
}
