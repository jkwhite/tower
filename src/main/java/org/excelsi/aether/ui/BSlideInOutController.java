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

class BSlideInOutController extends Controller {
    public static final int ENTER_NORTH = 0;
    public static final int EXIT_NORTH = 1;
    public static final int ENTER_SOUTH = 2;
    public static final int EXIT_SOUTH = 3;
    public static final int ENTER_EAST = 4;
    public static final int EXIT_EAST = 5;
    public static final int ENTER_WEST = 6;
    public static final int EXIT_WEST = 7;
    public static final int GROW_CENTER = 8;
    public static final int SHRINK_CENTER = 9;
    public static final int GROW = 10;
    public static final int SHRINK = 11;

    private BComponent _component;
    private float _time;
    private float _elapsed = 0f;
    private int _dX;
    private int _dY;
    private int _oX;
    private int _oY;
    private int _mode;
    private boolean _center = false;
    private int _cwidth;
    private int _cheight;


    public BSlideInOutController(BComponent component, int mode) {
        this(component, mode, 1f);
    }

    public BSlideInOutController(BComponent component, int mode, float time) {
        _time = time;
        _component = component;
        _oX = component.getX();
        _oY = component.getY();
        _dX = component.getX();
        _dY = component.getY();
        _mode = mode;
        int w = DisplaySystem.getDisplaySystem().getWidth();
        int h = DisplaySystem.getDisplaySystem().getHeight();
        switch(mode) {
            case ENTER_NORTH:
                component.setLocation(component.getX(), DisplaySystem.getDisplaySystem().getHeight()+component.getHeight());
                _oY = component.getY();
                break;
            case ENTER_SOUTH:
                component.setLocation(component.getX(), -component.getHeight());
                _oY = component.getY();
                break;
            case EXIT_NORTH:
                _dY = DisplaySystem.getDisplaySystem().getHeight();
                break;
            case EXIT_SOUTH:
                _dY = -component.getHeight();
                if(_dY==0) {
                    _dY = -24; // for some reason some components say height 0?
                }
                break;
            case ENTER_EAST:
                component.setLocation(DisplaySystem.getDisplaySystem().getWidth(), component.getY());
                _oX = component.getX();
                break;
            case EXIT_EAST:
                _dX = DisplaySystem.getDisplaySystem().getWidth();
                break;
            case ENTER_WEST:
                component.setLocation(-component.getWidth(), component.getY());
                _oX = component.getX();
                break;
            case EXIT_WEST:
                _dX = -component.getWidth();
                break;
            case GROW:
                _oX = component.getX();
                _oY = component.getY();
                _dX = _oX-component.getWidth()/2;
                _dY = _oY-component.getHeight()/2;
                _center = true;
                _cwidth = component.getX()*2;
                _cheight = component.getY()*2;
                //System.err.println("ox="+_oX+", oy="+_oY+", dx="+_dX+", dy="+_dY+", cw="+_cwidth+", ch="+_cheight);
                break;
            case GROW_CENTER:
                _oX = w/2;
                _oY = h/2;
                _dX = _oX-component.getWidth()/2;
                _dY = _oY-component.getHeight()/2;
                _center = true;
                _cwidth = w;
                _cheight = h;
                break;
            case SHRINK:
                _dX = _oX+component.getWidth()/2;
                _dY = _oY+component.getHeight()/2;
                _center = true;
                _cwidth = _dX*2;
                _cheight = _dY*2;
                break;
            case SHRINK_CENTER:
                _dX = w/2;
                _dY = h/2;
                _center = true;
                _cwidth = w;
                _cheight = h;
                break;
            default:
                throw new IllegalArgumentException("unknown mode '"+mode+"'");
        }
        setMinTime(0f);
        setMaxTime(Float.MAX_VALUE);
        setSpeed(1.0f);
    }

    public void update(float dt) {
        if(isActive()) {
            _elapsed += getSpeed()*dt;
            if(_elapsed>=getMinTime()&&_elapsed<=getMaxTime()) {
                float pct = _elapsed / _time;
                if(pct>1) {
                    pct = 1;
                }
                float orig, dest;
                if((_mode&1)==0) {
                    pct = 1-pct;
                    orig = pct*pct;
                    dest = 1-orig;
                }
                else {
                    dest = pct*pct;
                    orig = 1-dest;
                }
                int nx = (int) (orig*_oX+dest*_dX);
                int ny = (int) (orig*_oY+dest*_dY);
                _component.setLocation(nx, ny);
                //System.err.println("nx="+nx+", ny="+ny);
                if(_center) {
                    int ex = 2*(_cwidth/2-nx);
                    int ey = 2*(_cheight/2-ny);
                    _component.setSize(ex, ey);
                }
                if(nx==_dX && ny==_dY) {
                    setActive(false);
                    done();
                }
            }
        }
    }

    protected void done() {
    }
}
