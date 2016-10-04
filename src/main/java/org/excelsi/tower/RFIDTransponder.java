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


public class RFIDTransponder extends Floor {
    private static final long serialVersionUID = 1L;
    private RFIDTransponder _o;
    private boolean _active;
    private boolean _vertical;
    private RFID _type;
    private MSpace[] _path;


    public RFIDTransponder(RFID type, boolean vertical) {
        _type = type;
        _vertical = vertical;
    }

    public RFIDTransponder(RFID type, boolean vertical, RFIDTransponder opposite) {
        _type = type;
        _vertical = vertical;
        _o = opposite;
        opposite._o = this;
        if(opposite._type.getClass()!=_type.getClass()) {
            throw new IllegalArgumentException("incompatible sibling transponder: "+_type.getClass()+"!="+opposite._type.getClass());
        }
    }

    public void setActive(boolean active) {
        _active = active;
        if(_o!=null) {
            _o._active = _active;
        }
        if(active) {
            _path = path(_o, true, new WalkableOrThisFilter(), 1f);
            for(int i=0;i<_path.length/2;i++) {
                ((NHSpace)_path[i]).addParasite(_type.clone());
                ((NHSpace)_path[_path.length-i-1]).addParasite(_type.clone());
            }
            if(_path.length%2==1) {
                ((NHSpace)_path[_path.length/2]).addParasite(_type.clone());
            }
            _o._path = _path;
        }
        else {
            for(MSpace m:_path) {
                for(Parasite p:((NHSpace)m).getParasites()) {
                    if(p instanceof RFID) {
                        ((NHSpace)m).removeParasite(p);
                    }
                }
            }
        }
    }

    public String getModel() {
        return _vertical?"|":"-";
    }

    public String getColor() {
        return "black";
    }

    public boolean isWalkable() {
        return false;
    }

    public boolean isDestroyable() {
        return true;
    }

    public void destroy() {
        N.narrative().print(this, "The RFID reader shatters!");
        NHSpace s = replace(new Floor());
        s.add(new Wire());
        s.add(new ScrapMetal());
        for(MSpace m:_path) {
            NHSpace sp = (NHSpace) m;
            for(Parasite p:sp.getParasites()) {
                if(p instanceof RFID) {
                    sp.removeParasite(p);
                }
            }
        }
    }

    public boolean look(final Context c, boolean nothing, boolean lootOnly) {
        boolean ret = super.look(c, nothing, lootOnly);
        if(!lootOnly) {
            c.n().print(c.actor(), "There is an RF transponder here.");
            return true;
        }
        return ret;
    }
}
