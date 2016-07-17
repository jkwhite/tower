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


import org.excelsi.tower.Consume;
import org.excelsi.aether.ui.*;
import com.jme.scene.Node;
import com.jme.curve.BezierCurve;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import org.excelsi.aether.NHBot;


/**
 * This is an all-consuming controller.
 */
public class ConsumingController extends RemovingCurveController implements StoppableController {
    private Node _food;


    public ConsumingController(NHBot b, Object a, Node n) {
        super(new BezierCurve("e", new Vector3f[]{new Vector3f(0.0f, 2.5f, 1f),
               new Vector3f(0.0f, 1.7f, 1f)}),
               NodeFactory.loadModel(((Consume.Consuming)a).getComestible(), "ee"));
        setRepeatType(RT_WRAP);
        setUpVector(MatrixNode.UP);
        if(!((Consume.Consuming)a).isOnGround()) {
            getSpatial().addController(this);
            n.attachChild(getSpatial());
            n.updateRenderState();
        }
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
