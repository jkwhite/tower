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


import org.excelsi.aether.Rand;
import org.excelsi.aether.ui.*;
import com.jme.scene.Node;
import com.jme.renderer.ColorRGBA;


public class ChromaticWallNodeFactory extends DefaultNHSpaceNodeFactory {
    public void lock(Node n) {
        n.lockBounds();
        n.lockShadows();
        n.lockMeshes();
    }

    public Node createNode(String name, Object o, Node parent) {
        Node n = super.createNode(name, o, parent);
        ColorModulator co = new ColorModulator(n.getChild(0), new ColorRGBA(0,0,0,0), new ColorRGBA(0f, 0f, 1f, 1f));
        FixedTimeController cc = new FixedTimeController(new FixedTimeController.Modulator[]{
            co}, FixedTimeController.CONSTANT, 5f, Rand.om.nextFloat());
        cc.setRepeatType(FixedTimeController.RT_WRAP);
        n.addController(cc);
        return n;
    }
}
