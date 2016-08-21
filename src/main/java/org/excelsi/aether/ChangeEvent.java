package org.excelsi.aether;


public class ChangeEvent<E> extends Event {
    private final String _type;
    private final E _old;
    private final E _new;


    public ChangeEvent(Object source, String type, E oldValue, E newValue) {
        super(source);
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
        return getClass().getSimpleName().toLowerCase()+"::"+_type+"::{from:"+_old+", to:"+_new+"}";
    }
}
