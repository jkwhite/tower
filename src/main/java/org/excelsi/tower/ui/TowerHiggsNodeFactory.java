/*
    Tower
    Copyright (C) 2007, John K White, All Rights Reserved
*/
/*
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/
package org.excelsi.tower.ui;


import com.jme.curve.BezierCurve;
import com.jme.curve.Curve;
import com.jme.curve.CurveController;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;

import org.excelsi.aether.*;
import org.excelsi.aether.ui.*;
import org.excelsi.matrix.Bot;
import org.excelsi.matrix.MSpace;
import static org.excelsi.tower.ui.GrassNodeFactory.tiny;


public class TowerHiggsNodeFactory extends NodeFactory {
    public void lock(Node n) {
    }

    public Node createNode(String name, Object o, final Node parent) {
        final Parasite p = (Parasite) o;

        parent.unlockTransforms();
        final Node grass = (Node) parent.getChild(0);
        Vector3f[] points = new Vector3f[4];
        points[0] = new Vector3f(grass.getLocalTranslation());
        points[points.length-1] = new Vector3f(grass.getLocalTranslation());
        for(int i=1;i<points.length-1;i++) {
            points[i] = new Vector3f(tiny(), 0f, tiny());
        }
        BezierCurve bc = new BezierCurve("g", points);
        final FixedCurveController c = new FixedCurveController(bc, grass, 2f);
        c.setRepeatType(c.RT_WRAP);
        grass.addController(c);
        EventQueue.getEventQueue().addMSpaceListener(p.getSpace(), new NHSpaceAdapter() {
            public void parasiteRemoved(NHSpace s, Parasite par) {
                if(par==p) {
                    grass.removeController(c);
                }
            }
        });
        return new Node(name);
    }
}
