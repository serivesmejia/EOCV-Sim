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

package com.github.serivesmejia.eocvsim.util.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DynamicBufferedImageRecycler {

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
