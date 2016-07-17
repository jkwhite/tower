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
import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Direction;


public abstract class Trap extends Parasite implements Createable {
    private String _kind;
    private int _detect;


    public Trap(String kind, int detect) {
        this(true, kind, detect);
    }

    public Trap(boolean hidden, String kind, int detect) {
        super(hidden);
        _kind = kind;
        _detect = detect;
    }

    public final String getCreationSkill() {
        return "traps";
    }

    public String getName() {
        return _kind+" trap";
    }

    public String getColor() {
        return "gray";
    }

    public String getModel() {
        return "^";
    }

    public int getHeight() {
        return 0;
    }

    public boolean isMoveable() {
        return false;
    }

    public void update() {
    }

    public void attacked(Armament a) {
    }

    public boolean notice(NHBot b) {
        if(b.isPlayer()) {
            if(isHidden()) {
                if(b.isPlayer()&&Rand.d100(b.getSkill("traps"))&&b.getEnvironment().getMSpace().distance(getSpace())<2) {
                    setHidden(false);
                    N.narrative().print(getSpace(), "There is "+Grammar.nonspecific(getName())+" here.");
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                N.narrative().print(b, "There is "+Grammar.nonspecific(_kind)+" trap here.");
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return getName();
    }
}
