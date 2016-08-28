package org.excelsi.aether;


public abstract class ContextualEvent<C> extends Event {
    private final C _ctx;


    public ContextualEvent(Object source, C ctx) {
        super(source);
        _ctx = ctx;
    }

    public C getContext() {
        return _ctx;
    }

}
