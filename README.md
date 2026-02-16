# MAX Bot Spring Boot Starter

[![Maven Central](https://img.shields.io/maven-central/v/io.github.astahovtech/max-bot-spring-boot-starter)](https://central.sonatype.com/artifact/io.github.astahovtech/max-bot-spring-boot-starter)
[![License](https://img.shields.io/github/license/AstahovArtem/max-bot-spring-boot-starter)](LICENSE)
[![CI](https://github.com/AstahovArtem/max-bot-spring-boot-starter/actions/workflows/ci.yml/badge.svg)](https://github.com/AstahovArtem/max-bot-spring-boot-starter/actions/workflows/ci.yml)
[![Java](https://img.shields.io/badge/Java-21+-blue)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](https://spring.io/projects/spring-boot)

Spring Boot стартер для создания ботов в мессенджере [MAX](https://max.ru).  
Вдохновлён [TelegramBots](https://github.com/rubenlagus/TelegramBots) — простой, декларативный, расширяемый.

## Возможности

- Декларативные аннотации: `@OnCommand`, `@OnMessage`, `@OnCallback`, `@OnBotStarted` и др.
- Все типы событий MAX API: сообщения, колбэки, добавление/удаление пользователей и бота, смена названия чата
- FSM/Conversation — конечный автомат для многошаговых диалогов (`state`)
- Несколько `@MaxBot`-классов в одном приложении — разбивайте бота по зонам ответственности
- Загрузка файлов: фото, видео, аудио, документы
- Inline-клавиатуры с callback- и link-кнопками
- Chat API: информация о чате, участники, выход
- Middleware/Interceptors + глобальный ErrorHandler
- Retry с backoff для upload-операций
- Spring Boot Actuator: health check + Micrometer метрики
- Long Polling (по умолчанию) и Webhook режимы

## Быстрый старт

### 1. Подключение зависимости

**Gradle (Kotlin DSL):**

```kotlin
dependencies {
    implementation("io.github.astahovtech:max-bot-spring-boot-starter:0.1.0")
}
```

**Gradle (Groovy):**

```groovy
dependencies {
    implementation 'io.github.astahovtech:max-bot-spring-boot-starter:0.1.0'
}
```

**Maven:**

```xml
<dependency>
    <groupId>io.github.astahovtech</groupId>
    <artifactId>max-bot-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

### 2. Настройка `application.yml`

```yaml
max:
  bot:
    access-token: ${MAX_BOT_TOKEN}
```

### 3. Создание бота

```java
@MaxBot
public class MyBot {

    @OnBotStarted
    public void onStart(Ctx ctx) {
        ctx.reply("Привет! Я бот для MAX.");
    }

    @OnCommand("help")
    public void help(Ctx ctx) {
        ctx.reply("Доступные команды:\n/help — помощь");
    }

    @OnMessage(textRegex = ".*")
    public void echo(Ctx ctx) {
        ctx.reply("Вы написали: " + ctx.text());
    }
}
```

Готово! Запускайте `@SpringBootApplication` и бот работает на long polling.

## Аннотации

| Аннотация | Описание | Параметры |
|---|---|---|
| `@MaxBot` | Помечает класс как бот-обработчик (автоматически `@Component`) | — |
| `@OnCommand("cmd")` | Обработка команды `/cmd` | `value`, `state`, `order` |
| `@OnMessage(textRegex)` | Обработка сообщений по regex | `textRegex`, `state`, `order` |
| `@OnCallback(prefix)` | Обработка callback по префиксу | `prefix`, `state`, `order` |
| `@OnBotStarted` | Пользователь нажал «Начать» | `order` |
| `@OnBotAdded` | Бот добавлен в чат | `order` |
| `@OnBotRemoved` | Бот удалён из чата | `order` |
| `@OnUserAdded` | Пользователь добавлен в чат | `order` |
| `@OnUserRemoved` | Пользователь удалён из чата | `order` |
| `@OnMessageEdited` | Сообщение отредактировано | `order` |
| `@OnMessageRemoved` | Сообщение удалено | `order` |
| `@OnChatTitleChanged` | Название чата изменено | `order` |

Все handler-методы принимают единственный параметр `Ctx`.

Параметр `order` управляет приоритетом: handler с меньшим значением проверяется первым (по умолчанию `0`).

## Контекст (Ctx)

```java
// --- Данные обновления ---
ctx.chatId()           // ID чата
ctx.text()             // Текст сообщения (или новое название чата для CHAT_TITLE_CHANGED)
ctx.sender()           // Отправитель (BotUser)
ctx.callbackData()     // Данные callback
ctx.callbackId()       // ID callback (для answerCallback)
ctx.messageId()        // ID сообщения
ctx.payload()          // Payload из deep link (для BOT_STARTED)

// --- Ответы ---
ctx.reply("text")                        // Отправить текст
ctx.reply(OutgoingMessage.text("hi")     // Отправить сообщение с клавиатурой
    .keyboard(keyboard).build())
ctx.editMessage("new text")              // Редактировать сообщение
ctx.deleteMessage()                      // Удалить сообщение
ctx.answerCallback("ok")                 // Ответить на callback (уведомление)
ctx.answerCallbackWithMessage(msg)       // Ответить на callback сообщением

// --- Чат ---
ctx.getChat()                            // Информация о чате (BotChat)
ctx.getChatMembers()                     // Участники чата (List<BotChatMember>)
ctx.leaveChat()                          // Покинуть чат

// --- Загрузка файлов ---
ctx.uploadImage(file)                    // Загрузить изображение → token
ctx.uploadVideo(file)                    // Загрузить видео → token
ctx.uploadAudio(file)                    // Загрузить аудио → token
ctx.uploadFile(file)                     // Загрузить файл → token
ctx.replyWithImage("text", file)         // Загрузить и отправить фото одной строкой

// --- FSM (состояния) ---
ctx.state()                              // Текущее состояние (String или null)
ctx.setState("STEP_2")                   // Установить состояние
ctx.clearState()                         // Очистить состояние

// --- API напрямую ---
ctx.api()                                // Доступ к MaxApi для нестандартных операций
```

## Несколько `@MaxBot`-классов

Для больших ботов разбивайте логику по нескольким классам. Все handler-ы из всех `@MaxBot`-классов автоматически собираются в единый диспетчер:

```
bot/
├── MainMenuBot.java        — /start, /help, главное меню
├── OrderBot.java           — оформление заказа (FSM)
├── SettingsBot.java        — настройки
└── AdminBot.java           — админ-команды
```

```java
@MaxBot
public class MainMenuBot {
    @OnCommand(value = "start", order = -100)  // проверяется первым
    public void start(Ctx ctx) { ... }
}

@MaxBot
public class OrderBot {
    private final OrderService orderService;

    public OrderBot(OrderService orderService) {  // Spring DI
        this.orderService = orderService;
    }

    @OnCommand("order")
    public void startOrder(Ctx ctx) { ... }

    @OnMessage(textRegex = ".*", state = "WAITING_PRODUCT")
    public void receiveProduct(Ctx ctx) { ... }
}
```

`@MaxBot`-классы — обычные Spring-компоненты. Можно инжектить любые бины: JPA-репозитории, RestTemplate, Redis, Kafka и т.д.

## Клавиатуры

```java
var keyboard = InlineKeyboard.builder()
    .row(Button.callback("Да", "confirm:yes"),
         Button.callback("Нет", "confirm:no"))
    .row(Button.link("Документация", "https://dev.max.ru"))
    .build();

ctx.reply(OutgoingMessage.text("Подтвердите:")
    .keyboard(keyboard)
    .build());
```

## FSM / Многошаговые диалоги

Используйте параметр `state` в аннотациях для создания диалоговых цепочек:

```java
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
}
```

Handler без `state` срабатывает **в любом состоянии** (например, `/cancel` для отмены из любого шага).

По умолчанию состояния хранятся in-memory (`InMemoryStateStore`). Для кластера или персистентности реализуйте `StateStore`:

```java
@Bean
public StateStore stateStore(RedisTemplate<String, String> redis) {
    return new RedisStateStore(redis); // ваша реализация — 3 метода
}
```

## Загрузка файлов

```java
// Загрузка + отправка по шагам
String token = ctx.uploadImage(new File("photo.jpg"));
ctx.reply(OutgoingMessage.text("Фото:")
    .attach(Attachment.photo(token))
    .build());

// Или коротко:
ctx.replyWithImage("Фото:", new File("photo.jpg"));
```

Поддерживаемые типы: `Attachment.photo()`, `Attachment.video()`, `Attachment.audio()`, `Attachment.file()`.

Можно добавить несколько вложений в одно сообщение:

```java
ctx.reply(OutgoingMessage.text("Документы:")
    .attach(Attachment.file(token1))
    .attach(Attachment.file(token2))
    .build());
```

## Interceptors и ErrorHandler

```java
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(Ctx ctx) {
        log.info("Update: type={}, chatId={}", ctx.update().type(), ctx.chatId());
        return true; // false — отклонить обновление
    }

    @Override
    public void postHandle(Ctx ctx) {
        log.info("Handler completed for chatId={}", ctx.chatId());
    }

    @Override
    public void onError(Ctx ctx, Exception ex) {
        log.error("Error in chatId={}", ctx.chatId(), ex);
    }
}

@Bean
public ErrorHandler errorHandler() {
    return (ctx, ex) -> ctx.reply("Произошла ошибка, попробуйте позже.");
}
```

## Webhook режим

```yaml
max:
  bot:
    access-token: ${MAX_BOT_TOKEN}
    webhook:
      enabled: true
      path: /webhook
      secret: your-secret-here
```

При `webhook.enabled=true` создаётся `@RestController` на указанном `path`. Не забудьте подключить `spring-boot-starter-web`.

## Spring Boot Actuator

При наличии `spring-boot-starter-actuator` в classpath автоматически появляется health check:

```
GET /actuator/health
```

```json
{
  "status": "UP",
  "components": {
    "maxBot": {
      "status": "UP",
      "details": {
        "botName": "MyBot",
        "botUsername": "my_bot",
        "botId": 12345
      }
    }
  }
}
```

## Micrometer метрики

При наличии `micrometer-core` в classpath автоматически собираются:

- `maxbot.updates.received` — счётчик обновлений (tag: `type`)
- `maxbot.handler.duration` — время обработки (tag: `type`)
- `maxbot.handler.errors` — счётчик ошибок (tags: `type`, `exception`)

## Retry

Upload-операции автоматически повторяются с exponential backoff (3 попытки, 500ms → 1000ms → 2000ms).

Кастомная политика:

```java
@Bean
public RetryPolicy retryPolicy() {
    return new RetryPolicy(5, 1000, 2.0); // 5 попыток, 1с стартовая задержка, x2
}
```

## Примеры

В модуле `examples/` — готовые примеры ботов:

| Пример | Описание |
|---|---|
| `ExampleBot` | Простой бот: echo, команды, callback-кнопки |
| `OrderBot` | FSM-диалог: многошаговый заказ |
| `pizza/` | **Полноценный бот доставки** — несколько `@MaxBot`-классов, FSM, Spring DI, interceptors, error handler |

### PizzaBot — структура

```
examples/.../pizza/
├── bot/
│   ├── MainMenuBot.java       — /start, /help, каталог, контакты
│   ├── OrderBot.java          — FSM-заказ: пицца → размер → адрес → подтверждение
│   └── AdminBot.java          — /stats — статистика заказов
├── service/
│   └── OrderService.java      — бизнес-логика заказов (in-memory)
├── model/
│   └── OrderDraft.java        — данные заказа + расчёт цены
└── config/
    └── BotConfig.java         — ErrorHandler + LoggingInterceptor
```

Запуск: `MAX_BOT_TOKEN=... ./gradlew :examples:bootRun`

## Требования

- Java 21+
- Spring Boot 3.x
- Токен бота MAX (получить на [business.max.ru](https://business.max.ru))

## Структура проекта

```
max-bot-spring-boot-starter/
├── max-bot-core/                — Ядро: модели, API, handlers, FSM, retry
├── max-bot-spring-boot-starter/ — Spring Boot стартер: аннотации, auto-config, actuator
└── examples/                    — Примеры ботов
```

## Содействие

Нашли баг или есть идея? Создайте [issue](https://github.com/AstahovArtem/max-bot-spring-boot-starter/issues) или отправьте pull request.

## Лицензия

[Apache-2.0](LICENSE)

---

Разработка ботов для MAX на заказ — Telegram: [@artem_astt](https://t.me/artem_astt)
