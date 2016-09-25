package org.excelsi.aether;


public interface NNarrative {
    void pause();
    void title(String m);
    void message(String m);
    void printf(NHBot source, String message, Object... args);
    void print(NHBot source, Object m);
    void print(NHSpace source, Object m);
    boolean confirm(String m);
    boolean confirm(NHBot source, String m);
    void poster(String m);
    //void act(Menu<E> m);
    <E> E choose(SelectionMenu<E> m);
}
