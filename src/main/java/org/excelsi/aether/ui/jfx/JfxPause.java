package org.excelsi.aether.ui.jfx;


import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;


public class JfxPause extends HudNode {
    public JfxPause(final String m, final Object notify) {
        addLogicHandler((le)->{
            le.consume();
            ((Group)getParent()).getChildren().remove(this);
            synchronized(notify) {
                notify.notify();
            }
        });
        Label l = new Label(m);
        l.getStyleClass().add("message");
        getChildren().add(l);
    }
}
