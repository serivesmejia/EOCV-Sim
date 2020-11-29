package com.github.serivesmejia.eocvsim.gui.util;

import org.opencv.core.Mat;
import org.openftc.easyopencv.MatRecycler;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;

public class MatPoster {

    private volatile JLabel postTo;

    private ArrayBlockingQueue<Mat> postQueue;

    private volatile MatRecycler matRecycler;

    private Thread posterThread = new Thread(new PosterRunnable());

    public MatPoster(JLabel postTo, int maxQueueItems) {

        this.postTo = postTo;

        postQueue = new ArrayBlockingQueue<>(maxQueueItems);
        matRecycler = new MatRecycler(maxQueueItems);

    }

    public void post(Mat m) {



    }

    private class PosterRunnable implements Runnable {
        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {

            }
        }
    }

}