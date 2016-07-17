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
import com.jme.math.Vector3f;
import com.jme.scene.Controller;


public class Gravity extends Controller {
    private Spatial _s;
    private Vector3f _velocity;
    private Vector3f _ground;
    private Vector3f _constant;
    private float _elapsed;
    private float _delay;
    private Runnable _r;
    private Vector3f ACCEL;


    public Gravity(Spatial s, Vector3f velocity, Vector3f ground) {
        this(s, velocity, ground, -120f, 0f);
    }

    public Gravity(Spatial s, Vector3f velocity, Vector3f ground, float grav, float delay) {
        this(s, velocity, ground, grav, delay, null);
    }

    public Gravity(Spatial s, Vector3f velocity, Vector3f ground, float grav, float delay, Runnable r) {
        _s = s;
        _velocity = velocity;
        _ground = ground;
        _constant = s.getLocalTranslation();
        _elapsed = 0f;
        setSpeed(1f);
        setMinTime(0f);
        setMaxTime(Float.MAX_VALUE);
        ACCEL = new Vector3f(0f, grav, 0f);
        _delay = delay;
        _r = r;
    }
    private boolean _debug;

    public void update(float dt) {
        if(isActive()) {
            if(_delay>0f) {
                _delay -= getSpeed()*dt;
                if(_delay<=0f&&_r!=null) {
                    //System.err.println("RUNNING");
                    _r.run();
                }
                return;
            }
            _elapsed += getSpeed()*dt;
            if(_elapsed>=getMinTime()&&_elapsed<=getMaxTime()) {
                float t = _elapsed-getMinTime();
                Vector3f a = ACCEL.mult(t*t).addLocal(_velocity.mult(t)).addLocal(_constant);
                if(a.y<_ground.y) {
                    a.y = _ground.y;
                    setActive(false);
                    _s.removeController(this);
                    done();
                }
                _s.setLocalTranslation(a);
            }
        }
    }

    protected void done() {
    }
}
