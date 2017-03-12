package org.excelsi.aether;


import org.excelsi.matrix.Typed;


public class AddEvent<C,E> extends ContextualEvent<C> {
    private final E _e;
    private final String _type;


    public AddEvent(Typed source, String type, C ctx, E e) {
        super(source, ctx);
        _type = type;
        _e = e;
    }

    @Override public String getType() {
        return _type;
    }

    public E getAdded() {
        return _e;
    }
}
