package com.github.serivesmejia.eocvsim.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;

public class BufferedImageRecycler {

    private final RecyclableBufferedImage[] allBufferedImages;
    private final ArrayBlockingQueue<RecyclableBufferedImage> availableBufferedImages;

    public BufferedImageRecycler(int num, int allImgWidth, int allImgHeight, int allImgType) {
        allBufferedImages = new RecyclableBufferedImage[num];
        availableBufferedImages = new ArrayBlockingQueue<>(num);

        for (int i = 0; i < allBufferedImages.length; i++) {
            allBufferedImages[i] = new RecyclableBufferedImage(i, allImgWidth, allImgHeight, allImgType);
            availableBufferedImages.add(allBufferedImages[i]);
        }
    }

    public BufferedImageRecycler(int num, Dimension allImgSize, int allImgType) {
        this(num, (int)allImgSize.getWidth(), (int)allImgSize.getHeight(), allImgType);
    }

    public BufferedImageRecycler(int num, int allImgWidth, int allImgHeight) {
        this(num, allImgWidth, allImgHeight, BufferedImage.TYPE_3BYTE_BGR);
    }

    public BufferedImageRecycler(int num, Dimension allImgSize) {
        this(num, (int)allImgSize.getWidth(), (int)allImgSize.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
    }

    public synchronized RecyclableBufferedImage takeBufferedImage() {

        if (availableBufferedImages.size() == 0) {
            throw new RuntimeException("All buffered images have been checked out!");
        }

        RecyclableBufferedImage buffImg = null;
        try {
            buffImg = availableBufferedImages.take();
            buffImg.checkedOut = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return buffImg;

    }

    public synchronized void returnBufferedImage(RecyclableBufferedImage buffImg) {
        if (buffImg != allBufferedImages[buffImg.idx]) {
            throw new IllegalArgumentException("This BufferedImage does not belong to this recycler!");
        }

        if (buffImg.checkedOut) {
            buffImg.checkedOut = false;
            buffImg.flush();
            availableBufferedImages.add(buffImg);
        } else {
            throw new IllegalArgumentException("This BufferedImage has already been returned!");
        }
    }

    public static class RecyclableBufferedImage extends BufferedImage {

        private int idx = -1;
        private volatile boolean checkedOut = false;

        private RecyclableBufferedImage(int idx, int width, int height, int imageType) {
            super(width, height, imageType);
            this.idx = idx;
        }

    }

}
