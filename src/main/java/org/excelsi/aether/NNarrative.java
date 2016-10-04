package org.excelsi.aether;


import org.excelsi.matrix.Direction;


public interface NNarrative {
    void pause();
    void title(String m);
    void message(String m);
    void printf(NHBot source, String message, Object... args);
    default void printfm(NHBot source, String message, Object... args) {
        printf(source, message, args);
    }
    void print(NHBot source, Object m);
    void print(NHSpace source, Object m);
    boolean confirm(String m);
    boolean confirm(NHBot source, String m);
    void poster(String m);
    //void act(Menu<E> m);
    <E> E choose(SelectionMenu<E> m);
    default Item choose(NHBot source, ItemConstraints constraints, boolean remove) {
        return this.<Item>choose(Menus.asMenu(constraints, source, remove));
    }

    void show(NHBot source, Object shown);
    void show(NHBot source, Object shown, DisplayHints hints);
    Direction direct(NHBot b, String msg);
}
