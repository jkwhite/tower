package org.excelsi.aether.ui;


import org.excelsi.matrix.MSpace;
import org.excelsi.aether.NHBot;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.aether.Bulk;
import org.excelsi.aether.Level;
import org.excelsi.aether.Stage;
import org.excelsi.aether.ChangeEvent;
import org.excelsi.aether.AddEvent;
import org.excelsi.aether.RemoveEvent;
import org.excelsi.aether.NHSpace;
import org.excelsi.aether.Item;

import com.jme3.scene.Node;
import com.jme3.scene.LightNode;
import com.jme3.light.Light;
import com.jme3.scene.Spatial;


public class LevelController extends ChangeController<Bulk,Stage> {
    @Override protected void added(final SceneContext c, final AddEvent<Bulk,Stage> l) {
    }

    @Override protected void removed(final SceneContext c, final RemoveEvent<Bulk,Stage> l) {
    }

    @Override protected void changed(final SceneContext c, final ChangeEvent<Bulk,Stage> e) {
        if(e.getFrom()!=null) {
            Node from = c.getNode(e.getFrom().getId());
            if(from!=null) {
                Nodes.detachFromParent(from);
            }
            //c.removeSpatial(e.getFrom());
            c.removeAll();
        }
        if(e.getTo()!=null) {
            final Node lev = (Node) c.getNodeFactory().createNode(e.getTo().getId(), e.getTo(), c);
            c.getRoot().attachChild(lev);
            c.addNode(lev);

            /*
            for(final MSpace m:e.getTo().getMatrix().spaces()) {
                final MatrixMSpace mms = (MatrixMSpace) m;
                if(mms!=null) {
                    createSpace(c, lev, mms);
                }
            }
            for(NHBot b:((Level)e.getTo()).bots()) {
                addBot(c, lev, b);
            }
            */
        }
    }

    private void addBot(final SceneContext c, final Node lev, final NHBot b) {
        Bots.attachBot(c, lev, b);
    }

    private Spatial createSpace(final SceneContext c, final Node lev, final MatrixMSpace mms) {
        return Spaces.createSpace(c, lev, (NHSpace) mms);
    }
}
