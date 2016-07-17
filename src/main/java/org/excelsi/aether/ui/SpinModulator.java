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


import com.jme.scene.Spatial;
import com.jme.math.Quaternion;


public class SpinModulator implements FixedTimeController.Modulator {
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    private Spatial _n;
    private float[] _rot = new float[]{0f, 0f, 0f};
    private float[] _begin;
    private float[] _end;


    public SpinModulator(Spatial n, int axis) {
        this(n, axis, 0f, (float)Math.PI*2f);
    }

    public SpinModulator(Spatial n, int axis, float begin, float end) {
        _n = n;
        _begin = new float[]{0, 0, 0};
        _end = new float[]{0, 0, 0};
        _begin[axis] = begin;
        _end[axis] = end;
    }

    public SpinModulator(Spatial n, float[] begin, float[] end) {
        _n = n;
        if(begin.length!=3||end.length!=3) {
            throw new IllegalArgumentException("begin and end must be length 3");
        }
        _begin = begin;
        _end = end;
    }

    public Spatial getSpatial() {
        return _n;
    }

    public void update(float orig, float dest) {
        _rot[0] = _begin[0]*orig+_end[0]*dest;
        _rot[1] = _begin[1]*orig+_end[1]*dest;
        _rot[2] = _begin[2]*orig+_end[2]*dest;
        _n.setLocalRotation(new Quaternion(_rot));
    }

    public void done() {
    }
}
