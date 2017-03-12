package org.excelsi.aether;


import org.excelsi.matrix.Typed;


public abstract class Event {
    private final Typed _source;


    public Event(final Typed source) {
        _source = source;
    }

    public Typed getSource() {
        return _source;
    }

    public abstract String getType();
}
