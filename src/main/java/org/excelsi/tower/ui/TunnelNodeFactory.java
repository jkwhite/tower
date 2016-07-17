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


public class TunnelNodeFactory extends DefaultNHSpaceNodeFactory {
    public Node createNode(String name, final Object o, Node parent) {
        Node n = super.createNode(name, o, parent);
        n.setLocalScale(1.5f);
        FixedTimeController c = new FixedTimeController(new FixedTimeController.Modulator[]{
                new SpinModulator(n, SpinModulator.Y)}, FixedTimeController.CONSTANT, 8f);
        c.setRepeatType(FixedTimeController.RT_WRAP);
        n.addController(c);
        return n;
    }
}
