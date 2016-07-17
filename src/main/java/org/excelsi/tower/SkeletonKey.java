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


public class SkeletonKey extends Tool implements Useable {
    public void use(NHBot b) {
        Direction chosen = N.narrative().direct(b, "Which direction?");
        unlock(b, chosen);
    }

    public int getFindRate() {
        return 5;
    }

    public float getWeight() {
        return 0.1f;
    }

    public float getSize() {
        return 0.1f;
    }

    public void unlock(NHBot b, Direction d) {
        NHSpace s = (NHSpace) b.getEnvironment().getMSpace().move(d);
        if(s==null||!(s instanceof Doorway)) {
            N.narrative().print(b, "There is no door there.");
            throw new ActionCancelledException();
        }
        Doorway door = (Doorway) s;
        if(door.isOpen()) {
            N.narrative().print(b, "On closer inspection, that door is open.");
            throw new ActionCancelledException();
        }
        else if(!door.isUnlockable()) {
            N.narrative().print(b, "Something is stuck in the lock.");
        }
        else if(!door.isLocked()) {
            //N.narrative().print(b, "On closer inspection, the door is not locked.");
            if(Rand.d100(10)) {
                N.narrative().print(b, "The key breaks! No luck...");
                b.getInventory().consume(this);
            }
            else if(getStatus()==Status.cursed) {
                N.narrative().print(b, "The key breaks off in the lock!");
                door.setUnlockable(false);
                b.getInventory().consume(this);
            }
            else {
                N.narrative().print(b, "Click!");
                door.setLocked(true);
            }
        }
        else {
            if(Rand.d100(10)||getStatus()==Status.cursed) {
                N.narrative().print(b, "The key breaks! No luck...");
                b.getInventory().consume(this);
            }
            else {
                N.narrative().print(b, "Click!");
                door.setLocked(false);
            }
        }
    }
}
