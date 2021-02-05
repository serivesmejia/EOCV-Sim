/*
 * Copyright (c) 2021 Sebastian Erives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

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

    private final String name;

    private final Thread posterThread;

    public final FpsCounter fpsCounter = new FpsCounter();

    private volatile boolean hasPosterThreadStarted = false;

    public MatPoster(String name, int maxQueueItems) {
        this(name, new MatRecycler(maxQueueItems + 2));
    }

    public MatPoster(String name, MatRecycler recycler) {
        postQueue = new EvictingBlockingQueue<>(new ArrayBlockingQueue<>(recycler.getSize()));
        matRecycler = recycler;
        posterThread = new Thread(new PosterRunnable(), "MatPoster-" + name + "-Thread");

        this.name = name;

        postQueue.setEvictAction((m) -> {
            matRecycler.returnMat(m);
            m.release();
        }); //release mat and return it to recycler if it's dropped by the EvictingBlockingQueue
    }

    public void post(Mat m) {

        //start mat posting thread if it hasn't been started yet
        if (!posterThread.isAlive() && !hasPosterThreadStarted && postables.size() != 0) posterThread.start();

        if (m == null || m.empty()) {
            Log.warn("MatPoster-" + name, "Tried to post empty or null mat, skipped this frame.");
            return;
        }

        MatRecycler.RecyclableMat recycledMat = matRecycler.takeMat();
        m.copyTo(recycledMat);

        postQueue.offer(recycledMat);

    }

    public Mat pull() throws InterruptedException {
        return postQueue.take();
    }

    public void addPostable(Postable postable) {
        postables.add(postable);
    }

    public void stop() {

        Log.info("MatPoster-" + name, "Destroying...");

        posterThread.interrupt();

        for (MatRecycler.RecyclableMat m : postQueue) {
            if (m != null) {
                m.returnMat();
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

                if (postQueue.size() == 0 || postables.size() == 0) continue; //skip if we have no queued frames

                fpsCounter.update();

                try {

                    MatRecycler.RecyclableMat takenMat = postQueue.take();

                    for (Postable postable : postables) {
                        postable.post(takenMat);
                    }

                    takenMat.release();
                    takenMat.returnMat();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception ex) {
                    continue;
                }

            }

            Log.warn("MatPoster-" + name +"-Thread", "Thread interrupted (" + Integer.toHexString(hashCode()) + ")");

        }
    }

}