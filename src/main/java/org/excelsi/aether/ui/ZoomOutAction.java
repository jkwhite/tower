package org.excelsi.aether.ui;


public class ZoomOutAction extends UIAction {
    @Override public void perform(final SceneContext c) {
        ((View)c.getCameraNode()).zoomOut();
    }
}
