package org.excelsi.aether.ui.jfx;


import javafx.scene.Group;
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

import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.excelsi.aether.Event;
import org.excelsi.aether.KeyEvent;
import org.excelsi.aether.Menu;
import org.excelsi.aether.MenuItem;


/**
 * Displays a selection menu.
 */
public class JfxMenu extends HudRegion {
    public JfxMenu(final Object notify, final Menu m) {
        addLogicHandler((le)->{
            le.consume();
            final Event e = le.e();
            if(e instanceof KeyEvent) {
                for(MenuItem item:m.getItems()) {
                    if(item.key().equals(((KeyEvent)e).key())) {
                        choose(m, item, notify);
                    }
                }
            }
        });
        final VBox menu = new VBox();
        for(MenuItem item:m.getItems()) {
            Label key = new Label(item.key()+" - ");
            key.getStyleClass().add("key");
            Label desc = new Label(item.description());
            desc.getStyleClass().add("keydesc");
            HBox line = new HBox();
            line.getChildren().add(key);
            line.getChildren().add(desc);
            Fx.decorate(line, (e)->{ choose(m, item, notify); });
            //System.err.println("DECORATED: "+desc);
            /*
            line.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    line.setEffect(new Glow());
                }
            });
            line.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    line.setEffect(null);
                }
            });
            line.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    choose(m, item, notify);
                }
            });
            */
            menu.getChildren().add(line);
            menu.getStyleClass().add("menu");
        }
        getChildren().add(menu);
    }

    private void choose(final Menu m, final MenuItem item, final Object notify) {
        //System.err.println("************ CHOSEN: "+item);
        ((Group)getParent()).getChildren().remove(this);
        m.getF().apply(item);
        m.setChoice(item);
        synchronized(notify) {
            notify.notify();
        }
    }
}
