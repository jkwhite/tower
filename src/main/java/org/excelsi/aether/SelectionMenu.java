package org.excelsi.aether;


import java.util.function.UnaryOperator;


public class SelectionMenu<E> extends Menu<E> {
    public SelectionMenu(MenuItem<E>... items) {
        super((sel)->{return sel;}, items);
    }

    class Selector implements UnaryOperator<MenuItem<E>> {
        public MenuItem<E> apply(MenuItem<E> s) {
            //setChoice(s);
            return s;
        }
    }
}
