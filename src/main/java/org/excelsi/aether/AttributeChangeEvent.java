package org.excelsi.aether;


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Bot;


public class AttributeChangeEvent<C,E> extends ContextualEvent<C> {
    private final E _e;
    private final String _type;
    private final String _attr;
    private final Object _from;
    private final Object _to;


    public AttributeChangeEvent(Object source, String type, C ctx, E e, String attr, Object from, Object to) {
        super(source, ctx);
        _type = type;
        _e = e;
        _attr = attr;
        _from = from;
        _to = to;
    }

    @Override public String getType() {
        return _type;
    }

    public E getE() {
        return _e;
    }

    public String getAttribute() {
        return _attr;
    }

    public Object getFrom() {
        return _from;
    }

    public Object getTo() {
        return _to;
    }

    @Override public String toString() {
        return "AttributeChangeEvent::{source:"+getSource()+", ctx:"+getContext()+", e: "+getE()+", attr:"+getAttribute()+", from:"+getFrom()+", to:"+getTo()+"}";
    }
}
