package org.excelsi.aether.ui.jfx;


import javafx.scene.Group;


public final class Fx {
    public static void removeAll(final Group p) {
        while(p.getChildren().size()>0) {
            p.getChildren().remove(0);
        }
    }

    private Fx() {}
}
