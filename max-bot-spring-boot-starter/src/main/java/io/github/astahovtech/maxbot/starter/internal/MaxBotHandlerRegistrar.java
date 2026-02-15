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
import io.github.astahovtech.maxbot.core.handler.impl.CallbackHandler;
import io.github.astahovtech.maxbot.core.handler.impl.CommandHandler;
import io.github.astahovtech.maxbot.core.handler.impl.MessageHandler;
import io.github.astahovtech.maxbot.starter.annotations.MaxBot;
import io.github.astahovtech.maxbot.starter.annotations.OnCallback;
import io.github.astahovtech.maxbot.starter.annotations.OnCommand;
import io.github.astahovtech.maxbot.starter.annotations.OnMessage;

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

            if (onCommand == null && onMessage == null && onCallback == null) {
                continue;
            }

            HandlerMethodValidator.validate(method);
            method.setAccessible(true);

            if (onCommand != null) {
                descriptors.add(new HandlerDescriptor(
                        beanClass.getSimpleName(), method.getName(), onCommand.order(), "OnCommand",
                        CommandHandler.class,
                        () -> new CommandHandler(onCommand.value(), action(beanFactory, beanName, beanClass, method))));
            }
            if (onMessage != null) {
                descriptors.add(new HandlerDescriptor(
                        beanClass.getSimpleName(), method.getName(), onMessage.order(), "OnMessage",
                        MessageHandler.class,
                        () -> new MessageHandler(onMessage.textRegex(), action(beanFactory, beanName, beanClass, method))));
            }
            if (onCallback != null) {
                descriptors.add(new HandlerDescriptor(
                        beanClass.getSimpleName(), method.getName(), onCallback.order(), "OnCallback",
                        CallbackHandler.class,
                        () -> new CallbackHandler(onCallback.prefix(), action(beanFactory, beanName, beanClass, method))));
            }
        }
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
