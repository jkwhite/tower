package org.excelsi.aether.ui;


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.aether.AddEvent;
import org.excelsi.aether.AttributeChangeEvent;
import org.excelsi.aether.ChangeEvent;
import org.excelsi.aether.ContainerAddEvent;
import org.excelsi.aether.RemoveEvent;
import org.excelsi.aether.NHSpace;
import org.excelsi.aether.Level;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.LightNode;
import com.jme3.light.Light;
import com.jme3.scene.Spatial;


public class SpaceController extends ChangeController<Level,NHSpace> {
    @Override protected void added(final SceneContext c, final AddEvent<Level,NHSpace> e) {
        //final NHSpace mms = e.getAdded();
        //final Node lev = c.getNode(e.getContext());
        //if(lev!=null) {
            //Spaces.createSpace(c, lev, mms);
        //}
        //else {
            //log().warn("no space for "+e.getContext());
        //}
    }

    @Override protected void removed(final SceneContext c, final RemoveEvent<Level,NHSpace> e) {
        //final Node sp = c.getNode(e.getRemoved());
        //if(sp!=null) {
            //Nodes.detachFromParent(sp);
        //}
        //else {
            //log().warn("no space for "+e.getRemoved());
        //}
    }

    @Override protected void changed(final SceneContext c, final ChangeEvent<Level,NHSpace> e) {
        //log().info("SPACE CHANGE: "+e);
    }

    @Override protected void attributeChanged(SceneContext c, AttributeChangeEvent<Level,NHSpace> e) {
        //log().info("SPACE ATTR CHANGE: "+e);
        final Node lev = c.getNode(e.getContext());
        if(lev!=null) {
            Spaces.updateSpace(c, lev, e.getE());
        }
    }
}
