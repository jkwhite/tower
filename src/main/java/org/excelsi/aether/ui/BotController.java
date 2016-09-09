package org.excelsi.aether.ui;


import org.excelsi.matrix.MSpace;
import org.excelsi.aether.NHSpace;
import org.excelsi.aether.NHBot;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.ChangeEvent;
import org.excelsi.aether.MoveEvent;
import org.excelsi.aether.AddEvent;
import org.excelsi.aether.RemoveEvent;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


public class BotController extends ChangeController<NHBot,NHSpace> {
    @Override protected void added(final SceneContext c, final AddEvent<NHBot,NHSpace> b) {
    }

    @Override protected void removed(final SceneContext c, final RemoveEvent<NHBot,NHSpace> b) {
    }

    @Override protected void changed(final SceneContext c, final ChangeEvent<NHBot,NHSpace> e) {
        if(e instanceof MoveEvent) {
            final MoveEvent me = (MoveEvent) e;
            final NHBot b = (NHBot) me.getBot();
            final Spatial s = c.getSpatial(me.getBot().getId());
            if(s==null) {
                throw new IllegalArgumentException("move for unknown bot "+me.getBot().getId());
            }
            Animations.move(s, s.getLocalTranslation(), Spaces.translation(me.getBot().getEnvironment().getSpace()));
            if(b.isPlayer()) {
                updateView(c, me.getBot().getEnvironment().getSpace());
            }
        }
    }

    private void updateView(final SceneContext c, final MSpace m) {
        c.<CloseView>getNode(View.NODE_CAMERA).center(Spaces.translation(m));
    }
}
