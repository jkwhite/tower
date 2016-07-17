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

//import com.jmex.effects.transients.*;
import com.jme.app.AbstractGame;
import com.jme.image.Texture;
import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.light.PointLight;
import com.jme.light.SpotLight;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.*;
import com.jme.scene.state.LightState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.*;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.curve.*;

import com.jmex.bui.*;
import com.jmex.bui.background.*;

import com.jmex.bui.layout.BorderLayout;

import java.util.ArrayList;
import java.util.List;


class BCurveController extends Controller {
    private BComponent _component;
    private Curve _curve;
    private float _time = 0;


    public BCurveController(BComponent component, Curve curve) {
        this(component, curve, 1f);
    }

    public BCurveController(BComponent component, Curve curve, float speed) {
        _component = component;
        _curve = curve;
        setMinTime(0f);
        setMaxTime(Float.MAX_VALUE);
        setSpeed(speed);
    }

    public void update(float dt) {
        if(isActive()) {
            _time += getSpeed()*dt;
            if(_time>=getMinTime()&&_time<=getMaxTime()) {
                Vector3f v = _curve.getPoint(_time);
                int nx = (int) v.x;
                int ny = (int) v.y;
                _component.setLocation(nx, ny);
            }
        }
    }
}
