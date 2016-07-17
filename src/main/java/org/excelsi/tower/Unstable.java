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
package org.excelsi.tower;


import org.excelsi.aether.*;
import java.util.List;
import java.util.ArrayList;


public class Unstable extends Affliction {
    public static final String NAME = "Unstable";
    private NHBot[] _all;
    private String[] _colors;


    public Unstable() {
        super(NAME, Onset.tick);
        _colors = Universe.getUniverse().getColormap().keySet().toArray(new String[0]);
    }

    public void compound(Affliction a) {
    }

    public String getStatus() {
        return NAME;
    }

    public String getExcuse() {
        return "destabilized";
    }

    public void beset() {
        if(_all==null) {
            _all = Universe.getUniverse().getBots();
        }
        if(Rand.om.nextBoolean()) {
            NHBot b = getBot();
            int attr = Rand.om.nextInt(_all.length);
            NHBot f = _all[attr];
            b.setSize(f.getSize());
            b.setModel(f.getModel());
            b.setColor(_colors[Rand.om.nextInt(_colors.length)]);
            if(Rand.d100(10)) {
                b.setAirborn(!b.isAirborn());
            }
        }
    }
}
