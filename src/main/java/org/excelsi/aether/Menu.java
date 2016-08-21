package org.excelsi.aether;


import java.util.function.Function;
import java.util.function.UnaryOperator;


public class Menu<E> {
    private final UnaryOperator<MenuItem<E>> _f;
    private final MenuItem<E>[] _items;

    private MenuItem<E> _choice;



    public Menu(UnaryOperator<MenuItem<E>> f, MenuItem<E>... items) {
        _f = f;
        _items = items;
    }

    public UnaryOperator<MenuItem<E>> getF() {
        return _f;
    }

    public MenuItem<E>[] getItems() {
        return _items;
    }

    public MenuItem<E> getChoice() {
        return _choice;
    }

    public void setChoice(MenuItem<E> choice) {
        _choice = choice;
    }
}
