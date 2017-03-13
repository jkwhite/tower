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

import org.excelsi.aether.Action;
import org.excelsi.aether.Director;
import org.excelsi.aether.Event;
import org.excelsi.aether.QueryEvent;
import org.excelsi.aether.KeyEvent;
import org.excelsi.aether.ui.UI;
import org.excelsi.aether.ui.SceneContext;
import org.excelsi.matrix.Typed;


public class JfxQuery extends HudRegion {
    public JfxQuery(final QueryEvent e, final SceneContext sc) {
        //getStyleClass().add("query");
        addLogicHandler((le)->{
            le.consume();
            final Event ke = le.e();
            if(ke instanceof KeyEvent) {
                switch(e.getQueryType()) {
                    case bool:
                        switch(((KeyEvent)ke).key()) {
                            case "y":
                                choose(e, true);
                                break;
                            case "n":
                                choose(e, false);
                                break;
                        }
                        break;
                    case direction:
                        final Action a = UI.findAction(le);
                        if(a instanceof Director) {
                            choose(e, ((Director)a).getDirection());
                        }
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }
        });
        final HBox line = new HBox();
        //final String m;
        switch(e.getQueryType()) {
            case bool:
                //m = e.getMessage()+" (y/n)";
                line.getChildren().add(new Label(e.getMessage()+" ("));
                line.getChildren().add(Fx.decorate(new Label("Yes"), (ev)->{choose(e, true);}));
                line.getChildren().add(new Label("/"));
                line.getChildren().add(Fx.decorate(new Label("No"), (ev)->{choose(e, false);}));
                line.getChildren().add(new Label(")"));
                break;
            case direction:
                //m = e.getMessage();
                line.getChildren().add(new Label(e.getMessage()));
                break;
            default:
                throw new IllegalStateException();
        }
        //final Label msg = new Label(m);
        final Centered c = new Centered(line, "query");
        //final Centered c = new Centered(msg, "query");
        //log().info("added query message with message '"+m+"'");
        //msg.setAlignment(Pos.CENTER);
        Fx.localize(sc, (Typed)e.getSource(), c);
        getChildren().add(c);
    }

    private void choose(final QueryEvent e, final Object choice) {
        ((Group)getParent()).getChildren().remove(this);
        Messages.instance().unstack(e);
        e.setAnswer(choice);
        synchronized(e) {
            e.notify();
        }
    }
}
