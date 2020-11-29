package com.github.serivesmejia.eocvsim.gui.util;

import com.github.serivesmejia.eocvsim.util.Log;
import org.firstinspires.ftc.robotcore.external.function.Consumer;
import org.firstinspires.ftc.robotcore.internal.collections.EvictingBlockingQueue;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class MatPoster {

    private final ArrayList<Postable> postables = new ArrayList<>();

    private final EvictingBlockingQueue<Mat> postQueue;
    private final Thread posterThread = new Thread(new PosterRunnable(), "MatPoster-Thread");

    private final int maxQueueItems;

    private volatile boolean hasPosterThreadStarted = false;

    public MatPoster(int maxQueueItems) {

        this.maxQueueItems = maxQueueItems;

        postQueue = new EvictingBlockingQueue<>(new ArrayBlockingQueue<>(maxQueueItems));
        postQueue.setEvictAction(Mat::release);

    }

    public void post(Mat m) {

        //start mat posting thread if it hasn't been started yet
        if(!posterThread.isAlive() && !hasPosterThreadStarted) posterThread.start();

        if(m == null || m.empty()) {
            Log.warn("MatPoster", "Tried to post empty or null mat, skipped this frame.");
            return;
        }

        postQueue.offer(m);

    }

    public void addPostable(Postable postable) {
        postables.add(postable);
    }

    public void stop() {
        posterThread.interrupt();
    }

    private class PosterRunnable implements Runnable {
        @Override
        public void run() {
            hasPosterThreadStarted = true;
            while(!Thread.interrupted()) {

                if(postQueue.size() == 0) continue; //skip if we have no queued frames

                try {

                    Mat takenMat = postQueue.take();

                    Imgproc.cvtColor(takenMat, takenMat, Imgproc.COLOR_RGB2BGR);

                    for(Postable postable : postables) {
                        postable.post(takenMat);
                    }

                    takenMat.release();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception ex) {
                    continue;
                }

            }

        }
    }

    public interface Postable {
        void post(Mat m);
    }

}