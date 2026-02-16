package io.github.astahovtech.maxbot.starter.transport;

import io.github.astahovtech.maxbot.core.api.MaxApi;
import io.github.astahovtech.maxbot.core.dispatcher.UpdateDispatcher;
import io.github.astahovtech.maxbot.core.mapper.UpdateMapper;
import io.github.astahovtech.maxbot.core.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import ru.max.botapi.MaxBotAPI;
import ru.max.botapi.model.UpdateList;

public class LongPollingRunner implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(LongPollingRunner.class);

    private final MaxBotAPI botApi;
    private final UpdateDispatcher dispatcher;
    private final UpdateMapper updateMapper;
    private final MaxApi maxApi;
    private final int timeout;
    private final int limit;

    private volatile boolean running;
    private volatile Thread pollingThread;

    public LongPollingRunner(MaxBotAPI botApi, UpdateDispatcher dispatcher,
                             UpdateMapper updateMapper, MaxApi maxApi,
                             int timeout, int limit) {
        this.botApi = botApi;
        this.dispatcher = dispatcher;
        this.updateMapper = updateMapper;
        this.maxApi = maxApi;
        this.timeout = timeout;
        this.limit = limit;
    }

    @Override
    public void start() {
        if (running) return;
        running = true;
        pollingThread = new Thread(this::poll, "max-bot-polling");
        pollingThread.setDaemon(true);
        pollingThread.start();
        log.info("MAX Bot long polling started (timeout={}s, limit={})", timeout, limit);
    }

    @Override
    public void stop() {
        running = false;
        if (pollingThread != null) {
            pollingThread.interrupt();
            try {
                pollingThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("MAX Bot long polling stopped");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    private void poll() {
        Long marker = null;
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                var query = botApi.getUpdates();
                if (marker != null) query.marker(marker);
                query.timeout(timeout);
                query.limit(limit);

                UpdateList result = query.execute();

                if (result.getUpdates() != null) {
                    for (ru.max.botapi.model.Update apiUpdate : result.getUpdates()) {
                        try {
                            Update update = updateMapper.map(apiUpdate);
                            dispatcher.dispatch(maxApi, update);
                        } catch (Exception e) {
                            log.error("Error dispatching update", e);
                        }
                    }
                }

                marker = result.getMarker();
            } catch (Exception e) {
                if (!running || Thread.currentThread().isInterrupted()) break;
                log.error("Long polling error, retrying in 3s", e);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
