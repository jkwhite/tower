package org.excelsi.aether;


public abstract class AddEvent<E> extends Event {
    private final E _e;


    public AddEvent(Object source, E e) {
        super(source);
        _e = e;
    }

    public E getAdded() {
        return _e;
    }
}
