package org.excelsi.aether.ui;


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.MatrixMSpace;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.ChangeEvent;
import org.excelsi.aether.OrientEvent;
import org.excelsi.aether.AddEvent;
import org.excelsi.aether.AttributeChangeEvent;
import org.excelsi.aether.RemoveEvent;
import org.excelsi.matrix.Direction;

import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


public class OrientController extends ChangeController<NHBot,Direction> {
    @Override protected void added(final SceneContext c, final AddEvent<NHBot,Direction> b) {
    }

    @Override protected void removed(final SceneContext c, final RemoveEvent<NHBot,Direction> b) {
    }

    @Override protected void changed(final SceneContext c, final ChangeEvent<NHBot,Direction> e) {
        if(e instanceof OrientEvent) {
            final OrientEvent me = (OrientEvent) e;
            final NHBot b = (NHBot) me.getBot();
            final Spatial s = c.getSpatial(me.getBot().getId());
            if(s!=null) {
                final int rot = rotFor(e.getTo());
                s.setLocalRotation(new Quaternion(new float[]{0f, UIConstants.ROTATIONS[rot], 0f}));
            }
            else {
                log().debug("no spatial for "+me.getBot());
            }
        }
    }

    @Override protected void attributeChanged(SceneContext c, AttributeChangeEvent<NHBot,Direction> e) {
    }

    public static int rotFor(Direction d) {
        if(d==null) {
            return 0;
        }
        switch(d) {
            case southwest:
                return 7;
            case west:
                return 6;
            case northwest:
                return 5;
            case north:
                return 4;
            case northeast:
                return 3;
            case east:
                return 2;
            case southeast:
                return 1;
            case south:
                return 0;
            default:
                //throw new IllegalArgumentException("unknown direction '"+d+"'");
                return 0;
        }
    }
}
