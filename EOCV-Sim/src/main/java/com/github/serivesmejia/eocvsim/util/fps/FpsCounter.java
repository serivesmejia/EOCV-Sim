package com.github.serivesmejia.eocvsim.util.fps;

import com.qualcomm.robotcore.util.ElapsedTime;

public class FpsCounter {

    private ElapsedTime elapsedTime = new ElapsedTime();

    private volatile int fpsC;
    private volatile int lastFps;

    public synchronized void update() {
        fpsC++;
        if(elapsedTime.seconds() >= 1) {
            lastFps = fpsC; fpsC = 0;
            elapsedTime.reset();
        }
    }

    public synchronized int getFPS(){
        return lastFps;
    }

}