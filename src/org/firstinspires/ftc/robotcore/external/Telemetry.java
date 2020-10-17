package org.firstinspires.ftc.robotcore.external;

import java.util.ArrayList;

public class Telemetry {

    private ArrayList<Item> telem = new ArrayList<>();
    private ArrayList<Item> lastTelem = new ArrayList<>();

    public Item errItem = new Item("", "");

    private volatile String lastTelemUpdate = "";

    private volatile String beforeTelemUpdate = "mai";

    private boolean autoClear = true;

    public Item addData(String caption, String value) {
        Item i = new Item(caption, value);
        telem.add(i);
        return i;
    }

    public Item addData(String caption, Object value) {
        return addData(caption, value.toString());
    }

    public Item addData(String caption, String value, Object... args) {
        return addData(caption, String.format(value, args));
    }

    public void update() {

        lastTelemUpdate = "";

        lastTelem = (ArrayList<Item>)telem.clone();

        evalLastTelemItems();

        if(autoClear) telem.clear();

    }

    private void evalLastTelemItems() {

        if(lastTelem == null) return;

        StringBuilder inTelemUpdate = new StringBuilder();

        int i = 0;

        for(Item item : lastTelem) {
            inTelemUpdate.append(item.toString()); //to avoid volatile issues we write into a stringbuilder
            if(i < telem.size()-1) inTelemUpdate.append("\n"); //append new line if this is not the lastest item
            i++;
        }

        if(!errItem.caption.trim().equals("")) {
            inTelemUpdate.append("\n");
            inTelemUpdate.append(errItem.toString());
        }

        lastTelemUpdate = inTelemUpdate.toString(); //and then we write to the volatile, public one

    }

    public void clear() {
        telem.clear();
    }

    public void setAutoClear(boolean autoClear) {
        this.autoClear = autoClear;
    }

    public boolean hasChanged() {

        boolean hasChanged = !lastTelemUpdate.equals(beforeTelemUpdate);
        beforeTelemUpdate = lastTelemUpdate;

        return hasChanged;

    }

    public ArrayList<Item> getItems() { return telem; }

    @Override
    public String toString() {

        evalLastTelemItems();

        return lastTelemUpdate;

    }

    public static class Item {

        protected String caption = "";
        protected String value = "";

        public Item(String caption, String value) {
            this.caption = caption;
            this.value = value;
        }
        
        public void set(String caption, String value) {
            setCaption(caption);
            setValue(value);
        }

        public void set(String caption, Object value) {
            setCaption(caption);
            setValue(value);
        }

        public void set(String caption, String value, Object... args) {
            setCaption(caption);
            setValue(value, args);
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setValue(Object value) {
            setValue(value.toString());
        }

        public void setValue(String value, Object... args) {
            setValue(String.format(value, args));
        }

        public String getCaption() { return caption; }

        @Override
        public String toString() {
            return caption + " : " + value;
        }

    }

}