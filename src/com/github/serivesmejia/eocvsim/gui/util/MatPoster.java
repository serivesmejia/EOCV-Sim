package com.github.serivesmejia.eocvsim.gui.util;

import com.github.serivesmejia.eocvsim.gui.Visualizer;
import com.github.serivesmejia.eocvsim.util.CvUtil;
import org.opencv.core.Mat;
import org.openftc.easyopencv.MatRecycler;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;

public class MatPoster {

    private final Visualizer visualizer;

    private final ArrayBlockingQueue<Mat> postQueue;
    private final Thread posterThread = new Thread(new PosterRunnable());

    private final int maxQueueItems;

    private volatile boolean hasPosterThreadStarted = false;

    public MatPoster(Visualizer visualizer, int maxQueueItems) {
        this.visualizer = visualizer;
        this.maxQueueItems = maxQueueItems;
        postQueue = new ArrayBlockingQueue<>(maxQueueItems);
    }

    public void post(Mat m) {

        //start mat posting thread if it hasn't been started yet
        if(!posterThread.isAlive() && !hasPosterThreadStarted) posterThread.start();

        if(postQueue.size() >= maxQueueItems) {
            try {
                postQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

        postQueue.add(m);

    }

    public void stop() {
        posterThread.interrupt();
    }

    private class PosterRunnable implements Runnable {
        @Override
        public void run() {
            hasPosterThreadStarted = true;
            while(!Thread.interrupted()) {
                synchronized(postQueue) {
                    if(postQueue.size() == 0) return;
                    try {
                        Mat currentMat = postQueue.take();
                        visualizer.updateVisualizedMat(currentMat);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}