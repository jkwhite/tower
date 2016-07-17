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


import org.excelsi.tower.Sakura;
import org.excelsi.aether.ui.*;
import com.jme.scene.Node;
import com.jme.curve.BezierCurve;
import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import org.excelsi.aether.Rand;
import com.jme.scene.Spatial;
import com.jme.curve.CurveController;


public class SakuraNodeFactory extends DefaultNHSpaceNodeFactory {
    public void lock(Node n) {
        n.lockBounds();
    }

    public Node createNode(String name, Object o, Node parent) {
        Node n = super.createNode(name, o, parent);
        Spatial sakura = n.getChild(0);
        sakura.setLocalTranslation(new Vector3f(2*tiny(), 0f, 2*tiny()));
        sakura.setLocalRotation(new Quaternion(new float[]{2f*(float)Math.PI*Rand.om.nextFloat(), 0f, (float) -Math.PI/2f}));

        if(Rand.d100(85)) {
            BezierCurve bc = new BezierCurve("s", new Vector3f[]{
                    new Vector3f(2, 6, -2),
                    new Vector3f(0, 0, 0)});
            Node falling = NodeFactory.loadModel("'", new Sakura().getColor());
            FixedCurveController f = new FixedCurveController(bc, falling, 4f+Rand.om.nextFloat(), Rand.om.nextFloat());
            f.setRepeatType(FixedCurveController.RT_WRAP);
            falling.addController(f);
            float angle = 2f*(float)Math.PI*Rand.om.nextFloat();
            falling.setLocalRotation(new Quaternion(new float[]{0f, angle, 0f}));
            FixedTimeController f2 = new FixedTimeController(new SpinModulator(falling.getChild(0), new float[]{0f, 0f, (float)Math.PI/2f},
                        new float[]{0f, (float)Math.PI*2f, (float)Math.PI/2f}), FixedTimeController.CONSTANT, 0.7f+Rand.om.nextFloat());
            //FixedTimeController f2 = new FixedTimeController(new SpinModulator(falling, new float[]{0f, 0f, (float)Math.PI/2f},
                        //new float[]{(float)Math.PI*2f, 0f, (float)Math.PI/2f}), FixedTimeController.CONSTANT, 0.7f);
            f2.setRepeatType(FixedCurveController.RT_WRAP);
            falling.getChild(0).addController(f2);
            n.attachChild(falling);
            falling.setCullMode(Node.CULL_NEVER);
        }

        //final WaterController cc = new WaterController(bc, n.getChild(0), 3f, 0f);
        //cc.setRepeatType(Controller.RT_WRAP);
        //n.getChild(0).addController(cc);

        //final Grass g = (Grass) o;
        //g.addMSpaceListener(new MSpaceAdapter() {
        //EventQueue.getEventQueue().addMSpaceListener(g, new NHSpaceAdapter() {
            //public void moved(MSpace source, MSpace from, MSpace to, Bot b) {
                //if(to==g) {
                    //NHBot nb = (NHBot) b;
                    //if(!nb.isLevitating()&&!nb.isAirborn()
                        //&&nb.getEnvironment().getPlayer().getEnvironment().getVisibleBots().contains(b)) {
                        //sakura.addController(new FixedCurveController(bc, sakura, 1f) {
                            //protected void done() {
                                //sakura.removeController(this);
                            //}
                        //});
                    //}
                //}
            //}
        //});
        return n;
    }

    private static float tiny() {
        return (Rand.om.nextFloat()-0.5f)/2f;
    }
}
