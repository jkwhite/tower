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
import org.excelsi.tower.Electromagnet;
import org.excelsi.aether.ui.*;
import org.excelsi.matrix.Bot;
import org.excelsi.matrix.MSpace;


public class ElectromagnetEMFNodeFactory extends NodeFactory {
    public void lock(Node n) {
    }

    public Node createNode(String name, final Object o, Node parent) {
        Node n = loadModel("-", "translucent-lavender");
        n.setLocalScale(new Vector3f(1f, 1f, 0f));
        FixedTimeController f = new FixedTimeController(new SizeModulator(n, SizeModulator.Z),
            FixedTimeController.CONSTANT, 0.7f, ((Electromagnet.EMF)o).getOffset()/*Rand.om.nextFloat()*/);
        f.setRepeatType(f.RT_WRAP);
        n.addController(f);
        return n;
    }
}
