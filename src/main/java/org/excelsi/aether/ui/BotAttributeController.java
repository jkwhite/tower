package org.excelsi.aether.ui;


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.ChangeEvent;
import org.excelsi.aether.BotAttributeChangeEvent;
import org.excelsi.aether.AddEvent;
import org.excelsi.aether.RemoveEvent;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


public class BotAttributeController extends ChangeController {
    @Override protected void added(final SceneContext c, final AddEvent b) {
    }

    @Override protected void removed(final SceneContext c, final RemoveEvent b) {
    }

    @Override protected void changed(final SceneContext c, final ChangeEvent e) {
        // TODO: move dead to bot space controller?
        final BotAttributeChangeEvent be = (BotAttributeChangeEvent) e;
        final Spatial bot = c.getSpatial(be.getBot().getId());
        switch(be.getAttribute()) {
            case "dead":
                Nodes.detachFromParent(bot);
        }
    }
}
