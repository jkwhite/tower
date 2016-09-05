package org.excelsi.aether.ui.jfx;


import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Group;

import org.excelsi.aether.BotAttributeChangeEvent;
import org.excelsi.aether.ChangeEvent;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.Affliction;


public class JfxPatsy extends HudNode {
    private String _format;


    public JfxPatsy() {
        super("status");
        addLogicHandler((le)->{
            if(le.e() instanceof BotAttributeChangeEvent) {
                botChange((BotAttributeChangeEvent)le.e());
            }
        });
    }

    public void setFormat(String format) {
        _format = format;
    }

    public String getFormat() {
        return _format;
    }

    private void botChange(BotAttributeChangeEvent e) {
        final NHBot b = (NHBot) e.getContext();
        if(b.isPlayer()) {
            final String text = String.format("%s the %-20s %3d/%3d",
                b.getName(),
                b.getProfession(),
                b.getHp(),
                b.getMaxHp()
            );
            StringBuilder s = new StringBuilder(text);
            for(final Affliction a:b.getAfflictions()) {
                s.append(" ").append(a.getStatus());
            }
            if(!getChildren().isEmpty()) {
                getChildren().remove(0);
            }
            final Label t = new Label(s.toString());
            t.getStyleClass().add("status");
            getChildren().add(t);
        }
    }
}
