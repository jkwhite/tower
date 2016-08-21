package org.excelsi.aether.ui;


import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


public class Nodes {
    public static void detachFromParent(final Spatial n) {
        if(n.getParent()!=null) {
            n.getParent().detachChild(n);
        }
    }

    public static Spatial center(final Spatial s) {
        s.getLocalTranslation().subtractLocal(s.getWorldBound().getCenter());
        return s;
    }

    private Nodes() {}
}
