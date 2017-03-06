package org.excelsi.aether.ui;


import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;
import org.excelsi.aether.Item;
import org.excelsi.aether.Parasite;
import org.excelsi.aether.NHSpace;


public class SpaceNode extends Node {
    private final NHSpace _space;


    public SpaceNode(final NHSpace space) {
        super(space.getId());
        _space = space;
    }

    public void attachItem(final SceneContext c, final Item item, final int idx, final boolean incremented) {
        if(getChild(item.getId())!=null) {
            System.err.println("WARN: pruned duplicate child for "+item+" on "+_space+": idx="+idx+", inc="+incremented);
            return;
        }
        final Spatial s = Spaces.createItem(c, item);
        s.setLocalTranslation(new Vector3f(0f, 0.2f*idx, 0f));
        attachChild(s);
    }

    public void attachParasite(final SceneContext c, final Parasite p) {
        //System.err.println("attaching parasite: "+p+" to "+this);
        if(getChild(p.getId())!=null) {
            System.err.println("WARN: pruned duplicate child for "+p+" on "+_space);
            return;
        }
        final Spatial s = Spaces.createParasite(c, p);
        //s.setLocalTranslation(new Vector3f(0f, 0.2f*idx, 0f));
        attachChild(s);
    }

    public void detachItem(final SceneContext c, final Item item) {
        //System.err.println("initial children: "+getChildren());
        final int idx = detachChildNamed(item.getId());
        if(idx==-1) {
            System.err.println("WARN: no such child "+item+" for "+_space);
        }
        //else {
            //System.err.println("removed child "+item+" for "+_space+" at "+idx);
        //}
        //System.err.println("remaining children: "+getChildren());
    }

    public void detachParasite(final SceneContext c, final Parasite p) {
        //System.err.println("initial children: "+getChildren());
        final int idx = detachChildNamed(p.getId());
        if(idx==-1) {
            System.err.println("WARN: no such child "+p+" for "+_space);
        }
    }
}
