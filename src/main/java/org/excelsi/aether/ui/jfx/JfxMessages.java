package org.excelsi.aether.ui.jfx;


import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
import com.jme3.scene.Spatial;

import org.excelsi.matrix.Typed;
import org.excelsi.aether.MessageEvent;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.Item;
import org.excelsi.aether.Grammar;


public class JfxMessages extends HudNode {
    //private final Map<Typed,List<MessageEvent>> _stacks = new HashMap<>();


    public JfxMessages() {
        addLogicHandler((le)->{
            if(le.e() instanceof MessageEvent) {
                final MessageEvent e = (MessageEvent) le.e();
                if(e.getMessage()==null) {
                    return;
                }
                final int offset = Messages.instance().stack(e);
                final Node t;
                if(e.getMessage() instanceof Item) {
                    if(e.getHints().isKeyed()) {
                        t = new Label(Grammar.format((NHBot)e.getSource(), "%K", ((NHBot)e.getSource()).getInventory(), e.getMessage()));
                    }
                    else {
                        t = new Label(e.getMessage().toString());
                    }
                }
                else {
                    final String msg = e.getMessage().toString();
                    if(msg.length()<80) {
                        t = new Label(msg.trim());
                    }
                    else {
                        BorderPane bt = new BorderPane();
                        final Text txt = new Text(msg.trim());
                        txt.getStyleClass().add("message");
                        txt.getStyleClass().add(e.getMessageType().toString());
                        bt.setTop(txt);
                        if(e.getMessageType()==MessageEvent.Type.narrative) {
                            bt.relocate(0,0);
                            bt.setPrefSize(600,600);
                            Fx.center(le.ctx(), bt);
                        }
                        t = bt;
                    }
                }
                if(e.getHints().isModal()) {
                    addModalHandler(e, t);
                }
                t.getStyleClass().add("message");
                t.getStyleClass().add(e.getMessageType().toString());
                if(e.getSource() instanceof Typed) {
                    Fx.localize(le.ctx(), (Typed)e.getSource(), t);
                    //System.err.println("m setting offset "+offset);
                    t.setLayoutY(Messages.UI_PX_OFFSET*offset);
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
                        st.getChildren().add(new PauseTransition(Duration.millis(2000+Messages.TIME_MS_OFFSET*offset)));
                        final FadeTransition out = new FadeTransition(Duration.millis(500), t);
                        out.setFromValue(1.0);
                        out.setToValue(0.0);
                        st.getChildren().add(out);
                        st.setOnFinished((ev)->{
                            JfxMessages.this.getChildren().remove(t);
                            Messages.instance().unstack(e);
                        });
                        break;
                    case permanent:
                    default:
                        break;
                }
                st.play();
            }
        });
    }

    /*
    private synchronized int stack(final MessageEvent e) {
        List<MessageEvent> ms = _stacks.get(e.getSource());
        if(ms==null) {
            ms = new LinkedList<>();
            _stacks.put(e.getSource(), ms);
        }
        ms.add(e);
        return ms.size()-1;
    }

    private synchronized void unstack(final MessageEvent e) {
        List<MessageEvent> ms = _stacks.get(e.getSource());
        if(ms!=null) {
            ms.remove(e);
            if(ms.isEmpty()) {
                _stacks.remove(e.getSource());
            }
        }
    }
    */
}
