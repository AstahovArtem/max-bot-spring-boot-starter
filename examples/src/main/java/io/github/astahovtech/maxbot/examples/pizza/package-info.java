/**
 * Пример PizzaBot — полноценный бот доставки пиццы.
 *
 * <p>Демонстрирует:</p>
 * <ul>
 *   <li>Несколько {@code @MaxBot}-классов для разных зон ответственности</li>
 *   <li>FSM (state machine) — многошаговый заказ с кнопками и текстовым вводом</li>
 *   <li>Inline-клавиатуры с навигацией</li>
 *   <li>Spring DI — сервисы и бизнес-логика</li>
 *   <li>ErrorHandler и HandlerInterceptor</li>
 * </ul>
 *
 * <p>Запуск: {@code MAX_BOT_TOKEN=... ./gradlew :examples:bootRun}
 * (общий ExampleApplication сканирует этот пакет автоматически)</p>
 */
package io.github.astahovtech.maxbot.examples.pizza;
