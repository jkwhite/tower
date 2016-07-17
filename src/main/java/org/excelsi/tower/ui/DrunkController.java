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


public class DrunkController extends FixedTimeController implements StoppableController {
    private Node _n;


    public DrunkController(NHBot b, Object a, Node n) {
        super(new FixedTimeController.Modulator[]{new SpinModulator(n.getParent().getParent(), SpinModulator.Z, 0f, (float)Math.PI/8f)},
                    FixedTimeController.SLOW_TO_FAST, 1f);
        //setRepeatType(FixedTimeController.RT_WRAP);
        n.getParent().getParent().addController(this);
        _n = n;
    }

    protected void done() {
        _n.getParent().getParent().removeController(this);
    }

    public void stop() {
        _n.getParent().getParent().addController(new FixedTimeController(new FixedTimeController.Modulator[]{
                    new SpinModulator(_n.getParent().getParent(), SpinModulator.Z, (float)Math.PI/8f, 0f)},
                    FixedTimeController.FAST_TO_SLOW, 1f) {
                protected void done() {
                    _n.getParent().getParent().removeController(this);
                }
        });
    }
}
