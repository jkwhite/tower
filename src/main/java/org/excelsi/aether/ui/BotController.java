package org.excelsi.aether.ui;


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.ChangeEvent;
import org.excelsi.aether.MoveEvent;
import org.excelsi.aether.AddEvent;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


public class BotController implements Controller<MSpace> {
    @Override public void added(final SceneContext c, final AddEvent<MSpace> b) {
    }

    @Override public void removed(final SceneContext c, final MSpace b) {
    }

    @Override public void changed(final SceneContext c, final ChangeEvent<MSpace> e) {
        if(e instanceof MoveEvent) {
            final MoveEvent me = (MoveEvent) e;
            final NHBot b = (NHBot) me.getBot();
            final Spatial s = c.getSpatial(me.getBot().getId());
            //Spaces.translate(me.getBot().getEnvironment().getSpace(), s);
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
