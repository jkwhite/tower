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
import org.excelsi.matrix.*;
import java.util.Collections;
import java.util.List;


public abstract class RoomMixin implements Mixin {
    public boolean match(Class c) {
        return Level.class.isAssignableFrom(c);
    }

    public final void mix(Object o) {
        if(!Rand.d100(getChance())) {
            return;
        }
        Level level = (Level) o;
        List<Level.Room> rooms = level.getRooms();
        Collections.shuffle(rooms);
        int[] min = getMinDimensions();
        for(Level.Room r:rooms) {
            if(r.width()<min[0]||r.height()<min[1]) {
                continue;
            }
            int doorcount = 0;
            boolean good = true;
fast:       for(int x=r.getX1();x<=r.getX2();x++) {
                for(int y=r.getY1();y<=r.getY2();y++) {
                    MSpace m = level.getSpace(x,y);
                    if(m==null || m.getClass()==Floor.class || m instanceof Wall) {
                        continue;
                    }
                    if(m instanceof Doorway) {
                        if(++doorcount==2) {
                            good = false;
                            break fast;
                        }
                        continue;
                    }
                    good = false;
                    break fast;
                }
            }
            if(good) {
                build(level, r);
                break;
            }
        }
    }

    protected abstract int getChance();

    protected abstract int[] getMinDimensions();

    protected abstract void build(Level level, Level.Room r);
}
