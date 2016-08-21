package org.excelsi.aether;


public class TitleEvent extends Event {
    private final String _title;


    public TitleEvent(Object source, String title) {
        super(source);
        _title = title;
    }

    @Override public String getType() {
        return "title";
    }

    public String getTitle() {
        return _title;
    }

    @Override public String toString() {
        return "title::{t:"+_title+"\"}";
    }
}
