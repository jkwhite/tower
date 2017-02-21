package org.excelsi.aether;


public class QueryEvent extends Event {
    public enum Type { bool, direction };


    private final String _m;
    private final Type _t;
    private Object _answer;


    public QueryEvent(Object source, Type t, String m) {
        super(source);
        _t = t;
        _m = m;
    }

    @Override public String getType() {
        return "query";
    }

    public String getMessage() {
        return _m;
    }

    public Type getQueryType() {
        return _t;
    }

    public void setAnswer(Object o) {
        _answer = o;
    }

    public <E> E getAnswer() {
        return (E) _answer;
    }

    @Override public String toString() {
        return "query::{t:"+_t+", m:\""+_m+"\"}";
    }
}
