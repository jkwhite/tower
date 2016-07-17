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


public class LockAction extends DefaultNHBotAction implements SpaceAction {
    static {
        Extended.addCommand("lock", new LockAction());
    }

    public String getDescription() {
        return "Locks or unlocks a lock";
    }

    public boolean isPerformable(NHBot b) {
        for(MSpace m:b.getEnvironment().getMSpace().surrounding()) {
            if(m instanceof Doorway&&!m.isOccupied()) {
                return true;
            }
        }
        return false;
    }

    public void perform() {
        Item i = N.narrative().choose(getBot(), new ItemConstraints(
            getBot().getInventory(), "Which key do you want to use?",
            "That is not a key.",
            new InstanceofFilter(SkeletonKey.class)), false);
        ((SkeletonKey)i).use(getBot());
    }

    public String toString() {
        return "Lock/Unlock";
    }
}
