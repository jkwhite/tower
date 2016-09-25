package org.excelsi.aether.ui;


import org.excelsi.aether.Action;
import org.excelsi.aether.AbstractAction;
import org.excelsi.aether.Context;


public abstract class UIAction extends AbstractAction {
    @Override public void perform() {
    }

    @Override public void perform(final Context c) {
    }

    abstract public void perform(SceneContext c);
}
