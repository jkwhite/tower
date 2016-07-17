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


import com.jme.curve.CurveController;
import com.jme.curve.Curve;
import com.jme.scene.*;


public class RemovingCurveController extends CurveController {
    private float _time = 0f;
    private Spatial _m;


    public RemovingCurveController(Curve c, Spatial mover) {
        super(c, mover);
        _m = mover;
    }

    public void update(float time) {
        super.update(time);
        _time += time*getSpeed();
        if(_time>1f&&getRepeatType()==RT_CLAMP) {
            done();
        }
    }

    protected void done() {
        _m.removeController(this);
    }

    protected final Spatial getSpatial() {
        return _m;
    }
}
