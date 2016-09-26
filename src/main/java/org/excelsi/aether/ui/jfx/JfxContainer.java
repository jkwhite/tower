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

import org.excelsi.aether.NHBot;
import org.excelsi.aether.SelectEvent;
import org.excelsi.aether.Container;
import org.excelsi.aether.Inventory;
import org.excelsi.aether.InfoEvent;
import org.excelsi.aether.Item;
import org.excelsi.aether.DisplayHints;


public class JfxContainer extends HudRegion {
    private static final String SEP = " - ";


    public JfxContainer(final InfoEvent e, final NHBot src, final Container c, final DisplayHints hints) {
        if(hints.isModal()) {
            addModalHandler(e);
        }
        final VBox v = new VBox();
        key(c, hints);
        final boolean showKeys = isKeyed(c);
        final StringBuilder text = new StringBuilder();
        final StringBuilder key = new StringBuilder();
        for(Item i:c.getItem()) {
            final HBox line = new HBox();
            createText(src, showKeys, c, i, key, text, SEP);
            if(showKeys) {
                final Label lkey = new Label(key.toString());
                lkey.getStyleClass().add("key");
                line.getChildren().add(lkey);
            }
            line.getChildren().add(new Label(text.toString()));
            v.getChildren().add(line);
            key.setLength(0);
            text.setLength(0);
            //Label it = new Label(i.toString());
            //v.getChildren().add(it);
        }
        v.getStyleClass().add("inventory");
        getChildren().add(v);
    }

    private void createText(final NHBot b, final boolean showKeys, final Container c, final Item i, final StringBuilder skey, final StringBuilder text, final String sep) {
        if(showKeys&&isKeyed(c)) {
            String key = keyFor(c, i);
            skey.append(key);
            skey.append(sep);
        }
        if(b.isBlind()) {
            text.append(i.toObscureString());
        }
        else {
            text.append(i.toString());
        }
    }

    private static boolean isKeyed(Container c) {
        return c instanceof Inventory && ((Inventory)c).isKeyed();
    }

    private static String keyFor(Container c, Item i) {
        return ((Inventory)c).keyFor(i);
    }

    private static void key(final Container c, DisplayHints hints) {
        if(hints.isKeyed() && c instanceof Inventory) {
            ((Inventory)c).setKeyed(true);
        }
    }
}
