package io.github.astahovtech.maxbot.starter.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.github.astahovtech.maxbot.core.Ctx;
import io.github.astahovtech.maxbot.core.handler.Handler;
import io.github.astahovtech.maxbot.core.handler.impl.BotStartedHandler;
import io.github.astahovtech.maxbot.core.handler.impl.CallbackHandler;
import io.github.astahovtech.maxbot.core.handler.impl.CommandHandler;
import io.github.astahovtech.maxbot.core.handler.impl.MessageHandler;
import io.github.astahovtech.maxbot.core.handler.impl.UpdateTypeHandler;
import io.github.astahovtech.maxbot.core.model.UpdateType;
import io.github.astahovtech.maxbot.starter.annotations.MaxBot;
import io.github.astahovtech.maxbot.starter.annotations.OnBotAdded;
import io.github.astahovtech.maxbot.starter.annotations.OnBotRemoved;
import io.github.astahovtech.maxbot.starter.annotations.OnBotStarted;
import io.github.astahovtech.maxbot.starter.annotations.OnCallback;
import io.github.astahovtech.maxbot.starter.annotations.OnChatTitleChanged;
import io.github.astahovtech.maxbot.starter.annotations.OnCommand;
import io.github.astahovtech.maxbot.starter.annotations.OnMessage;
import io.github.astahovtech.maxbot.starter.annotations.OnMessageEdited;
import io.github.astahovtech.maxbot.starter.annotations.OnMessageRemoved;
import io.github.astahovtech.maxbot.starter.annotations.OnUserAdded;
import io.github.astahovtech.maxbot.starter.annotations.OnUserRemoved;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class MaxBotHandlerRegistrar implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        BeanFactory beanFactory = (BeanFactory) registry;
        List<HandlerDescriptor> descriptors = new ArrayList<>();

        for (String beanName : registry.getBeanDefinitionNames()) {
            Class<?> beanClass = resolveBeanClass(registry.getBeanDefinition(beanName));
            if (beanClass == null || !beanClass.isAnnotationPresent(MaxBot.class)) {
                continue;
            }
            scanMethods(beanName, beanClass, beanFactory, descriptors);
        }

        descriptors.sort(Comparator.comparingInt(HandlerDescriptor::order)
                .thenComparing(HandlerDescriptor::methodName));

        Set<String> usedNames = new HashSet<>();
        for (HandlerDescriptor desc : descriptors) {
            String name = uniqueName(desc, usedNames);
            RootBeanDefinition def = new RootBeanDefinition();
            def.setBeanClass(desc.handlerType);
            def.setInstanceSupplier(desc.factory);
            registry.registerBeanDefinition(name, def);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    private void scanMethods(String beanName, Class<?> beanClass, BeanFactory beanFactory,
                             List<HandlerDescriptor> descriptors) {
        for (Method method : beanClass.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                continue;
            }

            OnCommand onCommand = method.getAnnotation(OnCommand.class);
            OnMessage onMessage = method.getAnnotation(OnMessage.class);
            OnCallback onCallback = method.getAnnotation(OnCallback.class);
            OnBotStarted onBotStarted = method.getAnnotation(OnBotStarted.class);
            OnBotAdded onBotAdded = method.getAnnotation(OnBotAdded.class);
            OnBotRemoved onBotRemoved = method.getAnnotation(OnBotRemoved.class);
            OnMessageEdited onMessageEdited = method.getAnnotation(OnMessageEdited.class);
            OnMessageRemoved onMessageRemoved = method.getAnnotation(OnMessageRemoved.class);
            OnUserAdded onUserAdded = method.getAnnotation(OnUserAdded.class);
            OnUserRemoved onUserRemoved = method.getAnnotation(OnUserRemoved.class);
            OnChatTitleChanged onChatTitleChanged = method.getAnnotation(OnChatTitleChanged.class);

            if (onCommand == null && onMessage == null && onCallback == null
                    && onBotStarted == null && onBotAdded == null && onBotRemoved == null
                    && onMessageEdited == null && onMessageRemoved == null
                    && onUserAdded == null && onUserRemoved == null && onChatTitleChanged == null) {
                continue;
            }

            HandlerMethodValidator.validate(method);
            method.setAccessible(true);

            Consumer<Ctx> action = action(beanFactory, beanName, beanClass, method);

            if (onCommand != null) {
                descriptors.add(descriptor(beanClass, method, onCommand.order(), "OnCommand",
                        CommandHandler.class, () -> new CommandHandler(onCommand.value(), action)));
            }
            if (onMessage != null) {
                descriptors.add(descriptor(beanClass, method, onMessage.order(), "OnMessage",
                        MessageHandler.class, () -> new MessageHandler(onMessage.textRegex(), action)));
            }
            if (onCallback != null) {
                descriptors.add(descriptor(beanClass, method, onCallback.order(), "OnCallback",
                        CallbackHandler.class, () -> new CallbackHandler(onCallback.prefix(), action)));
            }
            if (onBotStarted != null) {
                descriptors.add(descriptor(beanClass, method, onBotStarted.order(), "OnBotStarted",
                        BotStartedHandler.class, () -> new BotStartedHandler(action)));
            }
            if (onBotAdded != null) {
                descriptors.add(typeDescriptor(beanClass, method, onBotAdded.order(),
                        "OnBotAdded", UpdateType.BOT_ADDED, action));
            }
            if (onBotRemoved != null) {
                descriptors.add(typeDescriptor(beanClass, method, onBotRemoved.order(),
                        "OnBotRemoved", UpdateType.BOT_REMOVED, action));
            }
            if (onMessageEdited != null) {
                descriptors.add(typeDescriptor(beanClass, method, onMessageEdited.order(),
                        "OnMessageEdited", UpdateType.MESSAGE_EDITED, action));
            }
            if (onMessageRemoved != null) {
                descriptors.add(typeDescriptor(beanClass, method, onMessageRemoved.order(),
                        "OnMessageRemoved", UpdateType.MESSAGE_REMOVED, action));
            }
            if (onUserAdded != null) {
                descriptors.add(typeDescriptor(beanClass, method, onUserAdded.order(),
                        "OnUserAdded", UpdateType.USER_ADDED, action));
            }
            if (onUserRemoved != null) {
                descriptors.add(typeDescriptor(beanClass, method, onUserRemoved.order(),
                        "OnUserRemoved", UpdateType.USER_REMOVED, action));
            }
            if (onChatTitleChanged != null) {
                descriptors.add(typeDescriptor(beanClass, method, onChatTitleChanged.order(),
                        "OnChatTitleChanged", UpdateType.CHAT_TITLE_CHANGED, action));
            }
        }
    }

    private HandlerDescriptor typeDescriptor(Class<?> beanClass, Method method, int order,
                                             String annotationType, UpdateType updateType,
                                             Consumer<Ctx> action) {
        return descriptor(beanClass, method, order, annotationType,
                UpdateTypeHandler.class, () -> new UpdateTypeHandler(updateType, action));
    }

    private HandlerDescriptor descriptor(Class<?> beanClass, Method method, int order,
                                         String annotationType, Class<? extends Handler> handlerType,
                                         Supplier<Handler> factory) {
        return new HandlerDescriptor(beanClass.getSimpleName(), method.getName(),
                order, annotationType, handlerType, factory);
    }

    private Consumer<Ctx> action(BeanFactory beanFactory, String beanName, Class<?> beanClass, Method method) {
        Object bean = beanFactory.getBean(beanName);
        return ctx -> {
            try {
                method.invoke(bean, ctx);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(
                        "Handler invocation failed: " + beanClass.getName() + "#" + method.getName(),
                        e.getCause());
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(
                        "Handler invocation failed: " + beanClass.getName() + "#" + method.getName(), e);
            }
        };
    }

    private Class<?> resolveBeanClass(BeanDefinition bd) {
        if (bd instanceof AbstractBeanDefinition abd && abd.hasBeanClass()) {
            return abd.getBeanClass();
        }
        String className = bd.getBeanClassName();
        if (className == null) {
            return null;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private String uniqueName(HandlerDescriptor desc, Set<String> used) {
        String base = "maxBotHandler:" + desc.beanClassName + "#" + desc.methodName + ":" + desc.annotationType;
        String name = base;
        int counter = 0;
        while (used.contains(name)) {
            name = base + "#" + (++counter);
        }
        used.add(name);
        return name;
    }

    private record HandlerDescriptor(
            String beanClassName,
            String methodName,
            int order,
            String annotationType,
            Class<? extends Handler> handlerType,
            Supplier<Handler> factory
    ) {
    }
}
