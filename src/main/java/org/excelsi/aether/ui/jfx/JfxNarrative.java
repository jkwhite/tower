package org.excelsi.aether.ui.jfx;


import java.util.List;
import java.util.LinkedList;
import org.excelsi.aether.Narrative;

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
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.excelsi.aether.Event;
import org.excelsi.aether.EventBus;
import org.excelsi.aether.Menu;
import org.excelsi.aether.MenuItem;
import org.excelsi.aether.SelectEvent;
import org.excelsi.aether.MessageEvent;
import org.excelsi.aether.KeyEvent;
import org.excelsi.aether.PauseEvent;
import org.excelsi.aether.ui.Hud;
import org.excelsi.aether.ui.LogicEvent;


public class JfxNarrative extends Group {
    private static final Logger LOG = LoggerFactory.getLogger(JfxNarrative.class);


    public JfxNarrative() {
        addEventHandler(LogicEvent.TYPE, (le)->{
            LOG.debug("narrative got event: "+le);
            final Event e = le.e();
            if(e instanceof PauseEvent) {
                pause((PauseEvent)e);
            }
            else {
                onEvent(le);
            }
        });
    }

    private void onEvent(final LogicEvent le) {
        List<Node> frontier = new LinkedList<>();
        frontier.addAll(getChildren());
        while(!frontier.isEmpty()) {
            final Node child = frontier.remove(0);
            if(child instanceof Parent) {
                frontier.addAll(0, ((Parent)child).getChildrenUnmodifiable());
            }
            if(child instanceof Hud) {
                ((Hud)child).onEvent(le);
                if(le.isConsumed()) {
                    break;
                }
            }
        }
    }

    private void pause(PauseEvent e) {
        final JfxPause m = new JfxPause("-- More --", e);
        getChildren().add(0, m);
    }
}
