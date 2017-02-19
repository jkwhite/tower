package org.excelsi.aether.ui;


public class ZoomInAction extends UIAction {
    @Override public void perform(final SceneContext c) {
        ((View)c.getCameraNode()).zoomIn();
    }
}
