package org.excelsi.aether.ui.jfx;


import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Group;


public class JfxPause extends HudNode {
    public JfxPause(final String m, final Object notify) {
        addLogicHandler((le)->{
            le.consume();
            ((Group)getParent()).getChildren().remove(this);
            synchronized(notify) {
                notify.notify();
            }
        });
        getChildren().add(new Label(m));
    }
}
