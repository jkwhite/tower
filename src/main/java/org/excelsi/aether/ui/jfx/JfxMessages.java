package org.excelsi.aether.ui.jfx;


import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Group;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;
import javafx.scene.text.Text;
import javafx.scene.layout.BorderPane;
import com.jme3.math.Vector3f;

import org.excelsi.matrix.Id;
import org.excelsi.aether.MessageEvent;
import org.excelsi.aether.NHBot;


public class JfxMessages extends HudNode {
    public JfxMessages() {
        addLogicHandler((le)->{
            if(le.e() instanceof MessageEvent) {
                final MessageEvent e = (MessageEvent) le.e();
                final Node t;
                if(e.getMessage().length()<80) {
                    t = new Label(e.getMessage().trim());
                }
                else {
                    BorderPane bt = new BorderPane();
                    bt.setCenter(new Text(e.getMessage().trim()));
                    t = bt;
                }
                t.getStyleClass().add("message");
                t.getStyleClass().add(e.getMessageType().toString());
                if(e.getSource() instanceof NHBot) {
                    Vector3f wp = le.ctx().getSpatial((Id)e.getSource()).getWorldTranslation();
                    Vector3f sp = le.ctx().getCamera().getScreenCoordinates(wp);
                    final int w = le.ctx().getCamera().getWidth();
                    final int h = le.ctx().getCamera().getHeight();
                    t.setTranslateX(sp.x-50);
                    t.setTranslateY(h-40-sp.y);
                    //System.err.println("W****: "+((Label)t).getPrefWidth());
                    //System.err.println("setting screen coords: "+sp+" from "+wp+" for "+e.getSource()+" message: "+e.getMessage()+"; jfxx="+t.getTranslateX()+", jfxy="+t.getTranslateY()+"; jfxlx="+t.getLayoutX()+", jfxly="+t.getLayoutY());
                }
                getChildren().add(t);
                final SequentialTransition st = new SequentialTransition();
                switch(e.getMessageType()) {
                    case ephemeral:
                        final FadeTransition ein = new FadeTransition(Duration.millis(500), t);
                        ein.setFromValue(0.0);
                        ein.setToValue(1.0);
                        st.getChildren().add(ein);
                        break;
                    case permanent:
                        final FadeTransition pin = new FadeTransition(Duration.millis(2000), t);
                        pin.setFromValue(0.0);
                        pin.setToValue(1.0);
                        st.getChildren().add(pin);
                    default:
                        break;
                }
                switch(e.getMessageType()) {
                    case ephemeral:
                        st.getChildren().add(new PauseTransition(Duration.millis(3000)));
                        final FadeTransition out = new FadeTransition(Duration.millis(1000), t);
                        out.setFromValue(1.0);
                        out.setToValue(0.0);
                        st.getChildren().add(out);
                        break;
                    case permanent:
                    default:
                        break;
                }
                st.play();
            }
        });
    }
}
