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


import org.excelsi.tower.*;
import org.excelsi.aether.ui.*;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.curve.BezierCurve;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import org.excelsi.aether.*;
import com.jme.math.FastMath;


public class QuaffController extends RemovingCurveController implements StoppableController {
    public QuaffController(NHBot b, Object a, Node n) {
        super(new BezierCurve("e", new Vector3f[]{new Vector3f(0.0f, 2.5f, 0f),
               new Vector3f(0.0f, 4.5f, 0f)}),
               NodeFactory.loadModel(((Quaff)a).getOriginalItem(), "ee"));
        setRepeatType(RT_CLAMP);
        setUpVector(MatrixNode.UP);
        getSpatial().addController(this);
        Spatial s = getSpatial();
        n.attachChild(s);
        setSpeed(1.3f);
        FixedTimeController t = new FixedTimeController(new SpinModulator(s, SpinModulator.X, -FastMath.PI/2f, FastMath.PI/2f),
            FixedTimeController.CONSTANT, 1f, 0f) {
            protected void done() {
                getSpatial().removeController(this);
            }
        };
        t.setSpeed(1.3f);
        s.addController(t);
        n.updateRenderState();
        Audio.getAudio().play(b, "potion");
    }

    public void stop() {
        setRepeatType(RT_CLAMP);
    }

    protected void done() {
        super.done();
        if(getSpatial().getParent()!=null) {
            getSpatial().getParent().detachChild(getSpatial());
        }
    }
}
