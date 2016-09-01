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
import static org.excelsi.aether.Brain.*;


/**
 * A daemon that heals using potions or pills.
 */
public class HealDaemon extends Daemon {
    private Item _com = null;
    private Chemical _nop = new Chemical("nop");


    public void poll(final Context c) {
        if(in.attack==null&&in.b.getHp()<in.b.getMaxHp()/2) {
            for(Item i:in.b.getInventory().getItem()) {
                if((i instanceof Potion || i instanceof Pill) && i.hasFragment(Healing.class)) {
                    strength=12;
                    _com = i;
                    return;
                }
            }
        }
        strength = 0;
    }

    @Override public void perform(final Context c) {
        NHBotAction a;
        if(_com instanceof Pill) {
            a = new Consume((Pill)_com);
        }
        else {
            a = new Quaff((Potion)_com);
        }
        a.setBot(in.b);
        a.perform();
    }

    public Chemical getChemical() {
        return _nop;
    }
}

