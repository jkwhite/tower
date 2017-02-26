package org.excelsi.aether;


public abstract class StageEvent extends Event {
    private final Stage _s;


    public StageEvent(Object source, Stage s) {
        super(source);
        _s = s;
    }

    public final Stage getStage() {
        return _s;
    }
}
