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


import java.util.Map;
import static org.excelsi.aether.Brain.*;
import org.excelsi.aether.*;


public class MAttackDaemon extends AttackDaemon {
    @Override public void perform(final Context c) {
        if(in.b.threat(_last)==Threat.kos) {
            if(in.b.getEnvironment().getMSpace().isAdjacentTo(_last.getEnvironment().getMSpace())) {
                Armament a = (Armament) in.b.getWielded();
                if(a!=null&&a.getType()!=Armament.Type.melee) {
                    for(Item i:in.b.getInventory().getItem()) {
                        if(i instanceof Armament && i.getSlotType()==SlotType.hand&&((Armament)i).getType()==Armament.Type.melee) {
                            Wield w = new Wield(i);
                            w.setBot(in.b);
                            w.perform();
                            return;
                        }
                    }
                }
            }
            super.perform(c);
        }
    }
}
