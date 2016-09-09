package org.excelsi.aether;


public class ChangeEvent<C,E> extends ContextualEvent<C> {
    private final String _type;
    private final E _old;
    private final E _new;


    public ChangeEvent(Object source, String type, C ctx, E oldValue, E newValue) {
        super(source, ctx);
        _type = type;
        _old = oldValue;
        _new = newValue;
    }

    @Override public String getType() {
        return _type;
    }

    public E getFrom() {
        return _old;
    }

    public E getTo() {
        return _new;
    }

    @Override public String toString() {
        return getClass().getSimpleName().toLowerCase()+"::"+_type+"::{ctx:"+getContext()+", from:"+_old+", to:"+_new+"}";
    }
}
