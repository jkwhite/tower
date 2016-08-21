package org.excelsi.aether;


import org.excelsi.aether.GameAction;
import org.excelsi.aether.InputSource;
import org.excelsi.aether.InputInterruptedException;


public class BusInputSource implements InputSource {
    private final String _sub;


    public BusInputSource() {
        _sub = EventBus.instance().subscribe("actions", "bus");
    }

    @Override public GameAction nextAction() throws InputInterruptedException {
        ActionEvent e = (ActionEvent) EventBus.instance().poll(_sub);
        return e!=null?e.action():null;
    }

    @Override public GameAction nextAction(long timeout) throws InputInterruptedException {
        ActionEvent e = (ActionEvent) EventBus.instance().poll(_sub, timeout);
        return e!=null?e.action():null;
    }

    @Override public GameAction actionFor(String key) {
        throw new UnsupportedOperationException();
    }

    @Override public void checkpoint() {
        throw new UnsupportedOperationException();
    }

    @Override public void playback() {
        throw new UnsupportedOperationException();
    }

    @Override public boolean isPlayback() {
        throw new UnsupportedOperationException();
    }

    @Override public String nextKey() throws InputInterruptedException {
        throw new UnsupportedOperationException();
    }
}
