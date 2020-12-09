package com.github.serivesmejia.eocvsim.util.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BufferedImageGiver {

    private final HashMap<Dimension, BufferedImageRecycler> recyclers = new HashMap<>();

    private final ArrayList<BufferedImage> allImages = new ArrayList<>();

    public synchronized BufferedImage giveBufferedImage(Dimension size, int recyclerSize) {

        //look for existing buff image recycler with desired dimensions
        for(Map.Entry<Dimension, BufferedImageRecycler> entry : recyclers.entrySet()) {
            if(entry.getKey().equals(size)) {

                BufferedImage buffImg = entry.getValue().takeBufferedImage();
                if(!allImages.contains(buffImg)) allImages.add(buffImg);

                return buffImg;

            }
        }

        //create new one if didn't found an existing recycler
        BufferedImageRecycler recycler = new BufferedImageRecycler(recyclerSize, size);
        recyclers.put(size, recycler);

        BufferedImage buffImg = recycler.takeBufferedImage();
        allImages.add(buffImg);

        return buffImg;

    }

    public synchronized void returnBufferedImage(BufferedImage buffImg) {

        if(!allImages.contains(buffImg))
            throw new IllegalArgumentException("Given BufferedImage does not belong here.");

        Dimension dimension = new Dimension(buffImg.getWidth(), buffImg.getHeight());

        buffImg.flush();

        recyclers.get(dimension).returnBufferedImage((BufferedImageRecycler.RecyclableBufferedImage) buffImg);

    }
    
    public synchronized void flushAll() {
        for(BufferedImageRecycler recycler : recyclers.values()) {
            recycler.flushAll();
        }
    }

}
