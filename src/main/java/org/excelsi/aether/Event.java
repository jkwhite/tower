package org.excelsi.aether;


public abstract class Event {
    private final Object _source;


    public Event(final Object source) {
        _source = source;
    }

    public Object getSource() {
        return _source;
    }

    public abstract String getType();
}
