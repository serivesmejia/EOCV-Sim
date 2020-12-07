package com.github.serivesmejia.eocvsim.util;

import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;

public class BufferedImageHolder {

    private final BufferedImageHold[] allHolders;
    private final ArrayBlockingQueue<BufferedImageHold> availableHolders;

    public BufferedImageHolder(int holderAmount) {

        allHolders = new BufferedImageHold[holderAmount];
        availableHolders = new ArrayBlockingQueue<>(holderAmount);

        for (int i = 0; i < allHolders.length; i++) {
            allHolders[i] = new BufferedImageHold(i);
            availableHolders.add(allHolders[i]);
        }

    }

    public BufferedImageHold hold(BufferedImage img) {

        if (!hasSpace()) {
            throw new RuntimeException("No more space for holding new BufferedImage!");
        }

        BufferedImageHold holder = null;
        try {
            holder = availableHolders.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (holder != null) {
            holder.image = img;
        }

        return holder;

    }

    public synchronized void returnHold(BufferedImageHold holder) {

        if (holder != allHolders[holder.idx]) {
            throw new IllegalArgumentException("This holder does not belong here!");
        }

        if (holder.isCheckedOut) {
            holder.isCheckedOut = false;
            holder.flush();
            holder.image = null; //remove reference to image (pls gc free this memory)
            availableHolders.add(holder);
        } else {
            try {
                throw new IllegalArgumentException("This holder was already returned!");
            } catch (IllegalArgumentException ex) {
                Log.error("BufferedImageHolder", ex);
            }
        }

    }

    public synchronized void flushAll() {
        for (BufferedImageHold holder : allHolders) {
            holder.flush();
        }
    }

    public synchronized boolean hasSpace() {
        return availableHolders.size() != 0;
    }

    public static class BufferedImageHold {

        protected final int idx;
        protected volatile BufferedImage image;
        protected volatile boolean isCheckedOut = false;

        protected BufferedImageHold(int idx) {
            this.idx = idx;
        }

        public BufferedImage getBufferedImage() {
            return image;
        }

        public void flush() {
            if (image != null) image.flush();
        }

        public boolean isReturned() {
            return !isCheckedOut;
        }

    }

}