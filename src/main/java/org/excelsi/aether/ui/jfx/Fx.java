package org.excelsi.aether.ui.jfx;


import javafx.scene.Group;
import javafx.scene.Node;
import javafx.event.EventHandler;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import org.excelsi.matrix.Typed;
import org.excelsi.aether.ui.SceneContext;


public final class Fx {
    public static void removeAll(final Group p) {
        while(p.getChildren().size()>0) {
            p.getChildren().remove(0);
        }
    }

    public static void localize(SceneContext c, Typed t, Node n) {
        if(t!=null) {
            final Spatial src = c.getSpatial(t);
            if(src!=null) {
                Vector3f wp = src.getWorldTranslation();
                Vector3f sp = c.getCamera().getScreenCoordinates(wp);
                final int w = c.getCamera().getWidth();
                final int h = c.getCamera().getHeight();
                n.setTranslateX(sp.x-50);
                n.setTranslateY(h-40-sp.y);
                //System.err.println("W****: "+((Label)n).getPrefWidth());
                //System.err.println("setting screen coords: "+sp+" from "+wp+" for "+n+" message: "+e.getMessage()+"; jfxx="+n.getTranslateX()+", jfxy="+n.getTranslateY()+"; jfxlx="+n.getLayoutX()+", jfxly="+n.getLayoutY());
            }
        }
        else {
            final int w = c.getCamera().getWidth();
            final int h = c.getCamera().getHeight();
            n.setTranslateX(w/2-20);
            n.setTranslateY(h/2-10);
        }
    }

    public static Node decorate(final Node n, final EventHandler<MouseEvent> onClick) {
        n.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                n.setEffect(new Glow());
            }
        });
        n.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                n.setEffect(null);
            }
        });
        n.setOnMouseClicked(onClick);
        return n;
    }

    private Fx() {}
}
