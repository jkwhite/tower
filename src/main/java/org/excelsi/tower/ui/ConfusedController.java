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


import org.excelsi.aether.ui.*;
import com.jme.scene.Node;
import com.jme.math.Quaternion;
import org.excelsi.aether.NHBot;


/**
 * This is a very confused controller.
 */
public class ConfusedController extends FixedTimeController implements StoppableController {
    private Node _n;


    public ConfusedController(NHBot b, Object a, Node n) {
        super(new FixedTimeController.Modulator[]{new SpinModulator(n.getParent().getParent(), SpinModulator.Y)},
                    FixedTimeController.CONSTANT, 1.5f);
        setRepeatType(FixedTimeController.RT_WRAP);
        n.getParent().getParent().addController(this);
        _n = n;
    }

    public void stop() {
        _n.getParent().getParent().setLocalRotation(new Quaternion(new float[]{0f, 0f, 0f}));
        _n.getParent().getParent().removeController(this);
    }
}
