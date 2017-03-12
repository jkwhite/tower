package org.excelsi.aether;


import org.excelsi.matrix.Typed;


public abstract class StageEvent extends Event {
    private final Stage _s;


    public StageEvent(Typed source, Stage s) {
        super(source);
        _s = s;
    }

    public final Stage getStage() {
        return _s;
    }
}
