package org.excelsi.aether;


import java.util.function.UnaryOperator;


public class SelectionMenu<E> extends Menu<E> {
    private final boolean  _remove;


    public SelectionMenu(MenuItem<E>... items) {
        this(false, items);
    }

    public SelectionMenu(boolean remove, MenuItem<E>... items) {
        super((sel)->{return sel;}, items);
        _remove = remove;
    }

    class Selector implements UnaryOperator<MenuItem<E>> {
        public MenuItem<E> apply(MenuItem<E> s) {
            //setChoice(s);
            return s;
        }
    }
}
