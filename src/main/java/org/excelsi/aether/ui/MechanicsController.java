package org.excelsi.aether.ui;


import org.excelsi.aether.Event;
import org.excelsi.aether.MechanicsEvent;
import com.jme3.scene.Spatial;
import com.jme3.scene.Node;
import com.jme3.math.Vector3f;


public class MechanicsController extends Enloggened implements Controller {
    @Override public void handle(final SceneContext c, final Event e) {
        final MechanicsEvent me = (MechanicsEvent) e;
        switch(me.getMechanicsType()) {
            case start:
                attackStarted(c, me);
                break;
        }
    }

    private void attackStarted(final SceneContext c, final MechanicsEvent e) {
        System.err.println("starting attack: "+e);
        final Node n = c.getNode(e.getAttacker().getId());
        if(n!=null) {
            final Spatial s = n.getChild("localMove");
            Animations.lunge(s, new Vector3f(0f,0f,UIConstants.SCALE*0.7f), 0.25f);
        }
        else {
            log().error("no ui for "+e.getAttacker()+" "+e.getAttacker().getId());
        }
    }
}
