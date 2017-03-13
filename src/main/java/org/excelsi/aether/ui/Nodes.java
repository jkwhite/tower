package org.excelsi.aether.ui;


import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.bounding.BoundingBox;

import org.excelsi.matrix.Typed;


public class Nodes {
    public static Typed findTyped(final Spatial s) {
        Node n = s.getParent();
        while(n!=null && !(n instanceof TypedNode)) {
            n = n.getParent();
        }
        if(n!=null) {
            return ((TypedNode)n).getTyped();
        }
        return null;
    }

    public static Geometry findGeometry(Spatial s) {
        if(s instanceof Geometry) {
            return (Geometry) s;
        }
        else if(s instanceof Node) {
            Node n = (Node) s;
            final Geometry[] gs = new Geometry[1];
            n.breadthFirstTraversal(new SceneGraphVisitor() {
                @Override public void visit(final Spatial child) {
                    if(child instanceof Geometry) {
                        gs[0] = (Geometry) child;
                    }
                }
            });
            return gs[0];
        }
        return null;
    }

    public static void detachFromParent(final Spatial n) {
        if(n!=null && n.getParent()!=null) {
            n.getParent().detachChild(n);
        }
    }

    public static Spatial center(final Spatial s) {
        if(s!=null) {
            s.getLocalTranslation().subtractLocal(s.getWorldBound().getCenter());
        }
        return s;
    }

    public static Spatial centerAbove(final Spatial s) {
        if(s.getWorldBound()!=null) {
            final Vector3f wc = s.getWorldBound().getCenter();
            s.getLocalTranslation().x -= wc.x;
            s.getLocalTranslation().z -= wc.z;
        }
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
