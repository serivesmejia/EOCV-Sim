package com.github.serivesmejia.eocvsim.gui.util;

import org.opencv.core.Mat;

import javax.swing.*;
public class MatPoster {

    private JLabel postTo;

    private Thread posterThread = new Thread(new PosterRunnable());

    public MatPoster(JLabel postTo) {
        this.postTo = postTo;
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