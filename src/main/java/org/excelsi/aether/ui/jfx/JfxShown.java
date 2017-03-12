package org.excelsi.aether.ui.jfx;


import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Group;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.text.Text;
import javafx.scene.layout.BorderPane;

import org.excelsi.aether.Event;
import org.excelsi.aether.KeyEvent;
import org.excelsi.aether.Container;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.InfoEvent;


public class JfxShown extends HudRegion {
    public JfxShown(final InfoEvent e) {
        addLogicHandler((le)->{
            le.consume();
            final Event ke = le.e();
            if(ke instanceof KeyEvent) {
                hide(e);
            }
        });
        final InfoEvent ie = e;
        final NHBot src = (NHBot) ie.getSource();
        if(ie.getShown() instanceof Container) {
            final Container cn = (Container)ie.getShown();
            final JfxContainer c = new JfxContainer(ie, src, cn, ie.hints());
            getChildren().add(c);
            //transition(c, null);
            //le.consume();
        }
        else if(ie.getShown() instanceof String) {
            BorderPane bt = new BorderPane();
            bt.setCenter(new Text(ie.getShown().toString().trim()));
            bt.getStyleClass().add("message");
            getChildren().add(bt);
            //le.consume();
        }
    }

    private void hide(final Event e) {
        ((Group)getParent()).getChildren().remove(this);
        synchronized(e) {
            e.notify();
        }
    }
}
