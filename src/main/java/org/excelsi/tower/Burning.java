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


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Direction;
import org.excelsi.aether.*;


public class Burning extends Parasite {
    private long _creation = Time.now();
    private long _duration;


    public Burning() {
        this(10);
    }

    public Burning(long duration) {
        _duration = duration;
    }

    public boolean isMoveable() {
        return false;
    }

    public String getModel() {
        return getSpace().getModel();
    }

    public String getColor() {
        return "orange";
    }

    public void attacked(Armament a) {
    }

    public int getHeight() {
        return 0;
    }

    public boolean notice(NHBot b) {
        return true;
    }

    public void trigger(NHBot b) {
    }

    public void update() {
        long t = Time.now();
        if(t>_creation+_duration) {
            getSpace().setColor("black");
            getSpace().removeParasite(this);
        }
        else if(t>=_creation+4) {
            for(MSpace m:getSpace().surrounding()) {
                if(m instanceof Combustible && Rand.d100(40)) {
                    Fire.ignite((Combustible)m, 500);
                }
            }
        }
        new Fire().invoke(null, getSpace(), null);
        if(getSpace().isOccupied()) {
            new Fire().inflict(getSpace().getOccupant(), new Source("a grass fire"));
        }
    }
}
