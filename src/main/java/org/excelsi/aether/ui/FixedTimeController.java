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


/** 
 * Controller that operates over a fixed length of time.
 */
public class FixedTimeController extends Controller implements StoppableController {
    /** quadratic mode that speeds up over time */
    public static final int SLOW_TO_FAST = 0;
    /** quadratic mode that slows down over time */
    public static final int FAST_TO_SLOW = 1;
    /** linear mode (constant speed) */
    public static final int CONSTANT = 2;
    private float _time;
    private float _elapsed = 0f;
    private float _ended;
    private int _mode;
    private float _delay;
    private float _endDelay;
    private Modulator[] _mods;


    public FixedTimeController(Modulator[] mods, int mode) {
        this(mods, mode, 1f);
    }

    public FixedTimeController(Modulator mod, int mode, float time) {
        this(new Modulator[]{mod}, mode, time);
    }

    public FixedTimeController(Modulator[] mods, int mode, float time) {
        this(mods, mode, time, 0f);
    }

    public FixedTimeController(Modulator mod, int mode, float time, float delay) {
        this(new Modulator[]{mod}, mode, time, delay);
    }

    public FixedTimeController(Modulator[] mods, int mode, float time, float delay) {
        _time = time;
        _mode = mode;
        _mods = mods;
        setMinTime(0f);
        setMaxTime(Float.MAX_VALUE);
        setSpeed(1.0f);
        _delay = delay;
    }

    public Modulator[] getModulators() {
        return _mods;
    }

    public void setDelay(float delay) {
        _delay = delay;
    }

    public float getDelay() {
        return _delay;
    }

    public void setEndDelay(float endDelay) {
        _endDelay = endDelay;
    }

    public float getEndDelay() {
        return _endDelay;
    }

    public void resetTime() {
        /*
        if(!isActive()) {
            _elapsed = 0f;
            setActive(true);
        }
        */
        _elapsed = 0f;
        //setMinTime(0f);
        //setMaxTime(Float.MAX_VALUE);
        setSpeed(1.0f);
        //_time+=_elapsed;
    }

    public void add(Modulator m) {
        Modulator[] mods = new Modulator[_mods.length+1];
        mods[0] = m;
        System.arraycopy(_mods, 0, mods, 1, _mods.length);
        _mods = mods;
    }

    public void stop() {
        for(Modulator m:_mods) {
            m.done();
        }
        done();
    }

    public void update(float dt) {
        if(isActive()) {
            if(_delay>0) {
                _delay -= getSpeed()*dt;
                return;
            }
            _elapsed += getSpeed()*dt;
            if(_elapsed>=getMinTime()&&_elapsed<=getMaxTime()) {
                float pct = _elapsed / _time;
                if(pct>1f) {
                    pct = 1;
                }
                float orig, dest;
                if(_mode==FAST_TO_SLOW) {
                    pct = 1-pct;
                    orig = pct*pct;
                    dest = 1-orig;
                }
                else if(_mode==SLOW_TO_FAST) {
                    dest = pct*pct;
                    orig = 1-dest;
                }
                else {
                    dest = pct;
                    orig = 1-dest;
                }
                for(Modulator m:_mods) {
                    m.update(orig, dest);
                }
                if(orig==0f) {
                    if(getRepeatType()==RT_CLAMP) {
                        setActive(false);
                        for(Modulator m:_mods) {
                            m.done();
                        }
                        done();
                    }
                    else if(getRepeatType()==RT_WRAP) {
                        if(_ended==0f) {
                            _ended = _elapsed;
                            wrapped();
                        }
                        if(_elapsed>=_ended+_endDelay*getSpeed()) {
                            _elapsed = 0f;
                            _ended = 0f;
                        }
                    }
                    else if(getRepeatType()==RT_CYCLE) {
                        throw new UnsupportedOperationException("RT_CYCLE not supported yet");
                    }
                    else {
                        setActive(false);
                        for(Modulator m:_mods) {
                            m.done();
                        }
                        done();
                    }
                }
            }
        }
    }

    protected void done() {
    }

    protected void wrapped() {
    }

    /**
     * Modulator describes one delta.
     *
     */
    public interface Modulator {
        /** 
         * Updates this modulator.
         * 
         * @param orig percentage of original
         * @param dest percentage of destination (normally 1-orig)
         */
        void update(float orig, float dest);

        /**
         * Invoked when this modulator is done.
         */
        void done();
    }
}
