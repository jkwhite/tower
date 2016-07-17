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
import com.jme.math.Vector3f;


public class SizeModulator implements FixedTimeController.Modulator {
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    private Spatial _n;
    private float[] _size = new float[]{0f, 0f, 0f};
    private float[] _begin;
    private float[] _end;


    public SizeModulator(Spatial n, int axis) {
        this(n, axis, 0.1f, 1f);
    }

    public SizeModulator(Spatial n, int axis, float begin, float end) {
        _n = n;
        _begin = new float[]{1, 1, 1};
        _end = new float[]{1, 1, 1};
        _begin[axis] = begin;
        _end[axis] = end;
    }

    public SizeModulator(Spatial n, float[] begin, float[] end) {
        _n = n;
        if(begin.length!=3||end.length!=3) {
            throw new IllegalArgumentException("begin and end must be length 3");
        }
        _begin = begin;
        _end = end;
    }

    public void update(float orig, float dest) {
        _size[0] = _begin[0]*orig+_end[0]*dest;
        _size[1] = _begin[1]*orig+_end[1]*dest;
        _size[2] = _begin[2]*orig+_end[2]*dest;
        _n.setLocalScale(new Vector3f(_size[0], _size[1], _size[2]));
    }

    public void done() {
    }
}
