package com.github.serivesmejia.eocvsim.util;

public class FpsLimiter {

    int fps;

    double start, diff, wait;

    public FpsLimiter(int fps) {
        this.fps = fps;
    }

    public void sync() throws InterruptedException {

        wait = 1d / ((double) fps / 1000d);

        diff = System.currentTimeMillis() - start;
        if (diff < wait) {
            Thread.sleep((long) (wait - diff));
        }

        start = System.currentTimeMillis();

    }

    public void setFPS(int fps) {
        this.fps = fps;
    }

    public int getFPS() { return fps; }

}
