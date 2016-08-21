package org.excelsi.aether.ui.jfx;


import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Group;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;

import org.excelsi.aether.Event;
import org.excelsi.aether.TitleEvent;


public class JfxTitle extends HudRegion {
    public JfxTitle() {
        setVisible(false);
        addLogicHandler((le)->{
            final Event e = le.e();
            if(e instanceof TitleEvent) {
                final TitleEvent te = (TitleEvent) e;
                if(!getChildren().isEmpty()) {
                    getChildren().remove(0);
                }
                if(!"".equals(te.getTitle())) {
                    final Label t = new Label(te.getTitle());
                    t.getStyleClass().add("title");
                    t.setPrefWidth(getPrefWidth());
                    getChildren().add(t);
                    transition(JfxTitle.this, null);
                    setVisible(true);
                }
                else {
                    setVisible(false);
                }
            }
        });
    }
}
