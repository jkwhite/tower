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


import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.scene.state.FogState;
import com.jme.scene.state.RenderState;


public class FogController extends Controller {
    private Modulator _m;
    private Spatial _s;
    private float _time;


    public FogController(Spatial s, Modulator m) {
        this(s, m, 1f);
    }

    public FogController(Spatial s, Modulator m, float speed) {
        _s = s;
        _m = m;
        setMinTime(0f);
        setMaxTime(Float.MAX_VALUE);
        setSpeed(speed);
    }

    public void update(float dt) {
        if(isActive()) {
            _time += getSpeed()*dt;
            if(_time>=getMinTime()&&_time<=getMaxTime()) {
                FogState fs = (FogState) _s.getRenderState(RenderState.RS_FOG);
                if(fs==null) {
                    setActive(false);
                    done();
                    return;
                }
                if(!_m.modulate(fs, _time)) {
                    setActive(false);
                    done();
                }
                //_s.setRenderState(fs);
                //_s.updateRenderState();
            }
        }
    }

    protected void done() {
    }

    interface Modulator {
        boolean modulate(FogState fs, float time);
    }
}
