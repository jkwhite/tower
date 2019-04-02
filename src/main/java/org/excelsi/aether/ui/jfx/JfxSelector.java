package org.excelsi.aether.ui.jfx;


import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Group;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import org.excelsi.aether.SelectEvent;
import org.excelsi.aether.QueryEvent;


/**
 * Creates displays for selection events.
 */
public class JfxSelector extends HudNode {
    public JfxSelector() {
        addLogicHandler((le)->{
            if(le.e() instanceof SelectEvent) {
                final JfxMenu menu = new JfxMenu(le.e(), ((SelectEvent)le.e()).getMenu());
                getChildren().add(menu);
                //System.err.println("***AADDDED MENU*****");
                transition(menu, null);
                le.consume();
            }
            else if(le.e() instanceof QueryEvent) {
                final JfxQuery q = new JfxQuery((QueryEvent)le.e(), le.ctx());
                final int offset = Messages.instance().stack(le.e());
                //System.err.println("q setting offset "+offset);
                q.setLayoutY(Messages.UI_PX_OFFSET*offset);
                getChildren().add(q);
                transition(q, null);
                le.consume();
            }
        });
    }
}
