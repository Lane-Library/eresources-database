package edu.stanford.irt.eresources;

import java.util.concurrent.Executor;

public class TestExecutor implements Executor {

    @Override
    public void execute(final Runnable runnable) {
        runnable.run();
    }
}
