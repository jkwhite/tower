package org.excelsi.aether;


public abstract class AddEvent<C,E> extends ContextualEvent<C> {
    private final E _e;


    public AddEvent(Object source, C ctx, E e) {
        super(source, ctx);
        _e = e;
    }

    public E getAdded() {
        return _e;
    }
}
