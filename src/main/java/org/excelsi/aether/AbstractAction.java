package org.excelsi.aether;


public abstract class AbstractAction implements Action {
    @Override public boolean isRepeat() {
        return false;
    }

    @Override public boolean isRecordable() {
        return true;
    }

    @Override public String getDescription() { return "No description available."; }
}
