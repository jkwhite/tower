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


import org.excelsi.matrix.*;
import org.excelsi.aether.*;


public class MagicBeanstalk extends Beanstalk implements Climbable {
    private boolean _desc;
    private int _h;


    public MagicBeanstalk(boolean desc, int h) {
        _desc = desc;
        _h = h;
    }

    protected Item fruit() {
        Bean b;
        if(Rand.d100(1)) {
            b = new MagicBean();
        }
        else {
            b = new Bean();
        }
        b.setCount(Rand.om.nextInt(4)+1);
        return b;
    }

    public Size getSize() {
        return Size.huge;
    }

    public String getName() {
        return "magic beanstalk";
    }

    public boolean notice(NHBot b) {
        if(b.isPlayer()) {
            N.narrative().print(b, "There is a "+getName()+" here.");
            String ex = "It extends through the ";
            if(isDescending()) {
                if(isAscending()) {
                    ex += "floor and ceiling.";
                }
                else {
                    ex += "floor.";
                }
            }
            else if(isAscending()) {
                ex += "ceiling.";
            }
            if(getSpace().getOccupant()==b) {
                N.narrative().print(b, ex);
            }
            return true;
        }
        return false;
    }

    public boolean isAscending() {
        return _h>0;
    }

    public boolean isDescending() {
        return _desc;
    }

    public MSpace findEndpoint(Matrix m) {
        MatrixMSpace sp = (MatrixMSpace) getSpace();
        MSpace e = m.getSpace(sp.getI(), sp.getJ());
        if(e==null) {
            e = new Blank() {
                public boolean isTransparent() { return true; }
                public boolean isWalkable() { return true; }
            };
            m.setSpace((MatrixMSpace)e, sp.getI(), sp.getJ());
        }
        if(!((NHSpace)e).hasParasite(MagicBeanstalk.class)) {
            ((NHSpace)e).addParasite(new MagicBeanstalk(true, _h-1));
        }
        return e;
    }
}
