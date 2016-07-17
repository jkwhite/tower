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


import com.jme.math.Vector3f;
import com.jme.renderer.*;
import com.jme.scene.*;

import java.util.ArrayList;
import java.util.List;

import org.excelsi.matrix.*;
import org.excelsi.aether.*;


public class SlideInOutController extends FixedTimeController {
    public SlideInOutController(Spatial spatial, Vector3f start, Vector3f end, int mode) {
        this(spatial, start, end, mode, 1f);
    }

    public SlideInOutController(Spatial spatial, Vector3f start, Vector3f end, int mode, float time) {
        super(new Modulator[]{new SlideModulator(spatial, start, end)}, mode, time);
    }

    public SlideInOutController(Spatial spatial, Vector3f start, Vector3f end, int mode, float time, float delay) {
        super(new Modulator[]{new SlideModulator(spatial, start, end)}, mode, time, delay);
    }

    public static class SlideModulator implements Modulator {
        private Spatial _spatial;
        private Vector3f _o;
        private Vector3f _d;
        private boolean _clear;


        public SlideModulator(Spatial spatial, Vector3f start, Vector3f end) {
            this(spatial, start, end, true);
        }

        public SlideModulator(Spatial spatial, Vector3f start, Vector3f end, boolean clear) {
            _spatial = spatial;
            _o = start;
            _d = end;
            _clear = clear;
        }

        public void setStart(Vector3f start) {
            _o = start;
        }

        public void setEnd(Vector3f end) {
            _d = end;
        }

        public void update(float orig, float dest) {
            Vector3f v = _spatial.getLocalTranslation();
            v.x = orig*_o.x + dest*_d.x;
            v.y = orig*_o.y + dest*_d.y;
            v.z = orig*_o.z + dest*_d.z;
        }

        public void done() {
            if(_clear) {
                _spatial = null;
            }
        }
    }
}
