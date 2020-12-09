package com.github.serivesmejia.eocvsim.gui.util;

import com.github.serivesmejia.eocvsim.util.Log;
import com.github.serivesmejia.eocvsim.util.fps.FpsCounter;
import org.firstinspires.ftc.robotcore.internal.collections.EvictingBlockingQueue;
import org.opencv.core.Mat;
import org.openftc.easyopencv.MatRecycler;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class MatPoster {

    private final ArrayList<Postable> postables = new ArrayList<>();

    private final EvictingBlockingQueue<MatRecycler.RecyclableMat> postQueue;
    private final MatRecycler matRecycler;

    private final Thread posterThread = new Thread(new PosterRunnable(), "MatPoster-Thread");

    public final FpsCounter fpsCounter = new FpsCounter();

    private volatile boolean hasPosterThreadStarted = false;

    public MatPoster(int maxQueueItems) {

        postQueue = new EvictingBlockingQueue<>(new ArrayBlockingQueue<>(maxQueueItems));
        matRecycler = new MatRecycler(maxQueueItems + 2);

        postQueue.setEvictAction((m) -> {
            matRecycler.returnMat(m);
            m.release();
        }); //release mat and return it to recycler if it's dropped by the EvictingBlockingQueue

    }

    public void post(Mat m) {

        //start mat posting thread if it hasn't been started yet
        if (!posterThread.isAlive() && !hasPosterThreadStarted) posterThread.start();

        if (m == null || m.empty()) {
            Log.warn("MatPoster", "Tried to post empty or null mat, skipped this frame.");
            return;
        }

        MatRecycler.RecyclableMat recycledMat = matRecycler.takeMat();
        m.copyTo(recycledMat);

        postQueue.offer(recycledMat);

    }

    public void addPostable(Postable postable) {
        postables.add(postable);
    }

    public void stop() {

        Log.info("MatPoster", "Destroying...");

        posterThread.interrupt();

        for (MatRecycler.RecyclableMat m : postQueue) {
            if (m != null) {
                matRecycler.returnMat(m);
            }
        }

        matRecycler.releaseAll();

    }

    public interface Postable {
        void post(Mat m);
    }

    private class PosterRunnable implements Runnable {
        @Override
        public void run() {
            hasPosterThreadStarted = true;
            while (!Thread.interrupted()) {

                if (postQueue.size() == 0) continue; //skip if we have no queued frames

                fpsCounter.update();

                try {

                    MatRecycler.RecyclableMat takenMat = postQueue.take();

                    for (Postable postable : postables) {
                        postable.post(takenMat);
                    }

                    takenMat.release();
                    matRecycler.returnMat(takenMat);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception ex) {
                    continue;
                }

            }

            Log.warn("MatPoster-Thread", "Thread interrupted (" + Integer.toHexString(hashCode()) + ")");

        }
    }

}