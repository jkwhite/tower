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
        final Node lev = Spaces.findLevel(c, b.getContext());
        if(lev!=null) {
            Bots.attachBot(c, lev, b.getContext());
        }
        else {
            log().debug("no level for "+b);
        }
    }

    @Override protected void removed(final SceneContext c, final RemoveEvent<NHBot,NHSpace> b) {
        final Node lev = Spaces.findLevel(c, b.getContext());
        if(lev!=null) {
            Bots.detachBot(c, lev, b.getContext());
        }
        else {
            log().debug("no level for "+b);
        }
    }

    @Override protected void changed(final SceneContext c, final ChangeEvent<NHBot,NHSpace> e) {
        if(e instanceof MoveEvent) {
            final MoveEvent me = (MoveEvent) e;
            final NHBot b = (NHBot) me.getBot();
            final Spatial s = c.getSpatial(me.getBot().getId());
            if(s!=null) {
                Animations.move(s, s.getLocalTranslation(), Spaces.translation(me.getBot().getEnvironment().getSpace()), 0.25f);
                if(b.isPlayer()) {
                    updateView(c, me.getBot().getEnvironment().getSpace());
                }
            }
            else {
                log().debug("move for unknown bot "+me.getBot().getId());
            }
        }
    }

    private void updateView(final SceneContext c, final MSpace m) {
        c.<CloseView>getCameraNode().center(Spaces.translation(m));
    }
}
