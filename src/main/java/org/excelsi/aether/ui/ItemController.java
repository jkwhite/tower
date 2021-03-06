package org.excelsi.aether.ui;


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.aether.AddEvent;
import org.excelsi.aether.AttributeChangeEvent;
import org.excelsi.aether.ChangeEvent;
import org.excelsi.aether.ContainerAddEvent;
import org.excelsi.aether.RemoveEvent;
import org.excelsi.aether.NHSpace;
import org.excelsi.aether.Item;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.LightNode;
import com.jme3.light.Light;
import com.jme3.scene.Spatial;


public class ItemController extends ChangeController<NHSpace,Item> {
    @Override protected void added(final SceneContext c, final AddEvent<NHSpace,Item> e) {
        ContainerAddEvent ce = (ContainerAddEvent) e;
        final NHSpace mms = (NHSpace) e.getSource();
        final SpaceNode sp = (SpaceNode) c.getNode(mms.getId());
        if(sp!=null) {
            sp.attachItem(c, e.getAdded(), ce.getIndex(), ce.getIncremented());
        }
        else {
            log().debug("no space for "+mms.getId());
        }
    }

    @Override protected void removed(final SceneContext c, final RemoveEvent<NHSpace,Item> l) {
        //log().info("remove event: "+l);
        final SpaceNode sp = (SpaceNode) c.getNode(l.getContext().getId());
        if(sp!=null) {
            sp.detachItem(c, l.getRemoved());
        }
        else {
            log().debug("no space for "+l.getContext());
        }
    }

    @Override protected void changed(final SceneContext c, final ChangeEvent<NHSpace,Item> e) {
    }

    @Override protected void attributeChanged(SceneContext c, AttributeChangeEvent<NHSpace,Item> e) {
    }
}
