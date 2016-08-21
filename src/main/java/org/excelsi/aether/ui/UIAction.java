package org.excelsi.aether.ui;


import org.excelsi.aether.Action;
import org.excelsi.aether.AbstractAction;


public abstract class UIAction extends AbstractAction {
    abstract public void perform(SceneContext c);
}
