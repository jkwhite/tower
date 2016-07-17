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


import com.jme.curve.Curve;
import com.jme.scene.Spatial;
import org.excelsi.aether.ui.FixedTimeController;
import com.jme.math.Vector3f;


public class FixedCurveController extends FixedTimeController {
    public FixedCurveController(Curve c, Spatial s, float time) {
        this(c, s, time, 0f);
    }

    public FixedCurveController(Curve c, Spatial s, float time, float delay) {
        super(new Modulator[]{new CurveModulator(c, s)}, CONSTANT, time, delay);
    }

    public static class CurveModulator implements Modulator {
        private Curve _c;
        private Spatial _s;
        private Vector3f _pos;


        public CurveModulator(Curve c, Spatial s) {
            _c = c;
            _s = s;
            _pos = s.getLocalTranslation();
        }

        public void update(float orig, float dest) {
            float time = dest/(orig+dest);
            if(time==1f) {
                time = 0.9999f;
            }
            _c.getPoint(time, _pos);
        }

        public void done() {
        }
    }
}
