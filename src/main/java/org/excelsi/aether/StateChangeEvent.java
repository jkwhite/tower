package org.excelsi.aether;


import org.excelsi.matrix.Typed;


public class StateChangeEvent extends Event {
    private final State _old;
    private final State _new;


    public StateChangeEvent(Typed source, State oldValue, State newValue) {
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
