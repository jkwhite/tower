package org.excelsi.aether;


public class AddEvent<C,E> extends ContextualEvent<C> {
    private final E _e;
    private final String _type;


    public AddEvent(Object source, String type, C ctx, E e) {
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
