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
import java.util.Collections;
import java.util.ArrayList;
import org.excelsi.matrix.MSpace;
import java.util.Arrays;


public class Propagate extends Affliction {
    private int _period = 15;
    private int _time = 0;


    public Propagate() {
        super("propagate", Onset.tick);
    }

    public void setPeriod(int per) {
        _period = per;
    }

    public void beset() {
        if(++_time==_period) {
            NHBot m = getBot();
            ArrayList<MSpace> spaces = new ArrayList<MSpace>(Arrays.asList(m.getEnvironment().getMSpace().surrounding()));
            Collections.shuffle(spaces);
            for(MSpace s:spaces) {
                if(s!=null&&!s.isOccupied()&&s.isWalkable()) {
                    s.setOccupant(Universe.getUniverse().createBot(m.getCommon()));
                    break;
                }
            }
            _time = 0;
        }
    }

    public String getStatus() {
        return null;
    }

    public String getExcuse() {
        return null;
    }

    public void compound(Affliction a) {
    }
}
