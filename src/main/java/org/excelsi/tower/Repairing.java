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
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class Repairing implements ProgressiveAction {
    private NHBot _b;
    private NHSpace _s;
    private Corpse _c;
    private int _t = 16;


    public Repairing(NHBot b, NHSpace s, Corpse c) {
        _b = b;
        _s = s;
        _c = c;
    }

    public String getExcuse() { return "repairing an animatron"; }

    public int getInterruptRate() { return 100; }

    public void interrupted() {
        N.narrative().printf(_b, "You stop repairing.");
    }

    public void stopped() {
    }

    public boolean iterate() {
        if(--_t==0) {
            NHBot a = _c.getSpirit();
            if(a!=null) {
                if(_s.isOccupied()) {
                    N.narrative().printf(_b, "Something's in the way.");
                }
                else {
                    N.narrative().printf(_b, "Success!");
                    a.setDead(false);
                    a.setHp(a.getMaxHp());
                    _s.consume(_c);
                    _s.setOccupant(a);
                }
            }
            else {
                N.narrative().printf(_b, "Gone forever...");
            }
            return false;
        }
        return true;
    }
}
