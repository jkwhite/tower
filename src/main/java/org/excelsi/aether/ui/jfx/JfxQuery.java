package org.excelsi.aether.ui.jfx;


import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Pos;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.excelsi.aether.Event;
import org.excelsi.aether.QueryEvent;
import org.excelsi.aether.KeyEvent;


public class JfxQuery extends HudRegion {
    public JfxQuery(final QueryEvent e) {
        //getStyleClass().add("query");
        addLogicHandler((le)->{
            le.consume();
            final Event ke = le.e();
            if(ke instanceof KeyEvent) {
                switch(((KeyEvent)ke).key()) {
                    case "y":
                        choose(e, true);
                        break;
                    case "n":
                        choose(e, false);
                        break;
                }
            }
        });
        final Label msg = new Label(e.getMessage()+" (y/n)");
        final Centered c = new Centered(msg, "query");
        //msg.setAlignment(Pos.CENTER);
        getChildren().add(c);
    }

    private void choose(final QueryEvent e, final Object choice) {
        ((Group)getParent()).getChildren().remove(this);
        e.setAnswer(choice);
        synchronized(e) {
            e.notify();
        }
    }
}
