package org.excelsi.aether;


import org.excelsi.matrix.Typed;


public class RemoveEvent<C,E> extends ContextualEvent<C> {
    private final E _e;
    private final String _type;


    public RemoveEvent(Typed source, String type, C ctx, E e) {
        super(source, ctx);
        _type = type;
        _e = e;
    }

    @Override public String getType() {
        return _type;
    }

    public E getRemoved() {
        return _e;
    }
}
