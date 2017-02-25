package org.excelsi.aether;


public class MessageEvent extends Event {
    public enum Type { ephemeral, permanent };


    private final Object _m;
    private final Type _t;
    private final DisplayHints _h;


    public MessageEvent(Object source, Type t, Object m) {
        this(source, t, m, DisplayHints.NONE);
    }

    public MessageEvent(Object source, Type t, Object m, DisplayHints h) {
        super(source);
        _t = t;
        _m = m;
        _h = h;
    }

    @Override public String getType() {
        return "message";
    }

    public Object getMessage() {
        return _m;
    }

    public Type getMessageType() {
        return _t;
    }

    public DisplayHints getHints() {
        return _h;
    }

    @Override public String toString() {
        return "message::{t:"+_t+", m:\""+_m+"\", h:"+_h+"}";
    }
}
