package org.excelsi.aether.ui.jfx;


import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Group;

import org.excelsi.aether.BotAttributeChangeEvent;
import org.excelsi.aether.ChangeEvent;
import org.excelsi.aether.Stage;


public class JfxStatus extends HudNode {
    public JfxStatus() {
        super("status");
        addLogicHandler((le)->{
            if(le.e() instanceof ChangeEvent) {
                final ChangeEvent e = (ChangeEvent) le.e();
                if(e.getTo() instanceof Stage) {
                    final Stage level = (Stage) e.getTo();
                    if(!getChildren().isEmpty()) {
                        getChildren().remove(0);
                    }
                    final Label t = new Label(String.format("%s - %s, Lv %s", level.getRealm(), level.getName(), level.getDisplayedFloor()));
                    t.getStyleClass().add("status");
                    getChildren().add(t);
                }
            }
        });
    }
}
