package org.excelsi.aether.ui;


import org.excelsi.matrix.MSpace;
import org.excelsi.aether.NHBot;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.aether.Bulk;
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


public class LevelController implements Controller<Bulk,Stage> {
    private static final String PREFIX = "level-";


    @Override public void added(final SceneContext c, final AddEvent<Bulk,Stage> l) {
    }

    @Override public void removed(final SceneContext c, final RemoveEvent<Bulk,Stage> l) {
    }

    @Override public void changed(final SceneContext c, final ChangeEvent<Bulk,Stage> e) {
        if(e.getFrom()!=null) {
            Node from = c.getNode(PREFIX+e.getFrom().getOrdinal());
            if(from!=null) {
                Nodes.detachFromParent(from);
            }
        }
        if(e.getTo()!=null) {
            final Node lev = (Node) c.getNodeFactory().createNode(PREFIX+e.getTo().getOrdinal(), e.getTo());
            c.getRoot().attachChild(lev);
            c.addNode(lev);

            for(final MSpace m:e.getTo().getMatrix().spaces()) {
                final MatrixMSpace mms = (MatrixMSpace) m;
                if(mms!=null) {
                    createSpace(c, lev, mms);
                }
            }
        }
    }

    private Spatial createSpace(final SceneContext c, final Node lev, final MatrixMSpace mms) {
        final NHSpace space = (NHSpace) mms;
        final SpaceNode ms = (SpaceNode) c.getNodeFactory().createNode(mms.getId(), mms);
        Spaces.translate(mms, ms);
        lev.attachChild(ms);
        final NHBot b = (NHBot) mms.getOccupant();
        if(b!=null) {
            final Spatial bot = c.getNodeFactory().createNode(b.getId(), mms.getOccupant());
            Spaces.translate(mms, bot);
            lev.attachChild(bot);
            c.addNode(bot);
            if(b.isPlayer()) {
                attachPatsy(lev, c, bot);
            }
        }
        final Item[] items = space.getItem();
        if(items!=null) {
            for(Item it:items) {
                //Spaces.attachItem(ms, Spaces.createItem(c, it));
                ms.attachItem(c, it);
            }
        }
        return ms;
    }

    private void attachPatsy(final Node parent, final SceneContext c, final Spatial patsy) {
        c.<CloseView>getNode(View.NODE_CAMERA).setPlayer(patsy);
        if(patsy instanceof Litten) {
            for(final Light light:((Litten)patsy).getAllLights()) {
                System.err.println("****************  ADDING LIGHT: "+light);
                parent.addLight(light);
            }
            //parent.addLight(((LightNode)patsy).getLight());;
        }
    }
}
