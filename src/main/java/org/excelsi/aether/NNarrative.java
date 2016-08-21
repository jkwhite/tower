package org.excelsi.aether;


public interface NNarrative {
    void pause();
    void title(String m);
    void message(String m);
    boolean confirm(String m);
    void poster(String m);
    //void act(Menu<E> m);
    <E> E choose(SelectionMenu<E> m);
}
