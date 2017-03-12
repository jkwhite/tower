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

    default void print(NHBot source, Object m) {
        print(source, m, DisplayHints.NONE);
    }

    void print(NHBot source, Object m, DisplayHints h);

    default void print(NHSpace source, Object m) {
        print(source, m, DisplayHints.NONE);
    }

    void print(NHSpace source, Object m, DisplayHints h);

    boolean confirm(String m);
    boolean confirm(NHBot source, String m);
    void poster(String m);
    void poster(NHBot source, String m);
    //void act(Menu<E> m);
    <E> E choose(NHBot source, SelectionMenu<E> m);

    default Item choose(NHBot source, ItemConstraints constraints, boolean remove) {
        return this.<Item>choose(source, Menus.asMenu(constraints, source, remove));
    }

    void show(NHBot source, Object shown);
    void show(NHBot source, Object shown, DisplayHints hints);
    Direction direct(NHBot b, String msg);
}
