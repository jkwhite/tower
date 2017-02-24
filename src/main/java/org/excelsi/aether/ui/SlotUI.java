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


import org.excelsi.aether.NHBot;
import org.excelsi.aether.SlotType;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


public abstract class SlotUI {
    public static SlotUI create(SlotType s, NHBot b, Spatial n) {
        if(n==null) {
            throw new IllegalArgumentException("null node");
        }
        switch(s) {
            case head:
                return new HeadUI(b, n);
            case back:
                return new BackUI(b, n);
            default:
                return null;
        }
    }

    protected NHBot _b;
    protected Spatial _n;

    protected SlotUI(NHBot b, Spatial n) {
        _b = b;
        _n = n;
    }

    public abstract void onMove();
}
