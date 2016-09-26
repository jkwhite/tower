package org.excelsi.aether;


public class InfoEvent extends Event {
    private final Object _shown;
    private final DisplayHints _hints;


    public InfoEvent(Object source, Object shown) {
        this(source, shown, DisplayHints.NONE);
    }

    public InfoEvent(Object source, Object shown, DisplayHints hints) {
        super(source);
        _shown = shown;
        _hints = hints;
    }

    public Object getShown() {
        return _shown;
    }

    public DisplayHints hints() {
        return _hints;
    }

    @Override public String getType() {
        return "info";
    }

    @Override public String toString() {
        return "info::{source:"+getSource()+", shown:"+_shown+"\"}";
    }
}
