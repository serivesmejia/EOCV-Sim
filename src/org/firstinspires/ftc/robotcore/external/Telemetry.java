package org.firstinspires.ftc.robotcore.external;

import java.util.ArrayList;

public class Telemetry {

    private ArrayList<Item> telem = new ArrayList<>();

    private volatile String lastTelemUpdate = "";

    private volatile String beforeTelemUpdate = "mai";

    private boolean autoClear = true;

    public Item addData(String caption, String value) {
        Item i = new Item(caption, value);
        telem.add(i);
        return i;
    }

    public Item addData(String caption, Object value) {
        Item i = new Item(caption, value.toString());
        telem.add(i);
        return i;
    }

    public Item addData(String caption, String value, Object... args) {
        return addData(caption, String.format(value, args));
    }

    public void update() {

        lastTelemUpdate = "";

        StringBuilder inTelemUpdate = new StringBuilder();

        int i = 0;
        for(Item item : telem) {
            inTelemUpdate.append(item.toString()); //to avoid volatile issues we write into a stringbuilder
            if(i < telem.size()-1) inTelemUpdate.append("\n"); //append new line if this is not the lastest item
            i++;
        }

        lastTelemUpdate = inTelemUpdate.toString(); //and then we write to the volatile, public one

        if(autoClear) telem.clear();

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
        return lastTelemUpdate;
    }

    public static class Item {

        protected String caption = "";
        protected String value = "";

        public Item(String caption, String value) {
            this.caption = caption;
            this.value = value;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public void setValue(String value) {
            this.value = value;
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