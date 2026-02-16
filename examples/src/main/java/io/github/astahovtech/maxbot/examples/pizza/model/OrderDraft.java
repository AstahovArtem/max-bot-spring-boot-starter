package io.github.astahovtech.maxbot.examples.pizza.model;

import java.time.LocalDateTime;

public class OrderDraft {

    private String pizza;
    private String size;
    private String address;
    private final LocalDateTime createdAt = LocalDateTime.now();

    public String pizza() {
        return pizza;
    }

    public void setPizza(String pizza) {
        this.pizza = pizza;
    }

    public String size() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String address() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public int price() {
        int base = switch (pizza) {
            case "margherita" -> 450;
            case "pepperoni" -> 550;
            case "quattro" -> 650;
            case "hawaiian" -> 500;
            default -> 500;
        };
        return switch (size) {
            case "M" -> (int) (base * 1.3);
            case "L" -> (int) (base * 1.6);
            default -> base;
        };
    }

    public String pizzaName() {
        return switch (pizza) {
            case "margherita" -> "Маргарита";
            case "pepperoni" -> "Пепперони";
            case "quattro" -> "4 сыра";
            case "hawaiian" -> "Гавайская";
            default -> pizza;
        };
    }

    public String sizeName() {
        return switch (size) {
            case "S" -> "25 см";
            case "M" -> "30 см";
            case "L" -> "35 см";
            default -> size;
        };
    }

    public String summary() {
        return pizzaName() + " (" + sizeName() + ")\nАдрес: " + address + "\nИтого: " + price() + " руб.";
    }
}
