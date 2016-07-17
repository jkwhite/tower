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


import org.excelsi.aether.ui.FixedCurveController;
import com.jme.scene.Node;
import com.jme.curve.BezierCurve;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import org.excelsi.aether.*;
import org.excelsi.tower.*;
import org.excelsi.aether.ui.*;


public class ColdModifier implements UIModifier {
    public void modify(Object o, Node n) {
        Cold f = (Cold) o;
        Node mod = NodeFactory.loadModel(f.getOwner().getModel(), "white");

        float alt = 0f;
        BezierCurve bc = new BezierCurve("f", new Vector3f[]{new Vector3f(0f, -alt, 0f), new Vector3f(0.2f, 0.5f,-1.0f)});
        final FixedCurveController.CurveModulator cm = new FixedCurveController.CurveModulator(bc, mod);
        ColorModulator co = new ColorModulator(mod, NodeFactory.colorFor("white"), NodeFactory.colorFor("yellow"));
        FixedTimeController cc = new FixedTimeController(new FixedTimeController.Modulator[]{
            cm, co}, FixedTimeController.CONSTANT, 1f, Rand.om.nextFloat());
        cc.setRepeatType(Controller.RT_WRAP);
        mod.addController(cc);
        n.attachChild(mod);
    }
}
