package org.excelsi.aether;


import org.excelsi.matrix.Typed;


public class PauseEvent extends Event {
    public PauseEvent(final Typed source) {
        super(source);
    }

    public String getType() {
        return "pause";
    }
}
