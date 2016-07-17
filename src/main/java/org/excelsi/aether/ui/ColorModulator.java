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
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;


/**
 * ColorModulator interpolates emissive color values between two points.
 * Multiple ColorModulators may operate simultaneously on the same node.
 */
public class ColorModulator implements FixedTimeController.Modulator {
    public enum Type { ambient, emissive, diffuse, specular };
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    private Spatial _n;
    private ColorRGBA _c;
    private ColorRGBA _o;
    private ColorRGBA _start;
    private ColorRGBA _current;
    private float _lastDest;
    private float _r;
    private float _g;
    private float _b;
    private float _a;
    private float _dr;
    private float _dg;
    private float _db;
    private float _da;
    private Type _t;


    public ColorModulator(Spatial n, ColorRGBA start, ColorRGBA end) {
        this(n, start, end, Type.emissive);
    }

    public ColorModulator(Spatial n, ColorRGBA start, ColorRGBA end, Type t) {
        if(n==null) {
            throw new IllegalArgumentException("null spatial");
        }
        _n = n;
        _c = end;
        _o = start;
        _t = t;
        _start = start;
        _current = new ColorRGBA(_o);
        _r = start.r;
        _dr = (end.r-start.r);
        _dg = (end.g-start.g);
        _db = (end.b-start.b);
        _da = (end.a-start.a);
        _g = start.g;
        _b = start.b;
        _a = start.a;
        _lastDest = 0f;
        //_n.setRenderState(NodeFactory.createMaterial(start));
        MaterialState m = (MaterialState) n.getRenderState(RenderState.RS_MATERIAL);
        MaterialState m2 = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
        if(m!=null) {
            //m2.setAlpha(m.getAlpha());
            m2.setAmbient(m.getAmbient());
            m2.setSpecular(m.getSpecular());
            m2.setDiffuse(m.getDiffuse());
            m2.setEmissive(m.getEmissive());
            m2.setShininess(m.getShininess());
            m2.setEnabled(m.isEnabled());
        }
        _n.setRenderState(m2);
    }

    public void update(float orig, float dest) {
        switch(_t) {
            case emissive:
                _o = new ColorRGBA(((MaterialState)_n.getRenderState(RenderState.RS_MATERIAL)).getEmissive());
                break;
            case ambient:
                _o = new ColorRGBA(((MaterialState)_n.getRenderState(RenderState.RS_MATERIAL)).getAmbient());
                break;
            case diffuse:
                _o = new ColorRGBA(((MaterialState)_n.getRenderState(RenderState.RS_MATERIAL)).getDiffuse());
                break;
            case specular:
                _o = new ColorRGBA(((MaterialState)_n.getRenderState(RenderState.RS_MATERIAL)).getSpecular());
                break;
        }
        float inc = dest-_lastDest;
        if(inc<0) {
            inc = dest;
            _o.set(_o.r-_c.r+_start.r, _o.g-_c.g+_start.g, _o.b-_c.b+_start.b, _o.a-_c.a+_start.a);
            _r = _o.r;
            _g = _o.g;
            _b = _o.b;
            _a = _o.a;
        }
        _lastDest = dest;
        _r = _o.r + inc*_dr;
        _g = _o.g + inc*_dg;
        _b = _o.b + inc*_db;
        _a = _o.a + inc*_da;
        _current.set(box(_r), box(_g), box(_b), box(_a));
        switch(_t) {
            case emissive:
                ((MaterialState)_n.getRenderState(RenderState.RS_MATERIAL)).setEmissive(_current);
                break;
            case ambient:
                ((MaterialState)_n.getRenderState(RenderState.RS_MATERIAL)).setAmbient(_current);
                break;
            case diffuse:
                ((MaterialState)_n.getRenderState(RenderState.RS_MATERIAL)).setDiffuse(_current);
                break;
            case specular:
                ((MaterialState)_n.getRenderState(RenderState.RS_MATERIAL)).setSpecular(_current);
                break;
        }
        _n.updateRenderState();
    }

    private static float box(float f) {
        return Math.min(Math.max(0f, f), 1f);
    }

    public void done() {
        ((MaterialState)_n.getRenderState(RenderState.RS_MATERIAL)).setEmissive(_start);
        _n.updateRenderState();
    }
}
