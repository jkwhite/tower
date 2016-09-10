package org.excelsi.aether.ui;


import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.bounding.BoundingBox;


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

    public static Spatial centerAbove(final Spatial s) {
        final Vector3f wc = s.getWorldBound().getCenter();
        s.getLocalTranslation().x -= wc.x;
        s.getLocalTranslation().z -= wc.z;
        return s;
    }

    public static Spatial centerBelow(final Spatial s) {
        final Vector3f wc = s.getWorldBound().getCenter();
        s.getLocalTranslation().x -= wc.x;
        s.getLocalTranslation().z -= wc.z;
        s.getLocalTranslation().y -= 2f*wc.y;
        return s;
    }

    private Nodes() {}
}
