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
package org.excelsi.tower.ui;


import org.excelsi.aether.ui.*;
import com.jme.scene.Node;
import com.jme.math.Quaternion;
import org.excelsi.aether.NHBot;
import com.jme.scene.state.*;
import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;


public class InvisibleController extends FixedTimeController implements StoppableController {
    private Node _n;


    public InvisibleController(NHBot b, Object a, Node n) {
        super(new FixedTimeController.Modulator[]{new AlphaModulator(n, 0.3f)},
                    FixedTimeController.CONSTANT, 1.5f);
        setRepeatType(FixedTimeController.RT_WRAP);
        n.addController(this);
        _n = n;
        FogState f = DisplaySystem.getDisplaySystem().getRenderer().createFogState();
        f.setColor(new ColorRGBA(0f,0f,0f,1f));
        f.setDensity(0.5f);
        f.setEnd(0.1f);
        f.setStart(0f);
        n.getParent().getParent().getParent().setRenderState(f);
    }

    public void stop() {
        //_n.setLocalRotation(new Quaternion(new float[]{0f, 0f, 0f}));
        _n.removeController(this);
        _n.getParent().getParent().getParent().clearRenderState(RenderState.RS_FOG);
        _n.getParent().getParent().getParent().updateRenderState();
    }
}
