package it.polimi.ingsw.Networking.Shared;

import it.polimi.ingsw.Networking.Configs.ServerConfigs;
import java.util.concurrent.*;

public class HeartBeat {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile ScheduledFuture<?> timeoutTask;
    private final Runnable onTimeout;
    private static final int TIMEOUT_SECONDS = ServerConfigs.DEFAULT_TIMEOUT_TIME;

    public HeartBeat(Runnable onTimeout) {
        this.onTimeout = onTimeout;
    }

    public void start() {
        resetTimer();
    }

    public void receivedPing() {
        resetTimer();
    }

    private void resetTimer() {
        if (timeoutTask != null) timeoutTask.cancel(false);
        timeoutTask = scheduler.schedule(onTimeout, TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public void stop() {
        if (timeoutTask != null) timeoutTask.cancel(false);
        scheduler.shutdownNow();
    }
}