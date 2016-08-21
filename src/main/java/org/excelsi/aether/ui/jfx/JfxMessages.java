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

import org.excelsi.aether.MessageEvent;


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
