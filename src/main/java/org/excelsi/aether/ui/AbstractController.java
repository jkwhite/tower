package org.excelsi.aether.ui;


public abstract class AbstractController<C,E> implements Controller<C,E> {
    private final E _e;


    public AbstractController(final E e) {
        _e = e;
    }

    protected final E e() {
        return _e;
    }
}
