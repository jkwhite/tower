package org.excelsi.aether;


import org.excelsi.matrix.Typed;


public abstract class ContextualEvent<C> extends Event {
    private final C _ctx;


    public ContextualEvent(Typed source, C ctx) {
        super(source);
        _ctx = ctx;
    }

    public C getContext() {
        return _ctx;
    }

}
