package org.excelsi.aether;


public final class Logic extends Thread {
    private final Historian _h;


    public Logic(Historian h) {
        _h = h;
    }

    @Override public void run() {
        while(true) {
            _h.tick();
        }
    }
}
