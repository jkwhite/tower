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
package org.excelsi.aether.ui;


import org.excelsi.aether.*;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;


public class HeadUI extends SlotUI {
    //private FixedTimeController _one, _two;
    private boolean _active = false;


    public HeadUI(NHBot b, Spatial n) {
        super(b, n);
        /*
        _one = new FixedTimeController(new SlideInOutController.SlideModulator(n,
            new Vector3f(0f, 3.7f, 0.15f), new Vector3f(0f, 4.1f, 0.15f), false),
            FixedTimeController.FAST_TO_SLOW, 0.1f) {
            protected void done() {
                _n.removeController(_one);
                _n.addController(_two);
            }
        };
        _two = new FixedTimeController(new SlideInOutController.SlideModulator(n,
            new Vector3f(0f, 4.1f, 0.15f), new Vector3f(0f, 3.7f, 0.15f), false),
            FixedTimeController.SLOW_TO_FAST, 0.1f) {
            protected void done() {
                _n.removeController(_two);
                _active = false;
            }
        };
        */
    }

    public void onMove() {
        /*
        if(!_active) {
            _active = true;
            _one.setActive(true);
            _one.resetTime();
            _two.setActive(true);
            _two.resetTime();
            _n.addController(_one);
        }
        */
    }
}
