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


import org.excelsi.tower.Grass;
import org.excelsi.aether.ui.DefaultNHSpaceNodeFactory;
import org.excelsi.aether.ui.FixedCurveController;
import com.jme.scene.Node;
import com.jme.curve.BezierCurve;
import com.jme.math.Vector3f;
import com.jme.curve.CurveController;
import com.jme.scene.Controller;
import org.excelsi.aether.Rand;
import com.jme.scene.state.MaterialState;
import com.jme.scene.shape.Box;
import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;
import org.excelsi.aether.ui.MatrixNode;
import com.jme.curve.Curve;
import com.jme.scene.Spatial;
import org.excelsi.aether.NHSpaceAdapter;
import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Bot;
import org.excelsi.aether.NHBot;
import org.excelsi.aether.EventQueue;


public class GrassNodeFactory extends DefaultNHSpaceNodeFactory {
    public void lock(Node n) {
        n.lockBounds();
        n.lockShadows();
        n.lockMeshes();
    }

    public Node createNode(String name, Object o, Node parent) {
        Node n = super.createNode(name, o, parent);
        final Spatial grass = n.getChild(0);
        grass.setLocalTranslation(new Vector3f(2*tiny(), 0f, 2*tiny()));

        Vector3f[] points = new Vector3f[4];
        points[0] = new Vector3f(grass.getLocalTranslation());
        points[points.length-1] = new Vector3f(grass.getLocalTranslation());
        for(int i=1;i<points.length-1;i++) {
            points[i] = new Vector3f(tiny(), 0f, tiny());
        }
        final BezierCurve bc = new BezierCurve("g", points);

        final Grass g = (Grass) o;
        EventQueue.getEventQueue().addMSpaceListener(g, new NHSpaceAdapter() {
            public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
                if(to==g) {
                    NHBot nb = (NHBot) b;
                    if(!nb.isLevitating()&&!nb.isAirborn()
                        &&nb.getEnvironment().getPlayer().getEnvironment().getVisibleBots().contains(b)) {
                        grass.addController(new FixedCurveController(bc, grass, 1f) {
                            protected void done() {
                                grass.removeController(this);
                            }
                        });
                    }
                }
            }
        });
        return n;
    }

    static float tiny() {
        return (Rand.om.nextFloat()-0.5f)/2f;
    }
}
