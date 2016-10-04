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
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


public class Electromagnet extends Floor {
    private static final long serialVersionUID = 1L;
    private Electromagnet _o;
    private boolean _active;


    public Electromagnet() {
    }

    public Electromagnet(Electromagnet opposite) {
        _o = opposite;
        opposite._o = this;
    }

    private MSpace[] _path;
    public void setActive(boolean active) {
        _active = active;
        if(_o!=null) {
            _o._active = _active;
        }
        if(active) {
            float t = 0f;
            _path = path(_o, true, new WalkableFilter(), 1f);
            for(int i=0;i<_path.length/2;i++) {
                ((NHSpace)_path[i]).addParasite(new EMF(t));
                ((NHSpace)_path[_path.length-i-1]).addParasite(new EMF(t));
                t += 0.15f;
                if(t>=0.6f) {
                    t = 0f;
                }
            }
            if(_path.length%2==1) {
                ((NHSpace)_path[_path.length/2]).addParasite(new EMF(t));
            }
        }
        else {
            for(MSpace m:_path) {
                for(Parasite p:((NHSpace)m).getParasites()) {
                    if(p instanceof EMF) {
                        ((NHSpace)m).removeParasite(p);
                    }
                }
            }
        }
    }

    public String getModel() {
        return "a#";
    }

    public String getColor() {
        return "red";
    }

    public int getDepth() {
        return -4;
    }

    public boolean look(final Context c, boolean nothing, boolean lootOnly) {
        boolean ret = super.look(c, nothing, lootOnly);
        if(!lootOnly) {
            c.n().print(c.actor(), "There is an electromagnetic coil here, covered in dust.");
            return true;
        }
        return ret;
    }

    public static class EMF extends Parasite {
        private float _offset;


        public EMF(float offset) {
            _offset = offset;
        }

        public float getOffset() {
            return _offset;
        }

        public void trigger(NHBot b) {
        }

        public boolean notice(NHBot b) {
            return true;
        }

        public void attacked(Armament a) {
        }

        public void update() {
        }

        public String getModel() { return "-"; }
        public String getColor() { return "translucent"; }
        public int getHeight() { return 0; }
        public boolean isMoveable() { return false; }
    }
}
