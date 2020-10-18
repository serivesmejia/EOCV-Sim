package org.firstinspires.ftc.robotcore.external;

import java.util.ArrayList;

public class Telemetry {

    private ArrayList<ItemOrLine> telem = new ArrayList<>();
    private ArrayList<ItemOrLine> lastTelem = new ArrayList<>();

    private String captionValueSeparator = " : ";

    public Item errItem = new Item("", "");

    private volatile String lastTelemUpdate = "";
    private volatile String beforeTelemUpdate = "mai";

    private boolean autoClear = true;

    public Item addData(String caption, String value) {
        Item i = new Item(caption, value);
        i.valueSeparator = captionValueSeparator;
        telem.add(i);
        return i;
    }

    public Item addData(String caption, Object value) {
        return addData(caption, value.toString());
    }

    public Item addData(String caption, String value, Object... args) {
        return addData(caption, String.format(value, args));
    }

    public Line addLine() {
        return addLine("");
    }

    public Line addLine(String caption) {
        Line line = new Line(caption);
        telem.add(line);
        return line;
    }

    public void update() {

        lastTelemUpdate = "";

        lastTelem = (ArrayList<ItemOrLine>) telem.clone();

        evalLastTelem();

        if(autoClear) clear();

    }

    private void evalLastTelem() {

        if(lastTelem == null) return;

        StringBuilder inTelemUpdate = new StringBuilder();

        int i = 0;

        for(ItemOrLine iol : lastTelem) {

            if(iol instanceof Item) {
                Item item = (Item)iol;
                item.valueSeparator = captionValueSeparator;
                inTelemUpdate.append(item.toString()); //to avoid volatile issues we write into a stringbuilder
            }else if(iol instanceof Line) {
                Line line = (Line)iol;
                inTelemUpdate.append(line.toString()); //to avoid volatile issues we write into a stringbuilder
            }

            if(i < lastTelem.size()-1) inTelemUpdate.append("\n"); //append new line if this is not the lastest item

            i++;
        }

        if(!errItem.caption.trim().equals("")) {
            inTelemUpdate.append("\n");
            inTelemUpdate.append(errItem.toString());
        }

        lastTelemUpdate = inTelemUpdate.toString(); //and then we write to the volatile, public one

    }

    public boolean removeItem(Item item) {

        if(telem.contains(item)) {
            telem.remove(item);
            return true;
        }

        return false;

    }

    public void clear() {

        for(ItemOrLine i : telem.toArray(new ItemOrLine[telem.size()-1])) {
            if(i instanceof Item) {
                if(!((Item) i).isRetained) telem.remove(i);
            } else {
                telem.remove(i);
            }
        }

    }

    public boolean hasChanged() {

        boolean hasChanged = !lastTelemUpdate.equals(beforeTelemUpdate);
        beforeTelemUpdate = lastTelemUpdate;

        return hasChanged;

    }

    public String getCaptionValueSeparator() { return captionValueSeparator; }

    public void setAutoClear(boolean autoClear) {
        this.autoClear = autoClear;
    }

    public void setCaptionValueSeparator(String captionValueSeparator) {
        this.captionValueSeparator = captionValueSeparator;
    }

    @Override
    public String toString() {

        evalLastTelem();

        return lastTelemUpdate;

    }

    public static class Item extends ItemOrLine {

        protected String caption = "";
        protected String value = "";

        protected String valueSeparator = " : ";

        protected boolean isRetained = false;

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

        public void setValue(Object value) {
            setValue(value.toString());
        }

        public void setValue(String value, Object... args) {
            setValue(String.format(value, args));
        }

        public void setRetained(boolean retained) { this.isRetained = retained; }

        public String getCaption() { return caption; }

        public boolean isRetained() { return isRetained; }

        @Override
        public String toString() {
            return caption + " " + valueSeparator + " " + value;
        }

    }

    public static class Line extends ItemOrLine {

        protected String caption = "";

        public Line(String caption) {
            this.caption = caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public String getCaption() { return caption; }

        @Override
        public String toString() {
            return caption;
        }

    }

    private static class ItemOrLine {

    }

}