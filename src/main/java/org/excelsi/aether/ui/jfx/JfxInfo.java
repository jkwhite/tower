package org.excelsi.aether.ui.jfx;


import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Group;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import org.excelsi.aether.Container;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.InfoEvent;


public class JfxInfo extends HudNode {
    public JfxInfo() {
        addLogicHandler((le)->{
            if(le.e() instanceof InfoEvent) {
                final InfoEvent ie = le.<InfoEvent>e();
                final NHBot src = (NHBot) ie.getSource();
                if(ie.getShown() instanceof Container) {
                    final Container cn = (Container)ie.getShown();
                    final JfxContainer c = new JfxContainer(ie, src, cn, ie.hints());
                    getChildren().add(c);
                    //transition(c, null);
                    le.consume();
                }
                else if(ie.getShown()==null) {
                    Fx.removeAll(this);
                }
            }
        });
    }
}
