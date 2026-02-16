package io.github.astahovtech.maxbot.examples.pizza.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.astahovtech.maxbot.examples.pizza.model.OrderDraft;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final ConcurrentHashMap<Long, OrderDraft> drafts = new ConcurrentHashMap<>();
    private final AtomicInteger completedOrders = new AtomicInteger(0);

    public void createDraft(long chatId) {
        drafts.put(chatId, new OrderDraft());
    }

    public OrderDraft getDraft(long chatId) {
        return drafts.get(chatId);
    }

    public void setPizza(long chatId, String pizza) {
        OrderDraft draft = drafts.get(chatId);
        if (draft != null) {
            draft.setPizza(pizza);
        }
    }

    public void setSize(long chatId, String size) {
        OrderDraft draft = drafts.get(chatId);
        if (draft != null) {
            draft.setSize(size);
        }
    }

    public void setAddress(long chatId, String address) {
        OrderDraft draft = drafts.get(chatId);
        if (draft != null) {
            draft.setAddress(address);
        }
    }

    public void placeOrder(long chatId) {
        OrderDraft draft = drafts.remove(chatId);
        if (draft != null) {
            completedOrders.incrementAndGet();
        }
    }

    public void cancelDraft(long chatId) {
        drafts.remove(chatId);
    }

    public int todayCount() {
        return completedOrders.get();
    }

    public int activeDraftsCount() {
        return drafts.size();
    }
}
