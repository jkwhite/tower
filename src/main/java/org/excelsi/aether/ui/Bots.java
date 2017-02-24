package org.excelsi.aether.ui;


import org.excelsi.aether.NHBot;
import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.MatrixMSpace;
import com.jme3.scene.Node;
import com.jme3.scene.LightNode;
import com.jme3.light.Light;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;


public final class Bots {
    public static void attachBot(final SceneContext c, final Node lev, final NHBot b) {
        if(!c.containsNode(b.getId())) {
            final Spatial bot = c.getNodeFactory().createNode(b.getId(), b, c);
            final MatrixMSpace mms = (MatrixMSpace) b.getEnvironment().getMSpace();
            Spaces.translate(mms, bot);
            c.addNode(bot);
            lev.attachChild(bot);
            if(b.isPlayer()) {
                attachPatsy(lev, c, bot);
            }
        }
    }

    public static void detachBot(final SceneContext c, final Node lev, final NHBot b) {
        final Spatial s = c.getNode(b);
        if(s!=null) {
            lev.detachChild(s);
        }
    }

    private static void attachPatsy(final Node parent, final SceneContext c, final Spatial patsy) {
        //c.<CloseView>getNode(View.NODE_CAMERA).setPlayer(patsy);
        c.<CloseView>getCameraNode().setPlayer(patsy);
        if(patsy instanceof Litten) {
            for(final Light light:((Litten)patsy).getAllLights()) {
                System.err.println("****************  ADDING LIGHT: "+light);
                parent.addLight(light);
            }
            //parent.addLight(((LightNode)patsy).getLight());;
        }
    }

    private Bots() {}
}
