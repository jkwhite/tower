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


import org.excelsi.tower.Liquid;
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


public class LiquidNodeFactory extends DefaultNHSpaceNodeFactory {
    private static final Vector3f[] POINTS;
    private static final Vector3f[] POINTS2;
    private static MaterialState _mat;

    static {
        Vector3f left = new Vector3f(-0.3f, 0f, 0f);
        Vector3f right = new Vector3f(0.3f, 0f, 0f);
        Vector3f middle = new Vector3f(0f, -0.3f, 0f);
        POINTS = new Vector3f[]{
            left, left, middle, right, right, middle, left, left
        };
        POINTS2 = new Vector3f[]{right, right, middle, left, left, middle, right, right};
    }

    public void lock(Node n) {
        n.lockBounds();
        n.lockShadows();
        n.lockMeshes();
    }

    public Node createNode(String name, Object o, Node parent) {
        Node n = super.createNode(name, o, parent);
        Node ret = n;
        n = (Node) n.getChild(0);
        float cyc = ((Liquid)o).getCycle();
        Vector3f[] points = POINTS;
        if(cyc==1f) {
            points = POINTS2;
        }
        BezierCurve bc = new BezierCurve("w", points);
        final LiquidController cc = new LiquidController(bc, n.getChild(0), 3f, 0f);
        cc.setRepeatType(Controller.RT_WRAP);
        n.getChild(0).addController(cc);
        Box box = new Box("h", new Vector3f(0.0f, -1f, 0.0f), MatrixNode.HORIZ_RATIO-0.2f, 0.6f, (MatrixNode.VERT_RATIO-0.2f)/2f);
        if(_mat==null) {
            MaterialState m = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
            m.setEnabled(true);
            m.setDiffuse(new ColorRGBA(0.0f, 0.0f, 0.0f, 1f));
            m.setAmbient(new ColorRGBA(0.0f, 0.0f, 0.0f, 1f));
            m.setSpecular(new ColorRGBA(0.0f, 0.0f, 0.0f, 1f));
            m.setShininess(0f);
            //m.setAlpha(0.8f);
            _mat = m;
        }
        box.setRenderState(_mat);
        n.attachChild(box);

        final Liquid w = (Liquid) o;
        //w.addMSpaceListener(new MSpaceAdapter() {
        EventQueue.getEventQueue().addMSpaceListener(w, new NHSpaceAdapter() {
            public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
                if(to==w) {
                    NHBot nb = (NHBot) b;
                    try {
                        if(nb.getEnvironment().getPlayer().getEnvironment().getVisibleBots().contains(b)) {
                            cc.ping();
                        }
                    }
                    catch(NullPointerException e) {
                        // TODO: not exactly sure why this can happen. it seems to occur
                        // when newly-added bots are within the player's visibility
                        // for a short period of time before they start moving,
                        // when the new bot is on water.
                    }
                }
            }
        });
        //return n;
        return ret;
    }

    private static float tiny() {
        return (Rand.om.nextFloat()-0.5f)/3f;
    }

    private static class LiquidController extends FixedCurveController {
        private int _mult = 1;
        private float _multElapsed = 0f;
        private float _s;


        public LiquidController(Curve c, Spatial s, float speed, float delay) {
            super(c, s, speed, delay);
            _s = speed;
        }

        public void update(float dt) {
            if(_mult>1) {
                _multElapsed += dt;
                if(_multElapsed>_s) {
                    _multElapsed = 0f;
                    _mult/=2;
                    setSpeed(_mult);
                }
            }
            super.update(dt);
        }

        void ping() {
            _mult = 4;
            setSpeed(_mult);
        }
    }
}
