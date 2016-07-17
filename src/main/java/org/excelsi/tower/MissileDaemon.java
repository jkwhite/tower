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


public class MissileDaemon extends AttackDaemon {
    private long _last = 0;
    private Item _m;


    public void MissileDaemon() {
    }

    public void poll() {
        super.poll();
        _m = null;
        if(strength>=0&&in.important!=null) {
            MSpace f = in.b.getEnvironment().getMSpace();
            MSpace t = in.important.getEnvironment().getMSpace();
            if(f.distance(t)>=2) {
                if(f.isCardinalTo(t)||f.isDiagonalTo(t)) {
                    Direction m = f.directionTo(t);
                    MSpace o = f.move(m);
                    while(o!=t&&o!=null) {
                        if(o.isOccupied()&&in.b.threat((NHBot)o.getOccupant())!=Threat.kos) {
                            break;
                        }
                        o = o.move(m);
                    }
                    if(o!=t) {
                        strength = -1;
                    }
                    else {
                        boolean found = false;
                        if(in.b.getWielded() instanceof Gun) {
                            _m = in.b.getWielded();
                            strength *= 3;
                            found = true;
                        }
                        else {
                            for(Item i:in.b.getInventory().getItem()) {
                                if((i instanceof Missile)
                                    || (i instanceof Wand&&((Wand)i).isDirectable() && ((Wand)i).getCharges()>0)) {
                                    _m = i;
                                    strength *= 3;
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if(!found) {
                            strength = -1;
                        }
                    }
                }
                else {
                    strength = -1;
                }
            }
            else {
                strength = -1;
            }
        }
    }

    public void run() {
        if(in.important!=null&&_m!=null) {
            in.b.getEnvironment().face(in.important);
            Direction d = in.b.getEnvironment().getMSpace().directionTo(in.important.getEnvironment().getMSpace());
            NHBotAction a;
            if(_m instanceof Wand) {
                a = new Zap((Wand)_m, d);
            }
            else if(_m instanceof Gun) {
                a = new FireAction((Gun)_m, d);
            }
            else {
                a = new Throw(_m, d);
            }
            a.setBot(in.b);
            a.perform();
        }
    }
}
