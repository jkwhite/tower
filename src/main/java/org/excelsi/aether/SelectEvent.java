package org.excelsi.aether;


import org.excelsi.matrix.Typed;


public class SelectEvent<E> extends Event {
    private final Menu<E> _m;


    public SelectEvent(Typed source, Menu<E> m) {
        super(source);
        _m = m;
    }

    @Override public String getType() {
        return "select";
    }

    public Menu<E> getMenu() {
        return _m;
    }

    @Override public String toString() {
        return "select::{menu:"+_m+"}";
    }
}
