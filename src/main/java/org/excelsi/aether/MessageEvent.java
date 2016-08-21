package org.excelsi.aether;


public class MessageEvent extends Event {
    public enum Type { ephemeral, permanent };


    private final String _m;
    private final Type _t;


    public MessageEvent(Object source, Type t, String m) {
        super(source);
        _t = t;
        _m = m;
    }

    @Override public String getType() {
        return "message";
    }

    public String getMessage() {
        return _m;
    }

    public Type getMessageType() {
        return _t;
    }

    @Override public String toString() {
        return "message::{t:"+_t+", m:\""+_m+"\"}";
    }
}
