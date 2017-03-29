package org.excelsi.aether;


import org.excelsi.matrix.Direction;


public class EmptyNarrative implements NNarrative {
    public EmptyNarrative() {
    }

    @Override public void pause() {
    }

    @Override public void title(String title) {
    }

    @Override public void message(String m) {
    }

    @Override public void print(NHBot source, Object m, DisplayHints h) {
    }

    @Override public void print(NHSpace source, Object m, DisplayHints h) {
    }

    @Override public void printf(NHBot source, String message, Object... args) {
    }

    @Override public boolean confirm(String m) {
        return false;
    }

    @Override public boolean confirm(final NHBot source, String m) {
        return false;
    }

    @Override public void poster(String m) {
    }

    @Override public void chronicle(String m) {
    }

    @Override public void poster(NHBot source, String m) {
    }

    @Override public <E> E choose(NHBot source, SelectionMenu<E> m) {
        throw new IllegalStateException();
    }

    @Override public void show(NHBot source, Object shown) {
    }

    @Override public void show(NHBot source, Object shown, DisplayHints hints) {
    }

    @Override public Direction direct(NHBot b, String msg) {
        throw new IllegalStateException();
    }
}
