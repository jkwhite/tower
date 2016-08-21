package org.excelsi.aether;


public class StateChangeEvent extends Event {
    private final State _old;
    private final State _new;


    public StateChangeEvent(Object source, State oldValue, State newValue) {
        super(source);
        _old = oldValue;
        _new = newValue;
    }

    @Override public String getType() {
        return "state";
    }

    public State getOldValue() {
        return _old;
    }

    public State getNewValue() {
        return _new;
    }

    @Override public String toString() {
        return "state::{old:"+_old+", new:"+_new+"}";
    }
}
