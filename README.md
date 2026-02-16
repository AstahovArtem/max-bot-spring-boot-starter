# MAX Bot Spring Boot Starter

Spring Boot стартер для создания ботов в мессенджере [MAX](https://max.ru).  
Вдохновлён [TelegramBots](https://github.com/rubenlagus/TelegramBots) — простой, декларативный, расширяемый.

## Возможности

- Декларативные аннотации: `@OnCommand`, `@OnMessage`, `@OnCallback`, `@OnBotStarted` и др.
- Все типы событий MAX API: сообщения, колбэки, добавление/удаление пользователей и бота, смена названия чата
- FSM/Conversation — конечный автомат для многошаговых диалогов (`state`)
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
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.astahovtech:max-bot-spring-boot-starter:main-SNAPSHOT")
}
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
| `@MaxBot` | Помечает класс как бот-обработчик | — |
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

## Контекст (Ctx)

```java
ctx.chatId()           // ID чата
ctx.text()             // Текст сообщения
ctx.sender()           // Отправитель (BotUser)
ctx.callbackData()     // Данные callback
ctx.messageId()        // ID сообщения

ctx.reply("text")                     // Отправить текст
ctx.reply(OutgoingMessage.text("hi")  // Отправить сообщение с клавиатурой
    .keyboard(keyboard).build())
ctx.editMessage("new text")           // Редактировать сообщение
ctx.deleteMessage()                   // Удалить сообщение
ctx.answerCallback("ok")              // Ответить на callback

ctx.getChat()                         // Информация о чате
ctx.getChatMembers()                  // Участники чата
ctx.leaveChat()                       // Покинуть чат

ctx.uploadImage(file)                 // Загрузить изображение → token
ctx.uploadFile(file)                  // Загрузить файл → token

ctx.state()                           // Текущее состояние FSM
ctx.setState("STEP_2")                // Установить состояние
ctx.clearState()                      // Очистить состояние
```

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

По умолчанию состояния хранятся in-memory. Для кастомного хранилища (Redis, DB) реализуйте `StateStore`:

```java
@Bean
public StateStore stateStore(RedisTemplate<String, String> redis) {
    return new RedisStateStore(redis); // ваша реализация
}
```

## Загрузка файлов

```java
String token = ctx.uploadImage(new File("photo.jpg"));
ctx.reply(OutgoingMessage.text("Фото:")
    .attach(Attachment.photo(token))
    .build());

// Или коротко:
ctx.replyWithImage("Фото:", new File("photo.jpg"));
```

Поддерживаемые типы: `Attachment.photo()`, `Attachment.video()`, `Attachment.audio()`, `Attachment.file()`.

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

## Структура проекта

```
max-bot-spring-boot-starter/
├── max-bot-core/              — Ядро: модели, API, handlers, FSM, retry
├── max-bot-spring-boot-starter/ — Spring Boot стартер: аннотации, auto-config, actuator
└── examples/                   — Примеры ботов
```

## Лицензия

Apache-2.0 license
