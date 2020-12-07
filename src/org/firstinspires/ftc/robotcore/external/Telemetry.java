package org.firstinspires.ftc.robotcore.external;

import com.github.serivesmejia.eocvsim.util.Log;

import java.util.ArrayList;

public class Telemetry {

    private final ArrayList<ItemOrLine> telem = new ArrayList<>();
    private ArrayList<ItemOrLine> lastTelem = new ArrayList<>();

    private String captionValueSeparator = " : ";

    public Item errItem = new Item("", "");

    private volatile String lastTelemUpdate = "";
    private volatile String beforeTelemUpdate = "mai";

    private boolean autoClear = true;

    public Item addData(String caption, String value) {

        Item item = new Item(caption, value);
        item.valueSeparator = captionValueSeparator;

        telem.add(item);

        return item;

    }

    public Item addData(String caption, Func valueProducer) {

        Item item = new Item(caption, valueProducer);
        item.valueSeparator = captionValueSeparator;

        telem.add(item);

        return item;

    }

    public Item addData(String caption, Object value) {

        Item item = new Item(caption, "");
        item.valueSeparator = captionValueSeparator;

        item.setValue(value);

        telem.add(item);

        return item;

    }

    public Item addData(String caption, String value, Object... args) {

        Item item = new Item(caption, "");
        item.valueSeparator = captionValueSeparator;

        item.setValue(value, args);

        telem.add(item);

        return item;

    }

    public Item addData(String caption, Func valueProducer, Object... args) {

        Item item = new Item(caption, "");
        item.valueSeparator = captionValueSeparator;

        item.setValue(valueProducer, args);

        telem.add(item);

        return item;

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

        for(ItemOrLine i : telem.toArray(new ItemOrLine[0])) {
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

    public static class Item implements ItemOrLine {

        protected String caption = "";

        protected Func valueProducer = null;

        protected String valueSeparator = " : ";

        protected boolean isRetained = false;

        public Item(String caption, String value) {
            setCaption(caption); setValue(value);
        }

        public Item(String caption, Func valueProducer) {
            this.caption = caption;
            this.valueProducer = valueProducer;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public void setValue(String value) {
            setValue((Func<String>) () -> value);
        }

        public void setValue(Func func) {
            this.valueProducer = func;
        }

        public void setValue(Object value) {
            setValue(value.toString());
        }

        public void setValue(String value, Object... args) {
            setValue(String.format(value, args));
        }

        public void setValue(Func func, Object... args) {
            setValue((Func<String>) () -> String.format(func.value().toString(), args));
        }

        public void setRetained(boolean retained) { this.isRetained = retained; }

        public String getCaption() { return caption; }

        public boolean isRetained() { return isRetained; }

        @Override
        public String toString() {
            return caption + " " + valueSeparator + " " + valueProducer.value().toString();
        }

    }

    public static class Line implements ItemOrLine {

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

    private static interface ItemOrLine {
        void setCaption(String caption);
        String getCaption();
    }

}