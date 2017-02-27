package org.excelsi.aether.ui;


public class SwitchViewAction extends UIAction {
    @Override public void perform(final SceneContext c) {
        ((View)c.getCameraNode()).next();
    }
}
