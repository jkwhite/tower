package org.excelsi.aether;


public abstract class RemoveEvent<C,E> extends ContextualEvent<C> {
    private final E _e;


    public RemoveEvent(Object source, C ctx, E e) {
        super(source, ctx);
        _e = e;
    }

    public E getRemoved() {
        return _e;
    }
}
