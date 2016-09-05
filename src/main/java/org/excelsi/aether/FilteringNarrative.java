package org.excelsi.aether;


public class FilteringNarrative implements NNarrative {
    private final NHBot _pov;
    private final NNarrative _delegate;


    public FilteringNarrative(NHBot pov, NNarrative delegate) {
        _pov = pov;
        _delegate = delegate;
    }

    @Override public void pause() {
        _delegate.pause();
    }

    @Override public void title(String title) {
        _delegate.title(title);
    }

    @Override public void message(String m) {
        _delegate.message(m);
    }

    @Override public void print(NHBot source, Object m) {
        if(_pov.getEnvironment().getVisibleBots().contains(source)) {
            _delegate.print(source, m);
        }
    }

    @Override public void printf(NHBot source, String message, Object... args) {
        if(_pov.getEnvironment().getVisibleBots().contains(source)) {
            _delegate.printf(source, message, args);
        }
    }

    @Override public boolean confirm(String m) {
        if(_pov.isHuman()) {
            return _delegate.confirm(m);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    @Override public void poster(String m) {
        _delegate.poster(m);
    }

    @Override public <E> E choose(SelectionMenu<E> m) {
        if(_pov.isHuman()) {
            return _delegate.choose(m);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }
}
