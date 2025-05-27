package org.qualiservice.qualianon.audit;

public class TimedRunner {

    public static long run(Runnable runnable) {
        final long startTime = System.currentTimeMillis();
        runnable.run();
        return System.currentTimeMillis() - startTime;
    }

    public static String format(long time) {
        return "(" + time + "ms)";
    }

}
